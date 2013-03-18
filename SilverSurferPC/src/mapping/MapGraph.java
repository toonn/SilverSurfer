package mapping;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapGraph {

    private final Map<Point, Tile> tiles = new HashMap<Point, Tile>();

    public void addContentToCurrentTile(final Point point, final TileContent content) {
        // TODO Wat als point == null?
        getTile(point).setContent(content);
    }

    /**
     * Adds a given obstruction to the edge on the given orientation of the tile on the given point
     */
    public void addObstruction(final Point point,
            final Orientation orientation, final Obstruction obst) {
        if (getTile(point) == null) {
            throw new IllegalArgumentException(
                    "currenttile is null in addObstruction");
        } else if (getTile(point).getEdge(orientation) == null) {
            // TODO ??? Volgens Tile is er een invar Tile.getEdge(direction) !=
            // null
            throw new IllegalArgumentException("edge is null in addobstruction");
        }

        getTile(point).getEdge(orientation).setObstruction(obst);
    }

    /**
     * Add a tile to the map and connect it to its neighbours
     * @param point
     */
    public void addTileXY(final Point point) {
        Tile tile = new Tile(point);
        tiles.put(point, tile);

        Set<Tile> neighbourTiles = new HashSet<Tile>();
        neighbourTiles.add(tiles.get(new Point((int) point.getX() - 1,
                (int) point.getY())));
        neighbourTiles.add(tiles.get(new Point((int) point.getX() + 1,
                (int) point.getY())));
        neighbourTiles.add(tiles.get(new Point((int) point.getX(), (int) point
                .getY() - 1)));
        neighbourTiles.add(tiles.get(new Point((int) point.getX(), (int) point
                .getY() + 1)));

        for (final Tile neighbourTile : neighbourTiles) {
            Orientation orientation = null;
            if (neighbourTile != null)
                if (tile.getPosition().getX() < neighbourTile.getPosition()
                        .getX())
                    orientation = Orientation.EAST;
                else if (tile.getPosition().getX() > neighbourTile
                        .getPosition().getX())
                    orientation = Orientation.WEST;
                else if (tile.getPosition().getY() < neighbourTile
                        .getPosition().getY())
                    orientation = Orientation.SOUTH;
                else if (tile.getPosition().getY() > neighbourTile
                        .getPosition().getY())
                    orientation = Orientation.NORTH;
            if (orientation != null) {
                tile.replaceEdge(orientation, neighbourTile.getEdge(orientation
                        .getOppositeOrientation()));
            }
        }
    }

    public Point getMapSize() {
        int[] minMax = new int[4];
        Tile tile = tiles.values().iterator().next();
        minMax[0] = tile.getPosition().x;
        minMax[2] = tile.getPosition().x;
        minMax[1] = tile.getPosition().y;
        minMax[3] = tile.getPosition().y;

        for (Tile tilee : tiles.values()) {
            int x = tilee.getPosition().x;
            int y = tilee.getPosition().y;
            if (x < minMax[0]) {
                minMax[0] = x;
            } else if (x > minMax[2]) {
                minMax[2] = x;
            }
            if (y < minMax[1]) {
                minMax[1] = y;
            } else if (y > minMax[3]) {
                minMax[3] = y;
            }
        }

        return new Point(Math.abs(minMax[0] - minMax[2]), Math.abs(minMax[1]
                - minMax[3]));
    }

    /**
     * Returns the obstruction on the given orientation of the current tile.
     * ofwel wall ofwel null
     */
    public Obstruction getObstruction(final Point point,
            final Orientation orientation) {
        return getTile(point).getEdge(orientation).getObstruction();
    }

    public Tile getTile(final Point point) {
        return tiles.get(point);
    }

    public Collection<Tile> getTiles() {
        return tiles.values();
    }

    // @SuppressWarnings("unused")
    // private void removeTile(final Point point) {
    // getTile(point).terminate();
    // tiles.remove(point);
    // }

    @Override
    public String toString() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Point point : tiles.keySet()) {
            final int x = (int) point.getX();
            final int y = (int) point.getY();
            if (x < minX) {
                minX = x;
            } else if (x > maxX) {
                maxX = x;
            }
            if (y < minY) {
                minY = y;
            } else if (y > maxY) {
                maxY = y;
            }
        }

        List<List<String>> columnListList = new ArrayList<List<String>>();
        for (int x = minX; x <= maxX; x++) {
            List<String> columnStringList = new ArrayList<String>();
            for (int y = minY; y <= maxY; y++) {
                Tile tile = getTile(new Point(x, y));
                List<String> tileString = new ArrayList<String>();

                if (tile == null) {
                    tileString.add("0000");
                    tileString.add("0000");
                    tileString.add("0000");
                } else {
                    for (String s : tile.toString().split("\n"))
                        tileString.add(s);
                }
                if (y != minY)
                    tileString.remove(0);

                for (String s : tileString) {
                    if (x != minX)
                        s = s.substring(1, s.length());
                    columnStringList.add(s);
                }
            }
            columnListList.add(columnStringList);
        }

        String mapGraphString = "";
        for (int row = 0; row < columnListList.get(0).size(); row++) {
            for (List<String> column : columnListList) {
                mapGraphString += column.get(row);
            }
            mapGraphString += "\n";
        }

        return mapGraphString;
    }
}