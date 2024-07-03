package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class FSTest {

  @Test
  public void serializeGameTest() {
    var game = new Game();
    game.dispatchDecks();
    try {
      game.save("./saves", "dummy");
    } catch (Exception e) {
      assertTrue(false, e.toString());
    }
  }

  @Test
  public void serializeDeserializeRecursiveTest() {
    try {
      var game = new Game();
      game.dispatchDecks();
      game.save("./saves/", "test.cardGame");

      var game2 = Game.load("./saves/test.cardGame");

      {
        var length = game.table.deckBlack.size();
        // I hate java so much
        for (var i = 0; i < length; i++) {
          var c1 = game.table.deckBlack.poll();
          var c2 = game2.table.deckBlack.poll();

          assertEquals(c1.cardIdx, c2.cardIdx);
          assertEquals(c1.suit, c2.suit);
        }
      }
      {
        var length = game.table.invisible.size();
        // I hate java so much
        for (var i = 0; i < length; i++) {
          var c1 = game.table.invisible.get(i);
          var c2 = game2.table.invisible.get(i);

          assertEquals(c1.cardIdx, c2.cardIdx);
          assertEquals(c1.suit, c2.suit);
        }
      }

    } catch (Exception e) {
      assertTrue(false, e.toString());
    }
  }
}
