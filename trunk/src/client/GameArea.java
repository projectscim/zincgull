package client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Random;
//import java.util.LinkedList;

import javax.swing.*;

public class GameArea extends JPanel implements ActionListener, KeyListener, Runnable{
	private static final long serialVersionUID = -5572295459928673608L;
	
	private Socket socket;		//socket connecting to server
	private DataOutputStream dos;
	private DataInputStream dis;
	private int port = 49051;	//mapserver-port
	
//	protected static LinkedList<Player> player = new LinkedList<Player>();
	URL url = getClass().getResource("../images/zincgull.png");
	private ImageIcon sprite = new ImageIcon(url);
	
	private Random randomize = new Random();
	private double random = randomize.nextDouble();
	
	private Timer tim = new Timer(1,this);
	private int turned = 1;
	private int xpos = 80;
	private int ypos = 50;
	private int speed = 1;
	private boolean[] arrowDown = new boolean[4];
	
	public GameArea() {
//		player.add(new Player());
		
      	this.addKeyListener(this);
      	this.setBackground(Color.WHITE);
      	this.setDoubleBuffered(true);
      	tim.addActionListener(this);
		tim.start();
		connect(true);	//try to connect, "true" because its the first time
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(/*player.*/sprite.getImage(), xpos-turned*28, ypos, turned*76, 56, null);
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
					if( !temp[5].equals( Double.toString(this.random) ) ){		//only paint new coordinates if they didnt come from this client
						xpos = Integer.parseInt(temp[0]);
						ypos = Integer.parseInt(temp[1]);
						turned = Integer.parseInt(temp[2]);
						speed = Integer.parseInt(temp[3]);
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
			dos.writeUTF( xpos +":"+ ypos +":"+ turned +":"+ speed +":"+ Zincgull.nick +":"+ random);
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
				dos.writeUTF( "/HELLO "+Zincgull.nick );		//say hello to server containing username
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
			arrowDown[40-e.getKeyCode()]=true;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()>=37 && e.getKeyCode()<=40)
			arrowDown[40-e.getKeyCode()]=false;
	}

	public void keyTyped(KeyEvent e) {}

	public void actionPerformed(ActionEvent e) {
		if (Zincgull.isMouseActive()&&(arrowDown[0]||arrowDown[1]||arrowDown[2]||arrowDown[3])) {
			sendData();
			calculateMove();
			repaint();	
		}
	}

	private void calculateMove() {
		if(arrowDown[0]){
			ypos=ypos+speed;
		}
		if(arrowDown[1]){
			xpos=xpos+speed;
			turned = 1;
		}
		if(arrowDown[2]){
			ypos=ypos-speed;
		}
		if(arrowDown[3]){
			xpos=xpos-speed;
			turned = -1;
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
