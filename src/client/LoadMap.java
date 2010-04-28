package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LoadMap {

	private static BufferedReader bf;
	private static final String path = "../maps/";
	private static final String sgmnt1 = "1-1.dat";
	private static ArrayList<String> tiles = new ArrayList<String>();
	
	public LoadMap() {
		loadMap(sgmnt1);
	}
	
	private static int loadMap(String segment) {
		String currentLine;
		
		//Load Map-file  
		try {
			bf = new BufferedReader(
				new InputStreamReader(
				new FileInputStream(
				new File(path+segment))));
			
		} catch (FileNotFoundException e1) {
			System.out.println("Unable to Load: "+path+segment);
			e1.printStackTrace();
			return -1;
		}
		
		try {			
			while(bf.ready()) {
				//Read Line
				currentLine = bf.readLine();
				
				//If != comment
				if(!currentLine.startsWith("//")) tiles.add(currentLine);			
			}
			
			bf.close();
			
		} catch (IOException e) {
			System.out.println("Failed to Read: "+segment);
			e.printStackTrace();
			return -1;
		}	
		return 0;
	}
	
	public static ArrayList<String> getMapSegment() {
		return tiles;
	}
	
	public static void main(String[] args) {
		new LoadMap();
	}
	
	public static char getTile(int x, int y) {
		return (tiles.get(y).charAt(x));
	}
	
}
