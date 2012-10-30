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
		int collums;
		int rows;
		try {
			
			//Setup I/O
			BufferedReader readbuffer = new BufferedReader(new FileReader(txtFile));
			String strRead;
			
			//Read first line, extract Map-dimensions.
			strRead = readbuffer.readLine();
			String values[] = strRead.split(" ");
			collums = Integer.valueOf(values[0]);
			rows = Integer.valueOf(values[1]);


			while ((strRead = readbuffer.readLine()) != null) {
				String splitarray[] = strRead.split("\t");
				
				for (String string : splitarray) {
					System.out.println(string + " split");
				}
				System.out.println("endofline");
			}
			System.out.println(collums + " collums");
			System.out.println(rows + " rows");

		} catch (FileNotFoundException e) {
			System.err.println("[I/O] Sorry, didn't find that File.");
		} catch (IOException e) {
			System.err.println("[I/O] Sorry, something went wrong reading the File.");
		}
		String strRead;

		return null;
	}
	
	public static void main(String[] args) {
		JFrame jfr = new JFrame();
		JFileChooser prompt = new JFileChooser();
		prompt.showDialog(jfr,"Open");
		
		File selected = prompt.getSelectedFile();
		MapReader.createMapFromFile(selected);
	}
}
