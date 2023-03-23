package gameplay;

import pieces.Card;
import pieces.Marble;
import structure.GamePanel;

public class Human extends Player{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2585342223760516220L;

	public Human(GamePanel gamePanel){
		
		super(gamePanel);

	}

	public void play(Marble marble, Card card) {
		int cardValue = card.getFace().getValue();

		moveMarble(marble, cardValue);

		getGamePanel().getUsedCards().add(card);
		getHand().remove(card);
		setTurnCount(getTurnCount() + 1);
		
		getGamePanel().repaint();
	}
	
	@Override
	public String toString() {
		return "Player 1";
	}

}
