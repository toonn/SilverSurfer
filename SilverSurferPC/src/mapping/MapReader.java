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
		//This prints out the information row by row
		for (int i = 0; i < infoMatrix.length; i++) {
			for (int j = 0; j < infoMatrix[i].length; j++) {
				System.out.println(infoMatrix[i][j]);
				
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
