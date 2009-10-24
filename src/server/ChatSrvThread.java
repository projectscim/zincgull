package server;
import java.io.*;
import java.net.*;

public class ChatSrvThread extends Thread {
	private ChatSrv server;
	private Socket socket;
	
	private String username;

	public ChatSrvThread( ChatSrv server, Socket socket ) {
		this.server = server;
		this.socket = socket;
		start();
	}

	public void run() {	
		try {
			DataInputStream dis = new DataInputStream( socket.getInputStream() );	//gets messages from client
			while (true) {
			String message = dis.readUTF();
			if( message.substring(0, 2).equals("->")){
				username = message.substring(3);
				System.out.println( "              username is \""+username+"\"" );
				server.sendToAll( "-> "+username+" joined, "+ChatSrv.getPeople()+" users online");
			}else{
				System.out.println( "MSG "+ChatSrv.getTime()+": Message from "+socket+"\n              "+message );
				server.sendToAll( message );
			}
			}
		} catch( EOFException ie ) {		//no failmsg
		} catch( IOException ie ) {
			// ie.printStackTrace();		//optional failmsg, mostly annoying red text
		} finally {
			server.removeConnection( socket, username );	//closing socket when connection is lost
		}
	}
}
