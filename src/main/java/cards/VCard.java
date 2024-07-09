package cards;

import java.io.Serializable;


public class VCard implements Serializable{
  // Index of card among registered cards
  public final int cardIdx;
  public final Suit suit;

  public VCard(int cardIdx, Suit suit) {
    this.cardIdx = cardIdx;
    this.suit = suit;
  }
}
