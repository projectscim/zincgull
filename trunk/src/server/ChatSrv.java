package server;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.util.*;

public class ChatSrv {
	private ServerSocket ss;
	//this is used to don't have to create a DOS every time you are writing to a stream
	private Hashtable<Socket, DataOutputStream> outputStreams = new Hashtable<Socket, DataOutputStream>();
	private static int people = 0;
	
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
		System.out.println( "INF "+getTime()+": Started the Zincgull chatserver on port "+port+"\n              listening on "+ss );
		
		while (true) {	//accepting connections forever
			Socket s = ss.accept();		//grab a connection
			System.out.println( "USR "+getTime()+": New connection from "+s );	//msg about the new connection
			DataOutputStream dos = new DataOutputStream( s.getOutputStream() );	//DOS used to write to client
			setPeople(getPeople() + 1);
			dos.writeUTF("Welcome to the Zincgull chatserver!");
			getOutputStreams().put( s, dos );		//saving the stream
			new ChatSrvThread( this, s );		//create a new thread for the stream
		}
	}
	// Enumerate all OutputStreams
	Enumeration<DataOutputStream> enumOutputStreams() {
		return getOutputStreams().elements();
	}

	void sendToAll( String message ) {
		synchronized( getOutputStreams() ) {		//sync so that no other thread screws this one over
			for (Enumeration<?> e = enumOutputStreams(); e.hasMoreElements(); ) {
				DataOutputStream dos = (DataOutputStream)e.nextElement();		//get all outputstreams
				try {
					dos.writeUTF( message );		//and send message
				} catch( IOException ie ) { 
					System.out.println( getTime()+": "+ie ); 		//failmsg
				}
			}
		}
	}
	
	void removeConnection( Socket s, String username ) {		//run when connection is discovered dead
		synchronized( getOutputStreams() ) {		//dont mess up sendToAll
			System.out.println( "USR "+getTime()+": Lost connection from "+s );
			getOutputStreams().remove( s );
			setPeople(getPeople() - 1);	//one less online
			if(getPeople() == 0) System.out.println( "INF "+getTime()+": No users online" );
			sendToAll("<- "+username+" left, "+getPeople()+" left online");	//tell everyone that someone left
			try {
				s.close();
			} catch( IOException ie ) {
				System.out.println( "ERR "+getTime()+": Error closing "+s );
				ie.printStackTrace();
			}
		}
	}
	
	public void listClients(){
		
	}
	
	public static String getTime(){
		DateFormat time = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		Date date = new GregorianCalendar().getTime();
		return time.format(date);
	}

	public void setPeople(int people) {
		ChatSrv.people = people;
	}

	public static int getPeople() {
		return people;
	}

	public void setOutputStreams(Hashtable<Socket, DataOutputStream> outputStreams) {
		this.outputStreams = outputStreams;
	}

	public Hashtable<Socket, DataOutputStream> getOutputStreams() {
		return outputStreams;
	}
}

