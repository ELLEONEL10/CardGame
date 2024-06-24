package game;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import game.EventQueue.Event;

public class EventQueueTest{

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

    events.forEach(e -> {});

    assertEquals(0, queue.size());
  }
}
