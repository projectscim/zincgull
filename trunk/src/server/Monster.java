package server;

/**
 * 
 * 
 * 
 * @author Andreas
 */
public class Monster implements Runnable {
	
	private String name;
	private int damage;
	private int health;
	private int level;
	private int aggro;
	private String spawnLocation;
	private boolean boss;

	
	public Monster() {
		
	}
	
	public void printStats() {
		System.out.println("Name: "+name);
		System.out.println("Health: "+health);
		System.out.println("Damage: "+damage);
		System.out.println("Level: "+level);
		System.out.println("Aggro: "+aggro);
		System.out.println("Spawn: "+spawnLocation);
		System.out.println("Boss: "+boss);
		System.out.println("--------------");
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

	public void run() {

		while(true) {
			//TODO Stuff
		}
		
	}
	
}
