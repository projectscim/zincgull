package server;

import java.sql.SQLException;
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
	private static int monstersPlains;
	private static int monstersDesert;
	private static int monstersForest;
	
	private static int monsterLimit;
	private static boolean isSurge;
	private static int chanceOfSurge; //one in ..
	private static int chanceToEndSurge; //one in ..
	private static long sleep;
	private static Logger log;
	
	private static Monster[] monster = new Monster[30];
	private static final boolean active = true; //Monsters spawned are always active/alive, active is used to increase readability.
	
	public MonsterService() {
		log = Logger.getLogger("MonsterService");
		BasicConfigurator.configure();
		log.info("MonsterService created");
		
		monsterLimit = 15;
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
		monster[monstersSpawned] = new Monster();
		processDbMonster(MonsterDatabase.getRandomMonster(), monster[monstersSpawned]);
		
		//Tell debug
		log.debug("Spawned: \""+monster[monstersSpawned].getName()+"\" in: "+monster[monstersSpawned].getSpawnLocation());
		
		//Start new Monster
		monster[monstersSpawned].thread.start();
		
		//Count
		monstersSpawned++;
		monsterCount++;
		log.debug("Monsters Spawned total: "+monstersSpawned);
		log.debug("Monsters Alive: "+monsterCount);
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
