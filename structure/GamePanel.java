package structure;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.Timer;

import gameplay.Cpu;
import gameplay.Human;
import gameplay.Player;
import pieces.*;

public class GamePanel extends JPanel implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MainWindow window;
	private GamePanel gamePanel;

	private Graphics2D g2d;
	
	private Timer timer;
	private int seconds;
	private int playedAtSeconds;
	
	private boolean gameOver;
	private boolean isBurning;
	private boolean isPaused;
	private boolean tricksterPlayed;
	private boolean terminated;
	
	private Color bgColor;
	private Color rulesButtonColor;
	
	private Deck deck;
	private ArrayList<Card> usedCards;
	private int cardWidth, cardHeight;
	private int dealCount;
	private Card selectedCard;
	private Point[] cardPositions = {new Point(250, 692), new Point(310, 692), new Point(370, 692),
			new Point(430, 692), new Point(490, 692), new Point(550, 692)};
	
	private Board board;
	
	private Point[] holePoints;
	private Point[] startPositions;
	private ArrayList<Marble> marbles;
	private Marble selectedMarble;
	private Marble selectedMarble2;
	private Point[][] marbleStartingPoints = {
			{new Point(695, 335), new Point(682, 350), new Point(708, 350), new Point(695, 365)},
			{new Point(392, 30), new Point(380, 45), new Point(404, 45), new Point(392, 60)},
			{new Point(87, 335), new Point(75, 350), new Point(99, 350), new Point(87, 365)},
			{new Point(392, 642), new Point(379, 657), new Point(405, 657), new Point(392, 672)},
	};
	
	private int count;
	private boolean canPlay;
	
	private Cpu[] cpu;
	private Human player;
	private Player[] players;
	
	private int cpuWaitTime;
	
	private Marble[] marblesOnBoard;
	
	Rectangle yesButton, noButton, pauseButton, rulesButton;
	//Rectangle menuItem1, menuItem2, menuItem3, menuItem4;
	
	ArrayList<Rectangle> menuItems;
	
	ImageIcon icon;
	
	Clip bgMusicClip;

	
	/*
	 * All values in arrays related to players start with the east player 
	 * and goes counter-clockwise
	 */
	
	
	public GamePanel(MainWindow mainWindow) {
		
		this.window = mainWindow;
		this.gamePanel = this;
		
		players = new Player[4];
		cpu = new Cpu[3];
		for (int i=0; i<3; i++) {
			cpu[i] = new Cpu(this);
			players[i] = cpu[i];
		}
		
		player = new Human(this);
		players[3] = player;
		
		for (int i=0; i<2; i++) {
			players[i].setPartner(players[(i+2)%4]);
			players[(i+2)%4].setPartner(players[i]);
		}

		bgColor = new Color(60, 100, 150);
		rulesButtonColor = new Color(220, 220, 220);
		this.setBackground(bgColor);

		timer = new Timer(1000, this);
		timer.start();

		cardWidth = 50;
		cardHeight = 70;

		deck = new Deck(true); // true boolean parameter means deck is shuffled

		deal();

		this.setBackground(Color.white);

		board = new Board(this, 50, 10);
		
		ClickListener clickListener = new ClickListener();
		DragListener dragListener = new DragListener();
		
		this.addMouseListener(clickListener);
		this.addMouseMotionListener(dragListener);
		
		holePoints = new Point[64];
		
		usedCards = new ArrayList<>();
		
		canPlay = true;

		startPositions = new Point[4];
		
		marblesOnBoard = new Marble[64];
		
		marbles = new ArrayList<>();
		
		cpuWaitTime = 2;
		
		yesButton = new Rectangle();
		noButton = new Rectangle();
		pauseButton = new Rectangle();
		rulesButton = new Rectangle();
		
		icon = new ImageIcon("Images/tac_image.png");
		
		try {
			playBackgroundMusic();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
		
		//new RulesWindow(this);
	
		//new AdjustementWindow(this, board, "Small circle spread", 200, 400, board.getSmallCircleSpread());

	}
	
	
	/*
	 * Paint methods
	 */
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(bgColor);
		
		g2d = (Graphics2D) g;

		RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
                
        g2d.setRenderingHints(rh);
    
        board.setPanelWidth(this.getWidth());
        board.setPanelHeight(this.getHeight());
        
        board.drawBoard(g2d);
        

        drawMarbles(g2d);
        
        drawCards(g2d);
        
        drawRulesButton(g2d);
        
        
        if (isBurning) {
        	dimPanel();
        	drawChoiceBox("Burn?", this.getWidth() - 200, this.getHeight() - 150);
        }
        else if (tricksterPlayed) {
        	dimPanel();
        	drawPlayableMarbles(g2d);
        }
        else if (isPaused) {
        	dimPanel();
        	drawPauseMenu();
        }
        
        
        if (gameOver) {
        	g2d.setColor(new Color(200, 0, 0));
        	g2d.setFont(new Font("Trattatello", Font.BOLD, 80));
        	int stringWidth = g2d.getFontMetrics().stringWidth("Winner!");
        	g2d.drawString("Winner!", 
        			(int) (this.getWidth()/2.0 - stringWidth/2.0),
        			(int) (this.getHeight()/2.0));
        }
        
        drawPauseButton(g2d);
	}
	
	private void drawMarbles(Graphics2D g2d) {
		
		if (count == 0) {
            holePoints = board.getHolePoints();
            player.setHolePositions(holePoints);
            player.setHomeBaseHoles(board.getHomeBaseHoles());
            for (int i=0; i<3; i++) {
            	cpu[i].setHomeBaseHoles(board.getHomeBaseHoles());
            }
            
            int interval = 0;
            for (int i=0; i<3; i++) {
            	cpu[i].setHolePositions(holePoints);
            	startPositions[i] = holePoints[interval];
            	interval += 16;
            }
            
        	initializeMarbles();
        	
        	board.setX((int) (this.getWidth()/2.0 - board.getWidth()/2.0));
        	
        	// TEMPORARILY PUT ALL HUMAN MARBLES IN
//        	for (int i=0; i<4; i++) {
//        		Marble m = players[3].getMarbles().get(i);
//        		m.setHoleNumberInHome(i);
//        		m.setPosition(players[3].getHomeBaseHoles()[i]);
//        		players[3].updateMarblePositionsInHome(m, i);
//        		players[3].setNumOfMarblesIn(4);
//        	}
        	
//        	checkForWinner();
        	
        	count++;
        }
		
		for (int i=0; i<marbles.size(); i++) {
        	Marble m = marbles.get(i);
        	
        	if (m.isSelected(selectedMarble) || m.isSelected(selectedMarble2)) {
        		m.setOutlineColor(g2d, Color.cyan);
        	}
        	else
        		m.setOutlineColor(g2d, Color.black);
        	
        	m.drawMarble(g2d);
        }
		
	}
	
	public void drawPlayableMarbles(Graphics2D g2d) {
		
		for (int i=0; i<marbles.size(); i++) {
			Marble m = marbles.get(i);
			
			if (m.isOut() && !m.isIn()) {
				m.drawMarble(g2d);
			}
		}
	}

	private void drawCards(Graphics2D g2d) {
		
		if (count == 1) {
			resizeHand();
			count++;
		}
		
        for (int i=0; i<players[3].getHand().size(); i++) {
        	Card card = players[3].getHand().get(i);
        	card.flipped(true);
        	card.drawCard(g2d, card.getX(), card.getY());
        }
        
        Random rand = new Random(1);
        
        for (int i=0; i<usedCards.size(); i++) {
        	
        	Card card = usedCards.get(i);
        	card.flipped(true);
        	card.setCardColor(Color.white);
        	
        	double theta = rand.nextDouble() - rand.nextDouble();
        	int xValue = (int) (this.getWidth()/2.0 - cardWidth/2.0 - 10 + rand.nextInt(20));
        	int yValue = (int) (this.getHeight()/2.0 - cardHeight - 10 + rand.nextInt(20));
        	
        	g2d.rotate(theta/2.0, xValue, yValue);
        	card.drawCard(g2d, xValue, yValue);
        	g2d.rotate(-theta/2.0, xValue, yValue);
        }

        drawDeck();
        
	}
	
	public void drawDeck() {

		int xValue = 80;
		int yValue = this.getHeight()-140;
		int deckSize = (int) (deck.size()/5.0);

		for (int i=0; i<deckSize; i++) {
			Card card = deck.getCardList().get(i);
			card.flipped(false);
			card.drawCard(g2d, xValue, yValue);
			xValue-=2;
			yValue -= 2;
		}

	}
	
	public void drawChoiceBox(String text, int x, int y) {
		g2d.setFont(new Font("Geneva", Font.PLAIN, 14));
		
		int stringWidth = g2d.getFontMetrics().stringWidth(text); 
		int stringHeight = g2d.getFontMetrics().getHeight();
		int width = 120;
		int height = 85;
		
		g2d.setColor(new Color(180, 190, 215));
		g2d.fillRoundRect(x, y, width, height, 65, 65);
		g2d.setColor(Color.black);
		g2d.drawRoundRect(x, y, width, height, 65, 65);
		
		g2d.drawString(text, (int) (x + width/2.0 - stringWidth/2.0), y + stringHeight + 5);
		
		yesButton = new Rectangle(x + 15, y + height - 39, 40, 25);
		noButton = new Rectangle(x + 65, y + height - 39, 40, 25);
		
		g2d.setColor(new Color(150, 130, 220));
		g2d.fillRoundRect(yesButton.x, yesButton.y, 
				(int) yesButton.getWidth(), (int) yesButton.getHeight(),
				20, 20);
		g2d.fillRoundRect(noButton.x, noButton.y, 
				(int) noButton.getWidth(), (int) noButton.getHeight(),
				20, 20);
		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke(1));
		g2d.drawRoundRect(yesButton.x, yesButton.y, 
				(int) yesButton.getWidth(), (int) yesButton.getHeight(),
				20, 20);
		g2d.drawRoundRect(noButton.x, noButton.y, 
				(int) noButton.getWidth(), (int) noButton.getHeight(),
				20, 20);
		
		g2d.drawString("Yes", (int) (yesButton.x + yesButton.getWidth()/2.0 - g2d.getFontMetrics().stringWidth("Yes")/2.0),
				(int) (yesButton.y + yesButton.getHeight() - g2d.getFontMetrics().getHeight()/2.0));
		g2d.drawString("No", (int) (noButton.x + noButton.getWidth()/2.0 -  g2d.getFontMetrics().stringWidth("No")/2.0),
				(int) (noButton.y + noButton.getHeight() - g2d.getFontMetrics().getHeight()/2.0));

	}
	
	public void drawPauseButton(Graphics2D g2d) {
		
		int buttonWidth = 18; // 3:5 ratio
		int buttonHeight = 30;
		int buttonX = 35;
		int buttonY = 35;
	
		pauseButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
		
		g2d.setStroke(new BasicStroke(6));
		
		int width = buttonWidth - 6;
		int height = buttonHeight - 10;
		int xValue = buttonX + 3;
		int yValue = buttonY + 5;
		
		if (!isPaused) {
			g2d.setColor(new Color(0, 0, 0, 175));
			g2d.drawLine(xValue, yValue, xValue, yValue + height);
			g2d.drawLine(xValue + width, yValue, xValue + width, yValue + height);
		}
		else {
			g2d.setColor(new Color(0, 0, 0));
			int[] xPoints = {xValue - 3, xValue - 3, xValue + width + 3};
			int[] yPoints = {yValue - 3, yValue + height + 3, yValue + (int) (height/2.0)};
			Polygon triangle = new Polygon(xPoints, yPoints, 3);
			g2d.fillPolygon(triangle);
			g2d.setColor(Color.lightGray);
			g2d.setStroke(new BasicStroke(2));
			g2d.drawPolygon(triangle);
		}
	}
	
	private void drawPauseMenu() {
		int menuWidth = 500; // 4:5 ratio
		int menuHeight = 625;
		int menuX = (int) (this.getWidth()/2.0 - menuWidth/2.0);
		int menuY = (int) (this.getHeight()/2.0 - menuHeight/2.0);
		g2d.setColor(new Color(110, 105, 190, 190));
		g2d.fillRoundRect(menuX, menuY, menuWidth, menuHeight, 20, 20);
		
		int menuItemWidth = 300;
		int menuItemHeight = 50;
		int menuItemX = (int) (menuX + menuWidth/2.0 - menuItemWidth/2.0);
		
		menuItems = new ArrayList<>();
		
		for (int i=0; i<5; i++) {
			menuItems.add(new Rectangle(
					menuItemX, menuY + menuItemHeight*(i+1) + (i+1)*50, menuItemWidth, menuItemHeight));
		}
		
		g2d.setColor(new Color(80, 50, 180, 240));
		
		for (int i=0; i<menuItems.size(); i++) {
			//g2d.draw(menuItems.get(i));
			Rectangle item = menuItems.get(i);
			
			g2d.fillRoundRect((int) item.getX(), (int) item.getY(),
					(int) item.getWidth(), (int) item.getHeight(), 30, 30);
		}
		
		g2d.setColor(Color.black);
		g2d.setFont(new Font("Avenir", Font.PLAIN, 30));
		
		g2d.drawString("Home",
				(int) (menuItems.get(0).getX() + menuItems.get(0).getWidth()/2.0
				- g2d.getFontMetrics().stringWidth("Home")/2.0),
						(int) (menuItems.get(0).getY() + menuItems.get(0).getHeight()/2.0
						+ g2d.getFontMetrics().getAscent()/2.0) - 5);
		
		g2d.drawString("Resume",
				(int) (menuItems.get(1).getX() + menuItems.get(1).getWidth()/2.0
				- g2d.getFontMetrics().stringWidth("Resume")/2.0),
						(int) (menuItems.get(1).getY() + menuItems.get(1).getHeight()/2.0
						+ g2d.getFontMetrics().getAscent()/2.0) - 5);

		g2d.drawString("Restart",
				(int) (menuItems.get(2).getX() + menuItems.get(2).getWidth()/2.0
				- g2d.getFontMetrics().stringWidth("Restart")/2.0),
						(int) (menuItems.get(2).getY() + menuItems.get(2).getHeight()/2.0
						+ g2d.getFontMetrics().getAscent()/2.0) - 5);
		
		g2d.drawString("Settings",
				(int) (menuItems.get(3).getX() + menuItems.get(3).getWidth()/2.0
				- g2d.getFontMetrics().stringWidth("Settings")/2.0),
						(int) (menuItems.get(3).getY() + menuItems.get(3).getHeight()/2.0
						+ g2d.getFontMetrics().getAscent()/2.0) - 5);
		
		g2d.drawString("Exit",
				(int) (menuItems.get(4).getX() + menuItems.get(4).getWidth()/2.0
				- g2d.getFontMetrics().stringWidth("Exit")/2.0),
						(int) (menuItems.get(4).getY() + menuItems.get(4).getHeight()/2.0
						+ g2d.getFontMetrics().getAscent()/2.0) - 5);
	}
	
	public void drawRulesButton(Graphics2D g2d) {
		
		//g2d.setColor(new Color(150, 0, 0));
		g2d.setFont(new Font("Aubrey", Font.PLAIN, 22));
		
		int buttonWidth = g2d.getFontMetrics().stringWidth("Rules");
		int buttonHeight = g2d.getFontMetrics().getAscent();
		int buttonX = this.getWidth() - buttonWidth - 40;
		int buttonY = this.getHeight() - 40;
		
		
		rulesButton = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
		
		g2d.setColor(rulesButtonColor);
		g2d.fillRoundRect(buttonX - 7, buttonY - 4, buttonWidth + 15, buttonHeight + 10, 10, 10);
		g2d.setColor(Color.black);
		g2d.drawRoundRect(buttonX - 7, buttonY - 4, buttonWidth + 15, buttonHeight + 10, 10, 10);
		
		//g2d.draw(rulesButton);
		
		int xValue = buttonX;
		int yValue = buttonY - 3;
		
		g2d.setColor(Color.black);
		g2d.drawString("Rules", xValue, yValue + buttonHeight);

	}
	
	public void dimPanel() {
		g2d.setColor(new Color(0, 0, 0, 150));
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
	}


	/*
	 * Other methods
	 */
	
	private void initializeMarbles() {
		
		int identifier = 0;
		
		for (int i=0; i<4; i++) {
			ArrayList<Marble> marbleGroup = new ArrayList<>();
			for (int j=0; j<4; j++) {
				Marble m = new Marble(holePoints, marbleStartingPoints[identifier][j], identifier);
				marbles.add(m);
				marbleGroup.add(m);
			}
			players[identifier].updateMarbles(marbleGroup);
			identifier++;
		}
		
	}

	public void reshuffleDeck() {
		deck = new Deck(true);
		usedCards.clear();
	}

	public void deal() {
		
		int dealStartIndex = 3 - dealCount;
		
		if (dealStartIndex < 0)
			dealStartIndex += 4;
		
		if ((dealCount + 1) % 5 == 0 && dealCount != 0) {
			
			for (int i=dealStartIndex; i<dealStartIndex + 4; i++) {
				if (i < 0) {
					players[i + 4].updateHand(deck.dealHand(6));
				}
				else {
					players[i % 4].updateHand(deck.dealHand(6));
				}
			}
			
			dealCount++;
		}
		else {

			for (int i=dealStartIndex; i>dealStartIndex - 4; i--) {
				if (i < 0) {
					players[i + 4].updateHand(deck.dealHand(5));
				}
				else {
					players[i % 4].updateHand(deck.dealHand(5));
				}
				
			}
			
			// Commented code below is used for giving players specific hands
			// for testing purposes
			
//			player.getHand().clear();
//			player.getHand().add(new Card(Face.ONE));
//			player.getHand().add(new Card(Face.ONE));
//			player.getHand().add(new Card(Face.FOUR));
//			player.getHand().add(new Card(Face.FIVE));
//			player.getHand().add(new Card(Face.TRICKSTER));
//
//			players[0].getHand().clear();
//			players[0].getHand().add(new Card(Face.ONE));
//			players[0].getHand().add(new Card(Face.ONE));
//			players[0].getHand().add(new Card(Face.THREE));
//			players[0].getHand().add(new Card(Face.ENGEL));
//			players[0].getHand().add(new Card(Face.FIVE));
//
//			players[1].getHand().clear();
//			players[1].getHand().add(new Card(Face.ONE));
//			players[1].getHand().add(new Card(Face.ONE));
//			players[1].getHand().add(new Card(Face.FOUR));
//			players[1].getHand().add(new Card(Face.FIVE));
//			players[1].getHand().add(new Card(Face.THREE));
//
//			players[2].getHand().clear();
//			players[2].getHand().add(new Card(Face.ONE));
//			players[2].getHand().add(new Card(Face.ONE));
//			players[2].getHand().add(new Card(Face.THREE));
//			players[2].getHand().add(new Card(Face.FOUR));
//			players[2].getHand().add(new Card(Face.FIVE));
	
			dealCount++;
		}
		
	}

	private void resizeHand() {
		int widthOfCardsTogether = player.getHand().size()*60;
		int x = (int) (this.getWidth()/2.0 - widthOfCardsTogether/2.0);
		int y = this.getHeight() - 80;

		for (int i=0; i<player.getHand().size(); i++) {
			cardPositions[i] = new Point(x, y);
			player.getHand().get(i).setX(x);
			player.getHand().get(i).setY(y);

			x += 60;
		}
	}

	public void checkSelectedMarble(Point p) {
		int playerNumber;
		if (players[3].hasAllMarblesIn())
			playerNumber = 1;
		else
			playerNumber = 3;
		
		if (tricksterPlayed) {
			boolean marbleFound = false;
			for (int i=0; i<4; i++) {
				for (int j=0; j<players[i].getMarbles().size(); j++) {
					Marble m = players[i].getMarbles().get(j);
					if (m.contains(p) && m.isOut() && !m.isIn()) {
						if (selectedMarble != null && selectedMarble2 == null) {
							if (selectedMarble.equals(m)) {
								selectedMarble = null;
							}
							else {
								selectedMarble2 = m;
								marbleFound = true;
							}
						}
						else if (selectedMarble == null && selectedMarble2 == null) {
							selectedMarble = m;
							marbleFound = true;
						}
						break;
					}
				}
				if (marbleFound) break;
			}
		}
		
		else {

			for (int i=0; i<players[playerNumber].getMarbles().size(); i++) {
				Marble m = players[playerNumber].getMarbles().get(i);
				if (m.contains(p)) {
					selectedMarble = m;
					break;
				}
			}
		}
	}
	
	public boolean allHandsEmpty() {
		if (players[0].getHand().isEmpty() && players[1].getHand().isEmpty() 
				&& players[2].getHand().isEmpty() && players[3].getHand().isEmpty()) {
			return true;
		}
		return false;
	}

	public void endRound() {
		if (usedCards.size() == 104) {
			reshuffleDeck();
		}

		deal();
		drawDeck();
		resizeHand();
		
		for (int i=0; i<4; i++) {
			players[i].setTurnCount(0);
		}
		
		JOptionPane.showMessageDialog(this,  null, "Round " + dealCount, JOptionPane.INFORMATION_MESSAGE, icon);
		
		repaint();
	}


	// this method updates the board array and performs 'kills'
	public void updateMarblePositionOnBoard(Marble marble) {

		if (!marble.isIn() && marble.getLastHoleNumber() == -1) {
			
			if (marblesOnBoard[marble.getHoleNumber()] != null) {
				Marble marbleInDestination = marblesOnBoard[marble.getHoleNumber()];
				marbleInDestination.setPosition(marbleInDestination.getHomeStartPosition());
				marbleInDestination.setMoveCount(0);
				players[marbleInDestination.getIdentifier()].setNumOfMarblesIn(
						players[marbleInDestination.getIdentifier()].getNumOfMarblesIn() - 1);
			}
			marblesOnBoard[marble.getHoleNumber()] = marble;
		}

		else if (!marble.isIn() && marble.isOut()) {
			
			if (marblesOnBoard[marble.getHoleNumber()] != null) {
				Marble marbleInDestination = marblesOnBoard[marble.getHoleNumber()];
				marbleInDestination.setPosition(marbleInDestination.getHomeStartPosition());
				marbleInDestination.setMoveCount(0);
				players[marbleInDestination.getIdentifier()].setNumOfMarblesIn(
						players[marbleInDestination.getIdentifier()].getNumOfMarblesIn() - 1);
			}
			
			// if seven is played
			int distanceToDest = marble.getLastHoleNumber() - marble.getHoleNumber();
			if (distanceToDest < 0) distanceToDest += 64;
			if (distanceToDest == 7) {
				for (int i=marble.getHoleNumber() + 1; i<marble.getLastHoleNumber(); i++) {
					if (i < 0) {
						i += 64;
					}
					if (marblesOnBoard[i % 64] != null) {
						marblesOnBoard[i % 64].setPosition(marblesOnBoard[i].getHomeStartPosition());
						marblesOnBoard[i % 64].setMoveCount(0);
						players[marblesOnBoard[i % 64].getIdentifier()].setNumOfMarblesIn(
								players[marblesOnBoard[i % 64].getIdentifier()].getNumOfMarblesIn() - 1);
						marblesOnBoard[i % 64] = null;
					}
				}
			}
			
			marblesOnBoard[marble.getHoleNumber()] = marble;
			marblesOnBoard[marble.getLastHoleNumber()] = null;
		}
		
		else if (marble.isIn() && marble.getLastHoleNumber() >= 0) {
			marblesOnBoard[marble.getLastHoleNumber()] = null;
		}

	}

	
	//This method checks the marblesOnBoard array to see if the given marble can move to the given position
	//by looking at the card value and current marble position
	
	public boolean canMoveToPosition(Marble m, int cardValue) {
		
		if (m.getIdentifier() < 0 || m.getIdentifier() >= 4) {
			throw new IllegalArgumentException("Marble has invalid identifier. Error 0.");
		}

		int destinationHoleNumber = m.getHoleNumber() - cardValue;
		
		int distanceToDest = m.getHoleNumber() - destinationHoleNumber;

		if (distanceToDest < 0) {
			distanceToDest += 64;
		}

		if (!m.isOut() && (cardValue !=1 && cardValue !=13)) return false;
		
		if (m.isOut() && !m.isIn()) {

			if (cardValue != 4 && cardValue != 7 && cardValue != 11 && cardValue <=13) {

				int startHoleNumber = players[m.getIdentifier()].getStartHoleNumber();
				
				// if marble is going in
				if ((m.getHoleNumber() - cardValue) <= startHoleNumber - 1 
						&& (m.getHoleNumber() - cardValue) >= (startHoleNumber - 4) 
						&& m.isGoingIn()) {
					
					int distanceToStart = m.getHoleNumber() - startHoleNumber;
					
					for (int i=m.getHoleNumber() - 1; i >= startHoleNumber; i--) {
						if (i < 0 || i >= 64) {
							throw new IllegalArgumentException("Index out of range (0-63). Error 1.");
						}
						if (marblesOnBoard[i] != null) {
							return false;
						}
					}
					
					for (int i=0; i < cardValue - distanceToStart; i++){
						if (i < 0 || i >= 4) {
							throw new IllegalArgumentException("Index out of range (0-3). Error 2.");
						}
						if (players[m.getIdentifier()].getMarblesInHome()[i] != null) {
							return false;
						}
					}
				}

				int startPoint = m.getHoleNumber() - 1;
				if (startPoint < 0) {
					startPoint += 64;
				}
				for (int i=startPoint; i>startPoint - distanceToDest + 1; i--) {

					int j = i;
					if (j < 0) {
						j += 64;
					}
					if (j < 0 || j >= 64) {
						throw new IllegalArgumentException("Index out of range (0-63). Error 3.");
					}
					if (marblesOnBoard[j % 64] != null) {
						return false;
					}
				}
			}

			else if (cardValue == 4) {

				int distanceFromStart = Math.abs(m.getStartHoleNumber() - m.getHoleNumber());
				
				if (distanceFromStart < 4 && distanceFromStart > 0) {
					
					for (int i=m.getHoleNumber() + 1; i<=m.getStartHoleNumber(); i++) {
						if (i < 0 || i >= 64) {
							throw new IllegalArgumentException("Index out of range (0-63). Error 4.");
						}
						if (marblesOnBoard[i] != null)
							return false;
					}
					
					for (int i=0; i< 4 - distanceFromStart; i++) {
						if (i < 0 || i >= 4) {
							throw new IllegalArgumentException("Index out of range (0-3). Error 5.");
						}
						if (players[m.getIdentifier()].getMarblesInHome()[i] != null)
							return false;
					}
				}
				
				else {

					for (int i=m.getHoleNumber() + 1; i<m.getHoleNumber() + 4; i++) {
						int index = i;
						if (index < 0)
							index += 64;
						if (index % 64 < 0 || index % 64 >= 64) {
							throw new IllegalArgumentException("Index out of range (0-63). Error 6.");
						}
						if (marblesOnBoard[index % 64] != null) {
							return false;
						}
					}
				}

			}

			else if (cardValue == 7) {

				int startHoleNumber = players[m.getIdentifier()].getStartHoleNumber();

				if ((m.getHoleNumber() - 7) <= startHoleNumber - 1
						&& (m.getHoleNumber() - 7) >= (startHoleNumber - 4) 
						&& m.isGoingIn()) {
					int distanceToStart = m.getHoleNumber() - startHoleNumber;

					for (int i=0; i < cardValue - distanceToStart; i++){
						if (i < 0 || i >= 4) {
							throw new IllegalArgumentException("Index out of range (0-3). Error 7.");
						}
						if (players[m.getIdentifier()].getMarblesInHome()[i] != null) {
							return false;
						}
					}

				}


			}
			
			else if (cardValue == 11) {
				
				if (!m.isOut() || m.isIn())
					return false;
				
				if (getNumOfMarblesOnBoard() < 2)
					return false;
			}

			// Kreiger conditions
			else if (cardValue == 16) {
				
				// check if marble is out and is not in
				if (!m.isOut() && !m.isIn()) 
					return false;
				
				if (getNumOfMarblesOnBoard() < 2)
					return false;
			}

		}
		
		if (m.isIn()){

			if (cardValue > 3) return false;

			int currentHomeBaseHole = -1;

			for (int i=0; i<4; i++) {
				currentHomeBaseHole = i;
				if (i < 0 || i >= 4) {
					throw new IllegalArgumentException("Index out of range (0-3). Error 8.");
				}
				if (m.getPosition().equals(players[m.getIdentifier()].getHomeBaseHoles()[i])) {
					break;
				}
			}

			int destinationHole = currentHomeBaseHole + cardValue;
			
			if (destinationHole > 3) return false;

			for (int i=currentHomeBaseHole + 1; i<=destinationHole; i++) {
				if (i < 0 || i >= 4) {
					throw new IllegalArgumentException("Index out of range (0-3). Error 9.");
				}
				if (players[m.getIdentifier()].getMarblesInHome()[i] != null) {
					return false;
				}
			}
		}

		return true;
	}
	
	private void checkForWinner() {
		int identifier = -1;
		for (int i=0; i<4; i++) {
			identifier = i;
			if (players[identifier].hasAllMarblesIn() 
					&& players[identifier].getPartner().hasAllMarblesIn()) {
				gameOver = true;
			}
		}
	}
	
	private void updatePartner(int identifier) {
	
		players[identifier].setStartHoleNumber(players[(identifier + 2) % 4].getStartHoleNumber());
		players[identifier].setHomeBaseHoles(players[(identifier + 2) % 4].getHomeBaseHoles());
		players[identifier].setMarblesInHome(players[(identifier + 2) % 4].getMarblesInHome());
		players[identifier].setMarblesOut(players[(identifier + 2) % 4].getMarblesOut());
		players[identifier].setMarblesOut(players[(identifier + 2) % 4].getNumOfMarblesOut());
		players[identifier].setStartPosition(players[(identifier + 2) % 4].getStartPosition());
		players[identifier].updateMarbles(players[(identifier + 2) % 4].getMarbles());
		
	}

	public void restartGame() {
		deck = new Deck(true);
		marblesOnBoard = new Marble[64];
		marbles = new ArrayList<>();
		usedCards = new ArrayList<>();

		for (int i=0; i<4; i++) {
			players[i].reset();
		}

		dealCount = 0;

		isPaused = false;
		bgMusicClip.stop();

		try {
			playBackgroundMusic();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
			e1.printStackTrace();
		}

		seconds = 0;

		board.setStartCircleColor(3);

		selectedMarble = null;

		initializeMarbles();
		deal();
		resizeHand();
		repaint();
	}
	
	public void save() throws IOException{
 
		LocalDateTime now = LocalDateTime.now();
		
		String fileName = now.toString().substring(0, 19);
		
		ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fileName + ".ser"));

		os.writeObject(marblesOnBoard);
		
		os.writeObject(players);
		
		os.close();
		
	}
	
	
	/*
	 *  Audio methods
	 */
	
	public void playCardSoundEffect() 
			throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		File file = new File("Sound_Effects/Card_sound.wav");
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
		Clip clip = AudioSystem.getClip();
		clip.open(audioStream);

		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
		gainControl.setValue(20f * (float) Math.log10(1.0f)); // volume must  be between 0 and 1

		clip.start();

	}
	
	public void playBackgroundMusic() 
			throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		File file = new File("Sound_Effects/Bg_music.wav");
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
		bgMusicClip = AudioSystem.getClip();
		bgMusicClip.open(audioStream);
		FloatControl gainControl = (FloatControl) bgMusicClip.getControl(FloatControl.Type.MASTER_GAIN);        
		gainControl.setValue(20f * (float) Math.log10(0.2f)); // volume must  be between 0 and 1
		bgMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
		
		bgMusicClip.start();
	}

	
    /*
     *  Mouse Listeners
     */

	 public class ClickListener extends MouseAdapter{

		@Override
		 public void mousePressed(MouseEvent e) {
			 if (e.getButton() == MouseEvent.BUTTON1) {
				 
				 Point p = e.getPoint();
				 
				 if (rulesButton.contains(p)) {
					 
					 rulesButtonColor = new Color(150, 150, 150);
				 }
				 
				 if (pauseButton.contains(p)) {
					 if (isPaused) {
						 bgMusicClip.start();
						 isPaused = false;
					 }
					 else {
						 bgMusicClip.stop();
						 isPaused = true;
					 }
				 }
				 
				 else if (isPaused) {
					 
					 // Home button
					 if (menuItems.get(0).contains(p)) {
						 terminated = true;
						 window.remove(gamePanel);
						 window.addNewHomePanel();
						 window.validate();
						 window.repaint();
						 
					 }
					 
					// Resume button
					 else if (menuItems.get(1).contains(p)) {
						 isPaused = false;
						 bgMusicClip.start();
					 }

					 // Restart Button
					 else if (menuItems.get(2).contains(p)) {

						 String[] options = {"Restart", "Cancel"};
						 int response = JOptionPane.showOptionDialog(null, "Are you sure you want to restart?", null, 
								 JOptionPane.CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon, options, JOptionPane.CANCEL_OPTION);

						 if (response == 0) {
						 
							 restartGame();
							 
						 }

					 }

					 // Settings button
					 else if (menuItems.get(3).contains(p)) {
						JOptionPane.showConfirmDialog(
							null, 
							"Settings menu not yet implemented.", 
							"Not implemented", 
							JOptionPane.OK_CANCEL_OPTION);
					 }

					 // Exit button
					 else if (menuItems.get(4).contains(p)) {
						 String[] options = {"Exit without saving", "Save & Exit"};
						 int response = JOptionPane.showOptionDialog(null, "Would you like to save your progress?", null, 
								 JOptionPane.CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon, options, JOptionPane.OK_OPTION);
						 
						 if (response == 1) {
							JOptionPane.showConfirmDialog(
								null, 
								"Saving feature not yet implemented.", 
								"Not implemented", 
								JOptionPane.OK_CANCEL_OPTION);
							//  try {
							// 	save();
							// 	JOptionPane.showMessageDialog(gamePanel, "Successfully saved.", "Saved",
							// 			JOptionPane.INFORMATION_MESSAGE, icon);
							// 	System.exit(0);
							// } catch (IOException e1) {
							// 	e1.printStackTrace();
							// }
						 }
						 else if (response == 0) {
							 window.dispose();
							 System.exit(0);
						 }
					 }
					 
				 }
				 
				 else if (yesButton.contains(p) && isBurning) {
					 
					 player.setLastCardPlayed(selectedCard.getFace().getValue());
					 
					 player.getHand().remove(selectedCard);
					 usedCards.add(selectedCard);
					 canPlay = false;
					 
					 playedAtSeconds = seconds;
					 player.setTurnCount(player.getTurnCount() + 1);

					 for (int j=0; j<player.getHand().size(); j++) {
						 Card card = player.getHand().get(j);
						 card.setCardColor(new Color(180, 180, 180));
					 }
					 
					 board.setStartCircleColor(2);
					 
					 resizeHand();
					 repaint();
					 
					 try {
						playCardSoundEffect();
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
						e1.printStackTrace();
					}
					 
					 isBurning = false;
				 }
				 
				 else if (noButton.contains(p)) {
					 isBurning = false;
					 repaint();
				 }
				 
				 if (!terminated) {
					 checkSelectedMarble(p);
					 drawMarbles(g2d);
					 repaint();
				 }
				 
				 
				 
				 // if marbles are chosen for trickster
				 if (selectedMarble2 != null && !terminated) {
					 
					 Point tempPoint = selectedMarble.getPosition();
					 int tempHoleNumber = selectedMarble.getHoleNumber();
					 
					 selectedMarble.setPosition(selectedMarble2.getPosition());
					 marblesOnBoard[selectedMarble2.getHoleNumber()] = selectedMarble;
					 
					 selectedMarble2.setPosition(tempPoint);
					 marblesOnBoard[tempHoleNumber] = selectedMarble2;
					 selectedMarble = null;
					 selectedMarble2 = null;
					 
					 tricksterPlayed = false;
					 
					 usedCards.add(selectedCard);
					 players[3].getHand().remove(selectedCard);
					 resizeHand();
					 
					 resizeHand();
					 
					 playedAtSeconds = seconds;
					 players[3].setTurnCount(players[3].getTurnCount() + 1);

					 canPlay = false;
					 for (int j=0; j<player.getHand().size(); j++) {
						 Card card = player.getHand().get(j);
						 card.setCardColor(new Color(180, 180, 180));
					 }
					 
					 board.setStartCircleColor(2);
					 
					 repaint();
				 }
				 
//				 if (tricksterPlayed)
//					 tricksterPlayed = false;

				 if (selectedMarble == null && player.getNumOfMarblesOut() == 0 && !terminated) {
					 selectedMarble = players[3].getMarbles().get(0);
				 }
				 else if (selectedMarble == null && canPlay) {
					 JOptionPane.showMessageDialog(null, "Please select a marble.", null, JOptionPane.INFORMATION_MESSAGE, icon);
				 }

				 if (canPlay && selectedMarble != null && !isPaused && !terminated) {
					 for (int i=0; i<player.getHand().size(); i++) {

						 if (player.getHand().get(i).contains(p)) {

							 Card c = player.getHand().get(i);
							 selectedCard = c;
							 int cardValue = c.getFace().getValue();

							 if (canMoveToPosition(selectedMarble, cardValue)) {
								 
								 // if trickster is played
								 if (cardValue == 11) {
									 tricksterPlayed = true;
									 break;
								 }
								 
								 // if narr is played
								 else if (cardValue == 18) {
									 players[3].getHand().remove(i);
									 
									 ArrayList<Card> h1 = players[0].getHand();
									 ArrayList<Card> h2 = players[1].getHand();
									 ArrayList<Card> h3 = players[2].getHand();
									 ArrayList<Card> h4 = players[3].getHand();

									 players[0].updateHand(h4);
									 players[1].updateHand(h1);
									 players[2].updateHand(h2);
									 players[3].updateHand(h3);

									 resizeHand();

									 repaint();
									 break;
								 }
								 
								 else {

									 player.play(selectedMarble, c);
									 
									 player.setLastCardPlayed(cardValue);

									 playedAtSeconds = seconds;

									 resizeHand();

									 canPlay = false;
									 for (int j=0; j<player.getHand().size(); j++) {
										 Card card = player.getHand().get(j);
										 card.setCardColor(new Color(180, 180, 180));
									 }
									 
									 board.setStartCircleColor(2);

									 checkForWinner();

									 repaint();
									 break;
								 }
							 }
							 
							 else {
								 isBurning = true;
								 
								 break;
							 }
							 
						 }

					 }
					 
				 }
				 
			 }

		 }

		 @Override
    	public void mouseReleased(MouseEvent e) {
    		if (rulesButton.contains(e.getPoint())) {
    			rulesButtonColor = new Color(220, 220, 220);
    			new RulesWindow(gamePanel);
    		}
    	}

    }
    
    public class DragListener extends MouseMotionAdapter{
    	@Override
    	public void mouseMoved(MouseEvent e) {
    		if (canPlay && !gameOver && !isPaused && !terminated) {
    			for (int i=0; i<player.getHand().size(); i++) {
    				Card card = player.getHand().get(i);
    				if (card.contains(e.getPoint())) {
    					card.setWidth(cardWidth + 8);
    					card.setHeight(cardHeight + 8);
    					card.setX(cardPositions[i].x - 2);
    					card.setY(680);
    					card.setNumberFontSize(16);
    				}
    				else {
    					card.setWidth(cardWidth);
    					card.setHeight(cardHeight);
    					card.setY(692);
    					card.setNumberFontSize(15);
    					if (card.getX() != cardPositions[i].x)
    						card.setX(cardPositions[i].x + 2);
    				}
    				
    			}
    			repaint();
    		}

    	}
    	
    	@Override
    	public void mouseDragged(MouseEvent e) {
    		if (rulesButton.contains(e.getPoint())) {
    			rulesButtonColor = new Color(150, 150, 150);
    		}
    	}
	}

    
    /*
     * Action listener methods
     */
    
    @Override
    public void actionPerformed(ActionEvent e) {
    	if (e.getSource() == timer) {

    		if (!isPaused && !terminated)
    			seconds++;

    		if (!gameOver && !isPaused && !terminated) {

    			if ((dealCount - 1) % 4 == 0) {

    				if ((players[3].getHand().size() == 5 && dealCount == 1) || players[3].getHand().size() == 6) {
    					canPlay = true;
    					for (int i=0; i<player.getHand().size(); i++) {
    						Card card = player.getHand().get(i);
    						card.setCardColor(Color.white);
    					}
    				}

    				boolean lastTurnBug = false;

    				int turnNumber = players[3].getTurnCount();

    				// black's turn
    				if (cpu[2].getTurnCount() == (turnNumber - 1) && !canPlay
    						&& (seconds == playedAtSeconds + cpuWaitTime) && turnNumber != 0) {
    					cpu[2].play();
    					board.setStartCircleColor(1);
    					
    					if (players[2].hasAllMarblesIn()) {
    						updatePartner(2);
    					}
    					
    					checkForWinner();
    				}

    				// blue's turn
    				else if (cpu[1].getTurnCount() == (turnNumber - 1) && !canPlay
    						&& (seconds == playedAtSeconds + 2*cpuWaitTime) && turnNumber != 0) {
    					cpu[1].play();
    					board.setStartCircleColor(0);
    					
    					if (players[1].hasAllMarblesIn()) {
    						updatePartner(1);
    					}
    					
    					checkForWinner();
    				}

    				// red's turn
    				else if (cpu[0].getTurnCount() == (turnNumber - 1)
    						&& (seconds == playedAtSeconds + 3*cpuWaitTime) && !canPlay && turnNumber != 0
    						&& !lastTurnBug) {
    					cpu[0].play();
    					board.setStartCircleColor(3);
    					
    					if (players[0].hasAllMarblesIn()) {
    						updatePartner(0);
    					}
    					
    					checkForWinner();

    					canPlay = true;

    					for (int i=0; i<player.getHand().size(); i++) {
    						Card card = player.getHand().get(i);
    						card.setCardColor(Color.white);
    					}

    					if (allHandsEmpty()) {
    						canPlay = false;
    						endRound();
    						lastTurnBug = true;
    					}
    				}
    			}

    			else if ((dealCount - 1) % 4 == 1) {

    				int turnNumber = players[3].getTurnCount();
    				boolean lastTurnBug = false; // boolean needed to stop black from playing after cards are dealt

    				if (allHandsEmpty()) {
    					endRound();
    					lastTurnBug = true;
    				}

    				// black's first turn
    				if (cpu[2].getTurnCount() == 0 && !lastTurnBug) {
    					playedAtSeconds = seconds;
    					cpu[2].play();
    					board.setStartCircleColor(1);
    					
    					if (players[2].hasAllMarblesIn()) {
    						updatePartner(2);
    					}
    					
    					checkForWinner();

    				}

    				// black's turn
    				else if (cpu[2].getTurnCount() == turnNumber 
    						&& (seconds == playedAtSeconds + cpuWaitTime) && cpu[2].getTurnCount() != 0) {
    					cpu[2].play();
    					board.setStartCircleColor(1);
    					
    					if (players[2].hasAllMarblesIn()) {
    						updatePartner(2);
    					}
    					
    					checkForWinner();
    				}

    				// blue's turn
    				else if (cpu[1].getTurnCount() == turnNumber
    						&& (seconds == playedAtSeconds + 2*cpuWaitTime)) {
    					cpu[1].play();
    					board.setStartCircleColor(0);
    					
    					if (players[1].hasAllMarblesIn()) {
    						updatePartner(1);
    					}
    					
    					checkForWinner();
    				}

    				// red's turn
    				else if (cpu[0].getTurnCount() == turnNumber
    						&& (seconds == playedAtSeconds + 3*cpuWaitTime)) {
    					cpu[0].play();
    					board.setStartCircleColor(3);
    					
    					if (players[0].hasAllMarblesIn()) {
    						updatePartner(0);
    					}
    					
    					checkForWinner();
    					canPlay = true;

    					for (int i=0; i<player.getHand().size(); i++) {
    						Card card = player.getHand().get(i);
    						card.setCardColor(Color.white);
    					}
    				}

    			}

    			else if ((dealCount - 1) % 4 == 2) {

    				int turnNumber = players[3].getTurnCount();

    				// blue's first turn
    				if (cpu[1].getTurnCount() == 0) {
    					playedAtSeconds = seconds;
    					cpu[1].play();
    					board.setStartCircleColor(0);
    					
    					if (players[1].hasAllMarblesIn()) {
    						updatePartner(1);
    					}
    					
    					checkForWinner();
    				}

    				// black's turn
    				else if (cpu[2].getTurnCount() == (turnNumber-1)
    						&& (seconds == playedAtSeconds + cpuWaitTime) && turnNumber != 0) {
    					cpu[2].play();
    					board.setStartCircleColor(1);
    					
    					if (players[2].hasAllMarblesIn()) {
    						updatePartner(2);
    					}
    					
    					checkForWinner();

    					if (allHandsEmpty()) {
    						endRound();
    					}
    				}
    				// blue's turn
    				else if (cpu[1].getTurnCount() == turnNumber
    						&& (seconds == playedAtSeconds + 2*cpuWaitTime) && turnNumber != 0) {
    					cpu[1].play();
    					board.setStartCircleColor(0);
    					
    					if (players[1].hasAllMarblesIn()) {
    						updatePartner(1);
    					}
    					
    					checkForWinner();
    				}
    				// red's turn
    				else if (cpu[0].getTurnCount() == turnNumber
    						&& (seconds == playedAtSeconds + 3*cpuWaitTime)) {
    					cpu[0].play();
    					board.setStartCircleColor(3);
    					
    					if (players[0].hasAllMarblesIn()) {
    						updatePartner(0);
    					}
    					
    					checkForWinner();

    					canPlay = true;

    					for (int i=0; i<player.getHand().size(); i++) {
    						Card card = player.getHand().get(i);
    						card.setCardColor(Color.white);
    					}
    				}

    			}

    			else if ((dealCount - 1) % 4 == 3) {

    				boolean lastTurnBug = false;

    				int turnNumber = players[3].getTurnCount();

    				// red's first turn
    				if (cpu[0].getTurnCount() == 0 && !lastTurnBug) {
    					cpu[0].play();
    					board.setStartCircleColor(3);
    					
    					if (players[0].hasAllMarblesIn()) {
    						updatePartner(0);
    					}
    					
    					checkForWinner();
    					
    					canPlay = true;

    					for (int i=0; i<player.getHand().size(); i++) {
    						Card card = player.getHand().get(i);
    						card.setCardColor(Color.white);
    					}
    				}

    				// black's turn
    				else if (cpu[2].getTurnCount() == (turnNumber - 1) && !canPlay
    						&& (seconds == playedAtSeconds + cpuWaitTime) && turnNumber != 0) {
    					cpu[2].play();
    					board.setStartCircleColor(1);
    					
    					if (players[2].hasAllMarblesIn()) {
    						updatePartner(2);
    					}
    					
    					checkForWinner();
    				}

    				// blue's turn
    				else if (cpu[1].getTurnCount() == (turnNumber - 1) && !canPlay
    						&& (seconds == playedAtSeconds + 2*cpuWaitTime) && turnNumber != 0) {
    					cpu[1].play();
    					board.setStartCircleColor(0);
    					
    					if (players[1].hasAllMarblesIn()) {
    						updatePartner(1);
    					}
    					
    					checkForWinner();

    					if (allHandsEmpty()) {
    						endRound();
    						lastTurnBug = true;

    						canPlay = true;

    						for (int i=0; i<player.getHand().size(); i++) {
    							Card card = player.getHand().get(i);
    							card.setCardColor(Color.white);
    						}
    					}
    				}

    				// red's turn
    				else if (cpu[0].getTurnCount() == turnNumber
    						&& (seconds == playedAtSeconds + 3*cpuWaitTime) && !canPlay && turnNumber != 0
    						&& !lastTurnBug) {
    					cpu[0].play();
    					board.setStartCircleColor(3);
    					
    					if (players[0].hasAllMarblesIn()) {
    						updatePartner(0);
    					}
    					
    					checkForWinner();

    					canPlay = true;

    					for (int i=0; i<player.getHand().size(); i++) {
    						Card card = player.getHand().get(i);
    						card.setCardColor(Color.white);
    					}
    				}
    			}
    		}

		}
	}
	
	
	/*
	 * Getters and setters
	 */

	public ArrayList<Card> getUsedCards() {
		return usedCards;
	}

	public Point[] getHolePoints() {
		return holePoints;
	}
	
	public Point[] getStartPositions() {
		return startPositions;
	}

	public Marble[] getMarblesOnBoard() {
		return marblesOnBoard;
	}


	public Player[] getPlayers() {
		return players;
	}

	public int getNumOfMarblesOnBoard() {
		int marbleCount = 0;
		for (int i=0; i<64; i++) {
			if (marblesOnBoard[i] != null)
				marbleCount++;
		}
		return marbleCount;
	}
	
	public ArrayList<Marble> getMarbles(){
		return marbles;
	}
	
	public boolean isTerminated() {
		return terminated;
	}
	
}
