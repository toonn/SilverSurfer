package mapping;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * Used to read a map from a given ASCII file.
  */
public class MapReader {

	public static MapGraph createMapFromFile(File txtFile){
		String [][] infoMatrix = createInfoMatrixFromFile(txtFile);
		Tile[][] temporaryGraph = new Tile[infoMatrix.length][infoMatrix[0].length];
		
		//Fill a graph with the information in infoMatrix.
		MapGraph map;
		//This prints out the information row by row
		for (int i = 0; i < infoMatrix.length; i++){
			for (int j = 0; j < infoMatrix[i].length; j++) {
				Tile tileIJ = null;
				String[] seperatedInfoIJ = infoMatrix[i][j].split("\\.");
				
				//Create the desired tile form first
				//connect it to the graph in a right way later!
				tileIJ = new Tile(i,j);

				if (seperatedInfoIJ[0].equals("Cross")){
					tileIJ = createCrossFromTile(tileIJ);
				}
				else if (seperatedInfoIJ[0].equals("Straight")){
					tileIJ = createStraightFromTile(tileIJ, seperatedInfoIJ[1]);

				}
				else if (seperatedInfoIJ[0].equals("Corner")){
					tileIJ = createCornerFromTile(tileIJ, seperatedInfoIJ[1]);

				}
				else if (seperatedInfoIJ[0].equals("T")){
					tileIJ = createTFromTile(tileIJ, seperatedInfoIJ[1]);

				}
				else if (seperatedInfoIJ[0].equals("DeadEnd")){
					tileIJ = createDeadEndFromTile(tileIJ, seperatedInfoIJ[1]);
				}
				
				//Create a new graph with this as starting tile.
				if (i == 0 && j == 0){
					map = new MapGraph(tileIJ);
				}
				//Only add tileIJ to the tile North of it (there's no west now!)
				else if (j == 0){
					
				}
				//Add tileIJ to the tile North and West of it.
				else{
					
				}
				if(seperatedInfoIJ.length == 3)
					tileIJ.setContent(new Barcode(Integer.valueOf(seperatedInfoIJ[2])));
//				if(infoMatrix[i][j].contains(".")){
//					//There's a barcode
//					if((infoMatrix[i][j].split("\\.")).length >2)
//						System.out.println("Type: " + (infoMatrix[i][j].split("\\."))[0]+ " | Orientation: " + (infoMatrix[i][j].split("\\."))[1]+" | Barcode-Value: " + infoMatrix[i][j].split("\\.")[2]);		
//					//There's no barcode
//					else
//						System.out.println("Type: " + (infoMatrix[i][j].split("\\."))[0]+ " | Orientation: " + (infoMatrix[i][j].split("\\."))[1]);		
//				}
//				
//				//This is a 'Cross' with no obstruction at any side.
//				else{
//					tileIJ = new Tile(i, j);
//
//					//This is the virtual starting Tile.
//					if (i == 0 && j == 0){
//						map = new MapGraph(tileIJ);
//					}
//					else{
//						
//					}
//				}
				temporaryGraph[i][j] = tileIJ;
				System.out.println(temporaryGraph[i][j] );
			}
				
			System.out.println("new row");
		}
			
		return null;
	}
	
