package game;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * Game Events. Indicates very detailed what is happening during the game. <br>
 * 
 * <pre>
 * One of the possible scenarios of game flow:
 *     ---
 *     {@link Game#playRound()}
 *     ---
 *     GAME_START
 *     ROUND_START
 *     POLL_CARDS
 *     COMPARE_CARDS   
 *     COLLECT_CARDS
 *     ROUND_END 
 *
 * Or more complex with war declaration:
 *     ---
 *     {@link Game#playRound()}
 *     ---
 *     GAME_START
 *     ROUND_START
 *     POLL_CARDS
 *     COMPARE_CARDS   
 *     WAR_START  
 *     HIDE_CARDS
 *     ROUND_END 
 *     ---
 *     {@link Game#playRound()}
 *     ---
 *     ROUND_START
 *     POLL_CARDS
 *     COMPARE_CARDS   
 *     COLLECT_CARDS   
 *     WAR_END 
 *     ROUND_END 
 *     GAME_END
 * </pre>
 * 
 */
class EventQueue {
  public enum Player {
    WHITE(0), BLACK(1);
    int idx;
    Player(int i) {
     idx = i;
    }

    public static Player fromIdx(int i){
     return values()[i];
    }
  }

  public enum Event {

    /**
     * Indicates if game was started.
     * <hr>
     * {@link Event#winner} : null <br>
     * {@link Event#cardAmount} : null <br>
     * {@link Event#whiteCard} : null <br>
     * {@link Event#blackCard} : null
     */
    GAME_START,

    /**
     * Indicates if game was finished.
     * <hr>
     * {@link Event#winner} : Player | null - winner of the game<br>
     * {@link Event#cardAmount } : Integer - Amount of hidden cards going in winners deck
     * {@link Event#whiteCard} : null <br>
     * {@link Event#blackCard} : null
     */
    GAME_FINISH,

    /**
     * Indicates if round was started. <br>
     * NOTE: If there is no visible cards on table, than round is not ongoing.
     * <hr>
     * {@link Event#winner} : null <br>
     * {@link Event#cardAmount} : null <br>
     * {@link Event#whiteCard} : null <br>
     * {@link Event#blackCard} : null
     */
    ROUND_START,

    /**
     * Indicates if round was finished. <br>
     * NOTE: If there is no visible cards on table, than round is not ongoing.
     * <hr>
     * {@link Event#winner} : null <br>
     * {@link Event#cardAmount} : null <br>
     * {@link Event#whiteCard} : null <br>
     * {@link Event#blackCard} : null
     */
    ROUND_FINISH,

    /**
     * Indicates if war was started. <br>
     * <hr>
     * {@link Event#winner} : null <br>
     * {@link Event#cardAmount} : null <br>
     * {@link Event#whiteCard} : null <br>
     * {@link Event#blackCard} : null
     */
    WAR_START,

    // TODO: Add event WAR_CONTINUES

    /**
     * Indicates if war was finished. <br>
     * NOTE: This event holds no winner even tho there is.
     * To get winner see {@link Event#COMPARE_CARDS}
     * <hr>
     * {@link Event#winner} : null <br>
     * {@link Event#cardAmount } : null <br>
     * {@link Event#whiteCard} : null <br>
     * {@link Event#blackCard} : null
     */
    WAR_END,

    /**
     * Poll cards on start of every round. <br>
     * <hr>
     * {@link Event#winner} : null <br>
     * {@link Event#cardAmount } : Integer - Poll 2 (4 in general) cards from every
     * player's deck to hidden deck if war is declared <br>
     * {@link Event#whiteCard} : VCard - Polled card from white deck <br>
     * {@link Event#blackCard} : VCard - Polled card from black deck
     */
    POLL_CARDS,

    /**
     * Turn upside down cards and move to hidden stack if war was declared.
     * <hr>
     * {@link Event#winner} : null
     * <br>
     * {@link Event#cardAmount } : null
     * <br>
     * {@link Event#whiteCard} : VCard - Card from white side being hidden. <br>
     * {@link Event#blackCard} : VCard - Card from black side being hidden.
     */
    HIDE_CARDS,

    /**
     * Collect all cards on table if player wins the round
     * <hr>
     * {@link Event#winner} : Player - player who collects all cards on the table.
     * <br>
     * {@link Event#cardAmount } : Integer - amount of hidden cards player collects.
     * <br>
     * {@link Event#whiteCard} : VCard - Visible card winner is collecting. <br>
     * {@link Event#blackCard} : VCard - Visible card winner is collecting.
     */
    COLLECT_CARDS,

    /**
     * Compare cards and determine winner of the round. <br>
     * <hr>
     * {@link Event#winner} : Player | null - winner of the round. If there is no
     * winner, than its a draw and value is null <br>
     * {@link Event#cardAmount} : null <br>
     * {@link Event#whiteCard} : null <br>
     * {@link Event#blackCard} : null
     */
    COMPARE_CARDS;

    Player winner = null;

    /** Amount of invisible (face down) cards */
    Integer cardAmount = null;
    VCard whiteCard = null;
    VCard blackCard = null;

    /** Helper function to create event fast */
    public static Event create(Event e, Player winner, Integer cardAmount, VCard whiteCard, VCard blackCard){
     e.winner = winner;
     e.cardAmount = cardAmount;
     e.whiteCard = whiteCard;
     e.blackCard = blackCard;
     return e;
    }

  }

  protected Queue<Event> evQueue = new LinkedList<Event>();

  /**
   * Iterate over all evens in queue <br>
   * NOTE: Once readed, queue is being flushed out. <br>
   * <br>
   * {@code events.forEach((e) -> println(e)) }
   */
  public void forEach(Consumer<? super Event> action) {
    evQueue.forEach(action);
    evQueue.clear();
  }

  protected void add(Event e){
   evQueue.add(e);
  }
}
