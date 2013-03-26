package mapping;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import commands.BarcodeCommand;

public class MapReader {

    public static MapGraph createMapFromFile(final File txtFile) {
        final String[][] infoMatrix = createInfoMatrixFromFile(txtFile);
        return createMapFromInfoMatrix(infoMatrix);
    }

	public static MapGraph createMapFromInfoMatrix(final String[][] infoMatrix) {
		//Fill a graph with the information in infoMatrix.
        MapGraph map = new MapGraph();
        for (int row = 0; row < infoMatrix.length; row++)
            for (int column = 0; column < infoMatrix[row].length; column++)
                map.addTile(new Point(column, row));

        for (int row = 0; row < infoMatrix.length; row++)
            for (int column = 0; column < infoMatrix[row].length; column++) {
                final String[] seperatedInfoIJ = infoMatrix[row][column].split("\\.");
                Point pointIJ = new Point(column, row);
                Tile tileIJ = map.getTile(pointIJ);
                generateStructures(seperatedInfoIJ, tileIJ);
                generateObjects(seperatedInfoIJ, tileIJ);
            }
        tagTreasures(infoMatrix, map);
        initializeSeesaws(infoMatrix, map);
        return map;
	}

	public static MapGraph createMapFromTiles(List<peno.htttp.Tile> tiles){
		for (peno.htttp.Tile tile : tiles) {
			System.out.println("x: " + tile.getX() + " y: " + tile.getY() + " token: " + tile.getToken());
		}
		return null;
	}
    private static String[][] createInfoMatrixFromFile(final File f) {
	    int collums;
	    int rows;
	    String strRead;
	    try {
	        //Setup I/O
	        final BufferedReader readbuffer = new BufferedReader(new FileReader(f));
	
	        //Read first line, extract Map-dimensions.
	        strRead = readbuffer.readLine();
	        final String values[] = strRead.split(" ");
	        collums = Integer.valueOf(values[0]);
	        rows = Integer.valueOf(values[1]);
	
	        final String[][] tileTypes = new String[rows][collums];
	        int lineNo = 0;
	        while ((strRead = readbuffer.readLine()) != null) {
	            //Separate comment from non-comment
	            final String splitComment[] = strRead.split("#");
	
	            //Filter whitespace and split information on tabs
	            if (isValuableString(splitComment[0])) {
	                final String splitarray[] = splitComment[0].split("\t");
		            int collumNo = 0;
	                for (final String string : splitarray)
	                    if (isValuableString(string))
	                        tileTypes[lineNo][collumNo++] = string;
	                lineNo++;
	            }
	        }
	        readbuffer.close();
	        return tileTypes;
	    } catch (final Exception e) {
	        System.err.println("[I/O] Sorry, something went wrong reading the File.");
		    return new String[0][0];
	    }
	}

    /**
     * Checks if this String has any character that isn't a whitespace-character.
     */
    private static boolean isValuableString(final String string) {
        final char[] chars = new char[string.length()];
        string.getChars(0, string.length(), chars, 0);
        for (final char c : chars)
            if (!Character.isWhitespace(c))
                return true;
        return false;
    }

	public static void generateStructures(final String[] seperatedInfoIJ, Tile tile) {
		if (seperatedInfoIJ[0].equals("Cross"))
		    createCrossFromTile(tile);
		else if (seperatedInfoIJ[0].equals("Straight"))
		    createStraightFromTile(tile, switchStringToOrientation(seperatedInfoIJ[1]));
		else if (seperatedInfoIJ[0].equals("Corner"))
		    createCornerFromTile(tile, switchStringToOrientation(seperatedInfoIJ[1]));
		else if (seperatedInfoIJ[0].equals("T"))
		    createTFromTile(tile, switchStringToOrientation(seperatedInfoIJ[1]));
		else if (seperatedInfoIJ[0].equals("DeadEnd"))
		    createDeadEndFromTile(tile, switchStringToOrientation(seperatedInfoIJ[1]));
		else if (seperatedInfoIJ[0].equals("Closed"))
			createClosedFromTile(tile);
		else if (seperatedInfoIJ[0].equals("Seesaw"))
			createSeesawFromTile(tile, switchStringToOrientation(seperatedInfoIJ[1]));
		else
			throw new IllegalArgumentException("MapReader.generateStructures: Unchecked structure in the map!");
	}

