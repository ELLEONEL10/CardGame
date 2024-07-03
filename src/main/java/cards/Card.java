package cards;

import java.io.Serializable;

public abstract class Card implements Serializable{
  private static String PREFIX = "assets/cards/";

  public String getAssetPath(Suit suit) {
    switch (suit) {
      case CLUBS:
        return PREFIX + this.getClass().getSimpleName().toLowerCase() + "/clubs.png";
      case DIAMONDS:
        return PREFIX + this.getClass().getSimpleName().toLowerCase() + "/diamonds.png";
      case HEARTS:
        return PREFIX + this.getClass().getSimpleName().toLowerCase() + "/hearts.png";
      case SPADES:
        return PREFIX + this.getClass().getSimpleName().toLowerCase() + "/spades.png";
      default:
        return null;
    }
  }
}
