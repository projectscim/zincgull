package server;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.util.*;

public class ChatSrv {
	private ServerSocket ss;
	//this is used to dont have to creata a DOS everytime you are writing to a stream
	private Hashtable<Socket, DataOutputStream> outputStreams = new Hashtable<Socket, DataOutputStream>();
	private int people = 0;
	
	// Constructor and while-accept loop
	public ChatSrv( int port ) {
		try {
			listen( port );
		} catch (IOException e) {
			System.out.println( "ERR "+getTime()+": Something failed");
			e.printStackTrace();
		}
	}
	
	// Usage: java Server <port>
	static public void main( String args[] ){
		new ChatSrv( 49050 );	//create server
	}
	
	private void listen( int port ) throws IOException {
		ss = new ServerSocket( port );
		System.out.println( "INF "+getTime()+": Started the Zincgull chatserver on port "+port);
		System.out.println( "              listening on "+ss );
		
		while (true) {	//accepting connections forever
			Socket s = ss.accept();		//grab a connection
			System.out.println( "USR "+getTime()+": New connection from "+s );	//msg about the new connection
			DataOutputStream dos = new DataOutputStream( s.getOutputStream() );	//DOS used to write to client
			people++;
			dos.writeUTF("Welcome to the Zincgull chatserver! Online: "+people);
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
					System.out.println( getTime()+": "+ie ); 		//failmsg
				}
			}
		}
	}
	
	void removeConnection( Socket s ) {		//run when connection is discovered dead
		synchronized( outputStreams ) {		//dont mess up sendToAll
			System.out.println( "USR "+getTime()+": Lost connection from "+s );
			outputStreams.remove( s );
			people--;	//one less online
			if(people == 0) System.out.println( "INF "+getTime()+": No users online" );
			sendToAll("<- someone left, "+people+" left online");	//tell everyone that someone left
			try {
				s.close();
			} catch( IOException ie ) {
				System.out.println( "ERR "+getTime()+": Error closing "+s );
				ie.printStackTrace();
			}
		}
	}
	public static String getTime(){
		DateFormat time = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		Date date = new GregorianCalendar().getTime();
		return time.format(date);
	}
}

