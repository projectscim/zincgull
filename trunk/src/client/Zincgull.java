package client;

import java.awt.Graphics;
import javax.swing.JApplet;

public class Zincgull extends JApplet{
	private static final long serialVersionUID = 7197415241156375302L;
	
	public void init() {
		this.setSize(800, 600);
		
		new GameArea();
		
		this.setVisible(true);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		new GameArea();
	}
}
