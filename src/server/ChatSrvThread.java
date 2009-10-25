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
				//System.out.println( "MSG "+ChatSrv.getTime()+": "+message );	//DEBUG
				if( !specialCommand(message) ){		//check if it's a special command or not
					System.out.println( "MSG "+ChatSrv.getTime()+": "+username+": "+message.substring(5) );
					server.sendToAll( username+": "+message.substring(5) );
				}
			}
		} catch( EOFException ie ) {		//no failmsg
		} catch( IOException ie ) {
		} finally {
			server.removeConnection( socket, username );	//closing socket when connection is lost
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
		if( msg.substring(0, 1).equals("/") ){
			if( msg.length() >= 6){
				if( msg.substring(0, 5).equals("/msg ") ){
					return false;
				}else if( msg.substring(0, 6).equals("/nick ") ){	//expecting a hello-message at first connection
					String tmp = msg.substring(6);
					System.out.println( "USR "+ChatSrv.getTime()+": "+username+" -> "+tmp );
					server.sendToAll( username+" became "+tmp );
					username = tmp;
					sendTo("/nick "+username);
					return true;
				}else if(msg.substring(0, 6).equals("/users") ){
					sendTo( "Currently there are "+Integer.toString( ChatSrv.getPeople() ) +" users online" );
					return true;
				}else if( msg.substring(0, 6).equals("/HELLO") ){	//expecting a hello-message at first connection
					if( msg.length() > 7 ){
						username = msg.substring(7);
						sendTo("Welcome to the Zincgull chatserver!");		//welcome-message
						String send = username+" joined, "+ChatSrv.getPeople()+" users online";
						System.out.println( "              "+send );
						server.sendToAll( "-> "+send);
						return true;
					}
					sendTo( "You need a nickname" );	//colorize this!
					return true;
				}
			}
		}
		sendTo( "Not a real command, type /help for possible commands" );	//colorize this!
		return true;
	}
}
