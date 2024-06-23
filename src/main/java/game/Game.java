package game;

import java.util.List;
import java.util.PriorityQueue;

import cards.Card;

public class Game {

  public enum VCard {
    CLUBS, DIAMONDS, HEARTS, SPADES;

    public int cardIdx;
  }

  public class Table {
    List<VCard> invisible;

    VCard cardWhite;
    VCard cardBlack;

    public PriorityQueue<VCard> deckWhite;
    public PriorityQueue<VCard> deckBlack;
  }

  public class GameState {
    public boolean isWar = false;
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

  public GameState step() {

    var vCardWhite = table.cardWhite;
    var vCardBlack = table.cardBlack;

    if (table.cardWhite == table.cardBlack) {
      state.isWar = true;
      state.setWinner(null);
      // Start war
    } else if (vCardBlack.cardIdx > vCardWhite.cardIdx) {

      table.deckBlack.add(vCardBlack);
      table.deckBlack.add(vCardWhite);

      for (var vCard : table.invisible)
        table.deckBlack.add(vCard);

      state.setWinner(Player.BLACK);

    } else if (vCardBlack.cardIdx < vCardWhite.cardIdx) {

      table.deckWhite.add(vCardBlack);
      table.deckWhite.add(vCardWhite);

      for (var vCard : table.invisible)
        table.deckBlack.add(vCard);

      state.setWinner(Player.BLACK);
    }

    return state;
  }
}
