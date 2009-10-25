package client;

import java.net.URL;

import javax.swing.ImageIcon;

public class Player {
	URL url = getClass().getResource("../images/zincgull.png");
	ImageIcon sprite = new ImageIcon(url);
	int xpos = 80;
	int ypos = 50;
	int speed = 1;
	int turned = 1;
	double id;
	boolean[] arrowDown = new boolean[4];
	
	public Player( double i ){
		this.id = i;
	}
	
	public Player( int x, int y, int s, int t, double i ){
		this.xpos = x;
		this.ypos = y;
		this.speed = s;
		this.turned = t;
		this.id = i;
	}
}
