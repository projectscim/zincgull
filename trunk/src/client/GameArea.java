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
	boolean[] arrowDown = new boolean[4];
	
	protected static LinkedList<Player> player = new LinkedList<Player>();
	
	public GameArea() {		
      	this.addKeyListener(this);
      	this.setBackground(Color.WHITE);
      	this.setDoubleBuffered(true);
      	tim.addActionListener(this);
		tim.start();
		connect(true, "80:50:1:1");	//try to connect, "true" because its the first time
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int i = 0; i < player.size(); i++) {
			if(player.get(i).id != 0.0){
				g.drawImage(player.get(i).sprite.getImage(), player.get(i).xpos-player.get(i).turned*(100/2), player.get(i).ypos, player.get(i).turned*100, 50, null);
			}
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
			int x = player.get(getId(Zincgull.random)).xpos;
			int y = player.get(getId(Zincgull.random)).ypos;
			int t = player.get(getId(Zincgull.random)).turned;
			int s = player.get(getId(Zincgull.random)).speed;
			Zincgull.connected = false;
			player.clear();
			repaint();
			connect(false, x+":"+y+":"+t+":"+s);
			return;
		}
	}

	private void sendData() {
		try {
			dos.writeUTF( player.get(getId(Zincgull.random)).xpos +":"+ player.get(getId(Zincgull.random)).ypos +":"+ player.get(getId(Zincgull.random)).turned +":"+ player.get(getId(Zincgull.random)).speed +":"+ Zincgull.random);
		} catch( IOException ie ) { 
			//Chat.chatOutput.append( Zincgull.getTime()+": MAP: Can't send coordinates\n" );
		}
	}
	
	public void connect(boolean first, String position){
		while (true) {
			try {
				socket = new Socket(Zincgull.host, port);
				//create streams for communication
				dis = new DataInputStream( socket.getInputStream() );
				dos = new DataOutputStream( socket.getOutputStream() );
				dos.writeUTF("/HELLO "+position+":"+Zincgull.random);
				// Start a background thread for receiving coordinates
				new Thread( this ).start();		//starts run()-method
				if(!first) Chat.chatOutput.append(Zincgull.getTime()+": MAP: Connected to mapserver\n");
				return;
			} catch( IOException e ) { 
				//System.out.println(e);
				if(first){
					Chat.chatOutput.append(Zincgull.getTime()+": MAP: Can't connect to server, trying again\n");
					first = false;
				}
			}
		}
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
			arrowDown[40-e.getKeyCode()]=true;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()>=37 && e.getKeyCode()<=40)
			arrowDown[40-e.getKeyCode()]=false;
	}

	public void keyTyped(KeyEvent e) {}

	public void actionPerformed(ActionEvent e) {
		if ( Zincgull.connected ) {
			if (Zincgull.isMouseActive()&&(arrowDown[0]||arrowDown[1]||arrowDown[2]||arrowDown[3])) {
				calculateMove();
				sendData();
				repaint();	
			}else{
				for (int i = 0; i < arrowDown.length; i++) {
					arrowDown[i] = false;
				}
			}
		}
	}

	private void calculateMove() {
		if(arrowDown[0]){
			player.get(getId(Zincgull.random)).ypos=player.get(getId(Zincgull.random)).ypos+player.get(getId(Zincgull.random)).speed;
		}
		if(arrowDown[1]){
			player.get(getId(Zincgull.random)).xpos=player.get(getId(Zincgull.random)).xpos+player.get(getId(Zincgull.random)).speed;
			player.get(getId(Zincgull.random)).turned = 1;
		}
		if(arrowDown[2]){
			player.get(getId(Zincgull.random)).ypos=player.get(getId(Zincgull.random)).ypos-player.get(getId(Zincgull.random)).speed;
		}
		if(arrowDown[3]){
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
			player.add(new Player(x,y,t,s,i));
			Zincgull.connected = true;
			repaint();
			return true;
		}else if( msg.substring(0, 4).equals("/SUB") ){
			//player.remove(getId( Double.parseDouble(temp[4]) ));
			player.set(getId(Double.parseDouble(msg.substring(5))), new Player(0,0,0,0,0.0));
			repaint();
			return true;
		}else if( msg.substring(0, 6).equals("/HELLO") ){
			Chat.chatOutput.append(Zincgull.getTime()+": "+msg.substring(7)+"\n");
			return true;
		}
		return false;
	}
}
