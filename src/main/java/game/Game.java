package game;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import cards.Card;
import cards.Suit;

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

  public void register(List<Card> cards) {
    registeredCards = cards;
  }

  public void register(Card card) {
    registeredCards.add(card);
  }

  public void dispatchDecks() {
    dispatchDecks(new Random().nextInt());
  }

  public void dispatchDecks(int seed) {
    var cards = new Stack<VCard>();
    // Iterate over all types of same card.
    for (var suitId = 0; suitId < 4; suitId++)
      // Iterate over all cards.
      for (var cardPrecendence = 0; cardPrecendence < registeredCards.size(); cardPrecendence++)
        // Add VCard to temporary list.
        cards.add(new VCard(cardPrecendence, Suit.fromId(suitId)));

    Collections.shuffle(cards, new Random(seed));
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
    }

    if (vCardWhite == null) {
      // table.setWinner(Player.BLACK);
      table.isFinished = true;
    }
  }

  // Perform actions according to current table
  public void playRound() {

    // Get current player's visible cards
    var vCardWhite = table.cardWhite;
    var vCardBlack = table.cardBlack;

    // Compare cards
    if (vCardWhite == vCardBlack) {
      // Starting the war
      table.isWar = true;
      // // There is no winner
      // table.setWinner(null);
    }
    // VCard.cardIdx does not just referse to registered card
    // But also represents it's priority
    // So we can use this index to determine which card is stronger
    // It is very fast operation, since we do that without looking up in registered
    // cards
    else if (vCardBlack.cardIdx > vCardWhite.cardIdx) {

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
    else if (vCardBlack.cardIdx < vCardWhite.cardIdx ) {
      // Push back cards to bottom of deck
      table.deckWhite.add(vCardBlack);
      table.deckWhite.add(vCardWhite);

      // Iterate over all invisible cards and add them as well
      // If there is no war, there should be no invisible cards
      // And the list is empty
      for (var vCard : table.invisible)
        table.deckBlack.add(vCard);

      // state.setWinner(Player.BLACK);

    }
  }
}
