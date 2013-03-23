package mapping;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Used to read a map from a given ASCII file.
 */
public class MapReader {

    public static MapGraph createMapFromFile(final File txtFile) {
        final String[][] infoMatrix = createInfoMatrixFromFile(txtFile);

        // Fill a graph with the information in infoMatrix.
        MapGraph map = new MapGraph();
        for (int row = 0; row < infoMatrix.length; row++)
            for (int column = 0; column < infoMatrix[row].length; column++)
                map.addTileXY(new Point(column, row));
            

        for (int row = 0; row < infoMatrix.length; row++) {
            for (int column = 0; column < infoMatrix[row].length; column++) {
                final String[] seperatedInfoIJ = infoMatrix[row][column]
                        .split("\\.");

                // Create the desired tile form first
                // connect it to the graph in a right way later!
                Point pointIJ = new Point(column, row);
                Tile tileIJ = map.getTile(pointIJ);

                generateStructures(seperatedInfoIJ, tileIJ);
            
                generateObjects(seperatedInfoIJ, tileIJ);
                
                
                
                // a seesaw has been specified
//                if (seperatedInfoIJ.length == 4
//                        && "s".equals(seperatedInfoIJ[2])) {
//                	
//                	// add a seesaw to the tile
//                	char[] values = seperatedInfoIJ[3].toCharArray();
//                	tileIJ.setContent(new Seesaw(tileIJ, values[2]));
//                	
//                	// set the right edges
//                	Orientation orientation = Orientation.switchStringToOrientation((String.valueOf(values[0])));
//                	if(Integer.valueOf(String.valueOf(values[1])) == 0)
//	                	tileIJ.getEdge(orientation).replaceObstruction(Obstruction.SEESAW_DOWN);
//                	else if(Integer.valueOf(String.valueOf(values[1])) == 1)
//                		tileIJ.getEdge(orientation).replaceObstruction(Obstruction.SEESAW_UP);
//                	
//                }

            }
        }
        //Make sure all pick-up-objects have the right value.
        for (int row = 0; row < infoMatrix.length; row++) {
            for (int column = 0; column < infoMatrix[row].length; column++) {
            	if(map.getTile(new Point(column,row)) != null)
            		if(map.getTile(new Point(column,row)).getContent() != null)
            			if(map.getTile(new Point(column,row)).getContent() instanceof TreasureObject){
            				//get location of barcode value.
            				Orientation closed = Orientation.switchStringToOrientation(infoMatrix[row][column].split("\\.")[1]);
            				Orientation open = closed.getOppositeOrientation();
            				
            				//open.TODO continue
            				System.out.println(row);
            				System.out.println(column);
            				System.out.println("ha");
            			}
            }
        }
        
        return map;
    }

	private static void generateObjects(final String[] seperatedInfoIJ,
			Tile tileIJ) {
		// a Barcode value has been specified
		if (seperatedInfoIJ.length == 3) {
			//An object has been specified.
			if (seperatedInfoIJ[2].equals("V"))
				tileIJ.setContent(new TreasureObject(tileIJ,0));
			//A StartTile has been specified
			else if (seperatedInfoIJ[2].startsWith("S")){
				Character player = seperatedInfoIJ[2].charAt(1);
				int pNo = Character.getNumericValue(player);
				Character oriChar = seperatedInfoIJ[2].charAt(2);
				Orientation ori = Orientation.switchStringToOrientation(oriChar.toString());
				StartBase base = new StartBase(tileIJ, pNo, ori);
				tileIJ.setContent(base);
			}
			//only possibility left @3 is a barcode.
			else{
				tileIJ.setContent(new Barcode(tileIJ, Integer
		                .valueOf(seperatedInfoIJ[2]), Orientation
		                .switchStringToOrientation(seperatedInfoIJ[1])));
			}
		    
		}

		//A StartTile has been specified
		if (seperatedInfoIJ.length == 4) {
			Character player = seperatedInfoIJ[3].charAt(1);
			int pNo = Character.getNumericValue(player);
			Character oriChar = seperatedInfoIJ[3].charAt(2);
			Orientation ori = Orientation.switchStringToOrientation(oriChar.toString());
			StartBase base = new StartBase(tileIJ, pNo, ori);
			tileIJ.setContent(base);
		}
	}

