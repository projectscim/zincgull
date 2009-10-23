package client;

//import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Chat extends Panel implements Runnable {
	private static final long serialVersionUID = -6395460343649750082L;
	// Components for the visual display of the chat windows
	private TextField tf = new TextField();
	private TextArea ta = new TextArea();
	// The socket connecting us to the server
	private Socket socket;
	// The streams we communicate to the server; these come
	// from the socket
	private DataOutputStream dout;
	private DataInputStream din;
	private InetAddress ip;
	// Constructor
	public Chat( String host, int port ) {
		ta.setEditable(false);
		try {
			ip = Inet4Address.getByName(host);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Set up the screen
		setLayout( new BorderLayout() );
		add( "North", tf );
		add( "Center", ta );
		// We want to receive messages when someone types a line
		// and hits return, using an anonymous class as
		// a callback
		tf.addActionListener( 
			new ActionListener() {
				public void actionPerformed( ActionEvent e ) {
					processMessage( e.getActionCommand() );
				}
			} 
		);
		// Connect to the server
		try {
			// Initiate the connection
			socket = new Socket( ip, port );
			// We got a connection! Tell the world
			System.out.println( "connected to "+socket );
			// Let's grab the streams and create DataInput/Output streams
			// from them
			din = new DataInputStream( socket.getInputStream() );
			dout = new DataOutputStream( socket.getOutputStream() );
			// Start a background thread for receiving messages
			new Thread( this ).start();
		} catch( IOException ie ) { 
			System.out.println( ie ); 
			ta.append( "Connection failed" );
		}
	}
	
	// Gets called when the user types something
	private void processMessage( String message ) {
		try {
			// Send it to the server
			dout.writeUTF( message );
			// Clear out text input field
			tf.setText( "" );
		} catch( IOException ie ) { 
			System.out.println( ie ); 
		}
	}
		
	// Background thread runs this: show messages from other window
	public void run() {
		try {
			// Receive messages one-by-one, forever
			while (true) {
				// Get the next message
				String message = din.readUTF();
				// Print it to our text window
				ta.append( message+"\n" );
			}
		} catch( IOException ie ) { 
			System.out.println( ie );
		}
	}
}
