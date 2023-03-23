package gameplay;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

import pieces.Card;
import pieces.Marble;
import structure.GamePanel;

public abstract class Player implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5420612696531751213L;


	private static int lastCardPlayed;
	
	
	private GamePanel gamePanel;

	private int identifier;
	private static int lastIdentifier;
	
	private Player partner;

	protected ArrayList<Card> hand;
	private ArrayList<Card> orderedHand;
	private ArrayList<Marble> marbles;
	private ArrayList<Marble> marblesOut;
	
	private int numOfMarblesOut;
	private int numOfMarblesIn;
	private Point[] holePoints;
	private Point[] homeBaseHoles;
	private Point startPosition;
	private int startHoleNumber;
	private Marble[] marblesInHome;
	private boolean allMarblesIn;
	
	private int turnCount;

	public Player(GamePanel gamePanel) {
		
		this.gamePanel = gamePanel;
		
		identifier = getNewIdentifier();
		
		if (identifier > 3) {
			identifier = 0;
			lastIdentifier = 1;
		}

		holePoints = new Point[64];
		hand = new ArrayList<>();
		marbles = new ArrayList<>();
		marblesOut = new ArrayList<>();

		startHoleNumber = identifier * 16;
		
		setMarblesInHome(new Marble[4]);
	}

	private int getNewIdentifier() {
		return lastIdentifier++;
	}

	public void moveMarble(Marble m, int cardValue) {

		if ((cardValue == 1 || cardValue == 13) && !m.isOut() && !m.isIn()) {
			m.setPosition(startPosition);
			
			if (allMarblesIn) {
				partner.marblesOut.add(m);
				partner.numOfMarblesOut++;
			}
			else {
				marblesOut.add(m);
				numOfMarblesOut++;
			}
			
			gamePanel.updateMarblePositionOnBoard(m);
		}

		else if (cardValue != 11  && cardValue != 4
				&& cardValue <=13 && m.isOut()) {
			
			// if marble is in already
			if (m.isIn() && cardValue < 4) {
				int currentHomeBaseHole = -1;
				for (int i=0; i<4; i++) {
					currentHomeBaseHole = i;
					if (m.getPosition().equals(homeBaseHoles[i])) {
						break;
					}
				}
				int destinationHole = currentHomeBaseHole + cardValue;

				m.setPosition(homeBaseHoles[destinationHole]);
				m.setHoleNumberInHome(destinationHole);
				
				updateMarblePositionsInHome(m, destinationHole);
				gamePanel.repaint();
			}
			
			// if marble can go in
			else if ((m.getHoleNumber() - cardValue) <= startHoleNumber - 1 
					&& (m.getHoleNumber() - cardValue) >= (startHoleNumber - 4) 
					&& m.isGoingIn() && m.getHoleNumber() >= startHoleNumber) {
				
				int destinationHole = cardValue - (m.getHoleNumber() - startHoleNumber) - 1;
				
				
				m.setPosition(homeBaseHoles[destinationHole]);
				m.setHoleNumberInHome(destinationHole);
				m.isIn(true);
				
				if (allMarblesIn) {
					partner.marblesInHome[destinationHole] = m;
					partner.numOfMarblesIn++;
				}
				else {
					marblesInHome[destinationHole] = m;
					numOfMarblesIn++;
				}
				
				updateMarblePositionsInHome(m, destinationHole);
				gamePanel.updateMarblePositionOnBoard(m);
			}
			
			// if marble can't go in
			else {
				int index = (m.getHoleNumber() - cardValue) % 64;
				if (index < 0) {
					index += 64;
				}
				
				m.setPosition(holePoints[index]);

				gamePanel.updateMarblePositionOnBoard(m);
			}
		
		}

		else if (cardValue == 4 && m.isOut() && !m.isIn()) {

			// if card can go in
			if ((m.getHoleNumber() >= (startHoleNumber - 3)) 
					&& (m.getHoleNumber() <= startHoleNumber) 
					&& m.isGoingIn()) {
				int destinationHole = (m.getHoleNumber() - (startHoleNumber - 3));
				
				m.setPosition(homeBaseHoles[destinationHole]);
				m.setHoleNumberInHome(destinationHole);
				m.isIn(true);
				
				if (allMarblesIn) {
					partner.numOfMarblesIn++;
				}
				else {
					numOfMarblesIn++;
				}
				
				updateMarblePositionsInHome(m, destinationHole);
				gamePanel.updateMarblePositionOnBoard(m);
			}
			
			// if card can't go in
			else {
				int index = (m.getHoleNumber() + cardValue) % 64;
			
				m.setPosition(holePoints[index]);
				
				gamePanel.updateMarblePositionOnBoard(m);
			}
			
		}
		
		// Tac movement
		else if (cardValue == 14) {
			
			
			
		}
		
		// Thief movement
		else if (cardValue == 15) {
			
		}
		
		// Krieger movement
		else if (cardValue == 16 && m.isOut() && !m.isIn()) {

			Marble marble = null;
			int index = m.getHoleNumber() - 1;
			while (marble == null) {
				if (index < 0) index += 64;
				marble = gamePanel.getMarblesOnBoard()[index];
				index--;
			}
			Point killedMarbleLocation = marble.getPosition();
			
			m.setPosition(killedMarbleLocation);
			
			gamePanel.updateMarblePositionOnBoard(m);
		}
		
		// Engel movement (unfinished)
		else if (cardValue == 17) {
			Player p = gamePanel.getPlayers()[(identifier + 3) % 4];
			for (int i=0; i<4; i++) {
				Marble marble = p.getMarbles().get(i);
				if (!marble.isOut() && !marble.isIn()) {
					marble.setPosition(p.getStartPosition());
					
					gamePanel.updateMarblePositionOnBoard(marble);
					break;
				}
			}
		}
		
		// Narr movement
//		else if (cardValue == 18) {
//			ArrayList<Card> h1 = gamePanel.getPlayers()[0].getHand();
//			ArrayList<Card> h2 = gamePanel.getPlayers()[1].getHand();
//			ArrayList<Card> h3 = gamePanel.getPlayers()[2].getHand();
//			ArrayList<Card> h4 = gamePanel.getPlayers()[3].getHand();
//			
//			gamePanel.getPlayers()[0].updateHand(h4);
//			gamePanel.getPlayers()[1].updateHand(h1);
//			gamePanel.getPlayers()[2].updateHand(h2);
//			gamePanel.getPlayers()[3].updateHand(h3);
//			
//			gamePanel.getPlayers()[m.getIdentifier()].setTurnCount(gamePanel.getPlayers()[m.getIdentifier()].getTurnCount() - 1);
//		}

		
		if (numOfMarblesIn == 4) {
			allMarblesIn = true;
		}
		
		lastCardPlayed = cardValue;
		
	}
	
	public void moveMarble(Marble marble1, Marble marble2, int cardValue) {
		if (cardValue == 11) {
			Point tempPosition = marble1.getPosition();
			int tempHoleNumber = marble1.getHoleNumber();
			marble1.setPosition(marble2.getPosition());
			marble2.setPosition(tempPosition);
			gamePanel.getMarblesOnBoard()[tempHoleNumber] = marble2;
			gamePanel.getMarblesOnBoard()[marble2.getHoleNumber()] = marble1;
		}
		
		lastCardPlayed = cardValue;
	}
	
	public void updateMarblePositionsInHome(Marble marble, int destinationHoleNumber) {
		
		if (marble.isIn() && marble.getLastHoleNumberInHome() != -2) {
			marblesInHome[marble.getLastHoleNumberInHome()] = null;
			marblesInHome[destinationHoleNumber] = marble;
			if (allMarblesIn) {
				partner.getMarblesInHome()[marble.getLastHoleNumberInHome()] = null;
				partner.getMarblesInHome()[destinationHoleNumber] = marble;
			}
		}
		else {
			marblesInHome[destinationHoleNumber] = marble;
			if (allMarblesIn) {
				partner.getMarblesInHome()[destinationHoleNumber] = marble;
			}
		}

	}
	
	
	public ArrayList<Marble> getMarbles() {
		return marbles;
	}

	
	public void updateMarbles(ArrayList<Marble> marbles) {
		this.marbles = marbles;
	}

	
	public ArrayList<Card> getHand() {
		return hand;
	}

	
	public void updateHand(ArrayList<Card> hand) {
		this.hand = hand;
	}
	
	
	public void setMarblesOut(int numOfMarblesOut) {
		this.numOfMarblesOut = numOfMarblesOut;
	}
	
	
	public int getNumOfMarblesOut() {
		return numOfMarblesOut;
	}
	
	
	public void setHolePositions(Point[] holePoints) {
		this.holePoints = holePoints;
		switch (identifier) {
		case 0: startPosition = holePoints[0];
		startHoleNumber = 0;
		break;
		case 1: startPosition = holePoints[16];
		startHoleNumber = 16;
		break;
		case 2: startPosition = holePoints[32];
		startHoleNumber = 32;
		break;
		case 3: startPosition = holePoints[48];
		startHoleNumber = 48;
		}
	}
	
	
	public Point[] getHolePositions() {
		return holePoints;
	}
	
	public void setStartPosition(Point p) {
		startPosition = p;
	}
	
	public Point getStartPosition() {
		return startPosition;
	}
	
	public void setHomeBaseHoles(Point[][] pList) {
		this.homeBaseHoles = pList[identifier];
	}
	
	public void setHomeBaseHoles(Point[] list) {
		homeBaseHoles = new Point[4];
		for (int i=0; i<4; i++){
			homeBaseHoles[i] = list[i];
		}
	}
	
	public Point[] getHomeBaseHoles() {
		return homeBaseHoles;
	}
	
	public GamePanel getGamePanel() {
		return gamePanel;
	}

	public void setGamePanel(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}
	
	public void setStartHoleNumber(int holeNumber) {
		this.startHoleNumber = holeNumber;
	}
	
	public int getStartHoleNumber() {
		return startHoleNumber;
	}

	public ArrayList<Marble> getMarblesOut() {
		return marblesOut;
	}

	public void setMarblesOut(ArrayList<Marble> marblesOut) {
		this.marblesOut = marblesOut;
	}
	
	public int getIdentifier() {
		return identifier;
	}

	public Marble[] getMarblesInHome() {
		return marblesInHome;
	}

	public void setMarblesInHome(Marble[] marblesInHome) {
		this.marblesInHome = marblesInHome;
	}

	public int getTurnCount() {
		return turnCount;
	}

	public void setTurnCount(int turnCount) {
		this.turnCount = turnCount;
	}
	
	public void setNumOfMarblesIn(int numOfMarblesIn) {
		this.numOfMarblesIn = numOfMarblesIn;
	}

	public int getNumOfMarblesIn() {
		return numOfMarblesIn;
	}

	public boolean hasAllMarblesIn() {
		return allMarblesIn;
	}

	public Player getPartner() {
		return partner;
	}

	public void setPartner(Player partner) {
		this.partner = partner;
	}

	public ArrayList<Card> getOrderedHand() {
		
		// insertion sort
		orderedHand = new ArrayList<Card>();
		for (int i=0; i<hand.size(); i++) {
			orderedHand.add(hand.get(i));
		}
		int n = orderedHand.size();
		
		for (int i=1; i<n; i++) {
			Card x = orderedHand.get(i);
			
			int j = i-1;
			while (j >= 0 && orderedHand.get(j).getFace().getValue() > x.getFace().getValue()) {
				orderedHand.set(j+1, orderedHand.get(j));
				j--;
			}
			orderedHand.set(++j, x);
		}
		
		return orderedHand;
	}
	
	public void setLastCardPlayed(int cardValue) {
		lastCardPlayed = cardValue;
	}
	
	public int getLastCardPlayed() {
		return lastCardPlayed;
	}
	
	public void reset() {
		
		marbles = new ArrayList<>();
		marblesOut = new ArrayList<>();
		hand = new ArrayList<>();
		orderedHand = new ArrayList<>();
		
		numOfMarblesOut = 0;
		numOfMarblesIn = 0;
		
		marblesInHome = new Marble[4];
		allMarblesIn = false;
		
		turnCount = 0;
	}
	
}