	private static void generateStructures(final String[] seperatedInfoIJ,
			Tile tileIJ) {
		if (seperatedInfoIJ[0].equals("Cross")) {
		    createCrossFromTile(tileIJ);
		} else if (seperatedInfoIJ[0].equals("Straight")) {
		    createStraightFromTile(
		            tileIJ,
		            Orientation
		                    .switchStringToOrientation(seperatedInfoIJ[1]));

		} else if (seperatedInfoIJ[0].equals("Corner")) {
		    createCornerFromTile(
		            tileIJ,
		            Orientation
		                    .switchStringToOrientation(seperatedInfoIJ[1]));

		} else if (seperatedInfoIJ[0].equals("T")) {
		    createTFromTile(
		            tileIJ,
		            Orientation
		                    .switchStringToOrientation(seperatedInfoIJ[1]));

		} else if (seperatedInfoIJ[0].equals("DeadEnd")) {
		    createDeadEndFromTile(
		            tileIJ,
		            Orientation
		                    .switchStringToOrientation(seperatedInfoIJ[1]));
		} else if (seperatedInfoIJ[0].equals("Closed")){
			createClosedFromTile(tileIJ);
		} else if (seperatedInfoIJ[0].equals("Seesaw")){
			createSeesawFromTile(tileIJ, Orientation
		                    .switchStringToOrientation(seperatedInfoIJ[1]));
		}
	}

    private static String[][] createInfoMatrixFromFile(final File f) {
	    int collums;
	    int rows;
	    try {
	
	        // Setup I/O
	        final BufferedReader readbuffer = new BufferedReader(
	                new FileReader(f));
	        String strRead;
	
	        // Read first line, extract Map-dimensions.
	        strRead = readbuffer.readLine();
	        final String values[] = strRead.split(" ");
	        collums = Integer.valueOf(values[0]);
	        rows = Integer.valueOf(values[1]);
	
	        final String[][] tileTypes = new String[rows][collums];
	        int lineNo = 0;
	        while ((strRead = readbuffer.readLine()) != null) {
	            int collumNo = 0;
	
	            // Seperate comment from non-comment
	            final String splitComment[] = strRead.split("#");
	
	            // Filter whitespace
	            if (isValuableString(splitComment[0])) {
	
	                // Split information on tabs
	                final String splitarray[] = splitComment[0].split("\t");
	
	                for (final String string : splitarray) {
	                    if (isValuableString(string)) {
	                        tileTypes[lineNo][collumNo++] = string;
	                    }
	                }
	
	                lineNo++;
	            }
	        }
	        readbuffer.close(); // toegevoegd om warning weg te werken, geeft
	                            // geen errors?
	        return tileTypes;
	
	    } catch (final Exception e) {
	        System.err
	                .println("[I/O] Sorry, something went wrong reading the File.");
	    }
	
	    return new String[0][0];
	
	}

    private static void createDeadEndFromTile(final Tile t,
	        final Orientation orientation) {
	
	    for (final Orientation ori : Orientation.values()) {
	        if (!ori.equals(orientation.getOppositeOrientation())) {
	            t.getEdge(ori).replaceObstruction(Obstruction.WALL);
	        }
	        else
	        {
	        	System.out.println(ori +" "+orientation);
	        	t.getEdge(ori).replaceObstruction(Obstruction.WHITE_LINE);
	        }
	    }
	}

	/**
	 * Makes sure all edges of this Tile are un-obstructed.
	 */
	private static void createCrossFromTile(final Tile t) {
	    return;
	}

	private static void createCornerFromTile(final Tile t, final Orientation orientation) {
	    t.getEdge(orientation).replaceObstruction(Obstruction.WALL);
	    t.getEdge(orientation.getOtherOrientationCorner()).replaceObstruction(
	            Obstruction.WALL);
	    t.getEdge(orientation.getOppositeOrientation()).replaceObstruction(Obstruction.WHITE_LINE);
	    t.getEdge(orientation.getOppositeOrientation().getOtherOrientationCorner()).replaceObstruction(
	            Obstruction.WHITE_LINE);
	}

	private static void createStraightFromTile(final Tile t,
            final Orientation orientation) {
        for (final Orientation ori : Orientation.values()) {
            if ((!(ori.equals(orientation)))
                    && (!(ori.equals(orientation.getOppositeOrientation())))) {
                t.getEdge(ori).replaceObstruction(Obstruction.WALL);
            }
            else
            	t.getEdge(ori).replaceObstruction(Obstruction.WHITE_LINE);
           

        }
    }

    private static void createTFromTile(final Tile t, final Orientation orientation) {
    	for (final Orientation ori : Orientation.values()) {
    		if(ori.equals(orientation))
    			t.getEdge(ori).replaceObstruction(Obstruction.WALL);
    		else
    			t.getEdge(ori).replaceObstruction(Obstruction.WHITE_LINE);
    	}
        
    }
    
    private static void createClosedFromTile(final Tile t){
    	for (final Orientation ori : Orientation.values())
    		t.getEdge(ori).replaceObstruction(Obstruction.WALL);
    }
    
    private static void createSeesawFromTile(final Tile t, Orientation ori){
    	createStraightFromTile(t, ori);
    	Seesaw saw = new Seesaw(t, ori);
    	t.setContent(saw);
    }

    /**
     * Checks if this String has any character that isn't a
     * whitespace-character.
     */
    private static boolean isValuableString(final String string) {
        final char[] chars = new char[string.length()];
        string.getChars(0, string.length(), chars, 0);
        for (final char c : chars) {
            if (!Character.isWhitespace(c)) {
                return true;
            }
        }

        return false;
    }
}
