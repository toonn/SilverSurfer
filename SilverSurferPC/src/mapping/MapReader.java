package mapping;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import commands.BarcodeCommand;

public class MapReader {

    public static MapGraph createMapFromFile(final File txtFile) {
        return createMapFromInfoMatrix(createInfoMatrixFromFile(txtFile));
    }

    private static String[][] createInfoMatrixFromFile(final File f) {
        int collums, rows;
        String strRead;
        try {
            // Setup I/O
            final BufferedReader readbuffer = new BufferedReader(new FileReader(f));

            // Read first line, extract Map-dimensions.
            strRead = readbuffer.readLine();
            final String values[] = strRead.split(" ");
            collums = Integer.valueOf(values[0]);
            rows = Integer.valueOf(values[1]);

            final String[][] tileTypes = new String[rows][collums];
            int lineNo = 0;
            while ((strRead = readbuffer.readLine()) != null) {
                // Separate comment from non-comment
                final String splitComment[] = strRead.split("#");

                // Filter whitespace and split information on tabs
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

    private static MapGraph createMapFromInfoMatrix(final String[][] infoMatrix) {
        MapGraph map = new MapGraph();
        for (int row = 0; row < infoMatrix.length; row++)
            for (int column = 0; column < infoMatrix[row].length; column++) {
                String[] seperatedInfoIJ = infoMatrix[row][column].split("\\.");
                map.addTile(new Point(column, row));
                Tile tileIJ = map.getTile(new Point(column, row));
                generateStructures(seperatedInfoIJ, tileIJ);
                generateObjects(seperatedInfoIJ, tileIJ);
            }
        tagTreasures(map);
        initializeSeesaws(map);
        return map;
    }

    public static void generateStructures(final String[] seperatedInfoIJ, Tile tile) {
        if (seperatedInfoIJ[0].equals("Cross"))
            createCrossFromTile(tile);
        else if (seperatedInfoIJ[0].equals("Straight"))
            createStraightFromTile(tile,
                    switchStringToOrientation(seperatedInfoIJ[1]));
        else if (seperatedInfoIJ[0].equals("Corner"))
            createCornerFromTile(tile,
                    switchStringToOrientation(seperatedInfoIJ[1]));
        else if (seperatedInfoIJ[0].equals("T"))
            createTFromTile(tile, switchStringToOrientation(seperatedInfoIJ[1]));
        else if (seperatedInfoIJ[0].equals("DeadEnd"))
            createDeadEndFromTile(tile,
                    switchStringToOrientation(seperatedInfoIJ[1]));
        else if (seperatedInfoIJ[0].equals("Closed"))
            createClosedFromTile(tile);
        else if (seperatedInfoIJ[0].equals("Seesaw"))
            createSeesawFromTile(tile,
                    switchStringToOrientation(seperatedInfoIJ[1]));
        else
            throw new IllegalArgumentException(
                    "MapReader.generateStructures(): Unchecked structure in the map!");
    }

    private static void createCrossFromTile(final Tile tile) {
        return;
    }

    private static void createStraightFromTile(final Tile tile,
            final Orientation orientation) {
        for (final Orientation ori : Orientation.values())
            if (ori.equals(orientation)
                    || ori.equals(orientation.getOppositeOrientation()))
                tile.getEdgeAt(ori).setObstruction(Obstruction.WHITE_LINE);
            else
                tile.getEdgeAt(ori).setObstruction(Obstruction.WALL);
    }

    private static void createCornerFromTile(final Tile tile,
            final Orientation orientation) {
        tile.getEdgeAt(orientation).setObstruction(Obstruction.WALL);
        tile.getEdgeAt(orientation.getCounterClockwiseOrientation())
                .setObstruction(Obstruction.WALL);
        tile.getEdgeAt(orientation.getOppositeOrientation()).setObstruction(
                Obstruction.WHITE_LINE);
        tile.getEdgeAt(
                orientation.getOppositeOrientation()
                        .getCounterClockwiseOrientation()).setObstruction(
                Obstruction.WHITE_LINE);
    }

    private static void createTFromTile(final Tile tile,
            final Orientation orientation) {
        for (final Orientation ori : Orientation.values())
            if (ori.equals(orientation))
                tile.getEdgeAt(ori).setObstruction(Obstruction.WALL);
            else
                tile.getEdgeAt(ori).setObstruction(Obstruction.WHITE_LINE);
    }

    private static void createDeadEndFromTile(final Tile tile,
            final Orientation orientation) {
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

    private static void createSeesawFromTile(final Tile tile,
            Orientation orientation) {
        createStraightFromTile(tile, orientation);
        Seesaw saw = new Seesaw(tile, orientation);
        tile.setContent(saw);
    }

    private static void generateObjects(final String[] seperatedInfoIJ, Tile tile) {
        // An extra value has been found.
        if (seperatedInfoIJ.length == 3) {
            // An object has been specified.
            if (seperatedInfoIJ[2].equals("V"))
                tile.setContent(new TreasureObject(tile, 0));

            // A StartTile has been specified.
            else if (seperatedInfoIJ[2].startsWith("S")) {
                Character player = seperatedInfoIJ[2].charAt(1);
                int playerNumber = Character.getNumericValue(player);
                Character oriChar = seperatedInfoIJ[2].charAt(2);
                Orientation orientation = switchStringToOrientation(oriChar.toString());
                StartBase base = new StartBase(tile, playerNumber, orientation);
                tile.setContent(base);
            }

            // A Barcode has been specified.
            else
                tile.setContent(new Barcode(tile, Integer.valueOf(seperatedInfoIJ[2]), switchStringToOrientation(seperatedInfoIJ[1])));
        }
    }

    /**
     * Make sure all treasure-objects get tagged in a correct way.
     */
    private static void tagTreasures(MapGraph map) {
    	for(Tile tile : map.getTiles())
    		if (tile.getContent() != null && tile.getContent() instanceof TreasureObject) {
    			TreasureObject treasure = (TreasureObject)tile.getContent();
    			Orientation orientation = switchStringToOrientation(tile.getToken().split("\\.")[1]).getOppositeOrientation();
    			Tile neighbour = tile.getEdgeAt(orientation).getNeighbour(tile);
    			treasure.setValue(Barcode.getPlayerNumberFrom(Integer.valueOf(neighbour.getToken().split("\\.")[2])));
    			treasure.setTeamNumber(Barcode.getTeamNumberFrom(Integer.valueOf(neighbour.getToken().split("\\.")[2])));
    		}
    }

    private static void initializeSeesaws(MapGraph map) {
    	for(Tile tile : map.getTiles())
    		if (tile.getContent() != null && tile.getContent() instanceof Seesaw) {
    			if (switchStringToOrientation(tile.getToken().split("\\.")[1]) == Orientation.NORTH) {
    				Tile northBarcodeTile = tile.getNorthNeighbour();
    				Tile southBarcodeTile = tile.getSouthNeighbour().getSouthNeighbour();
    				Tile southSeesawTile = tile.getSouthNeighbour();
    				int firstBarcode = Integer.valueOf(northBarcodeTile.getToken().split("\\.")[2]);
    				int secondBarcode = Integer.valueOf(southBarcodeTile.getToken().split("\\.")[2]);
    				int index = 0;
    				if (firstBarcode < secondBarcode) {
    					for (int i = 0; i < BarcodeCommand.SEESAW_START.length; i++)
    						if (firstBarcode == BarcodeCommand.SEESAW_START[i] || firstBarcode == BarcodeCommand.SEESAW_START_INVERSE[i])
    							index = i;
    					((Seesaw)(southSeesawTile.getContent())).switchClosed();
    					tile.getEdgeAt(Orientation.NORTH).setObstruction(Obstruction.SEESAW_DOWN);
    					southSeesawTile.getEdgeAt(Orientation.SOUTH).setObstruction(Obstruction.SEESAW_UP);
    				}
    				else {
    					for (int i = 0; i < BarcodeCommand.SEESAW_START.length; i++)
    						if (secondBarcode == BarcodeCommand.SEESAW_START[i] || secondBarcode == BarcodeCommand.SEESAW_START_INVERSE[i])
    							index = i;
    					((Seesaw)(tile.getContent())).switchClosed();
    					tile.getEdgeAt(Orientation.NORTH).setObstruction(Obstruction.SEESAW_UP);
    					southSeesawTile.getEdgeAt(Orientation.SOUTH).setObstruction(Obstruction.SEESAW_DOWN);
    				}
					((Seesaw)(tile.getContent())).setValue(index);
					((Seesaw)(southSeesawTile.getContent())).setValue(index);
					((Seesaw)(tile.getContent())).setOtherSeesaw((Seesaw)(southSeesawTile.getContent()));
					((Seesaw)(southSeesawTile.getContent())).setOtherSeesaw((Seesaw)(tile.getContent()));
    				tile.getEdgeAt(Orientation.SOUTH).setObstruction(Obstruction.SEESAW_FLIP);
    			}
    			else if (switchStringToOrientation(tile.getToken().split("\\.")[1]) == Orientation.EAST) {
    				Tile eastBarcodeTile = tile.getEastNeighbour();
    				Tile westBarcodeTile = tile.getWestNeighbour().getWestNeighbour();
    				Tile westSeesawTile = tile.getWestNeighbour();
    				int firstBarcode = Integer.valueOf(eastBarcodeTile.getToken().split("\\.")[2]);
    				int secondBarcode = Integer.valueOf(westBarcodeTile.getToken().split("\\.")[2]);
    				int index = 0;
    				if (firstBarcode < secondBarcode) {
    					for (int i = 0; i < BarcodeCommand.SEESAW_START.length; i++)
    						if (firstBarcode == BarcodeCommand.SEESAW_START[i] || firstBarcode == BarcodeCommand.SEESAW_START_INVERSE[i])
    							index = i;
    					((Seesaw)(westSeesawTile.getContent())).switchClosed();
    					tile.getEdgeAt(Orientation.EAST).setObstruction(Obstruction.SEESAW_DOWN);
    					westSeesawTile.getEdgeAt(Orientation.WEST).setObstruction(Obstruction.SEESAW_UP);
    				}
    				else {
    					for (int i = 0; i < BarcodeCommand.SEESAW_START.length; i++)
    						if (secondBarcode == BarcodeCommand.SEESAW_START[i] || secondBarcode == BarcodeCommand.SEESAW_START_INVERSE[i])
    							index = i;
    					((Seesaw)(tile.getContent())).switchClosed();
    					tile.getEdgeAt(Orientation.EAST).setObstruction(Obstruction.SEESAW_UP);
    					westSeesawTile.getEdgeAt(Orientation.WEST).setObstruction(Obstruction.SEESAW_DOWN);
    				}
					((Seesaw)(tile.getContent())).setValue(index);
					((Seesaw)(westSeesawTile.getContent())).setValue(index);
					((Seesaw)(tile.getContent())).setOtherSeesaw((Seesaw)(westSeesawTile.getContent()));
					((Seesaw)(westSeesawTile.getContent())).setOtherSeesaw((Seesaw)(tile.getContent()));
    				tile.getEdgeAt(Orientation.WEST).setObstruction(Obstruction.SEESAW_FLIP);
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

    public static MapGraph createMapFromTiles(List<peno.htttp.Tile> tiles) {
        MapGraph map = new MapGraph();
        for (peno.htttp.Tile receivedTile : tiles) {
            Point point = new Point((int) receivedTile.getX(), (int) receivedTile.getY());
            map.addTile(point);
            Tile tile = map.getTile(point);
            String[] seperatedInfoIJ = receivedTile.getToken().split("\\.");
            MapReader.generateStructures(seperatedInfoIJ, tile);
            MapReader.generateObjects(seperatedInfoIJ, tile);
        }
        for(Tile tile : map.getTiles())
            if(tile.getContent() != null && tile.getContent() instanceof TreasureObject) {
            	TreasureObject treasure = (TreasureObject)tile.getContent();
            	Orientation orientation = MapReader.switchStringToOrientation(tile.getToken().split("\\.")[1]).getOppositeOrientation();
            	Tile neighbour = tile.getEdgeAt(orientation).getNeighbour(tile);
            	treasure.setValue(Barcode.getPlayerNumberFrom(Integer.valueOf(neighbour.getToken().split("\\.")[2])));
            	treasure.setTeamNumber(Barcode.getTeamNumberFrom(Integer.valueOf(neighbour.getToken().split("\\.")[2])));
            }
        return map;
    }
}