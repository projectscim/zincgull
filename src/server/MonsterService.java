package server;

import java.util.LinkedList;
import java.util.Random;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * (the) MonsterService keeps track of monsters in play and spawn new ones.
 * <p>
 * Monster spawns, spawns new threads of the Monster-class.
 * 
 * @author Andreas
 */
public class MonsterService extends Thread {
		
	private static int monsterCount;
	private static int monstersSpawned; //Sets id
	//private static int monstersPlains;
	//private static int monstersDesert;
	//private static int monstersForest;
	
	private static int monsterLimit;
	private static boolean isSurge;
	private static int chanceOfSurge; //one in ..
	private static int chanceToEndSurge; //one in ..
	private static long sleep;
	private static Logger log;
	
	private static Monster monster;
	private static MapSrv mapSrv;
	private static LinkedList<Monster> monsterList = new LinkedList<Monster>();
	
	public MonsterService(MapSrv mapSrv) {
		log = Logger.getLogger("MonsterService");
		BasicConfigurator.configure();
		log.info("MonsterService created");
		
		MonsterService.mapSrv = mapSrv;
		monsterLimit = 1;
		chanceOfSurge = 5000; //one in .. every 'sleep'
		chanceToEndSurge = 10; //one in .. every 'sleep'
		log.info("Monster limit is set to: "+monsterLimit);
		log.info("Chance of Surge is set to: "+chanceOfSurge);
		log.info("Chance to end surge is set to: "+chanceToEndSurge);
		
		sleep = 5000;
		isSurge = false;
		log.info("Once started, MonsterService sleep: "+(sleep/1000)+" s during each loop");
		log.info("Ongoing Surge: "+isSurge);
	}
	
	private static void processDbMonster(Monster template, Monster newMonster) {
		//TODO Fail Safe - Checks
		log.debug("Processing monster from DB");
		
		newMonster.setName(template.getName());
		newMonster.setHealth(template.getHealth());
		newMonster.setDamage(template.getDamage());
		newMonster.setLevel(template.getAggro());
		newMonster.setAggro(template.getAggro());
		newMonster.setSpawnLocation(template.getSpawnLocation());
		newMonster.setBoss(template.isBoss());
		
	}
	
	private static void monsterSurge() {
		Random randomize = new Random();
		int random;
		int surgeInt = 42;
		int surgeEnd = 0;
		
		if(!isSurge) {
			random = randomize.nextInt(chanceOfSurge);
			log.debug("Randomizing for Surge, "+surgeInt+" will start surge");
			log.debug("Randomized: "+random);
			
			if(random == surgeInt) {
				log.info("MONSTER SURGE!");
				isSurge=true;
				monsterLimit = monsterLimit * 2;
				
				while(monsterCount < monsterLimit) {
					spawn();
				}
			}
		}
		else {
			random = randomize.nextInt(chanceToEndSurge);
			log.debug("Randomizing to end Surge, "+surgeEnd+" will end surge");
			log.debug("Randomized: "+random);
			
			if(random == surgeEnd) {
				log.info("MONSTER SURGE Ended");
				isSurge = false;
				monsterLimit = monsterLimit / 2;
			}
		}
		
	}
	
	private static void normal() {
		if(monsterLimit > monsterCount) {
			spawn();
		}
		else {
			log.debug("Not Spawning new Monster due to limit");
			log.debug("MonsterLimit: "+monsterLimit);
			log.debug("Monsters Alive: "+monsterCount);
		}
	}
	
	private static void spawn() {
		log.debug("Spawning new Monster");
		
		//Set up new Monster
		//monster[monstersSpawned] = new Monster();
		monster = new Monster();
		processDbMonster(MonsterDatabase.getRandomMonster(), monster);
		
		//Set ID!!
		monster.setId(monstersSpawned ); //starts from 0 until decent id-system is in place          (+1, monstersSpwaned havn't been up'd yet.)
		
		//Tell debug
		log.debug("Spawned: \""+monster.getName()+"\" in: "+monster.getSpawnLocation()+" Id: "+monster.getId());
		
		//Add new monster to MapServer
		addToMap(monster.getId(),monster.getXpos(),monster.getYpos(),monster.getTurned(),monster.getSpeed());
		
		//It's alive.
		monster.setAlive(true);
		
		//Add to list
		monsterList.add(monster); //atm, monsterId will be same as index. TODO A decent id-system.
		
		//Start new Monster
		monsterList.get((monster.getId())).thread.start();
		
		//Count
		monstersSpawned++;
		monsterCount++;
		log.debug("Monsters Spawned total: "+monstersSpawned);
		log.debug("Monsters Alive: "+monsterCount);
	}
	
	private static void addToMap(int id, int xpos, int ypos, int turned, int speed) {
		String spawnCoords;
		
		if(xpos == Monster.DEFAULT_XPOS  && ypos == Monster.DEFAULT_YPOS) {
			//TODO Calculate and set spawn area.
		}
		
		spawnCoords = String.valueOf(id)+":"+String.valueOf(xpos)+":"+String.valueOf(ypos)+":"+String.valueOf(turned)+":"+String.valueOf(speed);
		
		mapSrv.addMonster(id, spawnCoords);
	}

	public static void dyingMonster(Monster deadMonster) {
		log.debug("This Monster is dying: \""+deadMonster.getName()+"\"");
		
		monsterCount--;
		log.debug("MonsterCount is now: "+monsterCount);
	}
	
	public static void setIsSurge(boolean statement) {
		if(statement) isSurge = true;
		else isSurge = false;
	}

	public void run() {
		while(true) {
			//Slight chance of Monster Surge starting
			//Significant chance of Monster Surge ending
			monsterSurge();
			
			//Normal spawn
			normal();
			
			//Sleep
			try {
				log.debug("MonsterService will sleep for "+(sleep/1000)+" s");
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				log.debug("MonsterService Failed to sleep");
				e.printStackTrace();
			}
			
		}
		
	}
	
}