	private static String[][] createInfoMatrixFromFile(File f){
		int collums;
		int rows;
		try {
			
			//Setup I/O
			BufferedReader readbuffer = new BufferedReader(new FileReader(f));
			String strRead;
			
			//Read first line, extract Map-dimensions.
			strRead = readbuffer.readLine();
			String values[] = strRead.split(" ");
			collums = Integer.valueOf(values[0]);
			rows = Integer.valueOf(values[1]);

			String[][] tileTypes = new String[rows][collums];
			int lineNo = 0;
			while ((strRead = readbuffer.readLine()) != null) {
				int collumNo = 0;
				
				//Seperate comment from non-comment
				String splitComment[] = strRead.split("#");
				
				//Filter whitespace
				if (isValuableString(splitComment[0])){
					
					//Split information on tabs
					String splitarray[] = splitComment[0].split("\t");
					
					for (String string : splitarray) 
						if (isValuableString(string))
							tileTypes[lineNo][collumNo++] = string;
						
					lineNo++;
				}
			}
			
			return tileTypes;

		} catch (Exception e) {
			System.err.println("[I/O] Sorry, something went wrong reading the File.");
		}
		
		return new String[0][0];

	}
	
	
	/**
	 * Makes sure all edges of this Tile are un-obstructed.
	 */
	public static Tile createCrossFromTile(Tile t){
		
		emptyNorthEdge(t);
		emptyEastEdge(t);
		emptySouthEdge(t);
		emptyWestEdge(t);
		
		return t;
	}

	
	public static Tile createStraightFromTile(Tile t,String orientation){
		
		if(orientation.equals("N") || orientation.equals("S")){
			addEastWall(t);
			addWestWall(t);
		}
		
		else if (orientation.equals("E") || orientation.equals("W")){
			addNorthWall(t);			
			addSouthWall(t);
		}

		return t;
	}

	
	public static Tile createCornerFromTile(Tile t,String orientation){

		if(orientation.equals("N")){
			addNorthWall(t);
			emptyEastEdge(t);
			emptySouthEdge(t);
			addWestWall(t);
		}
		else if (orientation.equals("E")){
			addNorthWall(t);
			addEastWall(t);
			emptySouthEdge(t);
			emptyWestEdge(t);
			
		}
		else if (orientation.equals("S")){
			emptyNorthEdge(t);
			addEastWall(t);
			addSouthWall(t);
			emptyWestEdge(t);
		}
		else if (orientation.equals("W")){
			emptyNorthEdge(t);
			emptyEastEdge(t);
			addSouthWall(t);
			addWestWall(t);
		}
		return t;
	}
	
	public static Tile createTFromTile(Tile t,String orientation){
		//TODO vul in.
		if(orientation.equals("N")){
			
		}
		else if (orientation.equals("E")){
			
		}
		else if (orientation.equals("S")){
			
		}
		else if (orientation.equals("W")){
			
		}
		
		return t;
	}
	public static Tile createDeadEndFromTile(Tile t,String orientation){
		//TODO vul in.
		if(orientation.equals("N")){
			
		}
		else if (orientation.equals("E")){
			
		}
		else if (orientation.equals("S")){
			
		}
		else if (orientation.equals("W")){
			
		}
		return t;
	}
	
	private static void addNorthWall(Tile t) {
		if(t.getNorthEdge() == null)
			t.setNorthEdge(new Edge(t, null));
		
		t.getNorthEdge().setObstruction(Obstruction.WALL);
	}

	private static void addEastWall(Tile t) {
		if(t.getEastEdge() == null)
			t.setEastEdge(new Edge(t, null));
		
		t.getEastEdge().setObstruction(Obstruction.WALL);
	}

	private static void addSouthWall(Tile t) {
		if(t.getSouthEdge() == null)
			t.setSouthEdge(new Edge(t, null));
		
		t.getSouthEdge().setObstruction(Obstruction.WALL);
	}

	private static void addWestWall(Tile t) {
		if(t.getWestEdge() == null)
			t.setWestEdge(new Edge(t, null));
		
		t.getWestEdge().setObstruction(Obstruction.WALL);
	}

	private static void emptyNorthEdge(Tile t) {
		if (t.getNorthEdge() == null)
			t.setNorthEdge(new Edge(t, null));
		
		t.getNorthEdge().setObstruction(null);
	}

	private static void emptyEastEdge(Tile t) {
		if (t.getEastEdge() == null)
			t.setEastEdge(new Edge(t, null));
			
		t.getEastEdge().setObstruction(null);
	}

	private static void emptySouthEdge(Tile t) {
		if (t.getSouthEdge() == null)
			t.setSouthEdge(new Edge(t, null));
				
		t.getSouthEdge().setObstruction(null);
	}

	private static void emptyWestEdge(Tile t) {
		if (t.getWestEdge() == null)
			t.setWestEdge(new Edge(t, null));
			
		t.getWestEdge().setObstruction(null);
	}

	/**
	 * Checks if this String has any character that isn't a whitespace-character.
	 */
	private static boolean isValuableString(String string) {
		char[] chars = new char[string.length()];
		string.getChars(0, string.length(), chars, 0);
		for (char c : chars) 
			if (!Character.isWhitespace(c))
				return true;
		
		return false;
	}

	
	public static void main(String[] args) {
		JFrame jfr = new JFrame();
		JFileChooser prompt = new JFileChooser();
		prompt.showDialog(jfr,"Open");
		
		File selected = prompt.getSelectedFile();
		MapReader.createMapFromFile(selected);
//		String string= " ";
//		char[] chars = new char[string.length()];
//		string.getChars(0, string.length(), chars, 0);
//		for (char c : chars) {
//			System.out.println(c + " char");
//		}
	}
}
