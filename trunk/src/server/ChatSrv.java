package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatSrv {
	private ServerSocket ss;
	//this is used to dont have to creata a DOS everytime you are writing to a stream
	private Hashtable<Socket, DataOutputStream> outputStreams = new Hashtable<Socket, DataOutputStream>();
	
	// Constructor and while-accept loop
	public ChatSrv( int port ) throws IOException {
		listen( port );
	}
	
	// Usage: java Server <port>
	static public void main( String args[] ){
		try {
			new ChatSrv( 49050 );	//create server
		} catch (IOException e) {
			System.out.println( "ERR -> Something failed");
			e.printStackTrace();
		}
	}
	
	private void listen( int port ) throws IOException {
		ss = new ServerSocket( port );
		System.out.println( "INF -> Started the Zincgull chatserver on port "+port);
		System.out.println( "    --> listening on "+ss );
		
		while (true) {	//accepting connections forever
			Socket s = ss.accept();		//grab a connection
//			getNickname(s);				//get the nickname from the socket
			System.out.println( "USR -> New connection from "+s );	//msg about the new connection
			DataOutputStream dos = new DataOutputStream( s.getOutputStream() );	//DOS used to write to client
			dos.writeUTF("Welcome to the Zincgull chatserver!");
			outputStreams.put( s, dos );		//saving the stream
			new ChatSrvThread( this, s );		//create a new thread for the stream
		}
	}
	// Enumerate all OutputStreams
	Enumeration<DataOutputStream> getOutputStreams() {
		return outputStreams.elements();
	}

	void sendToAll( String message ) {
		synchronized( outputStreams ) {		//sync so that no other thread screws this one over
			for (Enumeration<?> e = getOutputStreams(); e.hasMoreElements(); ) {
				DataOutputStream dos = (DataOutputStream)e.nextElement();		//get all outputstreams
				try {
					dos.writeUTF( message );		//and send message
				} catch( IOException ie ) { 
					System.out.println( ie ); 		//failmsg
				}
			}
		}
	}
	
	void removeConnection( Socket s ) {		//run when connection is discovered dead
		synchronized( outputStreams ) {		//dont mess up sendToAll
			System.out.println( "USR -> Lost connection from "+s );
			outputStreams.remove( s );
			try {
				s.close();
			} catch( IOException ie ) {
				System.out.println( "ERR -> Error closing "+s );
				ie.printStackTrace();
			}
		}
	}
}

