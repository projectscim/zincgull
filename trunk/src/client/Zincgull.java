package client;

import java.awt.Graphics;
import javax.swing.JApplet;
import javax.swing.JPanel;

public class Zincgull extends JApplet{
	private static final long serialVersionUID = 7197415241156375302L;
	private JPanel game = new GameArea();
	
	public void init() {
		this.setSize(800, 600);
		
		this.add(game);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
	}
}
