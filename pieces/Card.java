package pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

public class Card{

	private Face face;
	private int cardWidth;
	private int cardHeight;
	private int x;
	private int y;
	private int startX;
	private int startY;
	private int centerX;
	private int count = 0;
	private Color bgColor;
	
	private boolean isFlipped;
	
	private int numFontSize;
	
	public Card(Face face) {
		this.face = face;
		
		cardWidth = 50;
		cardHeight = 70;
		
		bgColor = new Color(200, 200, 200);
		
		isFlipped = true;
		
		numFontSize = 15;
	}
	
	public Face getFace(){
        return face;
    }
    
	@Override
    public String toString(){
        return face.toString();
    }
    
    public void drawCard(Graphics2D g2d, int x, int y) {
    	
    	this.x = x;
    	this.y = y;
    	
    	if (count == 0) {
    		startX = x;
    		startY = y;
    		count++;
    	}
    	
    	if (isFlipped) {

    		g2d.setColor(bgColor);
    		g2d.fillRoundRect(x, y, cardWidth, cardHeight, 8, 8);


    		g2d.setStroke(new BasicStroke(2));
    		g2d.setColor(Color.black);
    		g2d.drawRoundRect(x, y, cardWidth, cardHeight, 8, 8); // 5:7 ratio

    		g2d.setFont(new Font("Trattatello", Font.PLAIN, numFontSize));

    		centerX = x + (int) (cardWidth/2.0);
    		int cardValue = face.getValue();

    		if (cardValue <= 13 && cardValue != 11) {

    			// paint special cards red
    			if (cardValue == 1 || cardValue == 4 || cardValue == 7 
    					|| cardValue == 13 || cardValue == 8) {
    				g2d.setColor(new Color(200, 0, 0)); // dark red color
    			}
    			else {
    				g2d.setColor(Color.black);
    			}
    			g2d.drawString(cardValue + "", x + 5, y + 15);
    			g2d.setFont(new Font("Trattatello", Font.PLAIN, -numFontSize));
    			g2d.drawString(cardValue + "",
    					x + cardWidth + (int) (g2d.getFontMetrics().stringWidth(cardValue + "")/2.0),
    					y + cardHeight - 14);
    			g2d.setFont(new Font("Trattatello", Font.PLAIN, numFontSize));
    		}
    		else {
    			int fontSize = 20;
    			String faceString = face.name(); // .toLowerCase();
    			while (g2d.getFontMetrics().stringWidth(faceString) > cardWidth - 5) {
    				fontSize--;
    				g2d.setFont(new Font("Aubrey", Font.BOLD, fontSize));
    			}

    			if (faceString.equalsIgnoreCase("trickster")) {
    				g2d.setColor(new Color(200, 0, 0));
    			}
    			else
    				g2d.setColor(Color.black);
    			g2d.drawString(faceString,
    					centerX - (int) (g2d.getFontMetrics().stringWidth(faceString)/2.0),
    					y + (int) (cardHeight/2.0));
    		}
    	}
    	else {

    		g2d.setColor(new Color(220, 220, 220));
    		g2d.fillRoundRect(x, y, cardWidth, cardHeight, 8, 8);
    		
    		g2d.setColor(new Color(25, 40, 100));
    		g2d.fillRect(x + 5, y + 5, cardWidth - 10, cardHeight - 10);
    		
    		int xValue = x + (int) (cardWidth/2.0);
    		int yValue = y + (int) (cardHeight/2.0);
    		double theta = 0;
    		
    		for (int i=0; i<6; i++) {
    			
    			g2d.setColor(new Color(200, 200, 200));
    			g2d.rotate(theta, xValue, yValue);
    			g2d.fillOval(xValue, yValue, 20, 5); // 4:1 ratio
    			
    			g2d.setStroke(new BasicStroke(1));
    			g2d.setColor(new Color(80, 80, 80));
    			g2d.drawOval(xValue, yValue, 20, 5);
    			g2d.drawLine(xValue, yValue, xValue + 20, yValue + 4);

    			g2d.rotate(-theta, xValue, yValue);
    			theta += Math.PI/3.0;
    		}
    		
    		g2d.setStroke(new BasicStroke(2));
    		g2d.setColor(Color.black);
    		g2d.drawRoundRect(x, y, cardWidth, cardHeight, 8, 8); // 5:7 ratio
    	}

    }
    
    public void flipped(boolean isFlipped) {
    	this.isFlipped = isFlipped;
    }
    
    public void translate(Graphics2D g2d, int x, int y) {
    	this.x = x;
    	this.y = y;
    }
    
    public boolean contains(int x2, int y2) {
    	if (x2 > x && x2 < x + cardWidth && y2 > y && y2 < y + cardHeight)
    		return true;
    	return false;
    }
    
    public boolean contains(Point p) {
    	if (p.getX() > x && p.getX() < x + cardWidth && p.getY() > y && p.getY() < y + cardHeight)
    		return true;
    	return false;
    }
    
    public void setWidth(int width) {
    	cardWidth = width;
    }
    
    public int getWidth() {
    	return cardWidth;
    }
    
    public void setHeight(int height) {
    	cardHeight = height;
    }
    
    public int getHeight() {
    	return cardHeight;
    }
    
    public void setX(int x) {
    	this.x = x;
    }
    
    public int getX() {
    	return x;
    }
    
    public void setY(int y) {
    	this.y = y;
    }
    
    public int getY() {
    	return y;
    }
    
    public int getStartX() {
    	return startX;
    }
    
    public int getStartY() {
    	return startY;
    }
    
    public void setCardColor(Color color) {
    	bgColor = color;
    }

	public boolean isPlayable() {
		
		return false;
	}
	
	public void setNumberFontSize(int fontSize) {
		numFontSize = fontSize;
	}

}
