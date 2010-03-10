package server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import local.SecretDBinfo;

/**
 * The MonsterDatabase returns Monster objects.<br>
 * MonsterDatbase loads Monsters from a specified (hardcoded) MySQL-database, if connection to the database fails it tries to load from a local backup instead. 
 * <p>
 * It can return Monster objects with a variety of methods, each picking monsters based on different requirements:
 * <ul>
 * <li>Completely random - getRandomMonster()
 * <li>Specific monster via name - getMonsterByName(String)
 * <li>Specific monster based on name & type - getMonsterByName(String, String)
 * </ul>
 * Different types of monsters are stored in separated ArrayLists therefore it is somewhat faster to get monsters by name if type is provided.
 * If the type provided doesn't exist it's treated as null and all ArrayLists are searched.
 * <p>
 * The backup database (local file), is dependent on correctly formated monster-databases, it's not very forgiving.
 * 
 * @author Andreas
 */

 // TODO Possibility to backup monsterlists.

public class MonsterDatabase implements SecretDBinfo {
	
	private static Random randomize;
	private static int random;
	private static FileInputStream fis;
	private static BufferedInputStream bis;
	private static DataInputStream dis;
	private static BufferedReader reader;
	
	private static Monster monster;
	private static ArrayList<Monster> critterList = new ArrayList<Monster>();
	private static ArrayList<Monster> lowLevelList = new ArrayList<Monster>();
	private static ArrayList<Monster> mediumLevelList = new ArrayList<Monster>();
	private static ArrayList<Monster> highLevelList = new ArrayList<Monster>();
	private static ArrayList<Monster> bossList = new ArrayList<Monster>();
	
	//MySQL-Database (info from SecretDBinfo)
	private static Connection conn = null;
	
	//Local Backup Database 
	private static final String md = "monster_database//";
	private static final String DBextension = ".dat";
	
	//The names of the different monster-types.
	//Changes affect local backup and monster-fetching.
	private static final String critter = "critter";
	private static final String lowLevel = "lowLevel";
	private static final String mediumLevel = "mediumLevel";
	private static final String highLevel = "highLevel";
	private static final String boss = "boss";
	
	public MonsterDatabase() throws SQLException {
		
		connectMySQL();
		
		if(conn != null) {
			loadMonsters();
			conn.close();
		}
		else loadAllMonstersFromLocalDB();
	}
	
