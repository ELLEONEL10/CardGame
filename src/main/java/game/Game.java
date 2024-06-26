package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import cards.Card;
import cards.Default;
import cards.Suit;
import game.EventQueue.Event;
import game.EventQueue.Player;

public class Game {
  // Representation of physical ingame table
  // Can be used for lookup any moment of the game
  public class Table {
    // Cards of all players during the war
    List<VCard> invisible = new Stack<VCard>();

    // Current player's card on table
    private VCard cardWhite;

    private VCard cardBlack;

    // Player's decks
    // We use queue, since it is first input -> first output
    // And we can put new cards in bottom of deck
    protected Queue<VCard> deckWhite;
    protected Queue<VCard> deckBlack;
    // Is war ongoing
    private boolean isWar = false;
    // Is game over
    private boolean isFinished = false;

    public VCard getCardWhite() {
      return cardWhite;
    }

    public VCard getCardBlack() {
      return cardBlack;
    }

    private Queue<VCard> getDeck(Player player) {
      if (player == null)
        return null;

      switch (player) {
        case BLACK:
          return deckBlack;
        case WHITE:
          return deckWhite;
        default:
          return null;
      }
    }

    public boolean isWar() {
      return isWar;
    }

    public boolean isFinished() {
      return isFinished;
    }

    private void finish() {
      isFinished = true;
    }

    /** Place given card on visible spots on table */
    protected void placeCards(VCard whiteCard, VCard blackCard) {
      cardBlack = blackCard;
      cardWhite = whiteCard;
    }

    /** Move cards to hidden deck */
    protected void hideCards(VCard whiteCard, VCard blackCard){
      invisible.add(whiteCard);
      invisible.add(blackCard);
    }

    protected VCard pollCard(Player player) {
      switch (player) {
        case BLACK:
          return deckBlack.poll();
        case WHITE:
          return deckWhite.poll();
        default:
          return null;
      }
    }
  }

  public List<Card> registeredCards;

  Table table = new Table();
  EventQueue events = new EventQueue();

  public Game() {
    registeredCards = Arrays.asList(new Default().cards);
  }

  public Game(Card[] cards) {
    registeredCards = new ArrayList<Card>(Arrays.asList(cards));
  }

  public Game(List<Card> cards) {
    registeredCards = cards;
  }

  public void dispatchDecks() {
    dispatchDecks(new Random().nextInt(), false);
  }

  public void dispatchDecksNoShuffle() {
    dispatchDecks(0, true);
  }

  public void dispatchDecks(int seed) {
    dispatchDecks(seed, false);
  }

  private void dispatchDecks(int seed, boolean noShuffle) {

    var cards = new Stack<VCard>();
    // Iterate over all types of same card.
    for (var suitId = 0; suitId < 4; suitId++)
      // Iterate over all cards.
      for (var cardPrecendence = 0; cardPrecendence < registeredCards.size(); cardPrecendence++)
        // Add VCard to temporary list.
        cards.add(new VCard(cardPrecendence, Suit.fromId(suitId)));

    if (!noShuffle)
      Collections.shuffle(cards, new Random(seed));

    table.deckBlack = new LinkedList<VCard>(cards.subList(0, (cards.size() / 2)));
    table.deckWhite = new LinkedList<VCard>(cards.subList((cards.size() / 2), cards.size()));
  }

