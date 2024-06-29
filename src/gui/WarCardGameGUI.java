package gui;

import game.Card;
import game.Player;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class WarCardGameGUI extends JFrame {
    private Player player1;
    private Player player2;
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

    public WarCardGameGUI() {
        setTitle("War Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false); // Disallow resizing

        // Load icons with better image quality and set resizing hints
        cardBackIcon = new ImageIcon(getClass().getResource("/images/cardbg.png"));
        cardBackIcon.setImage(cardBackIcon.getImage().getScaledInstance(250, 320, Image.SCALE_SMOOTH)); // Resize card image
        winIcon = new ImageIcon(getClass().getResource("/images/win.png"));
        loseIcon = new ImageIcon(getClass().getResource("/images/lose.png"));
        tieIcon = new ImageIcon(getClass().getResource("/images/tie.png"));

        // Calculate height based on 16:9 aspect ratio
        int width = 1080;
        int height = width * 9 / 16;

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image backgroundImg = new ImageIcon(getClass().getResource("/images/background.png")).getImage();
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

        player1ScoreLabel = new JLabel("Total score: 0", JLabel.CENTER);
        player1ScoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        player1ScoreLabel.setForeground(Color.BLACK);

        player2ScoreLabel = new JLabel("Total score: 0", JLabel.CENTER);
        player2ScoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        player2ScoreLabel.setForeground(Color.BLACK);

        scorePanel.add(player1ScoreLabel);
        scorePanel.add(player2ScoreLabel);

        playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.BOLD, 18));
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playSound("/sounds/play.wav");
                playRound();
            }
        });

        bottomPanel.add(scorePanel, BorderLayout.CENTER);
        bottomPanel.add(playButton, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // User and computer panels with padding
        JPanel userPanel = createUserPanel("/images/user.png", player1CardLabel);
        userPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding

        JPanel computerPanel = createUserPanel("/images/computer.png", player2CardLabel);
        computerPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding

        mainPanel.add(userPanel, BorderLayout.WEST);
        mainPanel.add(computerPanel, BorderLayout.EAST);

        createMenuBar();
        initGame();

        playSound("/sounds/start.wav");

        add(mainPanel, BorderLayout.CENTER);
        setSize(width, height); // Set size based on aspect ratio
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createUserPanel(String imagePath, JLabel cardLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel imageLabel = new JLabel(new ImageIcon(getClass().getResource(imagePath)));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        panel.add(imageLabel, BorderLayout.NORTH);
        panel.add(cardLabel, BorderLayout.CENTER);

        return panel;
    }

    private void initGame() {
        player1 = new Player("Player 1");
        player2 = new Player("Player 2");
        ArrayList<Card> deck = new ArrayList<>();
        String[] suits = {"hearts", "diamonds", "clubs", "spades"};
        for (String suit : suits) {
            for (int i = 1; i <= 13; i++) {
                deck.add(new Card(suit, i));
            }
        }
        Collections.shuffle(deck);
        for (int i = 0; i < deck.size(); i++) {
            if (i % 2 == 0) {
                player1.addCard(deck.get(i));
            } else {
                player2.addCard(deck.get(i));
            }
        }
    }

    private void playRound() {
        Card player1Card = player1.playCard();
        Card player2Card = player2.playCard();

        if (player1Card != null && player2Card != null) {
            player1CardLabel.setIcon(new ImageIcon(getClass().getResource(player1Card.getImagePath())));
            player2CardLabel.setIcon(new ImageIcon(getClass().getResource(player2Card.getImagePath())));

            int player1Score = Integer.parseInt(player1ScoreLabel.getText().split(": ")[1]);
            int player2Score = Integer.parseInt(player2ScoreLabel.getText().split(": ")[1]);

            if (player1Card.getValue() > player2Card.getValue()) {
                player1Score += player1Card.getValue();
                player1ScoreLabel.setText("Total score: " + player1Score);
                resultLabel.setText("");
                resultLabel.setIcon(winIcon);
                playSound("/sounds/win.wav");
            } else if (player2Card.getValue() > player1Card.getValue()) {
                player2Score += player2Card.getValue();
                player2ScoreLabel.setText("Total score: " + player2Score);
                resultLabel.setText("");
                resultLabel.setIcon(loseIcon);
                playSound("/sounds/lose.wav");
            } else {
                resultLabel.setText("");
                resultLabel.setIcon(tieIcon);
                playSound("/sounds/tie.wav");
            }

            revalidate();
            repaint();
        } else {
            if (player1Card == null) {
                resultLabel.setText("Computer wins the game!");
                resultLabel.setIcon(loseIcon);
            } else {
                resultLabel.setText("User wins the game!");
                resultLabel.setIcon(winIcon);
            }
            playButton.setEnabled(false);
        }
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newGameItem = new JMenuItem("New Game");

        newGameItem.addActionListener(e -> {
            initGame();
            playButton.setEnabled(true);
            player1ScoreLabel.setText("Total score: 0");
            player2ScoreLabel.setText("Total score: 0");
            resultLabel.setText("");
            resultLabel.setIcon(null);
            player1CardLabel.setIcon(cardBackIcon);
            player2CardLabel.setIcon(cardBackIcon);
        });

        fileMenu.add(newGameItem);
        menuBar.add(fileMenu);

        JMenu aboutMenu = new JMenu("About");
        JMenuItem aboutItem = new JMenuItem("About Us");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Developed by: Fadi Abbara, Anas Zahran, Liana Mikhailova, \r\n" + //
                        "Ömer Duran, Danylo Bazalinskyi, G. V."));
        aboutMenu.add(aboutItem);
        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);
    }

    private void playSound(String soundFile) {
        try {
            URL url = getClass().getResource(soundFile);
            if (url == null) {
                throw new IllegalArgumentException("Sound file not found: " + soundFile);
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
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
