package game;

import cards.Suit;

public class VCard {
  // Index of card among registered cards
  public final int cardIdx;
  public final Suit suit;

  public VCard(int cardIdx, Suit suit) {
    this.cardIdx = cardIdx;
    this.suit = suit;
  }
}
