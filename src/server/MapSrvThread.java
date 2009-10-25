package server;
import java.io.*;
import java.net.*;

public class MapSrvThread extends Thread {
	private MapSrv server;
	private Socket socket;
	private Double user;

	public MapSrvThread( MapSrv server, Socket socket ) {
		this.server = server;
		this.socket = socket;
		start();
	}

	public void run() {	
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
			String[] temp;
			temp = msg.substring(7).split(":");
			user = Double.parseDouble(temp[4]);
			sendTo("/HELLO Welcome to the Zincgull mapserver!");		//welcome-message
			
			for (int i = 0; i < MapSrv.positions.size(); i++) {
				sendTo("/ADD "+MapSrv.positions.get(i));
			}

			MapSrv.positions.add(msg.substring(7));
			server.sendToAll("/ADD "+msg.substring(7));
			return true;
		}
		return false;
	}
}
