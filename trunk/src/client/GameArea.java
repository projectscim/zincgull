package client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.*;

import local.Database;
import local.GlobalConstants;

public class GameArea extends JPanel implements ActionListener, KeyListener, Runnable, GlobalConstants{
	
	private static final long serialVersionUID = -5572295459928673608L;
	private static int myId;
	
	private Socket socket;		//socket connecting to server
	private DataOutputStream dos;
	private DataInputStream dis;
	private int port = 49051;	//mapserver-port
	private Timer tim = new Timer(20,this);
	boolean[] arrowDown = new boolean[4];
	boolean readMap = false;
	private final static int monsterIdStart = 1000;
	
	private URL url;
	
	private ArrayList<String> tiles = new ArrayList<String>();
	private ImageIcon[] groundTile = new ImageIcon[15];
	
	private ImageIcon[] playerImg = new ImageIcon[2];
	
	protected static LinkedList<Player> player = new LinkedList<Player>();
	protected static LinkedList<MonsterEcho> monster = new LinkedList<MonsterEcho>();
	
	//Connection conn = null;
	private static final long sleep = 5;
	
	public GameArea(int id) {
		myId = id;
		
		try {
			url = new URL("http://utterfanskap.se/zincgull/includes/images/players/player.png");		
			playerImg[0] = new ImageIcon(url);
			url = new URL("http://utterfanskap.se/zincgull/includes/images/players/otherPlayer.png");
			playerImg[1] = new ImageIcon(url);
			
			url = new URL("http://utterfanskap.se/zincgull/includes/images/tiles/ground.png");
			groundTile[0] = new ImageIcon(url);
			url = new URL("http://utterfanskap.se/zincgull/includes/images/tiles/topStop.png");
			groundTile[1] = new ImageIcon(url);
			url = new URL("http://utterfanskap.se/zincgull/includes/images/tiles/leftStop.png");
			groundTile[2] = new ImageIcon(url);
			url = new URL("http://utterfanskap.se/zincgull/includes/images/tiles/rightStop.png");
			groundTile[3] = new ImageIcon(url);
			url = new URL("http://utterfanskap.se/zincgull/includes/images/tiles/bottomStop.png");
			groundTile[4] = new ImageIcon(url);
			url = new URL("http://utterfanskap.se/zincgull/includes/images/tiles/leftTopCorner.png");
			groundTile[5] = new ImageIcon(url);
			url = new URL("http://utterfanskap.se/zincgull/includes/images/tiles/rightTopCorner.png");
			groundTile[6] = new ImageIcon(url);
			url = new URL("http://utterfanskap.se/zincgull/includes/images/tiles/leftBottomCorner.png");
			groundTile[7] = new ImageIcon(url);
			url = new URL("http://utterfanskap.se/zincgull/includes/images/tiles/rightBottomCorner.png");
			groundTile[8] = new ImageIcon(url);
			url = new URL("http://utterfanskap.se/zincgull/includes/images/tiles/exit.png");
			groundTile[9] = new ImageIcon(url);
			url = new URL("http://utterfanskap.se/zincgull/includes/images/tiles/highGround.png");
			groundTile[10] = new ImageIcon(url);
			url = new URL("http://utterfanskap.se/zincgull/includes/images/tiles/invLeftTopCorner.png");
			groundTile[11] = new ImageIcon(url);
			url = new URL("http://utterfanskap.se/zincgull/includes/images/tiles/invRightTopCorner.png");
			groundTile[12] = new ImageIcon(url);
			url = new URL("http://utterfanskap.se/zincgull/includes/images/tiles/invLeftBottomCorner.png");
			groundTile[13] = new ImageIcon(url);
			url = new URL("http://utterfanskap.se/zincgull/includes/images/tiles/invRightBottomCorner.png");
			groundTile[14] = new ImageIcon(url);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
      	this.addKeyListener(this);
      	this.setBackground(Color.WHITE);
      	this.setDoubleBuffered(true);
      	
      	tim.addActionListener(this);
		tim.start();
		connect(true, "80:50:1:1");	//try to connect, "true" because its the first time
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int imgInt;
		for (int i=0;i<tiles.size();i++) {
			for (int j=0;j < (tiles.get(i).length());j++) {
				switch(tiles.get(i).charAt(j)) {
				case ' ':
					imgInt = 0;
					g.drawImage(groundTile[imgInt].getImage(), TILE_SIZE*j, TILE_SIZE*i, TILE_SIZE, TILE_SIZE, null);
					break;
				case 'T':
					imgInt = 1;
					g.drawImage(groundTile[imgInt].getImage(), TILE_SIZE*j, TILE_SIZE*i, TILE_SIZE, TILE_SIZE, null);
					break;
				case 'L':
					imgInt = 2;
					g.drawImage(groundTile[imgInt].getImage(), TILE_SIZE*j, TILE_SIZE*i, TILE_SIZE, TILE_SIZE, null);
					break;
				case 'R':
					imgInt = 3;
					g.drawImage(groundTile[imgInt].getImage(), TILE_SIZE*j, TILE_SIZE*i, TILE_SIZE, TILE_SIZE, null);
					break;
				case 'Q':
					imgInt = 4;
					g.drawImage(groundTile[imgInt].getImage(), TILE_SIZE*j, TILE_SIZE*i, TILE_SIZE, TILE_SIZE, null);
					break;
				case 'C':
					imgInt = 5;
					g.drawImage(groundTile[imgInt].getImage(), TILE_SIZE*j, TILE_SIZE*i, TILE_SIZE, TILE_SIZE, null);
					break;
				case 'V':
					imgInt = 6;
					g.drawImage(groundTile[imgInt].getImage(), TILE_SIZE*j, TILE_SIZE*i, TILE_SIZE, TILE_SIZE, null);
					break;
				case 'B':
					imgInt = 7;
					g.drawImage(groundTile[imgInt].getImage(), TILE_SIZE*j, TILE_SIZE*i, TILE_SIZE, TILE_SIZE, null);
					break;
				case 'N':
					imgInt = 8;
					g.drawImage(groundTile[imgInt].getImage(), TILE_SIZE*j, TILE_SIZE*i, TILE_SIZE, TILE_SIZE, null);
					break;
				case 'E':
					imgInt = 9;
					g.drawImage(groundTile[imgInt].getImage(), TILE_SIZE*j, TILE_SIZE*i, TILE_SIZE, TILE_SIZE, null);
					break;
				case 'H':
					imgInt = 10;
					g.drawImage(groundTile[imgInt].getImage(), TILE_SIZE*j, TILE_SIZE*i, TILE_SIZE, TILE_SIZE, null);
					break;
				case 'I':
					imgInt = 11;
					g.drawImage(groundTile[imgInt].getImage(), TILE_SIZE*j, TILE_SIZE*i, TILE_SIZE, TILE_SIZE, null);
					break;
				case 'O':
					imgInt = 12;
					g.drawImage(groundTile[imgInt].getImage(), TILE_SIZE*j, TILE_SIZE*i, TILE_SIZE, TILE_SIZE, null);
					break;
				case 'P':
					imgInt = 13;
					g.drawImage(groundTile[imgInt].getImage(), TILE_SIZE*j, TILE_SIZE*i, TILE_SIZE, TILE_SIZE, null);
					break;
				case 'U':
					imgInt = 14;
					g.drawImage(groundTile[imgInt].getImage(), TILE_SIZE*j, TILE_SIZE*i, TILE_SIZE, TILE_SIZE, null);
					break;
				default:
					System.out.println("map failure");
				}
			}	
		}
		
		imgInt = 0;
		for (int i = 0; i < player.size(); i++) {
			if(player.get(i).getId() != 0.0){
				Player p = player.get(i);
				imgInt = (p.getId()==myId) ? 0 : 1; 
				g.drawImage(playerImg[imgInt].getImage(),
						(p.getXpos()-p.getTurned()*(playerImg[imgInt].getIconWidth()/2)),
						p.getYpos(),
						(p.getTurned()*playerImg[imgInt].getIconWidth()),
						(playerImg[imgInt].getIconHeight()),
						null);
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
		
		
			if(!readMap) {
				new client.LoadMap();
				
				for(int i=0;i<LoadMap.getMapSegment().size();i++) {
					tiles.add(LoadMap.getMapSegment().get(i));
				}
				
				repaint();
				readMap = true;
			}
			
			try {
				while (true) {
					
					//Keep database connection alive
					//if(conn == null) conn = Database.connect();
					
					String coords = dis.readUTF();
					if (!specialCommand(coords)) {
						
						String[] temp;
						temp = coords.split(":");
						
						if(Integer.valueOf(temp[4]) < monsterIdStart) { //if player
							Player ps = player.get(getId(Integer.valueOf(temp[4])));
							if( !temp[4].equals( Integer.toString(myId) ) ){		//only paint new coordinates if they didnt come from this client
								ps.setXpos(Integer.parseInt(temp[0]));
								ps.setYpos(Integer.parseInt(temp[1]));
								ps.setTurned(Integer.parseInt(temp[2]));
								ps.setSpeed(Integer.parseInt(temp[3]));
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
							
							monster.add(new MonsterEcho(x,y,t,i,mi,h/*,conn*/));
							
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
				
				/*try {
					conn.close();
				} catch (SQLException e) {}*/
				
				Player p = player.get(getId(myId));
				Chat.chatOutput.append(Zincgull.getTime()+": MAP: Connection reset, reconnecting\n");
				int x = p.getXpos();
				int y = p.getYpos();
				int t = p.getTurned();
				int s = p.getSpeed();
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
		Player p = player.get(getId(myId));
		try {
			dos.writeUTF( p.getXpos() +":"+ p.getYpos() +":"+ p.getTurned() +":"+ p.getSpeed() +":"+ myId);
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
				dos.writeUTF("/HELLO "+position+":"+myId);
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
				//Sleep a bit
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {}
			}
		}
	}
	
	public static int getId(int d){
		for (int i = 0; i < player.size(); i++) {
			if( player.get(i).getId() == d ){	//needs to be unique
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

	public void actionPerformed(ActionEvent arg0) {
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
		Player p = player.get(getId(myId));
		
		int j = 0;
		for (int i = 0; i < arrowDown.length; i++) {
			if(arrowDown[i]) j++;
			if(j>2) return;
		}
		
		if(arrowDown[3]&&arrowDown[1]) return;
		else if(arrowDown[0]&&arrowDown[2]) return;
		
		checkCollision(p);
		
		if(arrowDown[0]){
			p.setYpos(p.getYpos()+p.getSpeed());
		}
		if(arrowDown[1]){
			p.setXpos(p.getXpos()+p.getSpeed());
			p.setTurned(Player.NOT_TURNED);
		}
		if(arrowDown[2]){
			p.setYpos(p.getYpos()-p.getSpeed());
		}
		if(arrowDown[3]){
			p.setXpos(p.getXpos()-p.getSpeed());
			p.setTurned(Player.TURNED);
		}
	}
	
	private void checkCollision(Player p) {
		int tx1 = (p.getXpos()-(TILE_SIZE/2))/TILE_SIZE;
		int tx2 = ((p.getXpos()-(TILE_SIZE/2))+TILE_SIZE)/TILE_SIZE;
		int ty1 = p.getYpos()/TILE_SIZE;
		int ty2 = (p.getYpos()+TILE_SIZE)/TILE_SIZE;
			
		if(arrowDown[3]) {
			if((LoadMap.getTile(tx1, ty1)!=' ') || (LoadMap.getTile(tx1, ty2)!=' ')) {
				int asdf = (tx1*TILE_SIZE);
				p.setXpos(asdf+2+(TILE_SIZE/2)+TILE_SIZE);
			}
		}else if(arrowDown[1]) {
			if((LoadMap.getTile(tx2, ty1)!=' ') || (LoadMap.getTile(tx2, ty2)!=' ')) {
				int asdf = (tx2*TILE_SIZE);
				p.setXpos(asdf-2-(TILE_SIZE/2));
			}
		}else if(arrowDown[2]) {
			if((LoadMap.getTile(tx1, ty1)!=' ') || (LoadMap.getTile(tx2, ty1)!=' ')) {
				int asdf = (ty1*TILE_SIZE)+TILE_SIZE;
				p.setYpos(asdf+2);
			}
		}else if(arrowDown[0]) {
			if((LoadMap.getTile(tx1, ty2)!=' ') || (LoadMap.getTile(tx2, ty2)!=' ')) {
				int asdf = (ty2*TILE_SIZE)-TILE_SIZE;
				p.setYpos(asdf-2);
			}
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
			int i = Integer.parseInt(temp[4]);	
			
			player.add(new Player(x,y,t,s,i));
			Zincgull.connected = true;
			repaint();
			return true;	
			
		}else if( msg.substring(0, 4).equals("/SUB") ){
			//player.remove(getId( Double.parseDouble(temp[4]) ));
			player.set(getId(Integer.parseInt(msg.substring(5))), new Player(0,0,0,0,0));
			repaint();
			return true;
		}else if( msg.substring(0, 6).equals("/HELLO") ){
			Chat.chatOutput.append(Zincgull.getTime()+": "+msg.substring(7)+"\n");
			return true;
		}
		return false;
	}
}
