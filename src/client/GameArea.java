package client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class GameArea extends JPanel implements ActionListener, KeyListener, Runnable{
	private static final long serialVersionUID = -5572295459928673608L;
	
	private Socket socket;		//socket connecting to server
	private DataOutputStream dos;
	private DataInputStream dis;
	private int port = 49051;	//mapserver-port
	
	URL url = getClass().getResource("../images/zincgull.png");
	private ImageIcon sprite = new ImageIcon(url);
	private Timer tim = new Timer(1,this);
	private int turned = 1;
	private int xpos = 80;
	private int ypos = 50;
	private int speed = 1;
	private boolean[] keyDown = new boolean[4];
	
	public GameArea() {
      	this.addKeyListener(this);
      	this.setBackground(Color.WHITE);
      	this.setDoubleBuffered(true);
      	tim.addActionListener(this);
		tim.start();
		connectServer(true);	//try to connect, "true" because its the first time
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(sprite.getImage(), xpos-turned*28, ypos, turned*76, 56, null);
		if(Zincgull.isMouseActive()){
			this.requestFocus();
		}
	}

	//keep receiving messages from the server
	public void run() {
		try {
			while (true) {
				String coords = dis.readUTF();
				String[] temp;
				temp = coords.split(":");
				if( Zincgull.nick != temp[4] ){
					xpos = Integer.parseInt(temp[0]);
					ypos = Integer.parseInt(temp[1]);
					turned = Integer.parseInt(temp[2]);
					speed = Integer.parseInt(temp[3]);
					repaint();
				}
			}
		} catch( IOException ie ) {
			Chat.chatOutput.append(Zincgull.getTime()+": MAP: Connection reset, trying to reconnect\n\n");
			connectServer(false);
		}
	}

	private void sendData() {
		try {
			dos.writeUTF( xpos +":"+ ypos +":"+ turned +":"+ speed +":"+ Zincgull.nick);
		} catch( IOException ie ) { 
			Chat.chatOutput.append( Zincgull.getTime()+": MAP: Can't send coordinates\n" );
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
				//dos.writeUTF( "/hello "+Zincgull.nick );		//say hello to server
				// Start a background thread for receiving coordinates
				new Thread( this ).start();		//starts run()-method
				reconnect = false;
			} catch( IOException e ) { 
				System.out.println(e);
				if(first){
					Chat.chatOutput.append(Zincgull.getTime()+": MAP: Can't connect to map-server, but trying to reconnect\n");
					first = false;
				}
			}
		}
	}
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()>=37 && e.getKeyCode()<=40)
			keyDown[40-e.getKeyCode()]=true;
	}
	
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()>=37 && e.getKeyCode()<=40)
			keyDown[40-e.getKeyCode()]=false;
	}

	public void keyTyped(KeyEvent e) {}

	public void actionPerformed(ActionEvent e) {
		if(Zincgull.isMouseActive()){
			sendData();
			calculateMove();
			repaint();	
		}
	}

	private void calculateMove() {
		if(keyDown[0]){
			ypos=ypos+speed;
		}
		if(keyDown[1]){
			xpos=xpos+speed;
			turned = 1;
		}
		if(keyDown[2]){
			ypos=ypos-speed;
		}
		if(keyDown[3]){
			xpos=xpos-speed;
			turned = -1;
		}
		
	}
}
