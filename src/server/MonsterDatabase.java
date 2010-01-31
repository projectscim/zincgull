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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

/**
 * The MonsterDatabase returns Monster objects.
 * <p>
 * It can return Monster objects with a large variety of methods, each picking monsters based on different requirements.
 * <p>
 * The MonserDatabase is dependent on correctly formated monster-databases, it's not very forgiving.
 * 
 * @author Andreas
 */

 // TODO Fail safe "unable to load monsterlist" to hardcoded default.
 // TODO Possibility to backup monsterlists.
 // TODO To get around the issue with easily corrupted databases: Make Monster Serializable - only allow creation of new monsters through internal methods.
 //       Quicker fix than to write code to make it fail safe. Very much preferable even? since it makes it possible to prevent OPed creatures.

public class MonsterDatabase {
	
	private static Monster monster;
	private static Random randomize;
	private static int random;
	private static ArrayList<Monster> critterList = new ArrayList<Monster>();
	private static ArrayList<Monster> lowLevelList = new ArrayList<Monster>();
	private static ArrayList<Monster> mediumLevelList = new ArrayList<Monster>();
	private static ArrayList<Monster> highLevelList = new ArrayList<Monster>();
	private static ArrayList<Monster> bossList = new ArrayList<Monster>();
	private static FileInputStream fis;
	private static BufferedInputStream bis;
	private static DataInputStream dis;
	private static BufferedReader reader;
	private static String temp;
	private static Connection conn = null;
	
	//filename of 
	private static final String md = "monster_database//";
	private static final String critter = "critter.dat";
	private static final String lowLevel = "lowLevel.dat";
	private static final String mediumLevel = "mediumLevel.dat";
	private static final String highLevel = "highLevel.dat";
	private static final String boss = "boss.dat";
	
	public MonsterDatabase() throws SQLException {
		loadCritters();
		loadLowLevels();
		loadMediumLevels();
		loadHighLevels();
		loadBosses();
		
		System.out.println("Loading Complete!\n");
		
		for(int i=0;i<7;i++) {
			monster = getRandomMonster();
			monster.printStats();
		}
		
		connectMySQL();
		
		conn.close();
	}
	
	private void connectMySQL() {
		String host = "localhost";
		String database = "arxe_java";
		String url = "jdbc:mysql://"+host+"/"+database;
		String user = "arxe";
		String pass = "asdf";
		
		conn = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, user, pass);
			
			System.out.println("Connection Successful");
			
		} catch (InstantiationException e) {
			System.out.println("fail1");
		} catch (IllegalAccessException e) {
			System.out.println("fail2");
		} catch (ClassNotFoundException e) {
			System.out.println("fail3");
		} catch (SQLException e) {
			System.out.println("fail4");
		}
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
	
	private void loadMonsters() {
		
	}
	
	private static int loadMonsters(String databaseName) {
		
		String type = databaseName.substring(0, databaseName.indexOf('.')); //Gets the filename of the database read in order to give accurate reports.
		
		try {
			fis = new FileInputStream(md + databaseName);
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
						System.out.println("ERROR: "+type+" database may be corrupt");
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
				
				System.out.println("Monsters added to "+type+"List: "+i);
				
			} catch (IOException e) {
				System.out.println("FATAL ERROR: Unable to read "+type+" list");
			}
				
			fis.close();
			bis.close();
			dis.close();
			reader.close();
				
			
		} catch (FileNotFoundException e) {
			System.out.println(md + databaseName);
			System.out.println("FATAL ERROR: Unable to Load "+type+" List");
			return -1;
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Unable to Read "+type+" List");
		}
		
		return 0;
	}
	
	
	/**
	 * TODO returns
	 * @return
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
	
	private static void randomMonster(String databaseName) {
		
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
	
	public static void main(String[] args) throws SQLException {
		new MonsterDatabase();
	}
}
