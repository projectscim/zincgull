package server;
import java.io.*;
import java.net.*;

public class MapSrvThread extends Thread {
	private MapSrv server;
	private Socket socket;
	
	private String username;

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
					server.sendToAll( coords );
					String[] temp;
					temp = coords.split(":");
					username = temp[4];
					System.out.println( "MAP "+MapSrv.getTime()+": COORDS: "+coords );
				}
			}
		} catch( EOFException ie ) {		//no failmsg
		} catch( IOException ie ) {
		} finally {
			server.removeConnection( socket, username );	//closing socket when connection is lost
		}
	}
	
	public boolean specialCommand( String msg ){
		if( msg.substring(0, 6).equals("/hello") ){
			username = msg.substring(7);
			System.out.println( "              "+username+" joined, "+ChatSrv.getPeople()+" users online" );
			return true;
		}
		return false;
	}
}
