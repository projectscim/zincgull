package client;

import java.net.URL;

import javax.swing.ImageIcon;

public class Player {
	URL url = getClass().getResource("../images/zincgull.png");
	ImageIcon sprite = new ImageIcon(url);
	int turned = 1;
	int xpos = 80;
	int ypos = 50;
	int speed = 1;
	boolean[] arrowDown = new boolean[4];
	
	
}
