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
			if(!specialCommand(message)){		//check if it's a special command or not
				System.out.println( "MSG "+ChatSrv.getTime()+": "+message );
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
	
	void sendTo( String message ) {
		//synchronized( ChatSrv.enumOutputStreams ) {;		//sync so that no other thread screws this one over
		try {
			DataOutputStream dos = new DataOutputStream( socket.getOutputStream() );	//get outputstreams
			dos.writeUTF( message );		//and send message
		} catch( IOException ie ) { 
			System.out.println( ChatSrv.getTime()+": "+ie ); 		//failmsg
		}
	}
	
	public boolean specialCommand( String msg ){
		if( msg.substring(0, 5).equals("/nick")){	//expecting a hello-message at first connection
			username = msg.substring(6);
			System.out.println( "              username is \""+username+"\"" );
			server.sendToAll( "-> "+username+" joined, "+ChatSrv.getPeople()+" users online");
			return true;
		}else if(msg.substring(0, 6).equals("/users")){
			sendTo( Integer.toString( ChatSrv.getPeople() ) );
		}
		return false;
	}
}
