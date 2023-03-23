package extra;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import pieces.Board;
import structure.GamePanel;

public class AdjustementWindow extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2728475872150908996L;
	
	public AdjustementWindow(GamePanel gamePanel, Board board, String description,
			int min, int max, int initialValue) {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(275, 125);
		this.setTitle("Adjustements");
		//this.setLocationRelativeTo(null);
		this.setLocation(1150, 200);
		this.setLayout(new BorderLayout());
		this.setResizable(false);
		
		this.add(new AdjustementPanel(gamePanel, board, description, min, max, initialValue, this), BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
	public AdjustementWindow(GamePanel gamePanel, Board board, String description,
			double min, double max, double initialValue) {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(275, 125);
		this.setTitle("Adjustements");
		//this.setLocationRelativeTo(null);
		this.setLocation(1150, 200);
		this.setLayout(new BorderLayout());
		this.setResizable(false);
		
		this.add(new AdjustementPanel(gamePanel, board, description, min, max, initialValue, this), BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
}
