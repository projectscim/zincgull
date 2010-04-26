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
	private static final int MOVING_RIGHT = 2;
	private static final int MOVING_LEFT = 3;
	
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
		
		name = MonsterService.getName(id);
		
		System.out.println("name: "+name);
		
		try {
			String u = "http://utterfanskap.se/zincResources/images/monsters/"+((name.toLowerCase()).replace(' ', '_'))+".png";
			System.out.println(u);
			url = new URL(u);
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
	
//	private void checkRange() {
//		// TODO Auto-generated method stub
//		
//	}

	private void move() {
		//temp hardcoded move.
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
	
	public ImageIcon getImg() {
		return sprite;
	}
	
	public int getImgWidth() {
		return sprite.getIconWidth();
	}
	
	public int getImgHeight() {
		return sprite.getIconHeight();
	}
	
}
