package pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import structure.GamePanel;

public class Board implements ActionListener{
	
	private Timer timer;
	private int panelWidth, panelHeight;
	private int borderGap;
	private Color bgColor;
	private int homeBaseRadius;
	private int homeBaseCircleSpread;
	private double smallCircleSpread;
	private int x, y;
	private int boardWidth, boardHeight;
	private int size;
	private GamePanel gamePanel;
	private int[][] holePositions;
	private Point[] holePoints;
	private Point[][] homeBaseHoles;
	
	private int playersTurn = 3;
	
	private int circleRValue = 120;
	private int circleGValue = 230;
	private int circleBValue = 170;
	private Color selectedHomeCircleColor;
	private double xValue;
	
	public Board(GamePanel gamePanel, int x, int y) {
		
		this.gamePanel = gamePanel;
		
		timer = new Timer(100, this);
		timer.start();
		
		circleRValue = 120;
		circleGValue = 230;
		circleBValue = 170;
		
		selectedHomeCircleColor = new Color(circleRValue, circleGValue, circleBValue);
		
		this.x = x;
		this.y = y;
		
		size = 100;
		
		boardWidth = size*7;
		boardHeight = size*7;
		
		borderGap = 75;
		
		homeBaseCircleSpread = 304;
		
		smallCircleSpread = 1.4f;
		
		homeBaseRadius = 30;
		
		bgColor = new Color(185, 160, 90);
		
		holePositions = new int[64][2];
		holePoints = new Point[64];
		homeBaseHoles = new Point[4][4];
	}

	public void drawBoard(Graphics2D g2d) {
		
		int xBoardValues[] = {x, x + 3*size, x + 4*size, x + 7*size,
				x + 7*size, x + 4*size, x + 3*size, x, x};
		
		int yBoardValues[] = {y + 3*size, y, y, y + 3*size, y + 4*size,
				y + 7*size, y + 7*size, y + 4*size, y + 3*size};
		
		Polygon board = new Polygon(xBoardValues, yBoardValues, xBoardValues.length);
		
		g2d.setColor(bgColor);
        g2d.fill(board);
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(board);
        
        drawHomeBases(g2d);
        
        drawHoles(g2d);
	}

	private void drawHomeBases(Graphics2D g2d) {

		g2d.setStroke(new BasicStroke(3));
		
		double theta = 0;
		int circleRadius = homeBaseRadius;
		for (int i=0; i<4; i++) {
			
			g2d.setColor(new Color(80, 120, 80));

			g2d.fillOval((int) (Math.cos(theta)) * homeBaseCircleSpread + homeBaseCircleSpread 
					+ 15 + x,
					(int) (Math.sin(theta)) * -homeBaseCircleSpread + homeBaseCircleSpread 
					+ 15 + y,
					circleRadius*2, circleRadius*2);
			
			g2d.setColor(Color.black);
			
			if (i == playersTurn)
				g2d.setColor(selectedHomeCircleColor );
			else
				g2d.setColor(Color.black);
			
			g2d.drawOval((int) (Math.cos(theta)) * homeBaseCircleSpread + homeBaseCircleSpread 
					+ 15 + x,
					(int) (Math.sin(theta)) * -homeBaseCircleSpread + homeBaseCircleSpread 
					+ 15 + y,
					circleRadius*2, circleRadius*2);
			
			theta += Math.PI/2.0;

		}
		
		// draws center circle
		
		int centerCircleRadius = 80;
		
		g2d.setColor(new Color(80, 120, 80));

		g2d.fillOval(x + (int) (boardWidth/2.0)-centerCircleRadius,
				y + (int) (boardHeight/2.0)-centerCircleRadius,
				centerCircleRadius*2, centerCircleRadius*2);
		
		g2d.setColor(Color.black);
		
		g2d.drawOval(x + (int) (boardWidth/2.0)-centerCircleRadius,
				y + (int) (boardHeight/2.0)-centerCircleRadius,
				centerCircleRadius*2, centerCircleRadius*2);
		
		
		// draws home hook shapes
		
		g2d.setColor(Color.black);
		
		g2d.setStroke(new BasicStroke(4));
		
		int arcRadius = 40;
		
		// west hook
		g2d.drawLine(x + 78, y + (int) (boardHeight/2.0), x + 125,
				y + (int) (boardHeight/2.0));
		g2d.drawArc(x + 125, y + (int) (boardHeight/2.0)-arcRadius,
				arcRadius*2, arcRadius*2, 180, -270);
		
		// north hook
		g2d.drawLine(x + (int) (boardWidth/2.0), y + 78,
				x + (int) (boardWidth/2.0), y + 125);
		g2d.drawArc(x + (int) (boardWidth/2.0) - arcRadius, y + 125,
				arcRadius*2, arcRadius*2, 90, -270);
		
		
		//east hook
		g2d.drawLine(x + boardWidth - 78, y + (int) (boardHeight/2.0),
				x + boardWidth - 125, y + (int) (boardHeight/2.0));
		g2d.drawArc(x + boardWidth - (125 + arcRadius*2), y + (int) (boardHeight/2.0)-arcRadius,
				arcRadius*2, arcRadius*2, 0, -270);
		
		// south hook
		g2d.drawLine(x + (int) (boardWidth/2.0), y + boardHeight - 78,
				x + (int) (boardWidth/2.0), y + boardHeight - 125);
		g2d.drawArc(x + (int) (boardWidth/2.0) - arcRadius, y + boardHeight - 125 - arcRadius*2,
				arcRadius*2, arcRadius*2, 270, -270);
		
	}

