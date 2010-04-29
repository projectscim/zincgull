package client;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import local.Database;

public class ImageBank {
	//TODO check if file-extension exists, if not add and test extensions.
	
	private static ArrayList<ImageIcon> monsters = new ArrayList<ImageIcon>();
	private Connection conn = null;
	private static ImageIcon img;
	//private static ImageIcon monsterFail = new ImageIcon("../images/error/monsterFail.png");
	//private static Image img;
	//private static Image monsterFail;
	
	private static final String imgPath = "../images/monsters/";
	
	/**
	 * Immediately loads all the monsters images into an ArrayList. <br>
	 * ImageBank uses the MySQL-database to fetch image names (file-extension is assumed, atm, to be included).
	 * <br><br>
	 * Contains methods to fetch images based on monsterId. <br>
	 * Images are loaded and ordered by id and therefore the index of the ArrayList is compared to monsterId, when fetching.  
	 */
	public ImageBank() {
		
		/*System.out.println("ImageBank created");
		conn = Database.connect();
		String SQL = "SELECT image FROM Monster ORDER BY id";
		
		int i = 0;
		int y = 0;
		
		try {
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			
			System.out.println("Starting to Load images..");
			while(rs.next()) {
				img = new ImageIcon(imgPath+rs.getString("image")); 
				if(img != null) {
					monsters.add(img);
					i++; //one more success
				}
				else {
					System.out.println("FATAL ERROR: unable to load monster image");
					y++; //one more failure
				}
			}
			
		} catch (SQLException e) {
			System.out.println("SQLException in ImageBank, stmt: "+SQL);
			e.printStackTrace();
		} finally {
			img = null;
			
			//Print summary
			if(y==0) System.out.println("DONE. "+i+" image(s) loaded without errors.");
			else System.out.println("DONE. "+i+" image(s) loaded, failed to load "+y+"image(s).");
			
			
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println("Failed to close Database-connection.");
			}
		}*/
		
		URL url;
		try {
			url = new URL("http://utterfanskap.se/zincgull/includes/images/monsters/cuteBunny.png");	
			ImageIcon asdf = new ImageIcon(url);
			monsters.add(asdf);
			
			url = new URL("http://utterfanskap.se/zincgull/includes/images/monsters/cuteKoala.png");
			asdf = new ImageIcon(url);
			monsters.add(asdf);
			
			url = new URL("http://utterfanskap.se/zincgull/includes/images/monsters/rabidHound.png");
			asdf = new ImageIcon(url);
			monsters.add(asdf);
			
			url = new URL("http://utterfanskap.se/zincgull/includes/images/monsters/basilisk.png");
			asdf = new ImageIcon(url);
			monsters.add(asdf);
			
			url = new URL("http://utterfanskap.se/zincgull/includes/images/monsters/blackKnight.png");
			asdf = new ImageIcon(url);
			monsters.add(asdf);
			
			url = new URL("http://utterfanskap.se/zincgull/includes/images/monsters/whiteRabbit.png");
			asdf = new ImageIcon(url);
			monsters.add(asdf);
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static ImageIcon getImage(int id) {
		id -= 1; //Offset because db-table starts from 1 while ArrayList starts from 0.
		if(monsters.get(id) != null) return (monsters.get(id));
		//else return monsterFail;
		return null;
	}
	
	//For testing purposes
	public static void main(String[] args) {
		new ImageBank();
	}
}
