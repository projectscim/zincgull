package client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;

public class Sprite {
	URL url;
	ImageIcon sprite;
	protected int xpos;
	protected int ypos;
	protected double id;
	
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
}