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
    private VCard cardWhite;


    private VCard cardBlack;

    // Player's decks
    // We use queue, since it is first input -> first output
    // And we can put new cards in bottom of deck
    private Queue<VCard> deckWhite;
    private Queue<VCard> deckBlack;
    // Is war ongoing
    private boolean isWar = false;
    // Is game over
    private  boolean isFinished = false;

    public VCard getCardWhite() {
      return cardWhite;
    }

    public VCard getCardBlack() {
      return cardBlack;
    }

    public boolean isWar() {
      return isWar;
    }

    public boolean isFinished() {
      return isFinished;
    }

    protected void placeCards(VCard whiteCard, VCard blackCard) {
      cardBlack = blackCard;
      cardWhite = whiteCard;
    }

    protected VCard pollCard(Player player) {
      switch (player) {
        case BLACK:
          return deckBlack.poll();
        case WHITE:
          return deckWhite.poll();
        default:
          return null;
      }
    }
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

    // Poll visible cards
    var vCardWhite = table.pollCard(Player.WHITE);
    var vCardBlack = table.pollCard(Player.BLACK);

    // Place on table
    table.placeCards(vCardWhite, vCardBlack);

    // Pull 4 cards if it is a war
    if (table.isWar)
      // Player index
      for (var p = 0; p < 2; p++)
        for (var _i = 0; _i < 2; _i++)
          table.invisible.add(table.pollCard(Player.fromIdx(p)));

    // Detect if someone ran out of cards
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

    // Log to event
    var e = Event.POLL_CARDS;

    if (table.isWar)
      e.cardAmount = 4;
    else
      e.cardAmount = 0;

    e.blackCard = table.getCardBlack();
    e.whiteCard = table.getCardWhite();

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

      // Events
      events.add(compareEv);
      events.add(Event.WAR_START);
    } else {
      // VCard.cardIdx does not just referse to registered card
      // But also represents it's priority
      // So we can use this index to determine which card is stronger
      // It is very fast operation, since we do that without looking up in registered
      // cards
      var whiteWon = (vCardBlack.cardIdx < vCardWhite.cardIdx) ? true : false;
      var winner = (whiteWon) ? Player.WHITE : Player.BLACK;
      var winnerDeck = (whiteWon) ? table.deckWhite : table.deckBlack;

      // Events
      var collectEv = Event.COLLECT_CARDS;

      collectEv.winner = winner;
      collectEv.blackCard = vCardBlack;
      collectEv.whiteCard = vCardWhite;
      collectEv.cardAmount = table.invisible.size();

      compareEv.winner = winner;

      events.add(compareEv);
      events.add(Event.WAR_END);
      events.add(collectEv);

      table.isWar = false;
      // Push back cards to bottom of deck
      winnerDeck.add(vCardBlack);
      winnerDeck.add(vCardWhite);

      // Iterate over all invisible cards and add them as well
      // If there is no war, there should be no invisible cards
      // And the list is empty
      for (var vCard : table.invisible)
        winnerDeck.add(vCard);
    }

    events.add(Event.ROUND_FINISH);
  }
}
