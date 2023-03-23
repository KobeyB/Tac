package pieces;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
	
	private ArrayList<Card> deck;

	public Deck(boolean shuffled) {
		deck = new ArrayList<>();

		for (Face f: Face.values()) {
			for (int i=0; i<f.getAmount(); i++) {
				deck.add(new Card(f));
			}
		}
		
		if (shuffled) Collections.shuffle(deck);
		
	}
	
	public void shuffleDeck() {
		Collections.shuffle(deck);
	}
	
	public ArrayList<Card> dealHand(int numOfCards){
		
		ArrayList<Card> hand = new ArrayList<>();

		for (int i=0; i<numOfCards; i++) {
			hand.add(deck.get(0));
			deck.remove(0);
		}
		return hand;
	}
	
	public int size() {
		return deck.size();
	}
	
	public ArrayList<Card> getCardList(){
		return deck;
	}

}
