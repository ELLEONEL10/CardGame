package game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import cards.VCard;
import game.EventQueue.Event;
import game.EventQueue.Player;

public class Game implements Serializable {
  // Representation of physical ingame table
  // Can be used for lookup any moment of the game
  public class Table implements Serializable {
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

    public int getInvisible(){
      return table.invisible.size();
    }

    /**
     * Get the amount of cards in player's deck
     * Returns -1 if {@link game.EventQueue.Player} specified wrong
     */
    public int getDeckSize(Player player) {
      if (player == null)
        return -1;
      switch (player) {
        case BLACK:
          return deckBlack.size();
        case WHITE:
          return deckWhite.size();
        default:
          return -1;
      }
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
    protected void hideCards(VCard whiteCard, VCard blackCard) {
      invisible.add(whiteCard);
      invisible.add(blackCard);
    }

  }

  // Registered cards in game
  protected List<Card> registeredCards;
  protected Table table = new Table();
  // We let it be public, because we dont read eventqueue inside game logic
  // Client can do anything with it.
  // NOTE: You can also use game.getEvents();
  public EventQueue events = new EventQueue();

  protected String whiteUsername = null;
  protected String blackUsername = "Roboter";

  // --- Constructors ---
  public Game() {
    registeredCards = Arrays.asList(new Default().cards);
  }

  public Game(Card[] cards) {
    registeredCards = new ArrayList<Card>(Arrays.asList(cards));
  }

  public Game(List<Card> cards) {
    registeredCards = cards;
  }

  // --- Save/Load game to/form fs ---
  /** Load game from fs */
  public static Game load(String path) throws Exception {
    // Reading the object from a file
    FileInputStream file = new FileInputStream(path);
    ObjectInputStream in = new ObjectInputStream(file);

    // Method for deserialization of object
    var game = (Game) in.readObject();

    in.close();
    file.close();

    return game;
  }

  /** Save game to fs */
  public void save(String path, String name) throws Exception {
    // Create dirs if does not exists
    Files.createDirectories(Paths.get(path));

    var file = new FileOutputStream(path + "/" + name);

    // Serialize object
    var out = new ObjectOutputStream(file);

    // Method for serialization of object
    out.writeObject(this);

    out.close();
    file.close();
  }

  // --- Getters and Setters ---
  /** Set username */
  public void setUsername(Player player, String username) {
    switch (player) {
      case WHITE:
        this.whiteUsername = username;
        break;
      default:
        this.blackUsername = username;
        break;
    }
  }

  public Queue<Event> getEvents(){
    return this.events.evQueue;   
  }

  /** Get table to look up the state of game */
  public Table getTable() {
    return this.table;
  }

  /** Get username */
  public String getUsername(Player player) {
    switch (player) {
      case WHITE:
        return this.whiteUsername;
      default:
        return this.blackUsername;
    }
  }

  /**
   * Get score of white player, basically means amount cards in white player's
   * deck
   */
  public int getScoreWhite() {
    return table.deckWhite.size();
  }

  /**
   * Get score of black player, basically means amount cards in black player's
   * deck
   */
  public int getScoreBlack() {
    return table.deckBlack.size();
  }

  /** Get path to png image of given {@link VCard} */
  public String getAssetPath(VCard vCard) {
    var card = registeredCards.get(vCard.cardIdx);
    return card.getAssetPath(vCard.suit);
  }

  // --- Dispatching decks ---
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

  /**
   * Checks if given cards are nulls and if there is atleast one is null, returns
   * true
   */
  private boolean isGameOver(VCard vCardWhite, VCard vCardBlack) {
    if (vCardBlack == null || vCardWhite == null) {

      Player winner = null;
      // Assign winner
      // Winner can be only one player or none
      // So if first card is not null, than second will be null
      winner = (vCardWhite != null) ? Player.WHITE : winner;
      winner = (vCardBlack != null) ? Player.BLACK : winner;

      // Finish the game
      table.finish();

      // Can be null
      var winnerDeck = table.getDeck(winner);

      events.add(Event.ROUND_FINISH);
      events.add(Event.create(Event.GAME_FINISH, winner, table.invisible.size(), null, null));

      // If there is no winner, than invisible deck is not moving anywhere
      // Since that is a draw, we cannot move all the cards to specific player
      // That results each player to have zero cards and all cards move to invisible
      // stack
      // Probability of Draw is almost zero and the only case scenario with draw i
      // found when players have identical stacks at the beginning
      // To get Identical decks you need to call [Game.dispatchDecksNoShuffle]
      if (winner != null) {

        for (var vCard : table.invisible)
          winnerDeck.add(vCard);
        // Cards from invisible deck are moved out
        table.invisible.clear();
        // Add one of incoming cards to winner's stack
        winnerDeck.add((winner == Player.WHITE) ? vCardWhite : vCardBlack);
        // Add visible cards on table to winner's stack
        winnerDeck.add(table.getCardBlack());

        winnerDeck.add(table.getCardWhite());

      }

      // Indicate that game is over
      return true;
    }
    // Indicate that game is not over yet
    return false;
  }

  // Perform actions according to current table
  public void playRound() {
    // Clear events on each round start
    events.evQueue.clear();
    // Remove visible cards from table, since they are being moved in winner's deck
    // in previous round
    // Or if war was declared, they were moved in invisible deck
    // NOTE: We keep it cached between rounds, to reflect last state on table
    table.cardBlack = null;
    table.cardWhite = null;

    if (table.isFinished)
      return;

    events.add(Event.ROUND_START);
    // Poll current player's visible cards
    var vCardWhite = table.pollCard(Player.WHITE);
    var vCardBlack = table.pollCard(Player.BLACK);

    if (isGameOver(vCardWhite, vCardBlack))
      return;

    table.placeCards(vCardWhite, vCardBlack);

    var pollEv = Event.POLL_CARDS;
    pollEv.blackCard = vCardBlack;
    pollEv.whiteCard = vCardWhite;
    pollEv.cardAmount = 0;

    if (table.isWar())
      // Pull 4 cards from player's stacks
      for (var i = 0; i < 2; i++) {
        var invVCardWhite = table.pollCard(Player.WHITE);
        var invVCardBlack = table.pollCard(Player.BLACK);

        if (isGameOver(invVCardWhite, invVCardBlack))
          return;

        table.invisible.add(invVCardBlack);
        table.invisible.add(invVCardWhite);
      }

    if (table.isWar())
      pollEv.cardAmount = 4;
    events.add(pollEv);

    var compareEv = Event.COMPARE_CARDS;

    // Compare cards
    if (vCardWhite.cardIdx == vCardBlack.cardIdx) {
      // Starting the war
      table.isWar = true;

      // Events
      compareEv.winner = null;
      events.add(compareEv);
      if (!table.isWar)
        // We dont want to declare if war is already ongoing
        events.add(Event.WAR_START);
      events.add(Event.create(Event.HIDE_CARDS, null, null, vCardWhite, vCardBlack));

      // Hide
      table.hideCards(vCardBlack, vCardWhite);

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
      var cardAmount = table.invisible.size();
      var collectEv = Event.create(Event.COLLECT_CARDS, winner, cardAmount, vCardWhite, vCardBlack);
      collectEv.cardAmount = cardAmount;
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
      table.invisible.clear();
    }

    events.add(Event.ROUND_FINISH);
    
  }
}
