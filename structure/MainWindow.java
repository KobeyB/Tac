package structure;

import java.awt.BorderLayout;
import javax.swing.JFrame;

public class MainWindow extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3011614351186094575L;
	
	public final static int WIDTH = 800; // 800
	public final static int HEIGHT = 800; // 800
	
	private GamePanel gamePanel;
	private HomePanel homePanel;

	public MainWindow() {
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Tac");
		this.setSize(WIDTH, HEIGHT); // 1:1 ratio
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.setLayout(new BorderLayout());
		
		this.homePanel = new HomePanel(this);
		
		this.add(homePanel, BorderLayout.CENTER);

		this.setVisible(true);
		
	}
	
	public void addNewGamePanel() {
		gamePanel = new GamePanel(this);
		this.add(gamePanel, BorderLayout.CENTER);
	}

	public void addNewHomePanel() {
		homePanel = new HomePanel(this);
		this.add(homePanel, BorderLayout.CENTER);
	}
	
}
