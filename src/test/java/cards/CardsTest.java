package cards;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CardsTest{

  @Test
  public void shouldAnswerWithTrue() {
    assertTrue(true);
  }

  @Test
  public void DefaultCardsOrderTest() {
    var defaultCards = new Default();
    assertEquals("C2", defaultCards.cards[0].getClass().getSimpleName() );
    assertEquals("C3", defaultCards.cards[1].getClass().getSimpleName() );
    assertEquals("C4", defaultCards.cards[2].getClass().getSimpleName() );
    assertEquals("Queen", defaultCards.cards[10].getClass().getSimpleName() );
    assertEquals("King", defaultCards.cards[11].getClass().getSimpleName() );
    assertEquals("Ace", defaultCards.cards[12].getClass().getSimpleName() );
  }

  @Test
  public void DefaultCardsAmountTest() {
    var defaultCards = new Default();
    assertEquals(13, defaultCards.cards.length);
  }

  @Test
  public void DefaultCardsPathsTest() {
    var king = new Default().cards[11];
    assertEquals("assets/cards/king/diamonds.png", king.getAssetPath(Suit.DIAMONDS) );
    assertEquals("assets/cards/king/clubs.png", king.getAssetPath(Suit.CLUBS) );
    assertEquals("assets/cards/king/hearts.png", king.getAssetPath(Suit.HEARTS) );
    assertEquals("assets/cards/king/spades.png", king.getAssetPath(Suit.SPADES) );

    var c4 = new Default().cards[2];
    assertEquals("assets/cards/c4/diamonds.png", c4.getAssetPath(Suit.DIAMONDS) );
    assertEquals("assets/cards/c4/clubs.png", c4.getAssetPath(Suit.CLUBS) );
    assertEquals("assets/cards/c4/hearts.png", c4.getAssetPath(Suit.HEARTS) );
    assertEquals("assets/cards/c4/spades.png", c4.getAssetPath(Suit.SPADES) );

  }
}
