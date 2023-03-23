package structure;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class RulesPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4211609623920444022L;
	
	public RulesPanel() {
		
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(new Color(175, 200, 250));
		
		Graphics2D g2d = (Graphics2D) g;
		
		int fontSize = 16;
		
		int leftMargin = 20;
		int topMargin = 30;
		int stringHeight = fontSize;
		int spacing = 5;
		int x = leftMargin;
		int y = topMargin;
		
		g2d.setFont(new Font("Avenir Bold", Font.PLAIN, fontSize + 1));
		
		g2d.drawString("Objective:  ",
				leftMargin, topMargin);
		
		int stringWidth = g2d.getFontMetrics().stringWidth("Objective:  ");
		
		g2d.setFont(new Font("Avenir", Font.PLAIN, fontSize));
		
		x += stringWidth;
		g2d.drawString("Work with the person across from", x, y);
		
		y += stringHeight + spacing;
		g2d.drawString("you to get all 8 of your marbles in your home.", x, y);
		
		y += stringHeight + spacing*2;
		g2d.drawString("Once all four of your own marbles are \"in\"", x, y);
		
		y += stringHeight + spacing;
		g2d.drawString("you begin playing cards for your partner until", x, y);
		
		y += stringHeight + spacing;
		g2d.drawString("the game is won.", x, y);
		
		g2d.setFont(new Font("Avenir Bold", Font.PLAIN, fontSize + 1));
		
		x = leftMargin;
		y += 75;
		// g2d.drawString("Cards:  ", x, y);
		g2d.drawString("Play a 1 or a 13 to get a marble 'out'", x, y);
		
		stringWidth = g2d.getFontMetrics().stringWidth("Cards:  ");
		
		g2d.setFont(new Font("Avenir", Font.PLAIN, fontSize));
		
		x = leftMargin + stringWidth;
//		g2d.drawString("1's and 13's (\"Out\" cards) - These cards allow your marble to come out.",
//				x, y);
	}

}
