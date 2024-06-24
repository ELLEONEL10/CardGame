package cards;

public enum Suit{
    // Since actual card does not hold it's type we do it here
    CLUBS(0), DIAMONDS(1), HEARTS(2), SPADES(3);

    // Index of card among registered cards
    public int id;

    Suit(int id) {
      this.id = id;
    }
    // Create instance of this enum with indexing
    public static Suit fromId(int id) {
      return values()[id];
    }
}
