package pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Marble implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6595173394669655858L;

	private int identifier; // identifier is used to determine the team of the marble 
	// (i.e. even identifier is one team and odd identifier is the other)
	// (identifiers are from 0-3)
	
	private static int lastID;
	private int marbleID;
	
	private int x;
	private int y;
	private int width;
	
	private Color color;
	private String colorString;
	private Color outlineColor;
	
	private Point[] holePoints;
	private Point marblePosition;
	private Point lastPosition;
	private Point homeStartPosition;
	
	private int holeNumber;
	private int lastHoleNumber;
	private int holeNumberInHome;
	private int lastHoleNumberInHome;
	private int startHoleNumber;
	
	private int moveCount;
	
	private boolean isIn;
	
	public Marble(Point[] points, Point p, int identifier) {
		
		switch (identifier) {
		case 0: this.color = Color.red;
		break;
		case 1: this.color = new Color(20, 150, 255);
		break;
		case 2: this.color = new Color(40, 60, 110);
		break;
		case 3: this.color = new Color(30, 220, 40);
		}
		
		x = p.x;
		y = p.y;
		homeStartPosition = p;
		
		this.setIdentifier(identifier);
		
		marbleID = getNextID();
		
		outlineColor = Color.black;
		
		marblePosition = new Point(p.x, p.y);
		lastPosition = new Point();
		
		// starting hole number (when marble is in starting circle) is -1
		holeNumber = -1;
		setLastHoleNumber(-1);
		
		holeNumberInHome = -2;
		lastHoleNumberInHome = -2;
		
		holePoints = new Point[64];
		
		for (int i=0; i<64; i++) {
			holePoints[i] = points[i];
		}
		
		width = 18;
		
		switch(identifier) {
		
		case 0: this.colorString = "Red";
				startHoleNumber = 0;
		break;
		
		case 1: this.colorString = "Blue";
				startHoleNumber = 16;
		break;
		
		case 2: this.colorString = "Black";
				startHoleNumber = 32;
		break;
		
		case 3: this.colorString = "Green";
				startHoleNumber = 48;
		}
		
	}

	private int getNextID() {
		return lastID++;
	}

	public void drawMarble(Graphics2D g2d) {
		
		g2d.setColor(color);
		g2d.fillOval(x, y, width, width);
		g2d.setStroke(new BasicStroke(2));
		g2d.setColor(outlineColor);
		g2d.drawOval(x, y, width, width);
		
		g2d.setColor(Color.white);
		g2d.fillOval(x + 5, y + 4, 2, 2);
	}
	
	public void setOutlineColor(Graphics2D g2d, Color color) {
		outlineColor = color;
		//drawMarble(g2d);
	}
	
	public boolean contains(int x1, int y1) {
		if (x1 > x && x1 < x + width && y1 > 1 && y1 < y + width)
			return true;
		return false;
	}
	
	public boolean contains(Point p) {
		if (p.x > x && p.x < x + width && p.y > 1 && p.y < y + width)
			return true;
		return false;
	}
	
	public boolean equals(Marble m) {
		if (m == null) return false;
		return m.getMarbleID() == marbleID;
	}
	
	public boolean isSelected(Marble selectedMarble) {
		if (equals(selectedMarble)) return true;
		return false;
	}
	
	public void setPosition(Point p){
		lastPosition = marblePosition;
		setLastHoleNumber(holeNumber);
		
		x = p.x;
		y = p.y;
		
		marblePosition = new Point(x, y);
		moveCount++;
		
		holeNumber = getHoleNumber();
		
		try {
			playSoundEffect();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public Point getPosition() {
		return marblePosition;
	}
	
	public int getHoleNumber() {
		
		for (int i=0; i<holePoints.length; i++) {
			if (marblePosition.equals(holePoints[i])) {
				return i;
			}
		}
		
		if (isIn())
			return -2;
		
		return -1;
	}
	
	public boolean isOut() {
		if (getHoleNumber() != -1) return true;
		return false;
	}
	
	public void setX(int x) {
		this.x = x;
		
		marblePosition.x = x;
	}
	
	public int getX() {
		return x;
	}
	
	public void setY(int y) {
		this.y = y;
		
		marblePosition.y = y;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public String getColorString() {
		return colorString;
	}
	
	public void isIn(boolean isIn) {
		this.isIn = isIn;
	}
	
	public boolean isIn() {
		return isIn;
	}

	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public boolean isGoingIn() {
		if (moveCount > 1) return true;
		return false;
	}
	
	public void playSoundEffect() 
			throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		File file = new File("Sound_Effects/Marble_sound.wav");
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
		Clip clip = AudioSystem.getClip();
		clip.open(audioStream);
		
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
		gainControl.setValue(20f * (float) Math.log10(0.3f)); // volume must  be between 0 and 1
		
		clip.start();

	}
	
	@Override
	public String toString() {
		return colorString + " marble";
	}

	public Point getHomeStartPosition() {
		return homeStartPosition;
	}

	public int getMarbleID() {
		return marbleID;
	}
	
	public void setMoveCount(int i) {
		moveCount = i;
	}
	
	public int getMoveCount() {
		return moveCount;
	}

	public Point getLastPosition() {
		return lastPosition;
	}

	public int getLastHoleNumber() {
		return lastHoleNumber;
	}

	public void setLastHoleNumber(int lastHoleNumber) {
		this.lastHoleNumber = lastHoleNumber;
	}

	public int getHoleNumberInHome() {
		return holeNumberInHome;
	}

	public void setHoleNumberInHome(int holeNumberInHome) {
		lastHoleNumberInHome = this.holeNumberInHome;
		this.holeNumberInHome = holeNumberInHome;
		
	}

	public int getLastHoleNumberInHome() {
		return lastHoleNumberInHome;
	}

	public int getStartHoleNumber() {
		return startHoleNumber;
	}

}