	private void drawHoles(Graphics2D g2d) {
		
		double theta = 0;
		
		g2d.setStroke(new BasicStroke(2));

		// 64 small holes
		
		int circleRadius = 6;
		int xShift = (int) (boardWidth/2.0) - circleRadius;
		int yShift = (int) (boardHeight/2.0) - circleRadius;
		for (int i=0; i<64; i++) {
			
			g2d.setColor(new Color(30, 30, 30));
			
			int holeX = x + (int) (Math.cos(theta)*(boardWidth/2.0)/smallCircleSpread) + xShift;
			int holeY = y + (int) (Math.sin(theta)*-(boardWidth/2.0)/smallCircleSpread) + yShift;

			g2d.fillOval(holeX, holeY, circleRadius*2, circleRadius*2);

			g2d.setColor(Color.black);

			g2d.drawOval(holeX, holeY, circleRadius*2, circleRadius*2);
			
			theta += Math.PI/32.0;
			
			holePositions[i][0] = holeX;
			holePositions[i][1] = holeY;
			holePoints[i] = new Point(holeX - 2, holeY - 2);

		}

		/*
		 * Home base holes
		 */

		double spreadValueDouble = 8.7f;

		// east base

		theta = Math.PI/2.0;
		int j = 3;
		for (int i=0; i<4; i++) {
			int xValue = x + (int) (Math.cos(theta) * (boardWidth/2.0)/spreadValueDouble) 
					+ boardWidth - 171;
			int yValue = y + (int) ((Math.sin(theta) * -(boardHeight/2.0))/spreadValueDouble)
					+ boardHeight - (int) (boardHeight/2.0) - circleRadius;
			
			g2d.setColor(new Color(30, 30, 30));
			g2d.fillOval(xValue, yValue, circleRadius * 2, circleRadius * 2);

			g2d.setColor(Color.black);
			g2d.drawOval(xValue, yValue, circleRadius * 2, circleRadius * 2);

			theta += Math.PI/3.0;
			
			homeBaseHoles[0][j] = new Point(xValue - 2, yValue - 2);
			j--;
		}

		// north base

		theta = Math.PI;
		j = 3;
		for (int i=0; i<4; i++) {
			int xValue = x + (int) (Math.cos(theta) * (boardWidth/2.0)/spreadValueDouble)
					+ (int) (boardWidth/2.0) - 5;
			int yValue = y + (int) ((Math.sin(theta) * -(boardHeight/2.0))/spreadValueDouble)
					+ 160;
			
			g2d.setColor(new Color(30, 30, 30));
			g2d.fillOval(xValue, yValue, circleRadius * 2, circleRadius * 2);

			g2d.setColor(Color.black);
			g2d.drawOval(xValue, yValue, circleRadius * 2, circleRadius * 2);

			theta += Math.PI/3.0;
			
			homeBaseHoles[1][j] = new Point(xValue - 2, yValue - 2);
			j--;
		}

		// west base

		theta = -Math.PI/2.0;
		j = 3;
		for (int i=0; i<4; i++) {
			int xValue = x + (int) (Math.cos(theta) * (boardWidth/2.0)/spreadValueDouble) + 160;
			int yValue = y + (int) ((Math.sin(theta) * -(boardHeight/2.0))/spreadValueDouble)
					+ boardHeight - (int) (boardHeight/2.0) - circleRadius;
			
			g2d.setColor(new Color(30, 30, 30));
			g2d.fillOval(xValue, yValue, circleRadius * 2, circleRadius * 2);

			g2d.setColor(Color.black);
			g2d.drawOval(xValue, yValue, circleRadius * 2, circleRadius * 2);

			theta += Math.PI/3.0;
			
			homeBaseHoles[2][j] = new Point(xValue - 2, yValue - 2);
			j--;
		}

		// south base

		theta = 0;
		j = 3;
		for (int i=0; i<4; i++) {
			int xValue = x + (int) (Math.cos(theta) * (boardWidth/2.0)/spreadValueDouble)
					+ (int) (boardWidth/2.0) - 5;
			int yValue = y + (int) ((Math.sin(theta) * -(boardHeight/2.0))/spreadValueDouble)
					+ boardHeight - 172;
			
			g2d.setColor(new Color(30, 30, 30));
			g2d.fillOval(xValue, yValue, circleRadius * 2, circleRadius * 2);

			g2d.setColor(Color.black);
			g2d.drawOval(xValue, yValue, circleRadius * 2, circleRadius * 2);

			theta += Math.PI/3.0;
			
			homeBaseHoles[3][j] = new Point(xValue - 2, yValue - 2);
			j--;
		}

	}

