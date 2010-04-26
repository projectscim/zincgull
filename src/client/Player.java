package client;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import local.Database;

public class Player extends Sprite {
	
	//stats
	private String name;
	private int maxHealth;
	private int health;
	private int damage;
	private int level;
	private int xp;
	private boolean gm;
	
	public Player(int id) {
		this.id = id;
		turned = 1;
		
		getStats();
	}
	
	public Player(int x, int y, int s, int t, int i){
		this.xpos = x;
		this.ypos = y;
		this.speed = s;
		this.turned = t;
		this.id = i;
	}
	
	private int getStats() {
		Connection conn;
		long reconnTime = 3000;
		String SQL = "SELECT name, maxHealth, health, damage, level, xp, gm FROM chars WHERE id="+id;
		
		//Try to connect to DB, attempt reconnect upon failure, return -1 if total failure 
		int i = 0;
		while ((conn = Database.connect()) == null) {
			if(i++>3) return -1;
			
			//Sleep a bit
			try {
				Thread.sleep(reconnTime);
			} catch (InterruptedException e) {}
		}
		
		try {
			Statement stmt;
			ResultSet rs;
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);
			
			//Check number of results
			if(rs.getFetchSize() != 1) return -1;
			
			if(rs.next()) {
				this.name = rs.getString("name");
				this.maxHealth = rs.getInt("maxHealth");
				this.health = rs.getInt("health");
				this.damage = rs.getInt("damage");
				this.level = rs.getInt("level");
				this.xp = rs.getInt("xp");
				this.gm = (rs.getInt("gm")==1) ? true : false;
			}
			
			conn.close();	
			
		} catch (SQLException e) {
			System.out.println("SQLException, statement: "+SQL);
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public double getId() {
		return id;
	}
}
