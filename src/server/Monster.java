package server;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;

import client.Sprite;

/**
 * 
 * 
 * 
 * @author Andreas
 */
public class Monster extends Sprite implements Runnable {
	
	Thread thread = new Thread(this);
	
	private static final int idleSleep = 2000;
	private int activeSleep = 50;
	private static final int range = 5;

	public static final int DEFAULT_XPOS = 100;
	public static final int DEFAULT_YPOS = 100;
	
	private int id;
	
	//Stats
	private String name;
	private int damage;
	private int health;
	private int level;
	private int aggro;
	private String spawnLocation;
	private boolean boss;
	
	//Active
	private int state;
	private static final int DEAD = -1;
	private static final int WAITING = 0;
	private static final int ATTACKING = 1;
	
	private boolean alive;
	
	private URL url;
	private ImageIcon sprite;
	
	public Monster() {
		id = 0;
		xpos = DEFAULT_XPOS;
		ypos = DEFAULT_YPOS;
	}
	
	public Monster(int x, int y, int s, int t, int i) {
		//Monsters created with this constructor is not intended to be run. TODO Create MonsterSkel in Client-package instead?
		xpos = x;
		ypos = y;
		speed = s;
		turned = t;
		id = i;
		try {
			url = new URL("http://utterfanskap.se/images/monster.png");
		} catch (MalformedURLException e) {
			System.out.println("Monster Sprite Fail in Monster-constructor");
			e.printStackTrace();
		}
		sprite = new ImageIcon(url);
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

	public synchronized void run() {
		
		alive = true;
		
		while(alive) {
			
			activeSleep = 100;
			move();
			
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
	
	private void checkRange() {
		// TODO Auto-generated method stub
		
	}

	private void move() {
		//temp hardcoded move.
		if(xpos>=250) {
			ypos = 101;
		}
		else if(xpos<=100) {
			ypos = 100;
		}
		
		if(xpos <= 250 && ypos == 100) {
			xpos += 2;
		}
		else if(xpos>100 && ypos == 101) {
			xpos -= 2;
		}
		
		
		//update coords
		getCoords();
		
		//Send changes
		MapSrv.monsterPositions.set(id, coords);
		MapSrv.sendToAll(coords);
	}

	private void attack() {
		// TODO Auto-generated method stub
		
	}

	private void update() {
		state = ATTACKING;
		
	}
	
	
	//a Shitload of getters/setters
	public synchronized int getId() {
		return id;
	}
	
	public synchronized void setId(int id) {
		this.id = id;
	}
	
	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized int getDamage() {
		return damage;
	}

	public synchronized void setDamage(int damage) {
		this.damage = damage;
	}

	public synchronized int getHealth() {
		return health;
	}

	public synchronized void setHealth(int health) {
		this.health = health;
	}

	public synchronized int getLevel() {
		return level;
	}

	public synchronized void setLevel(int level) {
		this.level = level;
	}

	public synchronized int getAggro() {
		return aggro;
	}

	public synchronized void setAggro(int aggro) {
		this.aggro = aggro;
	}

	public synchronized String getSpawnLocation() {
		return spawnLocation;
	}

	public synchronized void setSpawnLocation(String spawnLocation) {
		this.spawnLocation = spawnLocation;
	}

	public synchronized boolean isBoss() {
		return boss;
	}

	public synchronized void setBoss(boolean boss) {
		this.boss = boss;
	}
	
	public synchronized void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public synchronized boolean getAlive() {
		return alive;
	}
	
	public synchronized ImageIcon getImg() {
		return sprite;
	}
	
}
