package server;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.util.*;

public class MapSrv extends Thread {
	private ServerSocket ss;
	//this is used to don't have to create a DOS every time you are writing to a stream
	static private Hashtable<Socket, DataOutputStream> outputStreams = new Hashtable<Socket, DataOutputStream>();
	protected static LinkedList<String> positions = new LinkedList<String>();
	protected static LinkedList<String> monsterPositions = new LinkedList<String>();
	private static int port;
	
	public MapSrv(int port) {
		MapSrv.port = port;
	}
	
	// Usage: java Server <port>
	static public void main( String args[] ){
		port = 49051;
		new MapSrv(port);	//create server
	}
	
	public static void addMonster(int id, String coords) {
		//atm, monsterId will be same as index. TODO A decent id-system.
		monsterPositions.add(coords);
		sendToAll(coords);
	}
	
	public static void monsterUpdate(int id, String coords) {
		monsterPositions.add(coords);
		sendToAll(coords);
	}
	
	public void run() {
		try {
			ss = new ServerSocket( port );
			System.out.println( "INF "+getTime()+": Started the Zincgull Mapserver on port "+port+"\n              listening on "+ss );
			
			while (true) {	//accepting connections forever
				Socket s = ss.accept();		//grab a connection
				System.out.println( "USR "+getTime()+": New connection from "+s );	//msg about the new connection
				DataOutputStream dos = new DataOutputStream( s.getOutputStream() );	//DOS used to write to client
				getOutputStreams().put( s, dos );		//saving the stream
				new MapSrvThread( this, s );		//create a new thread for the stream
			}
		} catch (IOException e) {
			System.out.println( "ERR "+getTime()+": Something failed");
			e.printStackTrace();
		}
		
	}
	// Enumerate all OutputStreams
	static Enumeration<DataOutputStream> enumOutputStreams() {
		return getOutputStreams().elements();
	}
	
	static void sendToAll( String message ) {
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
	
	static void removeConnection( Socket s, double d ) {		//run when connection is discovered dead
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

	static public void setOutputStreams(Hashtable<Socket, DataOutputStream> outputStreams) {
		outputStreams = outputStreams;
	}

	static public Hashtable<Socket, DataOutputStream> getOutputStreams() {
		return outputStreams;
	}


}

