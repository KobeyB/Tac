package structure;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class HomePanel extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5612593442066187215L;
	
	private Timer timer;
	private String title;
	private int fontSize;
	private int buttonWidth;
	private int buttonHeight;
	private int buttonPanelHeight;
	
	private Rectangle resumeGameButton, newGameButton, loadGameButton, settingsButton;
	
	private ArrayList<Rectangle> buttons;
	private ArrayList<Color> buttonColors;
	private ArrayList<String> buttonNames;
	
	private Color bgColor;
	private Color buttonColor;
	private Color clickedButtonColor;
	
	private HomePanel homePanel;
	private MainWindow window;
	
	
	public HomePanel(MainWindow window) {
		
		//timer = new Timer(32, this);
		//timer.start();
		
		this.homePanel = this;
		this.window = window;
		
		this.title = "Welcome to Tac!";
		this.fontSize = 60;
		
		this.buttonWidth = 300;
		this.buttonHeight = 100;
		
		this.buttons = new ArrayList<>();
		this.buttonColors = new ArrayList<>();
		this.buttonNames = new ArrayList<>();
		
		this.bgColor = new Color(130, 150, 200);
		this.buttonColor = new Color(230, 230, 230);
		this.clickedButtonColor = new Color(150, 150, 150);
		
		int x = (int) (this.getWidth()/2.0 - buttonWidth/2.0);
		int y = (int) (this.getHeight()/2.0);
		
		this.resumeGameButton = new Rectangle(x, y, buttonWidth, buttonHeight);
		y += buttonHeight + 20;
		this.newGameButton = new Rectangle(x, y, buttonWidth, buttonHeight);
		y += buttonHeight + 20;
		this.loadGameButton = new Rectangle(x, y, buttonWidth, buttonHeight);
		y += buttonHeight + 20;
		this.settingsButton = new Rectangle(x, y, buttonWidth, buttonHeight);
		
		this.buttons.add(resumeGameButton);
		this.buttons.add(newGameButton);
		this.buttons.add(loadGameButton);
		this.buttons.add(settingsButton);
		
		this.buttonPanelHeight = (buttonHeight + 20)*this.buttons.size();
		
		this.buttonNames.add("Resume");
		this.buttonNames.add("New Game");
		this.buttonNames.add("Load Game");
		this.buttonNames.add("Settings");
		
		for (int i=0; i<this.buttons.size(); i++) {
			this.buttonColors.add(this.buttonColor);
		}
		
		this.addMouseListener(new ClickListener());

	}

	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		this.setBackground(this.bgColor);
		
		Graphics2D g2d = (Graphics2D) g;
		
		//renderBackgroundAnimation(g2d);
		
		g2d.setFont(new Font("Optima", Font.PLAIN, fontSize));
		
		int stringWidth = g2d.getFontMetrics().stringWidth(this.title);
		int stringHeight = (int) (g2d.getFontMetrics().getDescent() + fontSize/2.0);
		g2d.drawString(title, (int) (this.getWidth()/2.0 - stringWidth/2.0), stringHeight + 25);
		
		int x = (int) (this.getWidth()/2.0 - buttonWidth/2.0);
		int y = (int) (this.getHeight() - this.buttonPanelHeight);
		
		for (int i=0; i<this.buttons.size(); i++) {
			
			Rectangle button = this.buttons.get(i);
			button.setLocation(x, y);

			g2d.setColor(this.buttonColors.get(i));

			g2d.fillRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);

			g2d.setColor(Color.black);

			g2d.drawRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
			
			g2d.setColor(Color.black);
			
			g2d.setFont(new Font("Aubrey", Font.PLAIN, fontSize - 8));
			
			stringWidth = g2d.getFontMetrics().stringWidth(buttonNames.get(i));
			stringHeight = (int) (g2d.getFontMetrics().getDescent() + fontSize/2.0);
			int stringX = (int) (x + buttonWidth/2.0 - stringWidth/2.0);
			int stringY = (int) (y + buttonHeight/2.0 + stringHeight/2.0 - 5);
			
			g2d.drawString(buttonNames.get(i), stringX, stringY);
			
			y += buttonHeight + 20;
		}
		
		g2d.setFont(new Font("Avenir", Font.PLAIN, 15));
		
		String author = "Author: Kobey Buhr";
		stringWidth = g2d.getFontMetrics().stringWidth(author);
		g2d.drawString(author, 20, this.getHeight() - 20);
	}
	
	public class ClickListener extends MouseAdapter{
		
		@Override
		public void mousePressed(MouseEvent e) {
			
			if (newGameButton.contains(e.getPoint())) {
				
			}
			
			else if (loadGameButton.contains(e.getPoint())) {
				
			}
			
			else if (settingsButton.contains(e.getPoint())) {
				
			}
			
			for (int i=0; i<buttons.size(); i++) {
				Rectangle button = buttons.get(i);
				
				if (button.contains(e.getPoint())) {
					buttonColors.set(i, clickedButtonColor);
					repaint();
					break;
				}
			}
			
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			
			if (newGameButton.contains(e.getPoint())) {
				window.remove(homePanel);
				window.addNewGamePanel();
				window.validate();
				window.repaint();
			}
			
			else if (loadGameButton.contains(e.getPoint())) {
				JOptionPane.showConfirmDialog(
					null, 
					"Loading function not yet implemented.", 
					"Not implemented", 
					JOptionPane.OK_CANCEL_OPTION);
			}
			
			else if (settingsButton.contains(e.getPoint())) {
				JOptionPane.showConfirmDialog(
					null, 
					"Settings menu not yet implemented.", 
					"Not implemented", 
					JOptionPane.OK_CANCEL_OPTION);
			}

			else if (resumeGameButton.contains(e.getPoint())) {
				JOptionPane.showConfirmDialog(
					null, 
					"Resume function not yet implemented.", 
					"Not implemented", 
					JOptionPane.OK_CANCEL_OPTION);
			}
			
			else {
				for (int i=0; i<buttons.size(); i++) {
					buttonColors.set(i, buttonColor);
					repaint();
				}
			}

			for (int i=0; i<buttons.size(); i++) {
				Rectangle button = buttons.get(i);
				
				if (button.contains(e.getPoint())) {
					buttonColors.set(i, buttonColor);
					repaint();
					break;
				}
			}

		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == timer) {
			repaint();
		}
		
	}
	
}
