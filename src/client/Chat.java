package client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Chat extends Panel implements Runnable {
	private static final long serialVersionUID = -6395460343649750082L;
	private TextField chatInput = new TextField();
	private TextArea chatOutput = new TextArea();

	private Socket socket;		//socket connecting to server
	private DataOutputStream dos;
	private DataInputStream dis;
	
	private String host = "localhost";
	private int port = 49050;
	
	public Chat() {		
		chatOutput.setEditable(false);
		this.setLayout( new BorderLayout() );
		this.add( "North", chatInput );
		this.add( "Center", chatOutput );
		
		// We want to receive messages when someone types a line
		// and hits return, using an anonymous class as
		// a callback
		chatInput.addActionListener( 
			new ActionListener() {
				public void actionPerformed( ActionEvent e ) {
					processMessage( e.getActionCommand() );
				}
			} 
		);
		this.setVisible(true);
		//_ISSUE: client wont start until server is started...
		connectServer(true);	//try to connect, "true" because its the first time
	}
	
	//handles everything that gets typed by the user
	private void processMessage( String message ) {
		try {
			dos.writeUTF( message );		//send
			chatInput.setText( "" );		//clear inputfield
		} catch( IOException ie ) { 
			System.out.println( ie ); 
			chatOutput.append( "Can't send message" );
		}
	}
	
	public void connectServer(boolean first){
		boolean reconnect = true;
		while (reconnect) {
			try {
				socket = new Socket(host, port);
				//create streams for communication
				dis = new DataInputStream( socket.getInputStream() );
				dos = new DataOutputStream( socket.getOutputStream() );
				// Start a background thread for receiving messages
				new Thread( this ).start();		//starts run()-method
				reconnect = false;
			} catch( IOException e ) { 
				if(first){
					chatOutput.append("Can't connect to server, but trying to reconnect.\n");
					first = false;
				}
			}
		}
	}
	
	//keep receiving messages from the server
	public void run() {
		try {
			while (true) {
				String message = dis.readUTF();		//read
				chatOutput.append( message+"\n" );	//print
			}
		} catch( IOException ie ) { 
			chatOutput.append("Connection reset, trying to reconnect\n");
			connectServer(false);
		}
	}
}
