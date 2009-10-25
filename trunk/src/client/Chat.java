package client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Chat extends JPanel implements Runnable {
	private static final long serialVersionUID = -6395460343649750082L;
	private JTextField chatInput = new JTextField();
	static TextArea chatOutput = new TextArea();	//not a JTextArea due to bad scroll-support

	private Socket socket;		//socket connecting to server
	private DataOutputStream dos;
	private DataInputStream dis;
	private int port = 49050;	//chatserver-port
	
	public Chat() {		
		chatOutput.setEditable(false);
		chatOutput.setBackground(Color.BLACK);
		chatOutput.setForeground(Color.GREEN);
		
		this.setLayout( new BorderLayout() );
		this.add( "North", chatInput );
		this.add( "Center", chatOutput );
		
		// We want to receive messages when someone types a line
		// and hits return, using an anonymous class as
		// a callback
		chatInput.addActionListener( 
			new ActionListener() {
				public void actionPerformed( ActionEvent e ) {
					if( !e.getActionCommand().isEmpty() ){	//make sure it's not null
						processMessage( e.getActionCommand() );
					}
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
			if ( message.substring(0, 1).equals("/") ) {	//only special commands start with a /
				if( message.length() == 5 && message.equals("/info") ){
					chatOutput.append(Zincgull.getTime()+": Client-information:\n");
					chatOutput.append("\t  Server is "+Zincgull.host+"\n");
					chatOutput.append("\t  Nickname is "+Zincgull.nick+"\n");
					chatOutput.append("\t  Unique ID is "+Zincgull.random+"\n");
				}else{
					dos.writeUTF( message );				//send command in full
				}
			}else{
				dos.writeUTF( "/msg "+message );		//send as regular message
			}
			chatInput.setText( "" );		//clear inputfield
		} catch( IOException ie ) { 
			System.out.println( ie ); 
			chatOutput.append( Zincgull.getTime()+": Can't send message\n" );
		}
	}
	
	public void connectServer(boolean first){
		boolean reconnect = true;
		while (reconnect) {
			try {
				socket = new Socket(Zincgull.host, port);
				//create streams for communication
				dis = new DataInputStream( socket.getInputStream() );
				dos = new DataOutputStream( socket.getOutputStream() );
				dos.writeUTF( "/HELLO "+Zincgull.nick +":"+ Zincgull.random );	//say hello to server, nickname and unique random
				// Start a background thread for receiving messages
				new Thread( this ).start();		//starts run()-method
				reconnect = false;
				if(!first) chatOutput.append(Zincgull.getTime()+": CHAT: Connected to server\n");
			} catch( IOException e ) { 
				if(first){
					chatOutput.append(Zincgull.getTime()+": CHAT: Can't connect to server, trying again\n");
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
				if( !specialCommand(message) ){
					chatOutput.append( Zincgull.getTime()+": "+message+"\n" );	//print
				}
			}
		} catch( IOException ie ) { 
			//System.out.println( ie );	//debug, not necessary with below line
			chatOutput.append(Zincgull.getTime()+": CHAT: Connection reset, reconnecting\n");
			connectServer(false);
		}
	}
	
	//possible commands the server can send
	public boolean specialCommand( String msg ){
		if( msg.substring(0, 1).equals("/") ){
			if( msg.length() >= 6){
				if( msg.substring(0, 5).equals("/msg ") ){
					return false;
				}else if( msg.substring(0, 6).equals("/nick ") ){	//expecting a hello-message at first connection
					Zincgull.nick = msg.substring(6);
					Sidebar.lblNick.setText(Zincgull.nick);
					return true;
				}
			}
		}
		return false;
	}
	
}
