package client;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JApplet;

public class Zincgull extends JApplet implements MouseListener{
	private static final long serialVersionUID = 7197415241156375302L;
	private static boolean mouseActive = false;
	
	public void init() {	
		this.add(new Sidebar(), BorderLayout.EAST);
		this.add(new Chat(), BorderLayout.SOUTH);
		this.add(new GameArea(), BorderLayout.CENTER);
		this.addMouseListener(this);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
	}

	public static void setMouseActive(boolean mouseActive) {
		Zincgull.mouseActive = mouseActive;
	}

	public static boolean isMouseActive() {
		return mouseActive;
	}

	public void mouseEntered(MouseEvent e) {
		setMouseActive(true);
		repaint();
	}

	public void mouseExited(MouseEvent e) {
		setMouseActive(false);
		repaint();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub	
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}
