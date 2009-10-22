package Main;

import java.applet.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.Timer;

public class Main extends Applet implements ActionListener, KeyListener{
	private static final long serialVersionUID = 7197415241156375302L;
	int width, height;

	private ImageIcon sprite=new ImageIcon("images//zincgull.png");
	private Timer tim = new Timer(1,this);
	private int turned = 1;
	private int xpos=20;
	private int ypos=20;
	private int speed=1;
	private boolean[] keyDown=new boolean[4];
	
	public void init() {
		width = getSize().width;
		height = getSize().height;
		
      	this.addKeyListener(this);
		tim.addActionListener(this);
		tim.start();
		this.requestFocus();
		System.out.println("lulz igen");
	}

	public void paint( Graphics g ) {
		super.paint(g);
		g.drawImage(sprite.getImage(), xpos-turned*28, ypos, turned*76, 56, null);
		this.requestFocus();
	}

	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()>=37 && e.getKeyCode()<=40)
			keyDown[40-e.getKeyCode()]=true;
	}

	
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()>=37 && e.getKeyCode()<=40)
			keyDown[40-e.getKeyCode()]=false;
		
	}

	public void keyTyped(KeyEvent e) {}

	public void actionPerformed(ActionEvent e) {
		calculateMove();
		repaint();
		
	}

	private void calculateMove() {
		if(keyDown[0]){
			ypos=ypos+speed;
		}
		if(keyDown[1]){
			xpos=xpos+speed;
			turned = 1;
		}
		if(keyDown[2]){
			ypos=ypos-speed;
		}
		if(keyDown[3]){
			xpos=xpos-speed;
			turned = -1;
		}
		
	}

}
