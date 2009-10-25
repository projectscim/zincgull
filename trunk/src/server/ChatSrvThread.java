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
			System.out.println( "MSG "+ChatSrv.getTime()+": "+message );
			
			if( !specialCommand1(message) ){		//check if it's a special command or not
				System.out.println( "MSG "+ChatSrv.getTime()+": "+username+": "+message );
				server.sendToAll( username+": "+message.substring(5) );
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
	
	public enum Cmd{	//list available commands
		msg, nick, hello, users;
	}
	
	public boolean specialCommand3( String msg ){
		//get the command-part
		int space = msg.indexOf(" ");
		String cmd = msg;
		if(space != -1){
			cmd = msg.substring(1, space-1);
		}
		
		if( cmd.substring(0, 1).equals("/") ){
			switch (Cmd.valueOf(cmd)) {
			case hello:
				if( msg.length() > 7 ){
					username = msg.substring(7);
					System.out.println( "              username is \""+username+"\"" );
					server.sendToAll( "-> "+username+" joined, "+ChatSrv.getPeople()+" users online");
					return true;
				}
				sendTo( "You need to specify a nickname" );	//colorize this!
				return false;
			case users:
				sendTo( "Currently there are "+Integer.toString( ChatSrv.getPeople() ) +" users online" );
				return true;
			case nick:
				if( msg.length() > 6 ){
					String tmp = msg.substring(6);
					System.out.println( "USR "+ChatSrv.getTime()+": "+username+" -> "+tmp );
					server.sendToAll( username+" became "+tmp );
					username = tmp;
					return true;
				}
				sendTo( "You need to specify your new nickname" );	//colorize this!
				return false;
			case msg:
				return false;
			default:
				sendTo( "Not a real command" );	//colorize this!
				return false;
			}
		}
		return false;
	}
	
	public boolean specialCommand2( String msg ){
		if( msg.substring(0, 1).equals("/") && !msg.substring(0, 5).equals("/msg ") ){
			if( msg.substring(0, 6).equals("/nick ") ){	//expecting a hello-message at first connection
				String tmp = msg.substring(6);
				System.out.println( "USR "+ChatSrv.getTime()+": "+username+" -> "+tmp );
				server.sendToAll( username+" became "+tmp );
				username = tmp;
				return true;
			}else if(msg.substring(0, 6).equals("/users") ){
				sendTo( "Currently there are "+Integer.toString( ChatSrv.getPeople() ) +" users online" );
				return true;
			}else if( msg.substring(0, 7).equals("/hello ") ){	//expecting a hello-message at first connection
				username = msg.substring(7);
				System.out.println( "              username is \""+username+"\"" );
				server.sendToAll( "-> "+username+" joined, "+ChatSrv.getPeople()+" users online");
				return true;
			}else{
				sendTo( "Not a real command" );	//colorize this!
			}
			return true;
		}
		return false;
	}
	
	public boolean specialCommand1( String msg ){
		if( msg.substring(0, 1).equals("/") ){
			if( msg.length() >= 6){
				if( msg.substring(0, 5).equals("/msg ") ){
					return false;
				}else if( msg.substring(0, 6).equals("/nick ") ){	//expecting a hello-message at first connection
					String tmp = msg.substring(6);
					System.out.println( "USR "+ChatSrv.getTime()+": "+username+" -> "+tmp );
					server.sendToAll( username+" became "+tmp );
					username = tmp;
					return true;
				}else if(msg.substring(0, 6).equals("/users") ){
					sendTo( "Currently there are "+Integer.toString( ChatSrv.getPeople() ) +" users online" );
					return true;
				}else if( msg.substring(0, 6).equals("/hello") ){	//expecting a hello-message at first connection
					if( msg.length() > 7 ){
						username = msg.substring(7);
						System.out.println( "              username is \""+username+"\"" );
						server.sendToAll( "-> "+username+" joined, "+ChatSrv.getPeople()+" users online");
						return true;
					}
					sendTo( "You need a nickname" );	//colorize this!
					return true;
				}
			}
		}
		sendTo( "Not a real command" );	//colorize this!
		return true;
	}
}
