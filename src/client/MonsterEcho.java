package client;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import local.Database;

public class MonsterEcho {
	
	private int id;
	
	//Info
	private int monsterId;
	private String name;
	private int damage;
	private int maxHealth;
	private int health;
	private int level;
	private boolean boss;

	//Coordinates
	private int xpos;
	private int ypos;
	private int turned;
	
	//Database
	boolean connMadeHere = false;
	
	public MonsterEcho(int xpos, int ypos, int turned, int id, int monsterId, int health/*, Connection conn*/) {
		
		/*if(conn == null) {
			conn = Database.connect();
			connMadeHere = true;
		}
		
		if((id > 0) && (conn != null)) {
			this.id = id; //Check uniqueness on higher level.
			this.monsterId = monsterId; //not unique
			this.health = health;
			this.xpos = xpos;
			this.ypos = ypos;
			this.turned = turned;
			
			String SQL = "SELECT name, health, damage, level, boss FROM Monster WHERE id="+(String.valueOf(monsterId));
			
			try {
				Statement stmt = conn.createStatement();
				ResultSet rs;
				
				rs = stmt.executeQuery(SQL);		
				rs.next();
				
				this.name = (rs.getString("name"));
				this.damage = (rs.getInt("damage"));
				this.maxHealth = (rs.getInt("health"));
				this.level = (rs.getInt("level"));
				this.boss = ((rs.getInt("boss")==1) ? true : false);
				
			} catch (SQLException e) {
				System.out.println("SQLexception in MonsterEcho: ");
				System.out.println("stmt: "+SQL);
				e.printStackTrace();
			} finally {
				if(connMadeHere) {
					try {
						conn.close();
					} catch (SQLException e) {}
				}
			}
			
		}
		else {
			this.id = -1;
			this.name = "error";
			this.damage = 0;
			this.health = 1;
			this.level = -1;
			this.boss = false;
			
			this.xpos = -100;
			this.ypos = -100;
			this.turned = 0;
		}*/
		
		this.id = id; //Check uniqueness on higher level.
		this.monsterId = monsterId; //not unique
		this.health = health;
		this.xpos = xpos;
		this.ypos = ypos;
		this.turned = turned;
		
		
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMonsterId() {
		return monsterId;
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
	public int getmaxHealth() {
		return maxHealth;
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
	public int getXpos() {
		return xpos;
	}
	public void setXpos(int xpos) {
		this.xpos = xpos;
	}
	public int getYpos() {
		return ypos;
	}
	public void setYpos(int ypos) {
		this.ypos = ypos;
	}
	public int getTurned() {
		return turned;
	}
	public void setTurned(int turned) {
		this.turned = turned;
	}
	public boolean isBoss() {
		return boss;
	}
	public void setBoss(boolean boss) {
		this.boss = boss;
	}
	
}