	/**
	 * Connects to MySQL-database.<br>
	 * Sets {@link #conn} if successful.<br>
	 * @see {@link #host}, {@link #user} and {@link #pass}
	 */
	private void connectMySQL() {
		conn = null;
		System.out.println("Connecting to MySQL-database..");
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, user, pass);
			System.out.println("Connection to MySQL-datbase established");
		} catch (InstantiationException e) {
			System.out.println("ERROR: InstantiationException\n" +
					"ERROR: Connection FAILED");
		} catch (IllegalAccessException e) {
			System.out.println("ERROR: IllegalAccessException\n" +
					"ERROR: Connection FAILED");
		} catch (ClassNotFoundException e) {
			System.out.println("ERROR: ClassNotFoundException\n" +
					"ERROR: Connection FAILED");
		} catch (SQLException e) {
			System.out.println("ERROR: SQLException\n" +
					"ERROR: Connection FAILED");
		}
	}
	
	/**
	 * Load Monster-objects from MySQL-database and stores them in appropriate ArrayList (as determined by the level of the loaded Monster).
	 * @throws SQLException
	 */
	private static void loadMonsters() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs;
		int level;
		
		rs = stmt.executeQuery("SELECT * FROM Monster ORDER BY level");
		while(rs.next()) {
			monster = new Monster();
			
			monster.setName(rs.getString("name"));
			monster.setHealth(rs.getInt("health"));
			monster.setDamage(rs.getInt("damage"));
			monster.setLevel(rs.getInt("level"));
			monster.setAggro(rs.getInt("aggro"));
			monster.setSpawnLocation(rs.getString("spawnLocation"));
			monster.setBoss((rs.getInt("boss")==1)?true:false); //tinyint
			
			level = monster.getLevel();
			if(monster.isBoss()) {
				bossList.add(monster);
			} else {
				if(level==0) critterList.add(monster);
				else if(level<=5) lowLevelList.add(monster);
				else if(level<=15) mediumLevelList.add(monster);
				else if(level<=25) highLevelList.add(monster);				
			}
		}
	}
	
	private static void loadAllMonstersFromLocalDB() {
		System.out.println("Loading local backup of the MonsterDB.");
		loadCritters();
		loadLowLevels();
		loadMediumLevels();
		loadHighLevels();
		loadBosses();
		System.out.println("Loading of Local Backup is Complete!\n");
	}
	
	private static void loadCritters() {
		loadMonsters(critter);
	}
	
	private static void loadLowLevels() {
		loadMonsters(lowLevel);
	}
	
	private static void loadMediumLevels() {
		loadMonsters(mediumLevel);
	}
	
	private static void loadHighLevels() {
		loadMonsters(highLevel);
	}
	
	private static void loadBosses() {
		loadMonsters(boss);
	}
	
	/**
	 * Reads Monster-objects from file and stores them in the appropriate ArrayList (as determined by databaseName).<br>
	 * Very dependent on the file being correctly formated.
	 * 
	 * @param databaseName - The name of the file containing the database (without file-extensions).
	 * @return 0 if database is existent, -1 otherwise.
	 */
	private static int loadMonsters(String databaseName) {
		String temp;
		
		try {
			fis = new FileInputStream(md + databaseName + DBextension);
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			reader = new BufferedReader(new InputStreamReader(dis));
			
			int i = 0;
			
			try {
				while(true) {
					monster = new Monster();
					
					//1-1. initial read
					temp = reader.readLine();
					if(temp == null) {
						break;
					}
					else if(temp.isEmpty()) {
						System.out.println("ERROR: "+databaseName+" database may be corrupt");
						break;
					}
					
					//1-2. get Name from initial reads
					temp = temp.substring(((temp.indexOf('='))+1));
					monster.setName(temp);
					
					//2. get Health
					temp = reader.readLine();
					monster.setHealth(Integer.valueOf(temp.substring((temp.indexOf('='))+1)));
					
					//3. get Damage
					temp = reader.readLine();
					monster.setDamage(Integer.valueOf(temp.substring((temp.indexOf('='))+1)));
					
					//4. get Level
					temp = reader.readLine();
					monster.setLevel(Integer.valueOf(temp.substring((temp.indexOf('='))+1)));
					
					//5. get Aggro
					temp = reader.readLine();
					monster.setAggro(Integer.valueOf(temp.substring((temp.indexOf('='))+1)));
					
					//6. get Spawn Location
					temp = reader.readLine();
					monster.setSpawnLocation(temp.substring(((temp.indexOf('='))+1)));
					
					//7. get Boss
					temp = reader.readLine();
					monster.setBoss(Boolean.valueOf(temp.substring((temp.indexOf('='))+1)));
					
					//8. get Blank
					temp = reader.readLine();
					/*if(temp!="\n"&&temp!=null) {
						System.out.println("FATAL ERROR: Critter Database is Corrupt");
						return -1;
					}*/
					
					//Add monster to correct ArrayList
					if(databaseName==critter) critterList.add(monster);
					else if(databaseName==lowLevel) lowLevelList.add(monster);
					else if(databaseName==mediumLevel) mediumLevelList.add(monster);
					else if(databaseName==highLevel) highLevelList.add(monster);
					else if(databaseName==boss) bossList.add(monster);
					
					i++; //Another Monster read
				}
				
				System.out.println("Monsters added to "+databaseName+"List: "+i);
				
			} catch (IOException e) {
				System.out.println("FATAL ERROR: Unable to read "+databaseName+" list");
			}
				
			fis.close();
			bis.close();
			dis.close();
			reader.close();
				
			
		} catch (FileNotFoundException e) {
			System.out.println(md + databaseName);
			System.out.println("FATAL ERROR: Unable to Load "+databaseName+" List");
			return -1;
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Unable to Read "+databaseName+" List");
		}
		
		return 0;
	}
	
	/**
	 * Searches all ArrayLists for the monster with the provided name.<br>
	 * Calls getMonsterByName(String,String) with null as the second argument.
	 * 
	 * @param name - The name of the Monster which should be returned (not case-sensitive).
	 * @return A Monster-object.
	 * @see #getMonsterByName(String, String)
	 */
	public static Monster getMonsterByName(String name) {
		monster = getMonsterByName(name, null);
		return monster;
	}
	
	/**
	 * Searches the appropriate ArrayList (as determined by type) for a Monster with the provided name.
	 * If type is null or not equal to any of the monster-types all ArrayLists will be searched.
	 * 
	 * @param name - The name of the Monster which should be returned (not case-sensitive).
	 * @param type - Null or one of the monster-types:
	 * <ul>
	 * <li>{@link #critter}
	 * <li>{@link #lowLevel}
	 * <li>{@link #mediumLevel}
	 * <li>{@link #highLevel}
	 * <li>{@link #boss}
	 * </ul>
	 * @return A Monster-object.
	 */
	public static Monster getMonsterByName(String name, String type) {
		int i = 0;
		boolean allLists = false;
		
		if(type!=null) {
			type = type.toLowerCase();
			if(type!=critter&&type!=lowLevel&&type!=mediumLevel&&type!=highLevel&&type!=boss) {
				allLists=true;
			}
		}
		else allLists = true;
		
		if(type==critter.toLowerCase() || allLists) {
			int size = critterList.size();
			i = 0;
			while(i<size) {
				if(critterList.get(i).getName().equalsIgnoreCase(name)) {
					monster = critterList.get(i);
					System.out.println("Monster found, returning: "+name);
					return monster;
				}
				i++;
			}
		}
		
		if(type==lowLevel.toLowerCase() || allLists) {
			int size = lowLevelList.size();
			i = 0;
			while(i<size) {
				if(lowLevelList.get(i).getName().equalsIgnoreCase(name)) {
					monster = lowLevelList.get(i);
					System.out.println("Monster found, returning: "+name);
					return monster;
				}
				i++;
			}
		}
		
		if(type==mediumLevel.toLowerCase() || allLists) {
			int size = mediumLevelList.size();
			i = 0;
			while(i<size) {
				if(mediumLevelList.get(i).getName().equalsIgnoreCase(name)) {
					monster = mediumLevelList.get(i);
					System.out.println("Monster found, returning: "+name);
					return monster;
				}
				i++;
			}
		}
		
		if(type==highLevel.toLowerCase() || allLists) {
			int size = highLevelList.size();
			i = 0;
			while(i<size) {
				if(highLevelList.get(i).getName().equalsIgnoreCase(name)) {
					monster = highLevelList.get(i);
					System.out.println("Monster found, returning: "+name);
					return monster;
				}
				i++;
			}
		}
		
		if(type==boss.toLowerCase() || allLists) {
			int size = bossList.size();
			i = 0;
			while(i<size) {
				if(bossList.get(i).getName().equalsIgnoreCase(name)) {
					monster = bossList.get(i);
					System.out.println("Monster found, returning: "+name);
					return monster;
				}
				i++;
			}
		}
		
		System.out.println("ERROR: getMonsterByName() was unable to find the monster: '"+name+"'");
		return null;
	}
	
	
	/**
	 * Calls the static randomMonster-method with a randomized argument (one of the 5 monster-types).
	 * @return A Monster-object.
	 * @see #randomMonster(String)
	 */
	public static Monster getRandomMonster() {
		 randomize = new Random();
		 random = randomize.nextInt(5); //0-4
		 monster = new Monster();
		
		switch(random) {
			case 0:
				randomMonster(critter);
				break;
			case 1:
				randomMonster(lowLevel);
				break;
			case 2:
				randomMonster(mediumLevel);
				break;
			case 3:
				randomMonster(highLevel);
				break;
			case 4:
				randomMonster(boss);
				break;
			default:
				monster = null;
				System.out.println("FATAL ERROR: Unable to randomize monster.");
		}
		
		return  monster;
	}
	
	/**
	 * Calls the randomMonster-method with type as argument.
	 * @see #randomMonster(String)
	 * 
	 * @param type - Compared to the name of the monster-types:
	 * <ul>
	 * <li>{@link #critter}
	 * <li>{@link #lowLevel}
	 * <li>{@link #mediumLevel}
	 * <li>{@link #highLevel}
	 * <li>{@link #boss}
	 * </ul>
	 * @return A Monster-object.
	 */
	public static Monster getRandomMonster(String type) {
		monster = new Monster();
		
		if(type==critter) randomMonster(critter);
		else if(type==lowLevel) randomMonster(lowLevel);
		else if(type==mediumLevel) randomMonster(mediumLevel);
		else if(type==highLevel) randomMonster(highLevel);
		else if(type==boss) randomMonster(boss);
		else System.out.println("FATAL ERROR: Unable to randomize monster.");
		
		return  monster;
	}
	
	/**
	 * Sets the static Monster-object to a random monster based on type.
	 * @param databaseName - Compared to the name of the monster-types:
	 * <ul>
	 * <li>{@link #critter}
	 * <li>{@link #lowLevel}
	 * <li>{@link #mediumLevel}
	 * <li>{@link #highLevel}
	 * <li>{@link #boss}
	 * </ul>
	 */
	private static void randomMonster(String databaseName) {
		//TODO Fail Safe empty list.
		if(databaseName==critter) {
			randomize = new Random();
			random = randomize.nextInt(critterList.size());
			monster = critterList.get(random);
		}
		else if(databaseName==lowLevel) {
			randomize = new Random();
			random = randomize.nextInt(lowLevelList.size());
			monster = lowLevelList.get(random);
		}
		else if(databaseName==mediumLevel) {
			randomize = new Random();
			random = randomize.nextInt(mediumLevelList.size());
			monster = mediumLevelList.get(random);
		}
		else if(databaseName==highLevel) {
			randomize = new Random();
			random = randomize.nextInt(highLevelList.size());
			monster = highLevelList.get(random);
		}
		else if(databaseName==boss) {
			randomize = new Random();
			random = randomize.nextInt(bossList.size());
			monster = bossList.get(random);
		}

	}
	
	/**
	 * Main-method to add the ability to run the MonsterDatbase as a StandAlone operation.
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {
		new MonsterDatabase();
	}
}
