package server;

import java.sql.SQLException;
import java.util.Random;

/**
 * (the) MonsterService keeps track of monsters in play and spawn new ones.
 * <p>
 * Monster spawns, spawns new threads of the Monster-class.
 * 
 * @author Andreas
 */
public class MonsterService extends Thread {
	
	private static int monsterCount;
	private static int monstersPlains;
	private static int monstersDesert;
	private static int monstersForest;
	
	private static int monsterLimit;
	private static boolean isSurge;
	private static int chanceOfSurge; //one in ..
	private static int chanceToEndSurge; //one in ..
	private static long sleep;
	
	private static Monster monster[] = new Monster[30];
	private static final boolean active = true; //Monsters spawned are always active/alive, active is used to increase readability.
	
	public MonsterService() {
		isSurge = false;
		monsterLimit = 15;
		chanceOfSurge = 5000; //one in .. every 'sleep'
		chanceToEndSurge = 20; //one in .. every 'sleep'
		sleep = 5000;
	}
	
	private static void monsterSurge() {
		/*Random randomize = new Random();
		int random;
		
		if(!isSurge) {
			random = randomize.nextInt(chanceOfSurge);
			
			if(random == 42) {
				isSurge=true;
				monsterLimit = monsterLimit * 2;
				
				while(monsterCount < monsterLimit) {
					monster = new Monster();
					monster = MonsterDatabase.getRandomMonster();
					monster.thread.start();
				}
			}
		}	
		else {
			random = randomize.nextInt(chanceToEndSurge);
			
			if(random==0) {
				isSurge = false;
				monsterLimit = monsterLimit / 2;
			}
		}*/
		
	}
	
	private synchronized static void spawn() {
		
		System.out.println(monsterCount);
		
		monster[monsterCount] = MonsterDatabase.getRandomMonster();
		monster[monsterCount].thread.start();
		
		monsterCount++;
		
	}
	
	public synchronized static void dyingMonster(Monster deadMonster) {
		System.out.println("this monster is dying:");
		deadMonster.printStats();
	}
	
	public synchronized static void setIsSurge(boolean statement) {
		if(statement) isSurge = true;
		else isSurge = false;
	}

	public synchronized void run() {
		while(true) {
			//Slight chance of Monster Surge starting
			//Significant chance of Monster Surge ending
			//monsterSurge();
			synchronized(new Object()) {
				spawn();
				
				monster[0].printStats();
			}
			
			
			//Sleep
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				System.out.println("MonsterService Failed to sleep");
				e.printStackTrace();
			}
			
		}
		
	}
	
}
