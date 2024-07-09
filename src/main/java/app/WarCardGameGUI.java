package app;

import game.Game;
import game.EventQueue;
import game.EventQueue.Event;
import game.EventQueue.Player;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import cards.VCard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class WarCardGameGUI extends JFrame {

  private JLabel player1CardLabel;
  private JLabel player2CardLabel;
  private JLabel player1ScoreLabel;
  private JLabel player2ScoreLabel;
  private JLabel resultLabel;
  private JButton playButton;
  private ImageIcon cardBackIcon;
  private ImageIcon winIcon;
  private ImageIcon loseIcon;
  private ImageIcon tieIcon;
  private Game game = null;
  Image backgroundImg;

  public WarCardGameGUI() {
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

    JPanel mainPanel = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (game.getTable().isWar()) {
          backgroundImg = new ImageIcon("assets/images/backgroundw.png").getImage();
        } else
          backgroundImg = new ImageIcon("assets/images/background.png").getImage();
        g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
      }
    };

    // Top panel with card displays and result
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setOpaque(false);

    JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 80, 0));
    cardsPanel.setOpaque(false);
    cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // Adjust margin

    player1CardLabel = new JLabel(cardBackIcon);
    player1CardLabel.setHorizontalAlignment(JLabel.CENTER);

    resultLabel = new JLabel("", JLabel.CENTER);

    player2CardLabel = new JLabel(cardBackIcon);
    player2CardLabel.setHorizontalAlignment(JLabel.CENTER);

    cardsPanel.add(player1CardLabel);
    cardsPanel.add(resultLabel);
    cardsPanel.add(player2CardLabel);

    topPanel.add(cardsPanel, BorderLayout.CENTER);

    mainPanel.add(topPanel, BorderLayout.CENTER);

    // Bottom panel with scores and play button
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setOpaque(false);

    JPanel scorePanel = new JPanel(new GridLayout(1, 2));
    scorePanel.setOpaque(false);

    player1ScoreLabel = new JLabel("Cards left: 26", JLabel.CENTER);
    player1ScoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    player1ScoreLabel.setForeground(Color.BLACK);

    player2ScoreLabel = new JLabel("Cards left: 26", JLabel.CENTER);
    player2ScoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    player2ScoreLabel.setForeground(Color.BLACK);

    scorePanel.add(player1ScoreLabel);
    scorePanel.add(player2ScoreLabel);

    playButton = new JButton("Play");
    playButton.setFont(new Font("Arial", Font.BOLD, 18));
    playButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        playSound("assets/sounds/play.wav");
        playRound();
      }
    });

    bottomPanel.add(scorePanel, BorderLayout.CENTER);
    bottomPanel.add(playButton, BorderLayout.SOUTH);

    mainPanel.add(bottomPanel, BorderLayout.SOUTH);

    // User and computer panels with padding and titles
    JPanel userPanel = createUserPanelWithTitle("assets/images/user.png", player1CardLabel, "Player 1");
    userPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding

    JPanel computerPanel = createUserPanelWithTitle("assets/images/computer.png", player2CardLabel, "Computer");
    computerPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding

    mainPanel.add(userPanel, BorderLayout.WEST);
    mainPanel.add(computerPanel, BorderLayout.EAST);

    createMenuBar();
    initGame();

    playSound("assets/sounds/start.wav");

    add(mainPanel, BorderLayout.CENTER);
    setSize(width, height); // Set size based on aspect ratio
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private JPanel createUserPanelWithTitle(String imagePath, JLabel cardLabel, String title) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);

    JLabel titleLabel = new JLabel(title, JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
    titleLabel.setForeground(Color.BLACK);

    JLabel imageLabel = new JLabel(new ImageIcon(imagePath));
    imageLabel.setHorizontalAlignment(JLabel.CENTER);

    panel.add(titleLabel, BorderLayout.NORTH);
    panel.add(imageLabel, BorderLayout.CENTER);
    panel.add(cardLabel, BorderLayout.SOUTH);

    return panel;
  }
  
  private void Update() {
    
    player1ScoreLabel.setText("Cards left: " + game.getTable().getDeckSize(Player.WHITE));
    player2ScoreLabel.setText("Cards left: " + game.getTable().getDeckSize(Player.BLACK));
    
    
    resultLabel.setIcon(null);
    
    
    var whiteCard = game.getTable().getCardWhite();
    var blackCard = game.getTable().getCardBlack();

    player1CardLabel.setIcon((whiteCard != null) ? new ImageIcon( game.getAssetPath(whiteCard)) : cardBackIcon );
    player2CardLabel.setIcon((blackCard != null) ? new ImageIcon( game.getAssetPath(blackCard)) : cardBackIcon);

    
    
  }
  

  private void GameFinishedSate(Event e){

    backgroundImg = new ImageIcon("assets/images/background.png").getImage();
    if (e.winner == null)
          resultLabel.setIcon(tieIcon);
        else
        switch (e.winner) {
          case WHITE:
            resultLabel.setIcon(winIcon);
            player2CardLabel.setIcon(null);
            resultLabel.setText("User wins the game!");
            player1ScoreLabel.setText("Cards left: 52 ");
            player2ScoreLabel.setText("Cards left: 0");
            break;
          case BLACK:
            resultLabel.setIcon(loseIcon);
            player1CardLabel.setIcon(null);
            resultLabel.setText("Computer wins the game!");
            player1ScoreLabel.setText("Cards left: 0 ");
            player2ScoreLabel.setText("Cards left: 52");
            break;
        }
  }
  private void initGame() {
    game = new Game();
    game.dispatchDecks();
  }

  private void playRound() {
    game.playRound();
    
    System.out.println(game.getEvents());
    for (var e : game.getEvents()) {
      switch (e) {

        case POLL_CARDS:
        Update();
        break;

        case COMPARE_CARDS:
        System.out.println(e.winner);
        if (e.winner == null) {
          resultLabel.setIcon(tieIcon);
          //playSound("assets/sounds/tie.wav");
        } 
        else
          switch (e.winner) {
            case WHITE:
              playSound("assets/sounds/win.wav");
              resultLabel.setIcon(winIcon);
              break;

            case BLACK:
              resultLabel.setIcon(loseIcon);
              playSound("assets/sounds/lose.wav");
              break;
          }  
        break;

        case GAME_FINISH:
        GameFinishedSate(e);
        break;
      }
      
      
    }

    revalidate();
    repaint();
  }

  private void createMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    JMenu fileMenu = new JMenu("File");
    JMenuItem newGameItem = new JMenuItem("New Game");
    JMenuItem saveButtonItem = new JMenuItem("Save");
    JMenuItem loadButtonItem = new JMenuItem("Load");
    newGameItem.addActionListener(e -> {
      initGame();
      playButton.setEnabled(true);
      player1ScoreLabel.setText("Cards left: 26");
      player2ScoreLabel.setText("Cards left: 26");
      resultLabel.setText("");
      resultLabel.setIcon(null);

      player1CardLabel.setIcon(cardBackIcon);
      player2CardLabel.setIcon(cardBackIcon);
    });
    saveButtonItem.addActionListener(e -> {
      System.out.println("Saved");
      try {
        this.game.save("./saves", "gameState");
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    });
    loadButtonItem.addActionListener(e -> {
      System.out.println("Loaded");
      try {
        this.game = game.load("./saves/gameState");
      } catch (Exception e1) {
        e1.printStackTrace();
      }
      backgroundImg = new ImageIcon("assets/images/background.png").getImage();
      Update();

    });
        fileMenu.add(newGameItem);
        fileMenu.add(saveButtonItem);
        fileMenu.add(loadButtonItem);
        menuBar.add(fileMenu);

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
                        Desktop.getDesktop().browse(new URI("https://github.com/ELLEONEL10/CardGame"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            JOptionPane.showMessageDialog(this, label, "About Us", JOptionPane.INFORMATION_MESSAGE);
        });
        aboutMenu.add(aboutItem);
        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);
    }

  private void playSound(String soundFile) {
    try {
      File f = new File(soundFile);
      AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
      Clip clip = AudioSystem.getClip();
      clip.open(audioIn);
      clip.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new WarCardGameGUI());
  }
}
