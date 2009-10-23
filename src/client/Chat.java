package client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.*;

public class Chat extends JPanel implements Runnable {
	private static final long serialVersionUID = -6395460343649750082L;
	private JTextField chatInput = new JTextField();
	private TextArea chatOutput = new TextArea();	//not a JTextArea due to bad scroll-support

	private Socket socket;		//socket connecting to server
	private DataOutputStream dos;
	private DataInputStream dis;
	
	private String host, nickname;
	private int port = 49050;
	
	public Chat(String serverAddress, String nick) {		
		chatOutput.setEditable(false);
		chatOutput.setBackground(Color.BLACK);
		chatOutput.setForeground(Color.GREEN);
		
		this.setLayout( new BorderLayout() );
		this.add( "North", chatInput );
		this.add( "Center", chatOutput );
		this.host = serverAddress;
		this.nickname = nick;
		
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
			dos.writeUTF( nickname+": "+message );		//send
			chatInput.setText( "" );		//clear inputfield
		} catch( IOException ie ) { 
			System.out.println( ie ); 
			chatOutput.append( "Can't send message.\n" );
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
				dos.writeUTF( "-> "+nickname+" joined" );		//say hello to server containing username
				// Start a background thread for receiving messages
				new Thread( this ).start();		//starts run()-method
				reconnect = false;
			} catch( IOException e ) { 
				System.out.println( e );
				if(first){
					System.out.println( "First time tried failed\n" );	//debug
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
				chatOutput.append( getTime()+": "+message+"\n" );	//print
			}
		} catch( IOException ie ) { 
			System.out.println( ie );
			chatOutput.append("Connection reset, trying to reconnect\n");
			connectServer(false);
		}
	}
	
	public String getTime(){
		DateFormat time = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		Date date = new GregorianCalendar().getTime();
		return time.format(date);
	}
}