	private static void createCrossFromTile(final Tile tile) {
	    return;
	}

	private static void createStraightFromTile(final Tile tile, final Orientation orientation) {
        for (final Orientation ori : Orientation.values()) 
            if (ori.equals(orientation) || ori.equals(orientation.getOppositeOrientation()))
            	tile.getEdgeAt(ori).setObstruction(Obstruction.WHITE_LINE);
            else
            	tile.getEdgeAt(ori).setObstruction(Obstruction.WALL);
    }

	private static void createCornerFromTile(final Tile tile, final Orientation orientation) {
	    tile.getEdgeAt(orientation).setObstruction(Obstruction.WALL);
	    tile.getEdgeAt(orientation.getCounterClockwiseOrientation()).setObstruction(Obstruction.WALL);
	    tile.getEdgeAt(orientation.getOppositeOrientation()).setObstruction(Obstruction.WHITE_LINE);
	    tile.getEdgeAt(orientation.getOppositeOrientation().getCounterClockwiseOrientation()).setObstruction(Obstruction.WHITE_LINE);
	}

    private static void createTFromTile(final Tile tile, final Orientation orientation) {
    	for (final Orientation ori : Orientation.values())
    		if(ori.equals(orientation))
    			tile.getEdgeAt(ori).setObstruction(Obstruction.WALL);
    		else
    			tile.getEdgeAt(ori).setObstruction(Obstruction.WHITE_LINE);
    }

    private static void createDeadEndFromTile(final Tile tile, final Orientation orientation) {
	    for (final Orientation ori : Orientation.values())
	        if (ori.equals(orientation.getOppositeOrientation()))
	        	tile.getEdgeAt(ori).setObstruction(Obstruction.WHITE_LINE);
	        else
	        	tile.getEdgeAt(ori).setObstruction(Obstruction.WALL);
	}
    
    private static void createClosedFromTile(final Tile tile) {
    	for (final Orientation ori : Orientation.values())
    		tile.getEdgeAt(ori).setObstruction(Obstruction.WALL);
    }
    
    private static void createSeesawFromTile(final Tile tile, Orientation orientation) {
    	createStraightFromTile(tile, orientation);
    	Seesaw saw = new Seesaw(tile, orientation);
    	tile.setContent(saw);
    }

	public static void generateObjects(final String[] seperatedInfoIJ, Tile tile) {
		//An extra value has been found.
		if (seperatedInfoIJ.length == 3) {
			//An object has been specified.
			if (seperatedInfoIJ[2].equals("V"))
				tile.setContent(new TreasureObject(tile, 0));
			
			//A StartTile has been specified.
			else if (seperatedInfoIJ[2].startsWith("S")) {
				Character player = seperatedInfoIJ[2].charAt(1);
				int playerNumber = Character.getNumericValue(player);
				Character oriChar = seperatedInfoIJ[2].charAt(2);
				Orientation orientation = switchStringToOrientation(oriChar.toString());
				StartBase base = new StartBase(tile, playerNumber, orientation);
				tile.setContent(base);
			}
			
			//A Barcode has been specified.
			else
				tile.setContent(new Barcode(tile, Integer.valueOf(seperatedInfoIJ[2]), switchStringToOrientation(seperatedInfoIJ[1])));
		}
	}
    
