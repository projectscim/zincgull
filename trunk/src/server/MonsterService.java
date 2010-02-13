package server;

import java.util.Random;

/**
 * (the) MonsterService keeps track of monsters in play and spawn new ones.
 * <p>
 * Monster spawns, spawns new threads of the Monster-class.
 * MonsterService can be run as a stand-alone application but is intended to be used as a part of the Zincgull-server.
 * 
 * @author Andreas
 */
public class MonsterService implements Runnable {
	
	Thread thread = new Thread(this);
	
	private static int monsterCount;
	private static int monstersPlains;
	private static int monstersDesert;
	private static int monstersForest;
	
	private static int monsterLimit;
	private static boolean isSurge;
	private static int chanceOfSurge; //one in ..
	private static int chanceToEndSurge; //one in ..
	private static long sleep;
	
	private static Monster monster;
	private static final boolean active = true; //Monsters spawned are always active/alive, active is used to increase readability.
	
	public MonsterService() {
		isSurge = false;
		monsterLimit = 15;
		chanceOfSurge = 5000; //one in .. every 'sleep'
		chanceToEndSurge = 20; //one in .. every 'sleep'
		sleep = 5000;
	}
	
	public static void main(String[] args) {
		new MonsterService();
	}
	
	private static void monsterSurge() {
		Random randomize = new Random();
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
		}
		
	}
	
	private static void spawn() {
		
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
		
			spawn();
			
			//Sleep 5s
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				System.out.println("MonsterService Failed to sleep");
				e.printStackTrace();
			}
		}
		
	}
	
}
