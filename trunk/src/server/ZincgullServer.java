package server;

import java.sql.SQLException;

public class ZincgullServer {
	
	private static MonsterDatabase MD;
	private static MonsterService MS;
	
	public static void main(String[] args) throws SQLException, InterruptedException {
		MD = new MonsterDatabase();
		Thread.sleep(5000);
		System.out.println("Starting MonsterService");
		MS = new MonsterService();
		MS.start();
		
		
		
		System.out.println("lol3");
		
		while(true) {
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Slept");
		}
		
	}
	
}
