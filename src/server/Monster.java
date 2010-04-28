package server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import javax.swing.ImageIcon;

import local.GlobalConstants;

import client.LoadMap;
import client.Sprite;

/**
 * 
 * 
 * 
 * @author Andreas
 */
public class Monster extends Sprite implements Runnable, GlobalConstants{
	
	Thread thread = new Thread(this);
	
	//private static final int idleSleep = 2000;
	private int activeSleep = 50;
	//private static final int range = 5;

	public static final int DEFAULT_XPOS = 300;
	public static final int DEFAULT_YPOS = 320;
	
	private int id;
	private int index;
	
	//Stats
	private int monsterId;
	private String name;
	private int damage;
	private int health;
	private int level;
	private int aggro;
	private String spawnLocation;
	private boolean boss;
	
	//Active
	private int state;
	//private static final int DEAD = -1;
	//private static final int WAITING = 0;
	//private static final int ATTACKING = 1;
	private static final int MOVING = 2;

	private static final int MOVING_RIGHT = 2;
	private static final int MOVING_LEFT = 3;
	
	private static double dx;
	private static double dy;
	
	private boolean alive;
	
	public Monster() {
		id = 0;
		xpos = DEFAULT_XPOS;
		ypos = DEFAULT_YPOS;
		
		speed = 5;
	}
	
	private void randomDirection() {
		//Randomize movement direction
		Random randomize = new Random();
		int angle = randomize.nextInt(361);
		
		System.out.println("RANDOM ANGLE: "+angle);
		
		setDirection(angle);
	}
	
	private void setDirection(double angle) {
		//if not valid angle
		if(angle<0 || angle>360) return;
		
		double speed = (double)(this.speed);
		
		if(angle<=90) {
			
			if(((angle/90)) <= 0.5) {
				dy = ((angle/90)*speed);
				dx = ((1-(angle/90))*speed);
			}
			else {
				dy = ((1-(angle/90))*speed);
				dx = ((angle/90)*speed);
			}
		}
		else if(angle>90 && angle<=180) {
			angle -= 90;
			
			if(((angle/90)) <= 0.5) {
				dy = ((1-(angle/90))*speed);
				dx = -((angle/90)*speed);
			}
			else {
				dy = (((1-angle)/90)*speed);
				dx = -((angle/90)*speed);
			}
		}
		else if(angle>180 && angle<=270) {
			angle -= 180;
			
			if(((angle/90)) <= 0.5) {
				dy = -((angle/90)*speed);
				dx = -((1-(angle/90))*speed);
			}
			else {
				dy = -((angle/90)*speed);
				dx = -(((1-angle)/90)*speed);
			}
		}
		else {
			angle -= 270;
			
			if(((angle/90)) <= 0.5) {
				dy = -(((1-angle)/90)*speed);
				dx = ((angle/90)*speed);
			}
			else {
				dy = -((1-(angle/90))*speed);
				dx = ((angle/90)*speed);
			}
		}
		
		System.out.println("DX: "+dx);
		System.out.println("DY: "+dy);
		
		state = MOVING;
	}
	
	public synchronized void printStats() {
		System.out.println("--MONSTER STATS--");
		System.out.println("Id: "+id); //From Sprite
		System.out.println("Name: "+name);
		System.out.println("Health: "+health);
		System.out.println("Damage: "+damage);
		System.out.println("Level: "+level);
		System.out.println("Aggro: "+aggro);
		System.out.println("Speed: "+speed); //From Sprite
		System.out.println("Spawn: "+spawnLocation);
		System.out.println("Boss: "+boss);
		System.out.println("--------------");
	}
	
	//Override Sprite
	public String getCoords() {
		coords = (String.valueOf(xpos)+":"
				+ String.valueOf(ypos)+":"
				+ String.valueOf(turned)+":"
				+ String.valueOf(monsterId)+":"
				+ String.valueOf(id)+":"
				+ String.valueOf(health));
	
		return coords;
	}

	public void run() {
		
		alive = true;
		
		while(alive) {
			
			activeSleep = 100;
			move();
			//collisionCheck();
			
			/*
			//Update state
			update();
			
			if(state==WAITING) {
				//do nothing
				activeSleep = idleSleep;
			}
			else if(state==ATTACKING) {
				checkRange();
				
				move();
				
				attack();
				
				activeSleep = 20;
			}
			else if(state==DEAD) {
				alive = false;
				activeSleep = 0;
			}
			*/
			//Sleep a bit
			try {
				Thread.sleep(activeSleep);
			} catch (InterruptedException e) {}
			
		}
		
		MonsterService.dyingMonster(this);
		
	}
	
	private void collisionCheck() {
		int tileY = (ypos + (TILE_SIZE/2))/TILE_SIZE;
		
		if(dx < 0) {
			int tile = (xpos/TILE_SIZE)+1;
			if(xpos<tile && LoadMaps.getTile(tile, tileY)!=' '){
				xpos = tile;
			}
		}
		else if(dx > 0) {
			int tile = (xpos/TILE_SIZE);
			if(xpos>tile && LoadMaps.getTile(tile, tileY)!=' '){
				xpos = tile-1;
			}
		}
	}
	
//	private void checkRange() {
//		// TODO Auto-generated method stub
//		
//	}

	private void move() {

		/*if(state != MOVING) {
			randomDirection();
			return;
		}
		
		if(xpos<0) {
			xpos = 5;
			randomDirection();
		}
		
		if(xpos>APPLET_WIDTH) {
			xpos = APPLET_WIDTH-5;
			randomDirection();
		}
		
		if(ypos<0) {
			ypos = 5;
			randomDirection();
		}
		
		if(ypos>APPLET_HEIGHT) {
			ypos = APPLET_HEIGHT-5;
			randomDirection();
		}
		
		xpos += dx;
		ypos += dy;*/
		
		if ((state != MOVING_RIGHT) && (state != MOVING_LEFT)) state = MOVING_LEFT;
		
		if(xpos>=850 && state!=MOVING_LEFT) {
			state = MOVING_LEFT;
		}
		else if(xpos<=100 && state!=MOVING_RIGHT) {
			state = MOVING_RIGHT;
		}
		
		if (state == MOVING_LEFT) {
			xpos-=2;
			turned = 1;
		}
		else if(state == MOVING_RIGHT) {
			xpos+=2;
			turned = -1;
		}
		
		//update coords
		getCoords();
		
		//Send changes
		index = MapSrv.getMonsterIndex(id);
		MapSrv.monsterPositions.set(index, coords);
		MapSrv.sendToAll(coords);
	}

//	private void attack() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void update() {
//		state = ATTACKING;
//		
//	}
	
	
	//a Shitload of getters/setters
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getMonsterId() {
		return monsterId;
	}
	
	public void setMonsterId(int id) {
		this.monsterId = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getAggro() {
		return aggro;
	}

	public void setAggro(int aggro) {
		this.aggro = aggro;
	}

	public String getSpawnLocation() {
		return spawnLocation;
	}

	public void setSpawnLocation(String spawnLocation) {
		this.spawnLocation = spawnLocation;
	}

	public boolean isBoss() {
		return boss;
	}

	public void setBoss(boolean boss) {
		this.boss = boss;
	}
	
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public boolean getAlive() {
		return alive;
	}
	
}