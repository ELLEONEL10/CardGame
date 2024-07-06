package game;

import game.Game;
import game.EventQueue;
import game.EventQueue.Event;

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
        // cardBackIcon = new ImageIcon(getClass().getResource("../../../../assets/images/cardbg.png"));
        cardBackIcon = new ImageIcon("assets/images/cardbg.png");
        cardBackIcon.setImage(cardBackIcon.getImage().getScaledInstance(250, 320, Image.SCALE_SMOOTH)); // Resize card image
        winIcon = new ImageIcon("assets/images/win.png");
        loseIcon = new ImageIcon("assets/images/lose.png");
        tieIcon = new ImageIcon("assets/images/tie.png");

        // Calculate height based on 16:9 aspect ratio
        int width = 1080;
        int height = width * 9 / 16;

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);


              	if (game.table.isWar()) {
            		 backgroundImg = new ImageIcon("assets/images/backgroundw.png").getImage();
              	}
              	else backgroundImg = new ImageIcon("assets/images/background.png").getImage();
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
                playSound("assets/sounds/play.wav");
                playRound();
            }
        });

        bottomPanel.add(scorePanel, BorderLayout.CENTER);
        bottomPanel.add(playButton, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // User and computer panels with padding
        JPanel userPanel = createUserPanel("assets/images/user.png", player1CardLabel);
        userPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding

        JPanel computerPanel = createUserPanel("assets/images/computer.png", player2CardLabel);
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

    private JPanel createUserPanel(String imagePath, JLabel cardLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel imageLabel = new JLabel(new ImageIcon(imagePath));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        panel.add(imageLabel, BorderLayout.NORTH);
        panel.add(cardLabel, BorderLayout.CENTER);

        return panel;
    }

    private void initGame() {
         game = new Game();
         game.dispatchDecks();
    }

    private void playRound() {
    	game.playRound();
    	
    	for (var e : game.events.evQueue)
    	{
		      if (e == Event.GAME_FINISH)
		      {
		    	  if(e.winner==null)  resultLabel.setIcon(tieIcon);   		
	 		
		    	  else
	 		    	  switch(e.winner)
	 		    	  {
	 		    	  case WHITE :
	 		    		  resultLabel.setIcon(winIcon);
	 		    		  resultLabel.setText("User wins the game!");	 		  	
	 	 		    	  player1ScoreLabel.setText("Cards left: 52 " );
	 	 		    	  player2ScoreLabel.setText("Cards left: 0" );
	 		    		  break;
	 		    		
	 		    	  case BLACK :
	 		    		  resultLabel.setIcon(loseIcon);
	 		    		  resultLabel.setText("Computer wins the game!");
	 	 		    	  player1ScoreLabel.setText("Cards left: 0 " );
	 	 		    	  player2ScoreLabel.setText("Cards left: 52" );
	 		    		  break;
	 		    	  }
		      }
		    	 if (e == Event.POLL_CARDS){
	 		    	  player1CardLabel.setIcon(new ImageIcon(game.getAssetPath(e.whiteCard)));
	 		    	  player2CardLabel.setIcon(new ImageIcon(game.getAssetPath(e.blackCard)));
	 		    	  player1ScoreLabel.setText("Cards left: " + game.getScoreWhite());
	 		    	  player2ScoreLabel.setText("Cards left: " + game.getScoreBlack());
	 		      }
		    	 if (e == Event.COMPARE_CARDS) {
	 		    	  if(e.winner==null)resultLabel.setIcon(tieIcon);  	
	 		    	  else
	 		    	  switch(e.winner)
	 		    	  {
	 		    	  case WHITE :
	 		    		
	 		    		  resultLabel.setIcon(winIcon);
	 		    		
	 		    		  break;
	 		    	  case BLACK :
	 		    		  resultLabel.setIcon(loseIcon);
	 		    		  break;
	 		    	  }
		    	 }
		      }


    	game.events.evQueue.clear();
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
            // AudioInputStream audioIn = AudioSystem.getAudioInputStream(new URL(soundFile));
            // Clip clip = AudioSystem.getClip();
            // clip.open(audioIn);
            // clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WarCardGameGUI());
    }
}
