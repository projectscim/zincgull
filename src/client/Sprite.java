package client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;

public class Sprite {
	URL url;
	ImageIcon sprite;
	protected int xpos = 100;
	protected int ypos = 75;
	protected int turned = 1;
	protected int speed = 5;
	protected double id;
	protected String coords;
	
	public Sprite() {
		
	}
	
	public Sprite(int x, int y, int s, double i) {
		try {
			url = new URL( "http://zincgull.rodstrom.se/zincgull.png" );
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		sprite = new ImageIcon(url);
		this.xpos = x;
		this.ypos = y;
		this.id = i;
	}
	
	public String getCoords() {
		coords = (String.valueOf(xpos)+":"
				+ String.valueOf(ypos)+":"
				+ String.valueOf(turned)+":"
				+ String.valueOf(speed));
	
		return coords;
	}
	
}
