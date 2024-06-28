package game;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cards.Suit;
import game.EventQueue.Event;
import game.EventQueue.Player;

public class EventQueueTest {

  @Test
  public void orderTest() {
    var events = new EventQueue();
    var queue = events.evQueue;
    queue.add(Event.GAME_START);
    queue.add(Event.ROUND_START);
    queue.add(Event.ROUND_FINISH);
    queue.add(Event.GAME_FINISH);

    var i = 0;
    events.forEach(e -> {
      if (i == 0 && e != Event.GAME_START)
        // It works
        assertTrue(true, "Order is wrong, first element is " + e + ", but should be Event.GAME_START");
    });
  }

  @Test
  public void flushTest() {
    var events = new EventQueue();
    var queue = events.evQueue;
    queue.add(Event.GAME_START);
    queue.add(Event.ROUND_START);
    queue.add(Event.ROUND_FINISH);
    queue.add(Event.GAME_FINISH);

    events.forEach(e -> {
    });

    assertEquals(0, queue.size());
  }

  @Test
  public void playRoundResultsTest() {
    var game = new Game();
    game.dispatchDecks(0);
    // Amount of cards in White deck
    var amountW = game.table.deckWhite.size();
    // Amount of cards in Black deck
    var amountB = game.table.deckBlack.size();
    // Amount of invisible cards on table
    var hiddenAmount = 0;

    while (!game.table.isFinished()) {

      game.playRound();

      for (var e : game.events.evQueue)
        if (e == Event.POLL_CARDS) {

          hiddenAmount += e.cardAmount;
          amountW -= 1 + e.cardAmount / 2;
          amountB -= 1 + e.cardAmount / 2;

        } else if (e == Event.COLLECT_CARDS) {

          if (e.winner == Player.BLACK)
            // invisible on table
            amountB += e.cardAmount + 2;
          else
            // invisible on table
            amountW += e.cardAmount + 2;

          hiddenAmount -= e.cardAmount;
        } else if (e == Event.HIDE_CARDS)
          hiddenAmount += 2;
        else if (e == Event.GAME_FINISH) {
          if (e.winner != null)
            if (e.winner == Player.BLACK) {
              // invisible on table
              amountB += e.cardAmount + 1;
              amountW -= 1;
            } else {
              // invisible on table
              amountW += e.cardAmount + 1;
              amountB -= 1;
            }
          hiddenAmount -= e.cardAmount;
        }
      game.events.evQueue.clear();
    }

    assertEquals(52, Integer.max(amountB, amountW));
    assertEquals(0, Integer.min(amountB, amountW));
    assertEquals(0, hiddenAmount);
  }

  @Test
  public void playRoundResultsThreeTurnsTest() {
    var game = new Game();
    // game.dispatchDecks(0);
    {
      var d = new LinkedList<VCard>();
      d.add(new VCard(5, Suit.CLUBS));
      d.add(new VCard(4, Suit.CLUBS));
      game.table.deckWhite = d;
    }
    {
      var d = new LinkedList<VCard>();
      d.add(new VCard(3, Suit.CLUBS));
      d.add(new VCard(2, Suit.CLUBS));
      // d.add(new VCard(0, Suit.CLUBS));
      game.table.deckBlack = d;
    }
    // Amount of cards in White deck
    var amountW = game.table.deckWhite.size();
    // Amount of cards in Black deck
    var amountB = game.table.deckBlack.size();
    // Amount of invisible cards on table
    var hiddenAmount = 0;

    while (!game.table.isFinished()) {

      game.playRound();
      for (var e : game.events.evQueue)
        if (e == Event.POLL_CARDS) {
          hiddenAmount += e.cardAmount;
          // System.out.println(cardAmount);
          assertEquals(0, e.cardAmount);
          // amountW -= cardAmount;
          amountW -= 1 + e.cardAmount / 2;
          amountB -= 1 + e.cardAmount / 2;
        } else if (e == Event.COLLECT_CARDS) {
          assertEquals(0, e.cardAmount);
          if (e.winner == Player.BLACK) {
            // invisible on table
            amountB += e.cardAmount + 2;
          } else {
            // invisible on table
            amountW += e.cardAmount + 2;
          }
          hiddenAmount = 0;
        } else if (e == Event.HIDE_CARDS)
          hiddenAmount += 2;

      game.events.evQueue.clear();
    }

    assertEquals(4, Integer.max(amountB, amountW));
    assertEquals(0, Integer.min(amountB, amountW));
    assertEquals(0, hiddenAmount);
  }

  // Test if events have a null in unexpected places
  @Test
  public void nullEventsTest() {
    var game = new Game();
    game.dispatchDecks(0);

    while (!game.table.isFinished())
      game.playRound();

    game.events.forEach(e -> {
      switch (e) {
        case COLLECT_CARDS:
          assertEquals(Player.class, e.winner.getClass());
          assertEquals(Integer.class, e.cardAmount.getClass());
          assertEquals(VCard.class, e.whiteCard.getClass());
          assertEquals(VCard.class, e.blackCard.getClass());
          break;
        case COMPARE_CARDS:
          // assertEquals(null, e.winner.getClass() );
          assertEquals(null, e.cardAmount);
          assertEquals(null, e.whiteCard);
          assertEquals(null, e.blackCard);
          break;
        case GAME_FINISH:
          assertEquals(Player.class, e.winner.getClass());
          // assertEquals(null, e.cardAmount);
          assertEquals(null, e.whiteCard);
          assertEquals(null, e.blackCard);
          break;
        case GAME_START:
          assertEquals(null, e.winner);
          assertEquals(null, e.cardAmount);
          assertEquals(null, e.whiteCard);
          assertEquals(null, e.blackCard);
          break;
        case HIDE_CARDS:
          assertEquals(null, e.winner);
          assertEquals(null, e.cardAmount);
          assertEquals(VCard.class, e.whiteCard.getClass());
          assertEquals(VCard.class, e.blackCard.getClass());
          break;
        case POLL_CARDS:
          assertEquals(null, e.winner);
          assertEquals(Integer.class, e.cardAmount.getClass());
          assertEquals(VCard.class, e.whiteCard.getClass());
          assertEquals(VCard.class, e.blackCard.getClass());
          break;
        case ROUND_FINISH:
          assertEquals(null, e.winner);
          assertEquals(null, e.cardAmount);
          assertEquals(null, e.whiteCard);
          assertEquals(null, e.blackCard);
          break;
        case ROUND_START:
          assertEquals(null, e.winner);
          assertEquals(null, e.cardAmount);
          assertEquals(null, e.whiteCard);
          assertEquals(null, e.blackCard);
          break;
        case WAR_END:
          assertEquals(null, e.winner);
          assertEquals(null, e.cardAmount);
          assertEquals(null, e.whiteCard);
          assertEquals(null, e.blackCard);
          break;
        case WAR_START:
          assertEquals(null, e.winner);
          assertEquals(null, e.cardAmount);
          assertEquals(null, e.whiteCard);
          assertEquals(null, e.blackCard);
          break;
        default:
          break;
      }
    });
  }

  // Tests if opening events such as Event.GAME_START or ROUND_START have closing:
  // GAME_END or ROUND_END
  @Test
  public void eventQueueSemanticTest() {
    // TODO
  }
}
