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
	private Timer tim = new Timer(20,this);
	boolean[] arrowDown = new boolean[4];
	
	protected static LinkedList<Player> player = new LinkedList<Player>();
	protected static LinkedList<MonsterEcho> monster = new LinkedList<MonsterEcho>();
	
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
				Player p = player.get(i);
				g.drawImage(p.sprite.getImage(), p.xpos-p.turned*(100/2), p.ypos, p.turned*100, 50, null);
			}
		}
		
		for (int i=0;i<monster.size();i++) {
			
			MonsterEcho m = monster.get(i);
			ImageIcon img = ImageBank.getImage(m.getMonsterId());
			
			g.drawImage(img.getImage(), 
					m.getXpos()-m.getTurned()*(img.getIconWidth()/2),
					m.getYpos(), 
					img.getIconWidth()*monster.get(i).getTurned(),
					img.getIconHeight(),
					null);
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
					
					if(Double.valueOf(temp[4])<1) { //if player
						Player ps = player.get(getId(Double.valueOf(temp[4])));
						if( !temp[4].equals( Double.toString(Zincgull.random) ) ){		//only paint new coordinates if they didnt come from this client
							ps.xpos = Integer.parseInt(temp[0]);
							ps.ypos = Integer.parseInt(temp[1]);
							ps.turned = Integer.parseInt(temp[2]);
							ps.speed = Integer.parseInt(temp[3]);
							repaint();
						}	
					}
					else if((getMonster(temp[4]))==-1) { //Since not player, if monster not already added
						System.out.println("add monster");
						int x = Integer.parseInt(temp[0]);	//X-pos
						int y = Integer.parseInt(temp[1]);	//Y-pos
						int t = Integer.parseInt(temp[2]);	//turned (1/0)
						int mi = Integer.parseInt(temp[3]);	//MonsterType-id, NOT UNIQUE
						int i = Integer.parseInt(temp[4]);	//id, UNIQUE
						int h = Integer.parseInt(temp[5]);	//health, the current health of the monster
						
						monster.add(new MonsterEcho(x,y,t,i,mi,h));
						
						repaint();
					}
					else { //not player and already added, update the MonsterEcho-object.
						MonsterEcho m = monster.get(getMonster(temp[4]));
						
						m.setXpos(Integer.parseInt(temp[0]));
						m.setYpos(Integer.parseInt(temp[1]));
						m.setTurned(Integer.parseInt(temp[2]));
						//m.setMonsterId(Integer.parseInt(temp[3])); 	No need, it will never change
						//m.setId(Integer.parseInt(temp[4])); 			No need, it will never change
						m.setHealth(Integer.parseInt(temp[5]));
						
						repaint();
					}
					
				}
			}
		} catch( IOException ie ) {
			Player p = player.get(getId(Zincgull.random));
			Chat.chatOutput.append(Zincgull.getTime()+": MAP: Connection reset, reconnecting\n");
			int x = p.xpos;
			int y = p.ypos;
			int t = p.turned;
			int s = p.speed;
			Zincgull.connected = false;
			player.clear();
			repaint();
			connect(false, x+":"+y+":"+t+":"+s);
			return;
		}
	}

	private int getMonster(String sid) {
		int id = Integer.valueOf(sid);
		for (int i = 0; i < monster.size(); i++) {
			if(monster.get(i).getId() == id) {	//needs to be unique
				return i;
			}
		}
		return -1;
	}

	private void sendData() {
		Player p = player.get(getId(Zincgull.random));
		try {
			dos.writeUTF( p.xpos +":"+ p.ypos +":"+ p.turned +":"+ p.speed +":"+ Zincgull.random);
		} catch( IOException ie ) { 
			//Chat.chatOutput.append( Zincgull.getTime()+": MAP: Can't send coordinates\n" );
		}
	}
	
	public void connect(boolean first, String position) {
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
		Player p = player.get(getId(Zincgull.random));
		if(arrowDown[0]){
			p.ypos=p.ypos+p.speed;
		}
		if(arrowDown[1]){
			p.xpos=p.xpos+p.speed;
			p.turned = 1;
		}
		if(arrowDown[2]){
			p.ypos=p.ypos-p.speed;
		}
		if(arrowDown[3]){
			p.xpos=p.xpos-p.speed;
			p.turned = -1;
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
