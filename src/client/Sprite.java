package client;


public class Sprite {
	protected int xpos;
	protected int ypos;
	protected int turned;
	protected int speed;
	protected int id;
	protected int xMap;
	protected int yMap;
	protected String coords;
	
	public static final int NOT_TURNED = 1;
	public static final int TURNED = -1;
	
	public String getCoords() {
		coords = (String.valueOf(xpos)+":"
				+ String.valueOf(ypos)+":"
				+ String.valueOf(turned)+":"
				+ String.valueOf(speed)+":"
				+ String.valueOf(id));
	
		return coords;
	}
	
	public int getXpos() {
		return this.xpos;
	}
	
	public void setXpos(int i) {
		this.xpos = i;
	}
	
	public int getYpos() {
		return this.ypos;
	}
	
	public void setYpos(int i) {
		this.ypos = i;
	}
	
	public int getTurned() {
		return this.turned;
	}
	
	public void setTurned(int i) {
		this.turned = i;
	}
	
	public int getSpeed() {
		return this.speed;
	}
	
	public void setSpeed(int s) {
		this.speed = s;
	}
	
}
