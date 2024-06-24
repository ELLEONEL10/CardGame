package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import cards.Card;
import cards.Default;
import cards.Suit;
import game.EventQueue.Event;
import game.EventQueue.Player;

public class Game {
  // Representation of physical ingame table
  // Can be used for lookup any moment of the game
  public class Table {
    // Cards of all players during the war
    List<VCard> invisible;

    // Current player's card on table
    VCard cardWhite;
    VCard cardBlack;

    // Player's decks
    // We use queue, since it is first input -> first output
    // And we can put new cards in bottom of deck
    protected Queue<VCard> deckWhite;
    protected Queue<VCard> deckBlack;
    // Is war ongoing
    protected boolean isWar = false;
    // Is game over
    protected boolean isFinished = false;
  }

  public List<Card> registeredCards;

  Table table = new Table();
  EventQueue events = new EventQueue();

  public Game() {
    registeredCards = Arrays.asList(new Default().cards);
  }

  public Game(Card[] cards) {
    registeredCards = new ArrayList<Card>(Arrays.asList(cards));
  }

  public Game(List<Card> cards) {
    registeredCards = cards;
  }

  public void dispatchDecks() {
    dispatchDecks(new Random().nextInt(), false);
  }

  public void dispatchDecksNoShuffle() {
    dispatchDecks(0, true);
  }

  public void dispatchDecks(int seed) {
    dispatchDecks(seed, false);
  }

  private void dispatchDecks(int seed, boolean noShuffle) {

    var cards = new Stack<VCard>();
    // Iterate over all types of same card.
    for (var suitId = 0; suitId < 4; suitId++)
      // Iterate over all cards.
      for (var cardPrecendence = 0; cardPrecendence < registeredCards.size(); cardPrecendence++)
        // Add VCard to temporary list.
        cards.add(new VCard(cardPrecendence, Suit.fromId(suitId)));

    if (!noShuffle)
      Collections.shuffle(cards, new Random(seed));

    System.err.println("jlfkjasldkfjlaskdfjlskfjslkdjslfdkj" + cards.size());

    table.deckBlack = new LinkedList<VCard>(cards.subList(0, (cards.size() / 2)));
    table.deckWhite = new LinkedList<VCard>(cards.subList((cards.size() / 2), cards.size()));
  }

  private void poll_cards() {

    if (table.cardBlack != null || table.cardWhite != null) {
      // Error
    }

    var vCardBlack = table.deckBlack.poll();
    var vCardWhite = table.deckWhite.poll();

    table.cardBlack = vCardBlack;
    table.cardWhite = vCardWhite;

    if (table.isWar) {
      for (var i = 0; i < 3; i++) {
        table.invisible.add(table.deckBlack.poll());
        table.invisible.add(table.deckWhite.poll());
      }
    }

    if (vCardBlack == null) {
      // table.setWinner(Player.WHITE);
      table.isFinished = true;
      var e = Event.GAME_FINISH;
      e.winner = Player.WHITE;
      events.add(e);
    }

    if (vCardWhite == null) {
      // table.setWinner(Player.BLACK);
      table.isFinished = true;
      var e = Event.GAME_FINISH;
      e.winner = Player.BLACK;
      events.add(e);
    }

    var e = Event.POLL_CARDS;

    if (table.isWar)
      e.cardAmount = 4;
    else
      e.cardAmount = 0;

    e.blackCard = table.cardBlack;
    e.whiteCard = table.cardWhite;

    events.add(e);

  }

  // Perform actions according to current table
  public void playRound() {

    events.add(Event.ROUND_START);

    poll_cards();
 
    // Get current player's visible cards
    var vCardWhite = table.cardWhite;
    var vCardBlack = table.cardBlack;

    var compareEv = Event.COMPARE_CARDS;

    // Compare cards
    if (vCardWhite == vCardBlack) {
      // Starting the war
      table.isWar = true;

      events.add(compareEv);
      events.add(Event.WAR_START);
      // // There is no winner
      // table.setWinner(null);
    }
    // VCard.cardIdx does not just referse to registered card
    // But also represents it's priority
    // So we can use this index to determine which card is stronger
    // It is very fast operation, since we do that without looking up in registered
    // cards
    else if (vCardBlack.cardIdx > vCardWhite.cardIdx) {
      compareEv.winner = Player.BLACK;
      events.add(compareEv);
      table.isWar = false;
      events.add(Event.WAR_END);

      var collectEv = Event.COLLECT_CARDS;

      collectEv.winner = Player.BLACK;
      collectEv.blackCard = vCardBlack;
      collectEv.whiteCard = vCardWhite;
      collectEv.cardAmount = table.invisible.size();

      events.add(collectEv);

      // Push back cards to bottom of deck
      table.deckBlack.add(vCardBlack);
      table.deckBlack.add(vCardWhite);

      // Iterate over all invisible cards and add them as well
      // If there is no war, there should be no invisible cards
      // And the list is empty
      for (var vCard : table.invisible)
        table.deckBlack.add(vCard);

      // table.setWinner(Player.BLACK);

    }
    // TODO: DRY
    else if (vCardBlack.cardIdx < vCardWhite.cardIdx) {

      compareEv.winner = Player.WHITE;
      events.add(compareEv);

      table.isWar = false;
      events.add(Event.WAR_END);

      var collectEv = Event.COLLECT_CARDS;

      collectEv.winner = Player.WHITE;
      collectEv.blackCard = vCardBlack;
      collectEv.whiteCard = vCardWhite;
      collectEv.cardAmount = table.invisible.size();

      events.add(collectEv);
      // Push back cards to bottom of deck
      table.deckWhite.add(vCardBlack);
      table.deckWhite.add(vCardWhite);

      // Iterate over all invisible cards and add them as well
      // If there is no war, there should be no invisible cards
      // And the list is empty
      for (var vCard : table.invisible)
        table.deckBlack.add(vCard);
    }

    events.add(Event.ROUND_FINISH);
  }
}
