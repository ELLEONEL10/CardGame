package game;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> deck;

    public Player(String name) {
        this.name = name;
        this.deck = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public void addCard(Card card) {
        deck.add(card);
    }

    public Card playCard() {
        if (deck.size() > 0) {
            return deck.remove(0);
        }
        return null;
    }
}
