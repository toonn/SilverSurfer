package mapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Used to read a map from a given ASCII file.
 */
public class MapReader {

    public static void createCornerFromTile(final Tile t,
            final Orientation orientation) {
        t.getEdge(orientation).setObstruction(Obstruction.WALL);
        t.getEdge(orientation.getOtherOrientationCorner()).setObstruction(
                Obstruction.WALL);
    }

    /**
     * Makes sure all edges of this Tile are un-obstructed.
     */
    public static void createCrossFromTile(final Tile t) {
        return;
    }

    public static void createDeadEndFromTile(final Tile t,
            final Orientation orientation) {

        for (final Orientation ori : Orientation.values()) {
            if (!ori.equals(orientation.getOppositeOrientation())) {
                t.getEdge(ori).setObstruction(Obstruction.WALL);
            }
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

            return tileTypes;

        } catch (final Exception e) {
            System.err
                    .println("[I/O] Sorry, something went wrong reading the File.");
        }

        return new String[0][0];

    }

    public static MapGraph createMapFromFile(final File txtFile,
            final int relCoX, final int relCoY) {
        final String[][] infoMatrix = createInfoMatrixFromFile(txtFile);

        // Fill a graph with the information in infoMatrix.
        MapGraph map = null;
        map = new MapGraph(relCoX, relCoY);
        // This prints out the information row by row
        for (int i = 0; i < infoMatrix.length; i++) {
            for (int j = 0; j < infoMatrix[i].length; j++) {

                Tile tileIJ = null;
                final String[] seperatedInfoIJ = infoMatrix[i][j].split("\\.");

                // Create the desired tile form first
                // connect it to the graph in a right way later!
                tileIJ = new Tile();

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
                }
                // a barcode value has been specified
                if (seperatedInfoIJ.length == 3) {
                    if (seperatedInfoIJ[1].equals("N")) {
                        tileIJ.setContent(new Barcode(Integer
                                .valueOf(seperatedInfoIJ[2]),
                                Orientation.NORTH, tileIJ));
                    } else if (seperatedInfoIJ[1].equals("E")) {
                        tileIJ.setContent(new Barcode(Integer
                                .valueOf(seperatedInfoIJ[2]), Orientation.EAST,
                                tileIJ));
                    } else if (seperatedInfoIJ[1].equals("S")) {
                        tileIJ.setContent(new Barcode(Integer
                                .valueOf(seperatedInfoIJ[2]),
                                Orientation.SOUTH, tileIJ));
                    } else if (seperatedInfoIJ[1].equals("W")) {
                        tileIJ.setContent(new Barcode(Integer
                                .valueOf(seperatedInfoIJ[2]), Orientation.WEST,
                                tileIJ));
                    }
                }

                map.setTileXY(j, i, tileIJ);
            }
        }
        return map;
    }

    public static void createStraightFromTile(final Tile t,
            final Orientation orientation) {
        for (final Orientation ori : Orientation.values()) {
            if ((!(ori.equals(orientation)))
                    && (!(ori.equals(orientation.getOppositeOrientation())))) {
                t.getEdge(ori).setObstruction(Obstruction.WALL);
            }

        }
    }

    public static void createTFromTile(final Tile t,
            final Orientation orientation) {
        t.getEdge(orientation).setObstruction(Obstruction.WALL);
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
