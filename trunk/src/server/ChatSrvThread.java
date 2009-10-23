package server;
import java.io.*;
import java.net.*;

public class ChatSrvThread extends Thread {
	private ChatSrv server;
	private Socket socket;

	public ChatSrvThread( ChatSrv server, Socket socket ) {
		this.server = server;
		this.socket = socket;
		start();
	}

	public void run() {	
		try {
			DataInputStream din = new DataInputStream( socket.getInputStream() );	//gets messages from client
			while (true) {
			String message = din.readUTF();
			System.out.println( "MSG -> Mesage sent from "+socket+"\n    --> "+message );
			server.sendToAll( message );
			}
		} catch( EOFException ie ) {		//no failmsg
		} catch( IOException ie ) {
			// ie.printStackTrace();		//optional failmsg, mostly annoying red text
		} finally {
			server.removeConnection( socket );	//closing socket when connection is lost
		}
	}
}
