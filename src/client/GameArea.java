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
	
	protected static LinkedList<Player> player = new LinkedList<Player>();
	
	public GameArea() {
		player.add(new Player(Zincgull.random));
		
      	this.addKeyListener(this);
      	this.setBackground(Color.WHITE);
      	this.setDoubleBuffered(true);
      	tim.addActionListener(this);
		tim.start();
		connect(true);	//try to connect, "true" because its the first time
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int i = 0; i < player.size(); i++) {
			g.drawImage(player.get(i).sprite.getImage(), player.get(i).xpos-player.get(i).turned*28, player.get(i).ypos, player.get(i).turned*76, 56, null);
		}
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
						player.get(getId(Double.valueOf(temp[4]))).xpos = Integer.parseInt(temp[0]);
						player.get(getId(Double.valueOf(temp[4]))).ypos = Integer.parseInt(temp[1]);
						player.get(getId(Double.valueOf(temp[4]))).turned = Integer.parseInt(temp[2]);
						player.get(getId(Double.valueOf(temp[4]))).speed = Integer.parseInt(temp[3]);
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
			dos.writeUTF( player.get(getId(Zincgull.random)).xpos +":"+ player.get(getId(Zincgull.random)).ypos +":"+ player.get(getId(Zincgull.random)).turned +":"+ player.get(getId(Zincgull.random)).speed +":"+ Zincgull.random);
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
				//dos.writeUTF( "/HELLO "+Zincgull.random );		//say hello to server containing username
				dos.writeUTF( "/HELLO "+player.get(getId(Zincgull.random)).xpos +":"+ player.get(getId(Zincgull.random)).ypos +":"+ player.get(getId(Zincgull.random)).turned +":"+ player.get(getId(Zincgull.random)).speed +":"+ Zincgull.random);
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
	
	public static Player getPlayer(Double d){
		for (int i = 0; i < player.size(); i++) {
			if( player.get(i).id == d ){	//needs to be unique
				return player.get(i);
			}
		}
		return player.get(getId(Zincgull.random));
	}
	
	public static int getId(Double d){
		for (int i = 0; i < player.size(); i++) {
			if( player.get(i).id == d ){	//needs to be unique
				return i;
			}
		}
		return 0;
	}
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()>=37 && e.getKeyCode()<=40){
			player.get(getId(Zincgull.random)).arrowDown[40-e.getKeyCode()]=true;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()>=37 && e.getKeyCode()<=40)
			player.get(getId(Zincgull.random)).arrowDown[40-e.getKeyCode()]=false;
	}

	public void keyTyped(KeyEvent e) {}

	public void actionPerformed(ActionEvent e) {
		if (Zincgull.isMouseActive()&&(player.get(getId(Zincgull.random)).arrowDown[0]||player.get(getId(Zincgull.random)).arrowDown[1]||player.get(getId(Zincgull.random)).arrowDown[2]||player.get(getId(Zincgull.random)).arrowDown[3])) {
			sendData();
			calculateMove();
			repaint();	
		}
	}

	private void calculateMove() {
		if(player.get(getId(Zincgull.random)).arrowDown[0]){
			player.get(getId(Zincgull.random)).ypos=player.get(getId(Zincgull.random)).ypos+player.get(getId(Zincgull.random)).speed;
		}
		if(player.get(getId(Zincgull.random)).arrowDown[1]){
			player.get(getId(Zincgull.random)).xpos=player.get(getId(Zincgull.random)).xpos+player.get(getId(Zincgull.random)).speed;
			player.get(getId(Zincgull.random)).turned = 1;
		}
		if(player.get(getId(Zincgull.random)).arrowDown[2]){
			player.get(getId(Zincgull.random)).ypos=player.get(getId(Zincgull.random)).ypos-player.get(getId(Zincgull.random)).speed;
		}
		if(player.get(getId(Zincgull.random)).arrowDown[3]){
			player.get(getId(Zincgull.random)).xpos=player.get(getId(Zincgull.random)).xpos-player.get(getId(Zincgull.random)).speed;
			player.get(getId(Zincgull.random)).turned = -1;
		}
		
	}
	
	//possible commands the server can send
	public boolean specialCommand( String msg ){
		String[] temp;
		temp = msg.substring(5).split(":");
		if( msg.substring(0, 4).equals("/ADD") ){
			int x = Integer.parseInt(temp[0]);
			int y = Integer.parseInt(temp[1]);
			int s = Integer.parseInt(temp[2]);
			int t = Integer.parseInt(temp[3]);
			double i = Double.parseDouble(temp[4]);		
			player.add(new Player(x,y,s,t,i));
			return true;
		}else if( msg.substring(0, 4).equals("/SUB") ){
			double tmp = Double.parseDouble(temp[4]);	
			player.remove(getId(tmp));
			return true;
		}else if( msg.substring(0, 6).equals("/HELLO") ){
			Chat.chatOutput.append(Zincgull.getTime()+": "+msg.substring(7)+"\n");
			return true;
		}
		return false;
	}
}
