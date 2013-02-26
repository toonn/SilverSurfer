package mapping;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class MapGraph {

    private final Map<Point, Tile> tiles = new HashMap<Point, Tile>();

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
     * Returns the obstruction on the given orientation of the current tile.
     * ofwel wall ofwel null
     */
    public Obstruction getObstruction(final Orientation orientation) {
        return getCurrentTile().getEdge(orientation).getObstruction();
    }

    public Tile getTile(final Point point) {
        if (tiles.containsKey(point)) {
            return tiles.get(point);
        }
        return null;
    }

    private void removeTile(final int x, final int y) {
        getTile(x, y).terminate();
        tiles.remove(getTile(x, y));
    }

    public void addTileXY(final int x, final int y, final Tile tile) {
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
        if (getTile(x, y) != (null)) {
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