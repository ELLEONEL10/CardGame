package game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;

import org.junit.jupiter.api.Test;

import cards.Suit;
import cards.VCard;
import game.EventQueue.Event;
import game.EventQueue.Player;

public class SpecificTest {

  @Test
  public void drawTest() {
    var game = new Game();

    game.table.deckBlack = new LinkedList<VCard>();
    game.table.deckWhite = new LinkedList<VCard>();

    var turns = 0;
    while (!game.table.isFinished()) {
      game.playRound();
      turns += 1;
    }

    for (var e : game.events.evQueue) 
      if (e == Event.GAME_FINISH)
        assertEquals(null, e.winner);
    

    assertEquals(1, turns);
  }

  @Test
  public void blackWinnerSingleCardTest() {
    var game = new Game();

    var d = new LinkedList<VCard>();
    d.add(new VCard(0, Suit.CLUBS));

    game.table.deckBlack = d;
    game.table.deckWhite = new LinkedList<VCard>();

    var turns = 0;
    while (!game.table.isFinished()) {
      game.playRound();
      turns += 1;
    }

    for (var e : game.events.evQueue) 
      if (e == Event.GAME_FINISH)
        assertEquals(Player.BLACK, e.winner);
   
    assertEquals(1, turns);
  }

  @Test
  public void whiteWinnerSingleCardTest() {
    var game = new Game();

    var d = new LinkedList<VCard>();
    d.add(new VCard(0, Suit.CLUBS));

    game.table.deckWhite = d;
    game.table.deckBlack = new LinkedList<VCard>();

    var turns = 0;
    while (!game.table.isFinished()) {
      game.playRound();
      turns += 1;
    }

    for (var e : game.events.evQueue) 
      if (e == Event.GAME_FINISH)
        assertEquals(Player.WHITE, e.winner);
   
    assertEquals(1, turns);
  }
  @Test
  public void whiteWinsTwoTurnsTest() {
    var game = new Game();

    {
      var d = new LinkedList<VCard>();
      d.add(new VCard(3, Suit.CLUBS));
      d.add(new VCard(2, Suit.CLUBS));
      game.table.deckWhite = d;
    }
    {
      var d = new LinkedList<VCard>();
      d.add(new VCard(1, Suit.CLUBS));
      // d.add(new VCard(0, Suit.CLUBS));
      game.table.deckBlack = d;
    }

    var turns = 0;
    while (!game.table.isFinished()) {
      game.playRound();
      turns += 1;
    }

    for (var e : game.events.evQueue) 
      if (e == Event.GAME_FINISH)
        assertEquals(Player.WHITE, e.winner);
   
    assertEquals(2, turns);
  }
  @Test
  public void blackWinsThreeTurnsTest() {
    var game = new Game();

    {
      var d = new LinkedList<VCard>();
      d.add(new VCard(3, Suit.CLUBS));
      d.add(new VCard(2, Suit.CLUBS));
      game.table.deckBlack= d;
    }
    {
      var d = new LinkedList<VCard>();
      d.add(new VCard(1, Suit.CLUBS));
      d.add(new VCard(0, Suit.CLUBS));
      game.table.deckWhite= d;
    }

    var turns = 0;
    while (!game.table.isFinished()) {
      game.playRound();
      turns += 1;
    }

    for (var e : game.events.evQueue) 
      if (e == Event.GAME_FINISH)
        assertEquals(Player.BLACK, e.winner);
   
    assertEquals(3, turns);
  }
  @Test
  public void blackWinsWarTest() {
    var game = new Game();

    {
      var d = new LinkedList<VCard>();
      // First compared
      d.add(new VCard(5, Suit.CLUBS)); 
      // Decider
      d.add(new VCard(6, Suit.CLUBS));
      // Goes to invisible stack
      d.add(new VCard(4, Suit.CLUBS));
      d.add(new VCard(3, Suit.CLUBS));
      game.table.deckBlack = d;
    }
    {
      var d = new LinkedList<VCard>();
      // First compared
      d.add(new VCard(5, Suit.CLUBS)); 
      // Decider
      d.add(new VCard(2, Suit.CLUBS));
      // Goes to invisible stack
      d.add(new VCard(4, Suit.CLUBS));
      d.add(new VCard(3, Suit.CLUBS));
      game.table.deckWhite = d;
    }

    var turns = 0;
    while (!game.table.isFinished()) {
      game.playRound();
      turns += 1;
    }

    for (var e : game.events.evQueue) 
      if (e == Event.GAME_FINISH)
        assertEquals(Player.BLACK, e.winner);
   
    assertEquals(3, turns);
  }
  @Test
  public void whiteWinsWarTest() {
    var game = new Game();

    {
      var d = new LinkedList<VCard>();
      // First compared
      d.add(new VCard(5, Suit.CLUBS)); 
      // War Decider
      d.add(new VCard(6, Suit.CLUBS));
      // Goes to invisible stack
      d.add(new VCard(4, Suit.CLUBS));
      d.add(new VCard(3, Suit.CLUBS));
      // Post war
      d.add(new VCard(11, Suit.CLUBS));
      game.table.deckWhite= d;
    }
    {
      var d = new LinkedList<VCard>();
      // First compared
      d.add(new VCard(5, Suit.CLUBS)); 
      // War Decider
      d.add(new VCard(2, Suit.CLUBS));
      // Goes to invisible stack
      d.add(new VCard(4, Suit.CLUBS));
      d.add(new VCard(3, Suit.CLUBS));
      // Post war
      d.add(new VCard(1, Suit.CLUBS));
      game.table.deckBlack = d;
    }

    var turns = 0;
    while (!game.table.isFinished()) {
      game.playRound();
      turns += 1;
    }

    for (var e : game.events.evQueue) 
      if (e == Event.GAME_FINISH)
        assertEquals(Player.WHITE, e.winner);
   
    assertEquals(4, turns);
  }

  @Test
  public void infiniteGameTest() {
    /*
     * 
     * This is the simplest layout of decks with infinite loop
     * 
     * White's Deck:
     * 0, 1
     * 
     * Black's Deck:
     * 1, 0
     * 
     * First battle:
     * 0 vs 1
     * Black wins
     * 
     * White's Deck:
     * 1
     * 
     * Black's Deck:
     * 0, 0, 1
     * or
     * 0, 1, 0
     * 
     * Second battle:
     * 1 vs 0
     * White wins
     * 
     * White's Deck:
     * 1, 0
     * or
     * 0, 1
     * 
     * Black's Deck:
     * 0, 1
     * or
     * 1, 0
     * 
     * As you can see it is infinite loop.
     * Thats why we need cycle detector
     */

    var game = new Game();
    {
      var d = new LinkedList<VCard>();
      d.add(new VCard(0, Suit.CLUBS));
      d.add(new VCard(1, Suit.CLUBS));
      game.table.deckBlack = d;
    }
    {
      var d = new LinkedList<VCard>();
      d.add(new VCard(1, Suit.CLUBS));
      d.add(new VCard(0, Suit.CLUBS));
      game.table.deckWhite = d;
    }

    var turns = 0;

    while (turns < 1000) {
      game.playRound();
      turns += 1;
    }

    assertEquals(false, game.table.isFinished());
  }
}
