package structure;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class RulesWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4793127634388405988L;
	
	private static RulesWindow lastRulesWindow;

	public RulesWindow(GamePanel gamePanel) {
		
		if (lastRulesWindow != null) {
			lastRulesWindow.dispose();
		}
		
		lastRulesWindow = this;
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(450, 450); // 1:1 ratio
		this.setTitle("Rules");
		this.setLocationRelativeTo(gamePanel);
		this.setLayout(new BorderLayout());
	
		this.add(new RulesPanel(), BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
}
