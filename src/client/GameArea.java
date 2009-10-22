package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameArea extends JPanel implements ActionListener, KeyListener{
	private static final long serialVersionUID = -5572295459928673608L;
	
	URL url = getClass().getResource("../images/zincgull.png");
	private ImageIcon sprite = new ImageIcon(url);
	private Timer tim = new Timer(1,this);
	private int turned = 1;
	private int xpos = 20;
	private int ypos = 20;
	private int speed = 1;
	private boolean[] keyDown = new boolean[4];
	
	public GameArea() {
      	this.addKeyListener(this);
      	this.setBackground(Color.WHITE);
      	this.setDoubleBuffered(true);
		tim.addActionListener(this);
		tim.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(sprite.getImage(), xpos-turned*28, ypos, turned*76, 56, null);
		if(Zincgull.isMouseActive()){
			this.requestFocus();
		}
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
