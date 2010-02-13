package server;

import client.Sprite;

/**
 * 
 * 
 * 
 * @author Andreas
 */
public class Monster extends Sprite implements Runnable {
	
	Thread thread = new Thread(this);
	
	//Stats
	private String name;
	private int damage;
	private int health;
	private int level;
	private int aggro;
	private String spawnLocation;
	private boolean boss;
	private int speed = 1;		//TODO add to MonsterDatabase
	
	//Active
	private int state;
	private static final int WAITING = 0;
	private static final int ATTACKING = 1;
	
	private boolean alive;
	
	public Monster() {}
	
	public void printStats() {
		System.out.println("--MONSTER STATS--");
		System.out.println("Name: "+name);
		System.out.println("Health: "+health);
		System.out.println("Damage: "+damage);
		System.out.println("Level: "+level);
		System.out.println("Aggro: "+aggro);
		System.out.println("Spawn: "+spawnLocation);
		System.out.println("Boss: "+boss);
		System.out.println("--------------");
	}

	public void run() {
		
		alive = true;
		
		while(alive) {
			if(state==WAITING) {
				//Check
			}
			else if(state==ATTACKING) {
				//Check
				
				//Move
				
				//Attack
			}			
		}
		
	}
	
	
	
	
	//a Shitload of getters/setters
	
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
	
}
