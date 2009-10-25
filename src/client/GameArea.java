package client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.LinkedList;

import javax.swing.*;

public class GameArea extends JPanel implements ActionListener, KeyListener, Runnable{
	private static final long serialVersionUID = -5572295459928673608L;
	
	private Socket socket;		//socket connecting to server
	private DataOutputStream dos;
	private DataInputStream dis;
	private int port = 49051;	//mapserver-port
	private Timer tim = new Timer(10,this);
	
//	private int id = (int) (Zincgull.random*1000000);
	private int id = 1;
	protected static LinkedList<Player> player = new LinkedList<Player>();
	
	public GameArea() {

		player.add(0, new Player());
		player.add(id, new Player());
		
      	this.addKeyListener(this);
      	this.setBackground(Color.WHITE);
      	this.setDoubleBuffered(true);
      	tim.addActionListener(this);
		tim.start();
		connect(true);	//try to connect, "true" because its the first time
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(player.get(id).sprite.getImage(), player.get(id).xpos-player.get(id).turned*28, player.get(id).ypos, player.get(id).turned*76, 56, null);
		if(Zincgull.isMouseActive()){
			this.requestFocus();
		}
	}

	//keep receiving messages from the server
	public void run() {
		try {
			while (true) {
				String coords = dis.readUTF();
				if (!specialCommand(coords)) {
					String[] temp;
					temp = coords.split(":");
					if( !temp[4].equals( Double.toString(Zincgull.random) ) ){		//only paint new coordinates if they didnt come from this client
						//int tmp = Integer.parseInt(temp[4])*100000;
						int tmp = 1;
						player.get(tmp).xpos = Integer.parseInt(temp[0]);
						player.get(tmp).ypos = Integer.parseInt(temp[1]);
						player.get(tmp).turned = Integer.parseInt(temp[2]);
						player.get(tmp).speed = Integer.parseInt(temp[3]);
						repaint();
					}
				}
			}
		} catch( IOException ie ) {
			Chat.chatOutput.append(Zincgull.getTime()+": MAP: Connection reset, reconnecting\n");
			connect(false);
		}
	}

	private void sendData() {
		try {
			dos.writeUTF( player.get(id).xpos +":"+ player.get(id).ypos +":"+ player.get(id).turned +":"+ player.get(id).speed +":"+ Zincgull.random);
		} catch( IOException ie ) { 
			//Chat.chatOutput.append( Zincgull.getTime()+": MAP: Can't send coordinates\n" );
		}
	}
	
	public void connect(boolean first){
		boolean reconnect = true;
		while (reconnect) {
			try {
				socket = new Socket(Zincgull.host, port);
				//create streams for communication
				dis = new DataInputStream( socket.getInputStream() );
				dos = new DataOutputStream( socket.getOutputStream() );
				dos.writeUTF( "/HELLO "+Zincgull.random );		//say hello to server containing username
				// Start a background thread for receiving coordinates
				new Thread( this ).start();		//starts run()-method
				reconnect = false;
				if(!first) Chat.chatOutput.append(Zincgull.getTime()+": MAP: Connected to mapserver\n");
			} catch( IOException e ) { 
				System.out.println(e);
				if(first){
					Chat.chatOutput.append(Zincgull.getTime()+": MAP: Can't connect to server, trying again\n");
					first = false;
				}
			}
		}
	}
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()>=37 && e.getKeyCode()<=40){
			player.get(id).arrowDown[40-e.getKeyCode()]=true;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()>=37 && e.getKeyCode()<=40)
			player.get(id).arrowDown[40-e.getKeyCode()]=false;
	}

	public void keyTyped(KeyEvent e) {}

	public void actionPerformed(ActionEvent e) {
		if (Zincgull.isMouseActive()&&(player.get(id).arrowDown[0]||player.get(id).arrowDown[1]||player.get(id).arrowDown[2]||player.get(id).arrowDown[3])) {
			sendData();
			calculateMove();
			repaint();	
		}
	}

	private void calculateMove() {
		if(player.get(id).arrowDown[0]){
			player.get(id).ypos=player.get(id).ypos+player.get(id).speed;
		}
		if(player.get(id).arrowDown[1]){
			player.get(id).xpos=player.get(id).xpos+player.get(id).speed;
			player.get(id).turned = 1;
		}
		if(player.get(id).arrowDown[2]){
			player.get(id).ypos=player.get(id).ypos-player.get(id).speed;
		}
		if(player.get(id).arrowDown[3]){
			player.get(id).xpos=player.get(id).xpos-player.get(id).speed;
			player.get(id).turned = -1;
		}
		
	}
	
	//possible commands the server can send
	public boolean specialCommand( String msg ){
		if( msg.substring(0, 4).equals("/ADD") ){
			return true;
		}else if( msg.substring(0, 4).equals("/SUB") ){
			return true;
		}else if( msg.substring(0, 6).equals("/HELLO") ){
			Chat.chatOutput.append(Zincgull.getTime()+": "+msg.substring(7)+"\n");
			return true;
		}
		return false;
	}
}
