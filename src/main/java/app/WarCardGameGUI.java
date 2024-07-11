package app;

import game.Game;
import game.EventQueue.Event;
import game.EventQueue.Player;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;

public class WarCardGameGUI extends JFrame {

  // UI Components
  private JLabel player1CardLabel;
  private JLabel player2CardLabel;
  private JLabel player1ScoreLabel;
  private JLabel player2ScoreLabel;
  private JLabel resultLabel;
  private JLabel player2IconLabel;
  private JLabel player1TieCardsLabel;
  private JLabel player2TieCardsLabel;
  private JLabel player1TieCardsTextLabel;
  private JLabel player2TieCardsTextLabel;
  private JButton playButton;
  private ImageIcon cardBackIcon;
  private ImageIcon winIcon;
  private ImageIcon loseIcon;
  private ImageIcon tieIcon;
  private Game game = null; // Instance of the game logic
  Image backgroundImg; // Background image
  private JTextField player1NameField;
  private JTextField player2NameField;

  // Constructor for the GUI
  public WarCardGameGUI() {
    // Setting up the main frame
    setTitle("War Card Game");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    setResizable(true); // Disallow resizing

    // Load icons with better image quality and set resizing hints
    cardBackIcon = new ImageIcon("assets/images/cardbg.png");
    cardBackIcon.setImage(cardBackIcon.getImage().getScaledInstance(250, 320, Image.SCALE_SMOOTH)); // Resize card image
    winIcon = new ImageIcon("assets/images/win.png");
    loseIcon = new ImageIcon("assets/images/lose.png");
    tieIcon = new ImageIcon("assets/images/tie.png");

    // Calculate height based on 16:9 aspect ratio
    int width = 1080;
    int height = width * 13 / 20;

    // Main panel with background image
    JPanel mainPanel = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Switch background based on game state (war or normal)
        if (game.getTable().isWar()) {
          backgroundImg = new ImageIcon("assets/images/backgroundw.png").getImage();
        } else {
          backgroundImg = new ImageIcon("assets/images/background.png").getImage();
        }
        g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
      }
    };

    // Top panel with card displays and result
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setOpaque(false);

    // Panel to hold the player cards and result label
    JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 80, 0));
    cardsPanel.setOpaque(false);
    cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // Adjust margin

    // Player 1's card label
    player1CardLabel = new JLabel(cardBackIcon);
    player1CardLabel.setHorizontalAlignment(JLabel.CENTER);

    // Result label to show the outcome of each round
    resultLabel = new JLabel("", JLabel.CENTER);

    // Player 2's card label
    player2CardLabel = new JLabel(cardBackIcon);
    player2CardLabel.setHorizontalAlignment(JLabel.CENTER);

    // Labels for additional cards during a tie
    player1TieCardsLabel = new JLabel(cardBackIcon);
    player1TieCardsLabel.setHorizontalAlignment(JLabel.CENTER);
    player1TieCardsLabel.setVisible(false); // Initially hidden

    player2TieCardsLabel = new JLabel(cardBackIcon);
    player2TieCardsLabel.setHorizontalAlignment(JLabel.CENTER);
    player2TieCardsLabel.setVisible(false); // Initially hidden

    // Text labels for "Cards" during a tie
    player1TieCardsTextLabel = new JLabel("Cards: 2", JLabel.CENTER);
    player1TieCardsTextLabel.setFont(new Font("Arial", Font.BOLD, 24));
    player1TieCardsTextLabel.setForeground(Color.YELLOW);
    player1TieCardsTextLabel.setVisible(false); // Initially hidden

    player2TieCardsTextLabel = new JLabel("Cards: 2", JLabel.CENTER);
    player2TieCardsTextLabel.setFont(new Font("Arial", Font.BOLD, 24));
    player2TieCardsTextLabel.setForeground(Color.YELLOW);
    player2TieCardsTextLabel.setVisible(false); // Initially hidden

    // Panel for Player 1's card and tie information
    JPanel player1Panel = new JPanel(new BorderLayout());
    player1Panel.setOpaque(false);
    player1Panel.add(player1CardLabel, BorderLayout.CENTER);
    player1Panel.add(player1TieCardsLabel, BorderLayout.WEST);
    player1Panel.add(player1TieCardsTextLabel, BorderLayout.SOUTH);

    // Panel for Player 2's card and tie information
    JPanel player2Panel = new JPanel(new BorderLayout());
    player2Panel.setOpaque(false);
    player2Panel.add(player2CardLabel, BorderLayout.CENTER);
    player2Panel.add(player2TieCardsLabel, BorderLayout.EAST);
    player2Panel.add(player2TieCardsTextLabel, BorderLayout.SOUTH);

    // Adding player panels and result label to cardsPanel
    cardsPanel.add(player1Panel);
    cardsPanel.add(resultLabel);
    cardsPanel.add(player2Panel);

    // Adding cardsPanel to topPanel
    topPanel.add(cardsPanel, BorderLayout.CENTER);

    // Adding topPanel to mainPanel
    mainPanel.add(topPanel, BorderLayout.CENTER);

    // Bottom panel with scores and play button
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setOpaque(false);

    // Panel to hold the score labels
    JPanel scorePanel = new JPanel(new GridLayout(1, 2));
    scorePanel.setOpaque(false);

    // Score label for Player 1
    player1ScoreLabel = new JLabel("Cards left: 26", JLabel.CENTER);
    player1ScoreLabel.setFont(new Font("Arial", Font.PLAIN, 22));
    player1ScoreLabel.setForeground(Color.YELLOW);

    // Score label for Player 2
    player2ScoreLabel = new JLabel("Cards left: 26", JLabel.CENTER);
    player2ScoreLabel.setFont(new Font("Arial", Font.PLAIN, 22));
    player2ScoreLabel.setForeground(Color.YELLOW);

    // Adding score labels to scorePanel
    scorePanel.add(player1ScoreLabel);
    scorePanel.add(player2ScoreLabel);

    // Play button to start the round
    playButton = new JButton("Play");
    playButton.setFont(new Font("Arial", Font.BOLD, 24));
    playButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        playSound("assets/sounds/play.wav");
        playRound();
      }
    });

    // Initialize the game
    initGame();

    // Adding scorePanel and playButton to bottomPanel
    bottomPanel.add(scorePanel, BorderLayout.CENTER);
    bottomPanel.add(playButton, BorderLayout.SOUTH);

    // Adding bottomPanel to mainPanel
    mainPanel.add(bottomPanel, BorderLayout.SOUTH);

    // User and computer panels with padding and titles
    JPanel whitePanel = createUserPanelWithTitle("assets/images/user.png", player1CardLabel, "Player 1");
    whitePanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding

    JPanel blackPanel = createUserPanelWithTitle("assets/images/ai.png", player2CardLabel, "Computer");
    blackPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding
    player2IconLabel = (JLabel) blackPanel.getComponent(1);
    toggleBlackIcon(false);
    // Adding user panels to mainPanel
    mainPanel.add(whitePanel, BorderLayout.WEST);
    mainPanel.add(blackPanel, BorderLayout.EAST);

    // Create the menu bar
    createMenuBar();

    // Play the start sound
    playSound("assets/sounds/start.wav");

    // Adding mainPanel to the frame
    add(mainPanel, BorderLayout.CENTER);
    setSize(width, height); // Set size based on aspect ratio
    setLocationRelativeTo(null); // Center the window
    setVisible(true); // Make the window visible
  }

  // Method to create user panel with title
  private JPanel createUserPanelWithTitle(String imagePath, JLabel cardLabel, String title) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);
  
    JTextField titleField = new JTextField(title, JLabel.CENTER);
    titleField.setFont(new Font("Arial", Font.BOLD, 28));
    titleField.setForeground(Color.YELLOW);
    titleField.setHorizontalAlignment(JTextField.CENTER);
    titleField.setOpaque(false); // Make the text field transparent
    titleField.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add margin to the text field
  
    // Assigning to appropriate field based on title
    if (title.equals("Player 1")) {
      player1NameField = titleField;
    } else {
      player2NameField = titleField;
    }
  
    JLabel imageLabel = new JLabel(new ImageIcon(imagePath));
    imageLabel.setHorizontalAlignment(JLabel.CENTER);
  
    // Adding components to the panel
    panel.add(titleField, BorderLayout.NORTH);
    panel.add(imageLabel, BorderLayout.CENTER);
    panel.add(cardLabel, BorderLayout.SOUTH);
  
    return panel;
  }
  

  private void toggleBlackIcon(boolean isRobot) {
    if (isRobot)
      player2IconLabel.setIcon(new ImageIcon("assets/images/ai.png"));
    else
    // Computer is actually player 2 (black)
      player2IconLabel.setIcon(new ImageIcon("assets/images/computer.png"));
  }
  // Method to update the UI based on game state
  private void Update() {
    resultLabel.setText(null); // Clear previous result text
    System.err.println(game.getUsername(Player.WHITE)); // Debugging output
    player1NameField.setText((game.getUsername(Player.WHITE).equals("")) ? "Player 1" : game.getUsername(Player.WHITE));
    player2NameField.setText((game.getUsername(Player.BLACK).equals("")) ? "Computer" : game.getUsername(Player.BLACK));

    player1ScoreLabel.setText("Cards left: " + game.getTable().getDeckSize(Player.WHITE));
    player2ScoreLabel.setText("Cards left: " + game.getTable().getDeckSize(Player.BLACK));

    player1TieCardsTextLabel.setText("Cards "+ game.getTable().getInvisible()/2);// Update labels during war
    player2TieCardsTextLabel.setText("Cards "+ game.getTable().getInvisible()/2);// Update labels during war

    resultLabel.setIcon(null); // Clear previous result icon

    // Get the current cards for each player
    var whiteCard = game.getTable().getCardWhite();
    var blackCard = game.getTable().getCardBlack();

    // Update card labels based on the current cards
    player1CardLabel.setIcon((whiteCard != null) ? new ImageIcon(game.getAssetPath(whiteCard)) : cardBackIcon);
    player2CardLabel.setIcon((blackCard != null) ? new ImageIcon(game.getAssetPath(blackCard)) : cardBackIcon);

    // Check if the game is in a tie state
    boolean isTie = game.getTable().isWar();
    player1TieCardsLabel.setVisible(isTie); // Show or hide based on tie state
    player2TieCardsLabel.setVisible(isTie); // Show or hide based on tie state
    player1TieCardsTextLabel.setVisible(isTie); // Show or hide based on tie state
    player2TieCardsTextLabel.setVisible(isTie); // Show or hide based on tie state

    // Set the result icon based on the game state
    if (isTie) {
      resultLabel.setIcon(tieIcon); // Show tie icon if it's a tie
    } else {
      resultLabel.setIcon((game.getTable().getCardWhite().cardIdx > game.getTable().getCardBlack().cardIdx) ? winIcon : loseIcon);
    }
  }

  // Method to update the game state when the game is finished
  private void GameFinishedSate(Event e) {
    backgroundImg = new ImageIcon("assets/images/background.png").getImage(); // Set background image to default
    revalidate();
    repaint();
    if (e.winner == null) {
      resultLabel.setIcon(tieIcon); // Show tie icon if the game ends in a tie
    } else {
      // Update UI based on the winner
      switch (e.winner) {
        case WHITE:
          resultLabel.setIcon(winIcon);
          player2CardLabel.setIcon(null); // Clear Player 2's card
          resultLabel.setText(player1NameField.getText() + " wins the game!"); // Display winning message
          player1ScoreLabel.setText("Cards left: 52 ");
          player2ScoreLabel.setText("Cards left: 0");
          break;
        case BLACK:
          resultLabel.setIcon(loseIcon);
          player1CardLabel.setIcon(null); // Clear Player 1's card
          resultLabel.setText(player2NameField.getText() + " wins the game!"); // Display winning message
          player1ScoreLabel.setText("Cards left: 0 ");
          player2ScoreLabel.setText("Cards left: 52");
          break;
      }
    }
  }

  // Method to initialize the game
  private void initGame() {
    game = new Game(); // Create a new game instance
    game.dispatchDecks(); // Distribute the decks to players
    game.setUsername(Player.WHITE, "Player 1"); // Set username for Player 1
    game.setUsername(Player.BLACK, "Computer"); // Set username for Player 2
  }

  // Method to play a round of the game
  private void playRound() {
    game.playRound(); // Play a round in the game

    System.out.println(game.getEvents()); // Debugging output
    for (var e : game.getEvents()) {
      // Handle different types of game events
      switch (e) {
        case POLL_CARDS:
          Update(); // Update UI when cards are polled
          break;
        case COMPARE_CARDS:
          System.out.println(e.winner); // Debugging output
          if (e.winner == null) {
            resultLabel.setIcon(tieIcon); // Show tie icon if there's no winner
            playSound("assets/sounds/tie.wav"); // Play tie sound
          } else {
            // Update UI based on the winner
            switch (e.winner) {
              case WHITE:
                playSound("assets/sounds/win.wav"); // Play win sound
                resultLabel.setIcon(winIcon); // Show win icon for Player 1
                break;
              case BLACK:
                resultLabel.setIcon(loseIcon); // Show lose icon for Player 1
                playSound("assets/sounds/lose.wav"); // Play lose sound
                break;
            }
          }
          break;
        case GAME_FINISH:
          GameFinishedSate(e); // Update UI when the game finishes
          break;
      }
    }

    revalidate();
    repaint();
  }

  // Method to create the menu bar
  private void createMenuBar() {
    JMenuBar menuBar = new JMenuBar(); // Create a menu bar

    // File menu with New Game, Save, and Load options
    JMenu fileMenu = new JMenu("File");
    JMenuItem newGameItem = new JMenuItem("New Game");
    JMenuItem saveButtonItem = new JMenuItem("Save");
    JMenuItem loadButtonItem = new JMenuItem("Load");

    // Action listener for new game
    newGameItem.addActionListener(e -> {
      initGame(); // Initialize a new game
      player1NameField.setText(game.getUsername(Player.WHITE)); // Update Player 1's name
      player2NameField.setText(game.getUsername(Player.BLACK)); // Update Player 2's name
      playButton.setEnabled(true); // Enable the play button
      player1ScoreLabel.setText("Cards left: 26"); // Reset score label
      player2ScoreLabel.setText("Cards left: 26"); // Reset score label
      resultLabel.setText(""); // Clear result text
      resultLabel.setIcon(null); // Clear result icon
      player1CardLabel.setIcon(cardBackIcon); // Reset Player 1's card
      player2CardLabel.setIcon(cardBackIcon); // Reset Player 2's card
      player1TieCardsLabel.setVisible(false); //hide tie cards
      player2TieCardsLabel.setVisible(false); //hide tie cards
      player1TieCardsTextLabel.setVisible(false); //hide tie cards
      player2TieCardsTextLabel.setVisible(false); //hide tie cards
    });

    // Action listener for save game
    saveButtonItem.addActionListener(e -> {
      System.out.println("Saved"); // Debugging output
      try {
        this.game.save("./saves", "gameState"); // Save the game state
      } catch (Exception e1) {
        e1.printStackTrace(); // Print any errors
      }
    });

    // Action listener for load game
    loadButtonItem.addActionListener(e -> {
      System.out.println("Loaded"); // Debugging output
      try {
        this.game = Game.load("./saves/gameState"); // Load the game state
      } catch (Exception e1) {
        e1.printStackTrace(); // Print any errors
      }

      backgroundImg = new ImageIcon("assets/images/background.png").getImage(); // Reset background image
      revalidate();
      repaint();
      Update(); // Update UI
    });

    // Action listeners for name fields to update game state
    player1NameField.addActionListener(e -> {
      game.setUsername(Player.WHITE, player1NameField.getText()); // Update Player 1's name
    });
    player2NameField.addActionListener(e -> {
      game.setUsername(Player.BLACK, player2NameField.getText()); // Update Player 2's name
    });

    // Add items to file menu
    fileMenu.add(newGameItem);
    fileMenu.add(saveButtonItem);
    fileMenu.add(loadButtonItem);
    menuBar.add(fileMenu);

    // Rules menu with View Rules option
    JMenu rulesMenu = new JMenu("Rules");
    JMenuItem rulesItem = new JMenuItem("View Rules");
    rulesItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
        "The objective of the game is to win all of the cards.\n" +
        "The deck is divided evenly and randomly among the players,\n" +
        " giving each a down stack. In unison, each player reveals the\n" +
        " top card of their deck - this is a \"battle\" - and the player with\n" +
        " the higher card takes both of the cards played and moves them to their stack.\n" +
        " Aces are high, and suits are ignored.\n" +
        "If the two cards played are of equal value,\n" +
        " then there is a war. Both players place the next 2 cards from their pile \n" +
        "face down and then another card face-up. The owner of the higher face-up card\n" +
        "wins the war and adds all the cards on the table to the bottom of their deck.\n" +
        "If the face-up cards are again equal then the battle repeats with another set of \n" +
        "face-down/up cards. This repeats until one player's face-up card is higher than their opponent's.\n" +
        "If a player runs out of cards during a war, that player immediately loses.\n" +
        "In others, the player may play the last card in their deck as their face-up card for the remainder\n" +
        "of the war or replay the game from the beginning.\n" +
        "The game will continue until one player has collected all of the cards."
    ));
    rulesMenu.add(rulesItem);
    menuBar.add(rulesMenu);

    // About menu with About Us option
    JMenu aboutMenu = new JMenu("About");
    JMenuItem aboutItem = new JMenuItem("About Us");
    aboutItem.addActionListener(e -> {
        JLabel label = new JLabel("<html>Developed by: Fadi Abbara, Anas Zahran, Liana Mikhailova,<br>" +
                "Ömer Duran, Danylo Bazalinskyi, G. V.<br><br>" +
                "<a href='https://github.com/ELLEONEL10/CardGame'>CLICK HERE</a> for GitHub Repo.</html>");
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/ELLEONEL10/CardGame")); // Open GitHub repo in browser
                } catch (Exception ex) {
                    ex.printStackTrace(); // Print any errors
                }
            }
        });
        JOptionPane.showMessageDialog(this, label, "About Us", JOptionPane.INFORMATION_MESSAGE);
    });
    aboutMenu.add(aboutItem);
    menuBar.add(aboutMenu);

    // Set the menu bar to the frame
    setJMenuBar(menuBar);
  }

  // Method to play a sound
  private void playSound(String soundFile) {
    try {
      File f = new File(soundFile);
      AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
      Clip clip = AudioSystem.getClip();
      clip.open(audioIn);
      clip.start(); // Play the sound
    } catch (Exception e) {
      e.printStackTrace(); // Print any errors
    }
  }

  // Main method to start the GUI
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new WarCardGameGUI());
  }
}
