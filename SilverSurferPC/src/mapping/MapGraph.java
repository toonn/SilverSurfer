package mapping;

import java.util.HashSet;
import java.util.Set;

public class MapGraph {

    private final int[] startingTileCoordinates = new int[2];
    private final int[] currentTileCoordinates = new int[2];
    private final Set<Tile> tiles = new HashSet<Tile>();

    /**
     * Creates a new Map
     */
    public MapGraph() {
        setStartingTileCoordinates(0, 0);
        setCurrentTileCoordinates(0, 0);
    }

    /**
     * Creates a new Map with the tile with defined coordinates as coordinates
     * as starting Tile.
     * 
     * @param start
     */
    public MapGraph(final int x, final int y) {
        setStartingTileCoordinates(x, y);
        setCurrentTileCoordinates(x, y);

    }

    public void addContentToCurrentTile(final Barcode code) {
        getCurrentTile().setContent(code);
    }

    /**
     * Adds a given obstruction to the edge on the given orientation of the
     * current tile.
     */
    public void addObstruction(final Obstruction obst,
            final Orientation orientation) {
        if (getCurrentTile() == null) {
            throw new IllegalArgumentException(
                    "currenttile is null in addobstruction");
        } else if (getCurrentTile().getEdge(orientation) == null) {
            throw new IllegalArgumentException("edge is null in addobstruction");
        }

        getCurrentTile().getEdge(orientation).setObstruction(obst);
    }

    public TileContent getContentCurrentTile() {
        return getCurrentTile().getContent();
    }

    /**
     * Returns the tile the simulator or robot is currently on.
     */
    public Tile getCurrentTile() {
        for (final Tile tile : tiles) {
            if (tile.getxCoordinate() == getCurrentTileCoordinates()[0]
                    && tile.getyCoordinate() == getCurrentTileCoordinates()[1]) {
                return tile;
            }
        }
        return null;
    }

    public int[] getCurrentTileCoordinates() {
        return currentTileCoordinates;
    }

    /**
     * Returns the obstruction on the given orientation of the current tile.
     * ofwel wall ofwel null
     */
    public Obstruction getObstruction(final Orientation orientation) {
        return getCurrentTile().getEdge(orientation).getObstruction();
    }

    /**
     * Returns the Tile on which this map was started.
     */
    public Tile getStartingTile() {
        for (final Tile tile : tiles) {
            if (tile.getxCoordinate() == getStartingTileCoordinates()[0]
                    && tile.getyCoordinate() == getStartingTileCoordinates()[1]) {
                return tile;
            }
        }
        // gebeurt nooit
        throw new IllegalStateException("bij getStartingTile in MapGraph");
    }

    public int[] getStartingTileCoordinates() {
        return startingTileCoordinates;
    }

    public Set<Tile> getTiles() {
        return tiles;
    }

    public Tile getTileWithCoordinates(final int xCoordinate,
            final int yCoordinate) {
        for (final Tile tile : tiles) {
            if (tile.getxCoordinate() == xCoordinate
                    && tile.getyCoordinate() == yCoordinate) {
                return tile;
            }
        }
        // als er op deze coordinaten nog geen tile staat
        return null;
    }

    public void removeTile(final int x, final int y) {
        getTileWithCoordinates(x, y).terminate();
        tiles.remove(getTileWithCoordinates(x, y));
    }

    public void setCurrentTileCoordinates(final int x, final int y) {
        currentTileCoordinates[0] = x;
        currentTileCoordinates[1] = y;
    }

    public void setStartingTileCoordinates(final int x, final int y) {
        startingTileCoordinates[0] = x;
        startingTileCoordinates[1] = y;
    }

    public void setTileXY(final int x, final int y, final Tile tile) {
        tile.setxCoordinate(x);
        tile.setyCoordinate(y);

        for (final Tile mapTile : tiles) {
            int[] ar = null;
            Orientation orientation;
            if (mapTile != null && tile.areNeighbours(mapTile)) {
                ar = new int[2];
                ar[0] = mapTile.getxCoordinate() - tile.getxCoordinate();
                ar[1] = mapTile.getyCoordinate() - tile.getyCoordinate();

                orientation = Orientation.getOrientationOfArray(ar);

                tile.replaceEdge(orientation,
                        mapTile.getEdge(orientation.getOppositeOrientation()));
            }

        }
        if (getTileWithCoordinates(x, y) != (null)) {
            removeTile(x, y);
        } else {
            tiles.add(tile);

        }
    }

    @Override
    public String toString() {
        String s = "MapGraph content:";
        for (final Tile t : tiles) {
            s += "\n+" + t.toString();
        }
        return s;
    }

}