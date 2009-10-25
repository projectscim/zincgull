package server;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.util.*;

public class MapSrv {
	private ServerSocket ss;
	//this is used to don't have to create a DOS every time you are writing to a stream
	private Hashtable<Socket, DataOutputStream> outputStreams = new Hashtable<Socket, DataOutputStream>();
	protected static LinkedList<String> positions = new LinkedList<String>();
	
	// Constructor and while-accept loop
	public MapSrv( int port ) {
		try {
			listen( port );
		} catch (IOException e) {
			System.out.println( "ERR "+getTime()+": Something failed");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	// Usage: java Server <port>
	static public void main( String args[] ){
		new MapSrv( 49051 );	//create server
	}
	
	private void listen( int port ) throws IOException {
		ss = new ServerSocket( port );
		System.out.println( "INF "+getTime()+": Started the Zincgull Mapserver on port "+port+"\n              listening on "+ss );
		
		while (true) {	//accepting connections forever
			Socket s = ss.accept();		//grab a connection
			System.out.println( "USR "+getTime()+": New connection from "+s );	//msg about the new connection
			DataOutputStream dos = new DataOutputStream( s.getOutputStream() );	//DOS used to write to client
			getOutputStreams().put( s, dos );		//saving the stream
			new MapSrvThread( this, s );		//create a new thread for the stream
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
	
	void removeConnection( Socket s, double d ) {		//run when connection is discovered dead
		synchronized( getOutputStreams() ) {		//dont mess up sendToAll
			positions.remove(getId(d));
			System.out.println( "USR "+getTime()+": Lost connection from "+s );
			getOutputStreams().remove( s );
			sendToAll("/SUB "+d);
			if(positions.isEmpty()) System.out.println( "USR "+getTime()+": No users online" );
			try {
				s.close();
			} catch( IOException ie ) {
				System.out.println( "ERR "+getTime()+": Error closing "+s );
				ie.printStackTrace();
			}
		}
	}
	
	public static int getId(Double d){
		String[] tmp;
		for (int i = 0; i < positions.size(); i++) {
			tmp = positions.get(i).split(":");
			if( tmp[4].equals( Double.toString(d) ) ){	//needs to be unique
				return i;
			}
		}
		return 0;
	}
	
	public static String getTime(){
		DateFormat time = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		Date date = new GregorianCalendar().getTime();
		return time.format(date);
	}

	public void setOutputStreams(Hashtable<Socket, DataOutputStream> outputStreams) {
		this.outputStreams = outputStreams;
	}

	public Hashtable<Socket, DataOutputStream> getOutputStreams() {
		return outputStreams;
	}
}

