package gameplay;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import pieces.Card;
import pieces.Marble;
import structure.GamePanel;

public class Cpu extends Player{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2061504568419967626L;

	private GamePanel gamePanel;
	
	private Marble marble;
	private Marble marble1;
	private Marble marble2;
	private Card card;
	
	private int cardValue;
	
	private boolean cardChosen;
	
	public Cpu(GamePanel gamePanel) {
		super(gamePanel);
		this.gamePanel = gamePanel;

	}

	public void play() {
		
		this.marble = null;
		this.marble1 = null;
		this.marble2 = null;
		
		this.cardChosen = false;
		
		/*
		 *  Go in
		 */
	
		for (int i=0; i<getMarblesOut().size() && !cardChosen; i++) {
			Marble m = getMarblesOut().get(i);

			if (!m.isIn()) {

				for (int j=getOrderedHand().size()-1; j>=0; j--) {
					this.card = getOrderedHand().get(j);
					this.cardValue = card.getFace().getValue();

					int moveDistance = cardValue;
					if (cardValue == 4)
						moveDistance *= -1;

					int destinationHole = m.getHoleNumber() - moveDistance;
					if (destinationHole < 0)
						destinationHole += 64;

					int startHoleNumber = getStartHoleNumber();
					if (startHoleNumber == 0) 
						startHoleNumber += 64;

					int upperBound = startHoleNumber - 4;
					int lowerBound = startHoleNumber;

					if (upperBound < 0) 
						upperBound += 64;

					if (gamePanel.canMoveToPosition(m, cardValue)  && m.isGoingIn()
							&& destinationHole >= upperBound
							&& destinationHole < lowerBound) {

						this.marble = m;
						this.cardChosen = true;
						break;
					}
				}
			}
		}
		
		
		/*
		 *  Play an out
		 */
		
		// Make sure marble on start isn't a friendly marble
		
		Marble marbleOnStart = gamePanel.getMarblesOnBoard()[getStartHoleNumber()];
		int marbleOnStartIdentifier;
		
		if (marbleOnStart != null)
			marbleOnStartIdentifier = marbleOnStart.getIdentifier();
		else
			marbleOnStartIdentifier = -1;

		for (int i=0; i<this.getHand().size() && !this.cardChosen
				&& marbleOnStartIdentifier % 2 != this.getIdentifier() % 2; i++) {
			this.card = this.getHand().get(i);
			this.cardValue = this.card.getFace().getValue();
			
			if (this.cardValue == 1 || this.cardValue == 13) {
				for (int j=0; j<this.getMarbles().size(); j++) {
					Marble m = this.getMarbles().get(j);
					
					if (!m.isOut()) {
						this.marble = m;
						this.cardChosen = true;
						break;
					}
				}
				
				// prevent loop from continuing to look for out cards if all
				// marbles are out
				if (this.marble == null) 
					break;
			}
		}
		
		
		/*
		 *  Move marble to end of home base
		 */
		
		for (int i=0; i<getMarbles().size() && !cardChosen; i++) {
			Marble m = getMarbles().get(i);
			if (m.isIn()) {
				
				for (int j=getOrderedHand().size() - 1; j>=0; j--) {
					card = getOrderedHand().get(j);
					cardValue = card.getFace().getValue();
					
					if (gamePanel.canMoveToPosition(m, cardValue)) {
						cardChosen = true;
						marble = m;
						break;
					}
				}
				
			}
		}
		
		
		/*
		 *  Play a four
		 */
		
		for (int i=0; i<getMarblesOut().size() && !cardChosen; i++) {
			Marble m = getMarblesOut().get(i);
			
			for (int j=0; j<getHand().size(); j++) {
				card = getHand().get(j);
				cardValue = card.getFace().getValue();
				
				if (cardValue == 4) {
					int upperBound = getStartHoleNumber() + 1;
					int lowerBound = getStartHoleNumber() - 4;
					if (lowerBound < 0) lowerBound += 64;
					if ((m.getHoleNumber() <= upperBound
							|| m.getHoleNumber() >= lowerBound) && gamePanel.canMoveToPosition(m, 4)) {
						marble = m;
						cardChosen = true;
						break;
					}
				}
			}
		}
		
		
		/*
		 * Play a trickster
		 * (not finished)
		 */
		
		for (int i=0; i<getHand().size() && !cardChosen; i++) {
			card = getHand().get(i);
			cardValue = card.getFace().getValue();
			
			marble1 = null;
			marble2 = null;
			
			if (cardValue == 11 && gamePanel.getNumOfMarblesOnBoard() > 1) {
				
				// get marbles behind home
				
				for (int j=getStartHoleNumber(); j<getStartHoleNumber() + 64; j++) {
					  
					int index = (getStartHoleNumber() + j) % 64;
					Marble m = gamePanel.getMarblesOnBoard()[index];

					if (m != null) {
						if (m.getHoleNumber() <= getStartHoleNumber() + 24 && m.getHoleNumber() >= getStartHoleNumber()) {
							marble1 = m;
							break;
						}
					}
				}
				
				//get own marble far from home and swap with marble close to home
				
				for (int j=this.getStartHoleNumber(); j>this.getStartHoleNumber()-64; j--) {
					int index = j % 64;
					if (index < 0) index += 64;
					
					Marble m = gamePanel.getMarblesOnBoard()[index];
					
					if (m != null) {
						marble2 = m;
						break;
					}
				}
			}
		}
		
		
		/*
		 * Play any card
		 */
		
		for (int i=0 ; i<getHand().size() && !cardChosen; i++) {
			card = getHand().get(i);
			cardValue = card.getFace().getValue();
			
			if (getNumOfMarblesOut() != 0) {
				for (int j=0; j<4; j++) {
					Marble tempMarble = getMarbles().get(j);
					if (tempMarble.isOut() && gamePanel.canMoveToPosition(tempMarble, cardValue)
							&& cardValue != 11) {
						marble = getMarbles().get(j);
						cardChosen = true;
						break;
					}
					else if (cardValue == 11 && gamePanel.canMoveToPosition(tempMarble, cardValue)) {
						for (int k=0; k<64; k++) {
							marble1 = gamePanel.getMarblesOnBoard()[k];
							if (marble1 != null) break;
						}
						for (int k=0; k<64; k++) {
							marble2 = gamePanel.getMarblesOnBoard()[k];
							if (marble2 != null && !marble2.equals(marble1)) break;
						}
					}
				}
				if (marble != null) break;
			}	
			
		}

		
		// CHANGE CONDITIONS OF FOLLOWING IF STATEMENT ONCE OTHER SPECIAL CARDS ARE IMPLEMENTED
		if (marble != null && cardValue != 11 && cardValue != 14 && cardValue != 15 && cardValue != 18 
				&& gamePanel.canMoveToPosition(marble, cardValue)) {
			System.out.println("Card played: " + cardValue);

			moveMarble(marble, cardValue);
		}
		
		else if (marble1 != null && marble2 != null & cardValue == 11) {
			moveMarble(marble1, marble2, cardValue);
		}
		
		else {
			this.setLastCardPlayed(cardValue);
			try {
				playCardSoundEffect();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
				e.printStackTrace();
			}
			System.out.println("Card burned: " + cardValue);
		}
		
		getGamePanel().getUsedCards().add(card);
		getHand().remove(card);
		setTurnCount(getTurnCount() + 1);

		getGamePanel().repaint();
	}

	public void playCardSoundEffect() 
			throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		File file = new File("Sound_Effects/Card_sound.wav");
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
		Clip clip = AudioSystem.getClip();
		clip.open(audioStream);

		clip.start();

	}
	
	@Override
	public String toString() {
		return "CPU " + (getIdentifier() + 1);
	}

}
