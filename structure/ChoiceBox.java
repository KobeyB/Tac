package structure;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ChoiceBox implements MouseListener{

	private boolean isVisible;
	private int x;
	private int y;
	private int width;
	private int height;
	private String text;
	
	private MouseListener mouseListener;
	
	public ChoiceBox(MouseListener mouseListener) {
		this.mouseListener = mouseListener;
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void addMouseListener(MouseListener mouseListener) {
		this.mouseListener = mouseListener;
	}
	
	public MouseListener getMouseListener() {
		return mouseListener;
	}
	
	public void drawChoiceBox(Graphics2D g2d) {
		g2d.setFont(new Font("Avenir", Font.PLAIN, 20));
		
		int stringWidth = g2d.getFontMetrics().stringWidth(text); 
		int stringHeight = g2d.getFontMetrics().getHeight();
		width = stringWidth + 10;
		height = stringHeight * 2;
		
		g2d.setColor(Color.white);
		g2d.fillRoundRect(x, y, width, height, 5, 5);
		g2d.setColor(Color.black);
		g2d.drawRoundRect(x, y, width, height, 5, 5);
		
		g2d.drawString(text, (int) (x + stringWidth/4.0), y + stringHeight);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