    /**
     * Make sure all treasure-objects get tagged in a correct way.
     */
	public static void tagTreasures(final String[][] infoMatrix, MapGraph map) {
		for (int row = 0; row < infoMatrix.length; row++)
            for (int column = 0; column < infoMatrix[row].length; column++)
            	if(map.getTile(new Point(column,row)) != null && map.getTile(new Point(column,row)).getContent() != null && map.getTile(new Point(column,row)).getContent() instanceof TreasureObject) {
            		TreasureObject treasure = ((TreasureObject)map.getTile(new Point(column,row)).getContent());
            		
            		//Get location of barcode value.
            		Orientation orientation = switchStringToOrientation(infoMatrix[row][column].split("\\.")[1]).getOppositeOrientation();
            		
            		//Get the player-number from the right barcode and update the object with it.
            		if(orientation.equals(Orientation.NORTH))
            			treasure.setValue(Barcode.getPlayerNumberFrom(Integer.valueOf(infoMatrix[row-1][column].split("\\.")[2])));
            		else if(orientation.equals(Orientation.EAST))
            			treasure.setValue(Barcode.getPlayerNumberFrom(Integer.valueOf(infoMatrix[row][column+1].split("\\.")[2])));
            		else if(orientation.equals(Orientation.SOUTH))
            			treasure.setValue(Barcode.getPlayerNumberFrom(Integer.valueOf(infoMatrix[row+1][column].split("\\.")[2])));
            		else if(orientation.equals(Orientation.WEST))
            			treasure.setValue(Barcode.getPlayerNumberFrom(Integer.valueOf(infoMatrix[row][column-1].split("\\.")[2])));
            		
            		//Get the team-number from the right barcode and update the object with it.
            		if(orientation.equals(Orientation.NORTH))
            			treasure.setTeamNumber(Barcode.getTeamNumberFrom(Integer.valueOf(infoMatrix[row-1][column].split("\\.")[2])));
            		else if(orientation.equals(Orientation.EAST))
            			treasure.setTeamNumber(Barcode.getTeamNumberFrom(Integer.valueOf(infoMatrix[row][column+1].split("\\.")[2])));
            		else if(orientation.equals(Orientation.SOUTH))
            			treasure.setTeamNumber(Barcode.getTeamNumberFrom(Integer.valueOf(infoMatrix[row+1][column].split("\\.")[2])));
            		else if(orientation.equals(Orientation.WEST))
            			treasure.setTeamNumber(Barcode.getTeamNumberFrom(Integer.valueOf(infoMatrix[row][column-1].split("\\.")[2])));
            	}
	}
    
