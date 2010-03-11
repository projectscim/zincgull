package server;

import java.sql.SQLException;

public class ZincgullServer {
	
	private static MonsterDatabase MD;
	private static MonsterService MS;
	private static MapSrv MapS;
	
	public static void main(String[] args) throws SQLException, InterruptedException {
		System.out.println("Starting MonsterDatabase");
		MD = new MonsterDatabase();
		Thread.sleep(5000);
		
		System.out.println("Starting MapServer");
		MapS = new MapSrv();
		MapS.start();
		Thread.sleep(5000);
		
		System.out.println("Starting MonsterService");
		MS = new MonsterService(MapS);
		MS.start();
		
		
		while(true) {
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Main Server has Slept");
		}
		
	}
	
}
