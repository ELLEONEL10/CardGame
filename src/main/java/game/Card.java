package game;

public class Card {
    private String suit;
    private int value;

    public Card(String suit, int value) {
        this.suit = suit;
        this.value = value;
    }

    public String getSuit() {
        return suit;
    }

    public int getValue() {
        return value;
    }

    public String getImagePath() {
        return "assets/cards/c" + value + "/" + suit + ".png";
    }

    @Override
    public String toString() {
        return value + " of " + suit;
    }
}