	public void setCircleSpread(int spread) {
		homeBaseCircleSpread = spread;
	}
	
	public int getCircleSpread() {
		return homeBaseCircleSpread;
	}
	
	public void setSmallCircleSpread(double spread) {
		smallCircleSpread = spread;
	}
	
	public double getSmallCircleSpread() {
		return smallCircleSpread;
	}
	
	public void setPanelWidth(int width) {
		panelWidth = width;
	}
	
	public int getPanelWidth() {
		return panelWidth;
	}
	
	public void setPanelHeight(int height) {
		panelHeight = height;
	}
	
	public int getPanelHeight() {
		return panelHeight;
	}

	public void setBorderGap(int borderGap) {
		this.borderGap = borderGap;
	}
	
	public int getBorderGap() {
		return borderGap;
	}
	
	public void setHomeBaseRadius(int homeBaseRadius) {
		this.homeBaseRadius = homeBaseRadius;
	}
	
	public int getHomeBaseRadius() {
		return homeBaseRadius;
	}
	
	public int getWidth() {
		return boardWidth;
	}
	
	public int getHeight() {
		return boardHeight;
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
	
	public int[][] getHolePositions(){
		return holePositions;
	}
	
	public Point[] getHolePoints() {
		return holePoints;
	}
	
	public Point[][] getHomeBaseHoles(){
		return homeBaseHoles;
	}
	
	public void setStartCircleColor(int index) {
		playersTurn = index;
		xValue = 0;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timer && !gamePanel.isTerminated()) {
			xValue += 0.1f;
			
			circleRValue = (int) (20*(Math.cos(xValue*3.0)) + 130);
			circleBValue = (int) (50*(Math.cos(xValue*3.0)) + 200);
			circleGValue = (int) (30*(Math.cos(xValue*3.0)) + 160);

			selectedHomeCircleColor = new Color(circleRValue, circleGValue, circleBValue);
			
			gamePanel.repaint();
		}
	}
	
}