  // Finish game if one player has no cards left
  /**
   * Checks if given cards are nulls and if there is atleast one is null, returns
   * true
   */
  private boolean isGameOver(VCard vCardWhite, VCard vCardBlack) {
    if (vCardBlack == null || vCardWhite == null) {

      Player winner;
      // Assign winner
      // Winner can be only one player or none
      // So if first card is not null, than second will be null
      winner = (vCardWhite != null) ? Player.WHITE : null;
      winner = (vCardBlack != null) ? Player.BLACK : null;

      // Finish the game
      table.finish();

      // Can be null
      var winnerDeck = table.getDeck(winner);

      // If there is no winner, than invisible deck is not moving anywhere
      // Since that is a draw, we cannot move all the cards to specific player
      // That results each player to have zero cards and all cards move to invisible stack
      // Probability of Draw is almost zero and the only case scenario with draw i found when players have identical stacks at the beginning
      // To get Identical decks you need to call [Game.dispatchDecksNoShuffle]
      if (winner != null) {
        for (var vCard : table.invisible)
          winnerDeck.add(vCard);
        // Cards from invisible deck are moved out
        table.invisible.clear();
      }

      events.add(Event.ROUND_FINISH);
      events.add(Event.create(Event.GAME_FINISH, winner, null, null, null));
      return true;
    }
    return false;
  private void finishGame(){
    
  }

  private void pollCards() {

    // if (table.cardBlack != null || table.cardWhite != null)
    // System.err.println("Not empty table on fresh turn");

    // Poll visible cards
    var vCardWhite = table.pollCard(Player.WHITE);
    var vCardBlack = table.pollCard(Player.BLACK);
    if (vCardBlack == null && vCardWhite == null) {

      table.isFinished = true;
      var e = Event.GAME_FINISH;
      e.winner = null;
      events.add(e);
      System.out.println("polling cards and draw");
      return;
    }
    // Detect if someone ran out of cards
    else if (vCardBlack == null) {
      table.isFinished = true;
      var e = Event.GAME_FINISH;
      e.winner = Player.WHITE;
      events.add(e);
      for (var vCard : table.invisible)
        table.deckWhite.add(vCard);

      table.deckWhite.add(vCardWhite);
      System.out.println("polling cards and White wins");
      return;
    }

    else if (vCardWhite == null) {
      table.isFinished = true;
      var e = Event.GAME_FINISH;
      e.winner = Player.BLACK;
      events.add(e);
      for (var vCard : table.invisible)
        table.deckBlack.add(vCard);

      table.deckBlack.add(vCardBlack);
      System.out.println("polling cards and Black wins");
      return;
    }

    // Place on table
    table.placeCards(vCardWhite, vCardBlack);

    // Pull 4 cards if it is a war
    if (table.isWar)
      for (var _i = 0; _i < 2; _i++) {
        var invCardW = table.pollCard(Player.WHITE);
        var invCardB = table.pollCard(Player.BLACK);
        if (invCardW == null && invCardB == null) {
          // Draw
          table.isFinished = true;
          var e = Event.GAME_FINISH;
          e.winner = null;
          events.add(e);
          System.out.println("War polling cards and draw");
          return;

        } else if (invCardW == null) {
          table.isFinished = true;
          var e = Event.GAME_FINISH;
          e.winner = Player.BLACK;
          events.add(e);
          for (var vCard : table.invisible)
            table.deckBlack.add(vCard);

          table.deckBlack.add(invCardB);
          table.deckBlack.add(table.cardBlack);
          table.deckBlack.add(table.cardWhite);
          // Clear if not invisible
          table.invisible.clear();
          System.out.println("War polling cards and Black wins");
          return;
        } else if (invCardB == null) {
          table.isFinished = true;
          var e = Event.GAME_FINISH;
          e.winner = Player.WHITE;
          events.add(e);
          for (var vCard : table.invisible)
            table.deckWhite.add(vCard);

          table.deckWhite.add(invCardW);
          table.deckWhite.add(table.cardBlack);
          table.deckWhite.add(table.cardWhite);

          System.out.println("War polling cards and White wins");
          table.invisible.clear();
          return;
        }

        table.invisible.add(invCardB);
        table.invisible.add(invCardW);
      }

    {
      // Log to event
      events
          .add(Event.create(Event.POLL_CARDS, null, (table.isWar) ? 4 : 0, table.getCardWhite(), table.getCardBlack()));

    }
  }

  // Perform actions according to current table
  public void playRound() {
    if (table.isFinished)
      return;

    events.add(Event.ROUND_START);

    pollCards();

    if (table.isFinished)
      return;

    // Get current player's visible cards
    var vCardWhite = table.getCardWhite();
    var vCardBlack = table.getCardBlack();

    var compareEv = Event.COMPARE_CARDS;

    // Compare cards
    if (vCardWhite.cardIdx == vCardBlack.cardIdx) {
      // Starting the war
      table.isWar = true;

      // Events
      // TODO: Debug
      // For some reason java defaulting to something else instead of null
      compareEv.winner = null;
      events.add(compareEv);

      if (!table.isWar)
        // We dont want to declare if war is already ongoing
        // TODO: Add event WAR_CONTINUES
        events.add(Event.WAR_START);

      // Hide
      table.invisible.add(vCardWhite);
      table.invisible.add(vCardBlack);

      events.add(Event.create(Event.HIDE_CARDS, null, null, vCardWhite, vCardBlack));
    } else {
      // VCard.cardIdx does not just referse to registered card
      // But also represents it's priority
      // So we can use this index to determine which card is stronger
      // It is very fast operation, since we do that without looking up in registered
      // cards
      var whiteWon = (vCardBlack.cardIdx < vCardWhite.cardIdx) ? true : false;
      var winner = (whiteWon) ? Player.WHITE : Player.BLACK;
      var winnerDeck = (whiteWon) ? table.deckWhite : table.deckBlack;

      // Events
      var collectEv = Event.create(Event.COLLECT_CARDS, winner, table.invisible.size(), vCardWhite, vCardBlack);

      compareEv.winner = winner;

      events.add(compareEv);
      if (table.isWar)
        events.add(Event.WAR_END);
      events.add(collectEv);

      table.isWar = false;
      // Push back cards to bottom of deck
      winnerDeck.add(vCardBlack);
      winnerDeck.add(vCardWhite);

      // Iterate over all invisible cards and add them as well
      // If there is no war, there should be no invisible cards
      // And the list is empty
      for (var vCard : table.invisible)
        winnerDeck.add(vCard);

      // Clear if not invisible
      table.invisible.clear();
    }

    // Remove visible cards from table, since they are being moved in winner's deck
    // Or if war was declared, they were moved in invisible deck
    table.cardBlack = null;
    table.cardWhite = null;

    events.add(Event.ROUND_FINISH);
  }
}
