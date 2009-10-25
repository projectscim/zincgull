package server;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.util.*;

public class ChatSrv {
	private ServerSocket ss;
	//this is used to don't have to create a DOS every time you are writing to a stream
	private Hashtable<Socket, DataOutputStream> outputStreams = new Hashtable<Socket, DataOutputStream>();
	protected static LinkedList<String> nick = new LinkedList<String>();
	
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
	
	void removeConnection( Socket s, double d ) {		//run when connection is discovered dead
		synchronized( getOutputStreams() ) {		//dont mess up sendToAll
			String user = getNickname(d);
			nick.remove(getId(d));	//one less online
			System.out.println( "USR "+getTime()+": Lost connection from "+s );
			String send = user +" left, "+nick.size()+" left online";
			System.out.println("              "+send);
			getOutputStreams().remove( s );
			if(nick.isEmpty()) System.out.println( "INF "+getTime()+": No users online" );
			sendToAll("<- "+send);	//tell everyone that someone left
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
	
	public static String getNickname(Double d){
		String[] tmp;
		for (int i = 0; i < nick.size(); i++) {
			tmp = nick.get(i).split(":");
			if( tmp[1].equals( Double.toString(d) ) ){	//needs to be unique
				return tmp[0];
			}
		}
		return "";
	}
	
	public static int getId(Double d){
		String[] tmp;
		for (int i = 0; i < nick.size(); i++) {
			tmp = nick.get(i).split(":");
			if( tmp[1].equals( Double.toString(d) ) ){	//needs to be unique
				return i;
			}
		}
		return 0;
	}
	
	public void setOutputStreams(Hashtable<Socket, DataOutputStream> outputStreams) {
		this.outputStreams = outputStreams;
	}

	public Hashtable<Socket, DataOutputStream> getOutputStreams() {
		return outputStreams;
	}
}

