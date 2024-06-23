package game;

import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import cards.Card;

public class Game {

  public enum VCard {
    // Since actual card does not hold it's type we do it here
    CLUBS(0), DIAMONDS(1), HEARTS(2), SPADES(3);

    VCard(int i) {
    }

    // Index of card among registered cards
    public int cardIdx;

    // Create instance of this enum with indexing
    public static VCard fromId(int id) {
      return values()[id];
    }
  }

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
    public Queue<VCard> deckWhite;
    public Queue<VCard> deckBlack;
  }

  public class GameState {
    public boolean isWar = false;

    // If true, than winner is interpretated as winner of game
    public boolean isFinished = false;

    // Turn winner or game winner
    private Player winner;

    public Player getWinner() {
      return winner;
    }

    public void setWinner(Player winner) {
      this.winner = winner;
    }
  }

  public enum Player {
    WHITE(0), BLACK(1);

    Player(int i) {
      // Allows to create player with Player(int)
    }
  }

  public List<Card> registeredCards;

  Table table = new Table();
  GameState state = new GameState();

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
    // Iterate over all types of same card
    for (var id = 0; id < 4; id++) {
      // Iterate over all cards
      for (var idx = 0; idx < registeredCards.size(); idx++) {

        var vCard = VCard.fromId(id);
        vCard.cardIdx = idx;

        cards.add(vCard);
      }
    }

    Collections.shuffle(cards, new Random(seed));
  }

  public Table poll_cards() {

    if (table.cardBlack != null || table.cardWhite != null) {
      // Error
    }

    var vCardBlack = table.deckBlack.poll();
    var vCardWhite = table.deckWhite.poll();

    table.cardBlack = vCardBlack;
    table.cardWhite = vCardWhite;

    if (state.isWar) {
      for (var i = 0; i < 3; i++) {
        table.invisible.add(table.deckBlack.poll());
        table.invisible.add(table.deckWhite.poll());
      }
    }

    if (vCardBlack == null) {
      state.setWinner(Player.WHITE);
      state.isFinished = true;
      return table;
    }

    if (vCardWhite == null) {
      state.setWinner(Player.BLACK);
      state.isFinished = true;
      return table;
    }

    return null;
  }

  // Perform actions according to current table
  public GameState step() {

    // Get current player's visible cards
    var vCardWhite = table.cardWhite;
    var vCardBlack = table.cardBlack;

    // Compare cards
    if (vCardWhite == vCardBlack) {
      // Starting the war
      state.isWar = true;
      // There is no winner
      state.setWinner(null);
    } 
    // VCard.cardIdx does not just referse to registered card
    // But also represents it's priority
    // So we can use this index to determine which card is stronger 
    // It is very fast operation, since we do that without looking up in registered cards
    else if (vCardBlack.cardIdx > vCardWhite.cardIdx) {

      // Push back cards to bottom of deck
      table.deckBlack.add(vCardBlack);
      table.deckBlack.add(vCardWhite);

      // Iterate over all invisible cards and add them as well
      // If there is no war, there should be no invisible cards
      // And the list is empty
      for (var vCard : table.invisible)
        table.deckBlack.add(vCard);

      state.setWinner(Player.BLACK);

    } 
    // TODO: DRY
    else if (vCardBlack.cardIdx < vCardWhite.cardIdx) {

      // Push back cards to bottom of deck
      table.deckWhite.add(vCardBlack);
      table.deckWhite.add(vCardWhite);

      // Iterate over all invisible cards and add them as well
      // If there is no war, there should be no invisible cards
      // And the list is empty
      for (var vCard : table.invisible)
        table.deckBlack.add(vCard);

      state.setWinner(Player.BLACK);

    }

    return state;
  }
}
