package server;
import java.io.*;
import java.net.*;

public class MapSrvThread extends Thread {
	private MapSrv server;
	private Socket socket;
	private Double user;
	private Monster monster;
	private boolean isMonster;

	public MapSrvThread( MapSrv server, Socket socket ) {
		this.server = server;
		this.socket = socket;
		isMonster = false;
		start();
	}
	
	public MapSrvThread(MapSrv server, Monster monster) {
		this.server = server;
		this.monster = monster; 
		isMonster = true;
		start();
	}

	public void run() {
		if(isMonster) monsterRun();
		else playerRun();
	}
	
	private void playerRun() {
		try {
			DataInputStream dis = new DataInputStream( socket.getInputStream() );	//gets messages from client
			while (true) {
				String coords = dis.readUTF();
				if (!specialCommand(coords)) {
					String[] temp;
					temp = coords.split(":");
					user = Double.parseDouble(temp[4]);
					MapSrv.positions.set(MapSrv.getId(user), coords);
					server.sendToAll( coords );
					System.out.println( "MAP "+MapSrv.getTime()+": COORDS: "+coords );
				}
			}
		} catch( EOFException ie ) {		//no failmsg
		} catch( IOException ie ) {
		} finally {
			server.removeConnection( socket, user );	//closing socket when connection is lost
		}
	}
	
	private void monsterRun() {
		System.out.println("MonsterRunMapServer");
		
		while(monster.getAlive()) {
			String coords = monster.getCoords();
			String[] temp;
			temp = coords.split(":");
			user = Double.parseDouble(temp[4]);
			MapSrv.positions.set(MapSrv.getId(user), coords);
			server.sendToAll(coords);
			System.out.println("MAP "+MapSrv.getTime()+": COORDS: "+coords);
			System.out.println(monster.getName());
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {}
		}

		//TODO Remove
	}
	
	void sendTo( String message ) {
		try {
			DataOutputStream dos = new DataOutputStream( socket.getOutputStream() );	//get outputstreams
			dos.writeUTF( message );		//and send message
		} catch( IOException ie ) { 
			System.out.println( ChatSrv.getTime()+": "+ie ); 		//failmsg
		}
	}
	
	public boolean specialCommand( String msg ){
		if( msg.substring(0, 6).equals("/HELLO") ){
			msg = msg.substring(7);
			String[] temp;
			temp = msg.split(":");
			user = Double.parseDouble(temp[4]);
			sendTo("/HELLO Welcome to the Zincgull mapserver!");		//welcome-message
			
			for (int i = 0; i < MapSrv.positions.size(); i++) {
				sendTo("/ADD "+MapSrv.positions.get(i));
			}

			MapSrv.positions.add(msg);
			server.sendToAll("/ADD "+msg);
			return true;
		}
		return false;
	}
}
