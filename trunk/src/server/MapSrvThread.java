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
				server.sendToAll( coords );
				String[] temp;
				temp = coords.split(":");
				username = temp[4];
				System.out.println( "MAP "+MapSrv.getTime()+": COORDS: "+coords );
			}
		} catch( EOFException ie ) {		//no failmsg
		} catch( IOException ie ) {
		} finally {
			server.removeConnection( socket, username );	//closing socket when connection is lost
		}
	}
}
