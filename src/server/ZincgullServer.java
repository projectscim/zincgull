package server;

import java.sql.SQLException;

public class ZincgullServer {
	
	private static MonsterDatabase MD;
	private static MonsterService MS;
	private static MapSrv MapS;
	private static ChatSrv CS;
	
	private static final int chatPort = 49050;
	private static final int mapPort = 49051;
	
	public static void main(String[] args) throws SQLException, InterruptedException {
		System.out.println("Starting MonsterDatabase");
		MD = new MonsterDatabase();
		Thread.sleep(5000);
		
		System.out.println("Starting ChatServer");
		CS = new ChatSrv(chatPort);
		CS.start();
		Thread.sleep(5000);
		
		System.out.println("Starting MapServer");
		MapS = new MapSrv(mapPort);
		MapS.start();
		Thread.sleep(5000);
		
		System.out.println("Starting MonsterService");
		MS = new MonsterService(MapS);
		MS.start();
		
		
		while(true) {
			try {
				Thread.sleep(120000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
