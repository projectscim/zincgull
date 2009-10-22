package client;

import java.awt.BorderLayout;
import java.awt.Graphics;
import javax.swing.JApplet;

public class Zincgull extends JApplet{
	private static final long serialVersionUID = 7197415241156375302L;
	
	public void init() {		
		this.add(new Sidebar(), BorderLayout.EAST);
		this.add(new GameArea());
	}
	
	public void paint(Graphics g) {
		super.paint(g);
	}
}
