package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.swing.JApplet;

public class Zincgull extends JApplet implements MouseListener{
	private static final long serialVersionUID = 7197415241156375302L;
	private static boolean mouseActive = false;

	private static Random randomize = new Random();
	static double random = randomize.nextDouble();
	
	static String host, nick;
	public static boolean connected = false;
	
	//start with parameters "host" and "nick"
	public void init() {
		//Initiate ImageBank
		new ImageBank();
		
		host = getParameter("host");
		nick = getParameter("nick");
		final int id = Integer.valueOf(getParameter("id"));
		
		//set size
		setPreferredSize(new Dimension(736,798));
		this.resize(736, 798);
		
		System.out.println("Game about to start..");
		
		this.add(new Chat(), BorderLayout.SOUTH);
		this.add(new GameArea(id), BorderLayout.CENTER);
		this.addMouseListener(this);
		
		
		//this.add(new Sidebar(), BorderLayout.EAST);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
	}
	
	public static String getTime(){
		DateFormat time = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		Date date = new GregorianCalendar().getTime();
		return time.format(date);
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
