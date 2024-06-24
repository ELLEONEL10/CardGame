package cards;

public abstract class Card {
  private static String ASSETS_PATH = "assets/";

  public String getAssetPath(Suit suit) {
    switch (suit) {
      case CLUBS:
        return ASSETS_PATH + this.getClass().getSimpleName() + "/clubs.png";
      case DIAMONDS:
        return ASSETS_PATH + this.getClass().getSimpleName() + "/diamonds.png";
      case HEARTS:
        return ASSETS_PATH + this.getClass().getSimpleName() + "/hearts.png";
      case SPADES:
        return ASSETS_PATH + this.getClass().getSimpleName() + "/spades.png";
      default:
        return null;
    }
  }
}