    public static void initializeSeesaws(final String[][] infoMatrix, MapGraph map) {
		for (int row = 0; row < infoMatrix.length; row++)
            for (int column = 0; column < infoMatrix[row].length; column++) 
            	if(map.getTile(new Point(column,row)) != null && map.getTile(new Point(column,row)).getContent() != null && map.getTile(new Point(column,row)).getContent() instanceof Seesaw) {
            		if(switchStringToOrientation(infoMatrix[row][column].split("\\.")[1]) == Orientation.NORTH) {
            			int firstBarcode = Integer.valueOf(infoMatrix[row-1][column].split("\\.")[2]);
            			int secondBarcode = Integer.valueOf(infoMatrix[row+2][column].split("\\.")[2]);
        				int index = 0;
            			if(firstBarcode < secondBarcode) {
            				for(int i = 0; i < BarcodeCommand.SEESAW_START.length; i++)
            					if(firstBarcode == BarcodeCommand.SEESAW_START[i] || firstBarcode == BarcodeCommand.SEESAW_START_INVERSE[i])
            						index = i;
            				((Seesaw)(map.getTile(new Point(column,row)).getContent())).setValue(index);
            				((Seesaw)(map.getTile(new Point(column,row+1)).getContent())).setValue(index);
            				((Seesaw)(map.getTile(new Point(column,row+1)).getContent())).switchClosed();
            				((Seesaw)(map.getTile(new Point(column,row)).getContent())).setOtherSeesaw((Seesaw)(map.getTile(new Point(column,row+1)).getContent()));
            				((Seesaw)(map.getTile(new Point(column,row+1)).getContent())).setOtherSeesaw((Seesaw)(map.getTile(new Point(column,row)).getContent()));
            				map.getTile(new Point(column,row)).getEdgeAt(Orientation.NORTH).setObstruction(Obstruction.SEESAW_DOWN);
            				map.getTile(new Point(column,row+1)).getEdgeAt(Orientation.SOUTH).setObstruction(Obstruction.SEESAW_UP);
            			}
            			else {
            				for(int i = 0; i < BarcodeCommand.SEESAW_START.length; i++)
            					if(secondBarcode == BarcodeCommand.SEESAW_START[i] || secondBarcode == BarcodeCommand.SEESAW_START_INVERSE[i])
            						index = i;
            				((Seesaw)(map.getTile(new Point(column,row)).getContent())).setValue(index);
            				((Seesaw)(map.getTile(new Point(column,row+1)).getContent())).setValue(index);
            				((Seesaw)(map.getTile(new Point(column,row)).getContent())).switchClosed();
            				((Seesaw)(map.getTile(new Point(column,row)).getContent())).setOtherSeesaw((Seesaw)(map.getTile(new Point(column,row+1)).getContent()));
            				((Seesaw)(map.getTile(new Point(column,row+1)).getContent())).setOtherSeesaw((Seesaw)(map.getTile(new Point(column,row)).getContent()));
            				map.getTile(new Point(column,row)).getEdgeAt(Orientation.NORTH).setObstruction(Obstruction.SEESAW_UP);
            				map.getTile(new Point(column,row+1)).getEdgeAt(Orientation.SOUTH).setObstruction(Obstruction.SEESAW_DOWN);
            			}
            			map.getTile(new Point(column,row)).getEdgeAt(Orientation.SOUTH).setObstruction(Obstruction.SEESAW_FLIP);
            		}
            		else if(switchStringToOrientation(infoMatrix[row][column].split("\\.")[1]) == Orientation.EAST) {
            			int firstBarcode = Integer.valueOf(infoMatrix[row][column+1].split("\\.")[2]);
            			int secondBarcode = Integer.valueOf(infoMatrix[row][column-2].split("\\.")[2]);
        				int index = 0;
            			if(firstBarcode < secondBarcode) {
            				for(int i = 0; i < BarcodeCommand.SEESAW_START.length; i++)
            					if(firstBarcode == BarcodeCommand.SEESAW_START[i] || firstBarcode == BarcodeCommand.SEESAW_START_INVERSE[i])
            						index = i;
            				((Seesaw)(map.getTile(new Point(column,row)).getContent())).setValue(index);
            				((Seesaw)(map.getTile(new Point(column-1,row)).getContent())).setValue(index);
            				((Seesaw)(map.getTile(new Point(column-1,row)).getContent())).switchClosed();
            				((Seesaw)(map.getTile(new Point(column,row)).getContent())).setOtherSeesaw((Seesaw)(map.getTile(new Point(column-1,row)).getContent()));
            				((Seesaw)(map.getTile(new Point(column-1,row)).getContent())).setOtherSeesaw((Seesaw)(map.getTile(new Point(column,row)).getContent()));
            				map.getTile(new Point(column,row)).getEdgeAt(Orientation.EAST).setObstruction(Obstruction.SEESAW_DOWN);
            				map.getTile(new Point(column-1,row)).getEdgeAt(Orientation.WEST).setObstruction(Obstruction.SEESAW_UP);
            			}
            			else {
            				for(int i = 0; i < BarcodeCommand.SEESAW_START.length; i++)
            					if(secondBarcode == BarcodeCommand.SEESAW_START[i] || secondBarcode == BarcodeCommand.SEESAW_START_INVERSE[i])
            						index = i;
            				((Seesaw)(map.getTile(new Point(column,row)).getContent())).setValue(index);
            				((Seesaw)(map.getTile(new Point(column-1,row)).getContent())).setValue(index);
            				((Seesaw)(map.getTile(new Point(column,row)).getContent())).switchClosed();
            				((Seesaw)(map.getTile(new Point(column,row)).getContent())).setOtherSeesaw((Seesaw)(map.getTile(new Point(column-1,row)).getContent()));
            				((Seesaw)(map.getTile(new Point(column-1,row)).getContent())).setOtherSeesaw((Seesaw)(map.getTile(new Point(column,row)).getContent()));
            				map.getTile(new Point(column,row)).getEdgeAt(Orientation.EAST).setObstruction(Obstruction.SEESAW_UP);
            				map.getTile(new Point(column-1,row)).getEdgeAt(Orientation.WEST).setObstruction(Obstruction.SEESAW_DOWN);
            			}
            			map.getTile(new Point(column,row)).getEdgeAt(Orientation.WEST).setObstruction(Obstruction.SEESAW_FLIP);
            		}
            	}
    }
	
    private static Orientation switchStringToOrientation(final String string) {
        if (string.equals("N"))
            return Orientation.NORTH;
        else if (string.equals("S"))
            return Orientation.SOUTH;
        else if (string.equals("E"))
            return Orientation.EAST;
        else
            return Orientation.WEST;
    }
}