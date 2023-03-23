package extra;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.JSlider;

import pieces.Board;
import structure.GamePanel;

public class AdjustementPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 631666359802013605L;
	
	private JSlider slider;
	private String description;
	private AdjustementWindow window;
	private Board board;
	private GamePanel gamePanel;

	public AdjustementPanel(GamePanel gamePanel, Board board, String description,
			int min, int max, int initialValue, AdjustementWindow window) {
		this.setBackground(Color.white);
		this.setLayout(new FlowLayout());
		
		this.board = board;
		this.gamePanel = gamePanel;

		slider = new JSlider(min, max, 304);
		
		// greater 2nd value for Dimension
		// translates the slider down
		slider.setPreferredSize(new Dimension(250, 110)); 
		slider.setPaintTicks(true);
		slider.setMinorTickSpacing((int)((max-min)/25.0));
		slider.setPaintTrack(true);
		slider.setMajorTickSpacing((int)((max-min)/5.0));
		slider.setPaintLabels(true);
		slider.setFont(new Font("Futura", Font.PLAIN, 10)); // Lucida Grande, Futura
		slider.setSnapToTicks(true);
		
		//slider
		
		this.description = description;
		this.window = window;
		
		this.add(slider);
	}
	
	public AdjustementPanel(GamePanel gamePanel, Board board, String description,
			double min, double max, double initialValue, AdjustementWindow window) {
		this.setBackground(Color.white);
		this.setLayout(new FlowLayout());
		
		this.board = board;
		this.gamePanel = gamePanel;

		slider = new JSlider((int)min,(int)max, (int) ((max-min)/2.0));
		
		// greater 2nd value for Dimension
		// translates the slider down
		slider.setPreferredSize(new Dimension(250, 110)); 
		slider.setPaintTicks(true);
		slider.setMinorTickSpacing((int)((max-min)/25.0));
		slider.setPaintTrack(true);
		slider.setMajorTickSpacing((int)((max-min)/5.0));
		slider.setPaintLabels(true);
		slider.setFont(new Font("Futura", Font.PLAIN, 10)); // Lucida Grande, Futura
		slider.setSnapToTicks(true);
		
		//slider
		
		this.description = description;
		this.window = window;
		
		this.add(slider);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(Color.white);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setFont(new Font("Avenir", Font.BOLD, 14));
		
		int stringWidth = g2d.getFontMetrics().stringWidth(description);
		g2d.drawString(description,
				(int) (window.getWidth()/2.0) - (int) (stringWidth/2.0),
				30);
		
		board.setCircleSpread(slider.getValue());
		gamePanel.repaint();
	}
	
}
