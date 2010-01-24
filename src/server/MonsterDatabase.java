package server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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

 // TODO Failsafe unable to load monsterlist to hardcoded default.
 // TODO Possibility to backup monsterlists.

public class MonsterDatabase {
	
	private static Monster monster;
	private static Random randomize;
	private static int random;
	private static ArrayList<Monster> critterList = new ArrayList<Monster>();
	private static FileInputStream fis;
	private static BufferedInputStream bis;
	private static DataInputStream dis;
	private static BufferedReader reader;
	private static final String cd = "monster_database//";
	private static String temp;
	
	public MonsterDatabase() {
		getCritterList();
		getRandomMonster();
		getRandomMonster();
		getRandomMonster();
		getRandomMonster();
		getRandomMonster();
		getRandomMonster();
		getRandomMonster();
		getRandomMonster();
	}
	
	
	private static int getCritterList() {
		
		try {
			fis = new FileInputStream(cd + "critter.dat");
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
						System.out.println("ERROR: Critter database may be corrupt");
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
					monster.setDamage(Integer.valueOf(temp.substring((temp.indexOf('='))+1)));
					
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
					
					critterList.add(monster);
					i++; //Another Monster read
				}
				
				System.out.println("Monsters added to critterList: "+i);
				
			} catch (IOException e) {
				System.out.println("FATAL ERROR: Unable to Read Critter List");
			}
				
			fis.close();
			bis.close();
			dis.close();
			reader.close();
				
			
		} catch (FileNotFoundException e) {
			System.out.println(cd + "\\critter.dat");
			System.out.println("FATAL ERROR: Unable to Load Critter List");
			return -1;
		} catch (IOException e) {
			System.out.println("FATAL ERROR: Unable to Read Critter List");
		}
		
		return 0;
	}
	
	private static void getLowLevelList() {
		
	}
	
	private static void getMediumLevelList() {
		
	}
	
	private static void getHighLevelList() {
		
	}
	
	private static void getBossList() {
		
	}
	
	
	/**
	 * TODO returns
	 * @return
	 */
	public static Monster getRandomMonster() {
		 randomize = new Random();
		 random = randomize.nextInt(5); //0-4
		 monster = new Monster();
		
		switch(random=0) { //TODO Remove test value
			case 0:
				monsterCritter("Random");
				break;
			case 1:
				monsterLowLevel();
				break;
			case 2:
				monsterMediumLevel();
				break;
			case 3:
				monsterHighLevel();
				break;
			case 4:
				monsterBoss();
				break;
			default:
				monster = null;
				System.out.println("FATAL ERROR: Unable to randomize monster.");
		}
		
		return  monster;
	}
	
	private static void monsterBoss() {
		
		
	}

	private static void monsterHighLevel() {
		// TODO Auto-generated method stub
		
	}

	private static void monsterMediumLevel() {
		// TODO Auto-generated method stub
		
	}

	private static void monsterLowLevel() {
		
		
	}
	
	private static void monsterCritter(String name) {
		if(name=="Random") {
			randomize = new Random();
			random = randomize.nextInt(critterList.size());
			
			Monster asdf = new Monster();
			
			asdf = critterList.get(random);
			System.out.println("Name: "+asdf.getName());
			System.out.println("Health: "+asdf.getHealth());
			System.out.println("Damage: "+asdf.getDamage());
			System.out.println("Level: "+asdf.getLevel());
			System.out.println("Aggro: "+asdf.getAggro());
			System.out.println("Spawn: "+asdf.getSpawnLocation());
			System.out.println("Boss: "+asdf.isBoss());
			System.out.println("--------------");
		}

		
		
	}
	
	public static void main(String[] args) {
		new MonsterDatabase();
	}
}
