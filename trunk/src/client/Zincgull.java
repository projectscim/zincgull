package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Zincgull extends JApplet{
	private static final long serialVersionUID = 7197415241156375302L;
	private JPanel game = new GameArea();
	private JPanel sidebar = new JPanel();
	
	public void init() {
		this.setSize(300, 200);
		
		sidebar.add(new JLabel("lol"));
		sidebar.setBackground(Color.WHITE);
		
		this.add(sidebar, BorderLayout.EAST);
		this.add(game);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
	}
}
