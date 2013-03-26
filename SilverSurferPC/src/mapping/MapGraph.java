package mapping;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class MapGraph {

    private final Map<Point, Tile> tiles = new HashMap<Point, Tile>();

    public Collection<Tile> getTiles() {
        return tiles.values();
    }

    public Tile getTile(final Point point) {
        return tiles.get(point);
    }

    public ArrayList<Tile> getStartTiles() {
        ArrayList<Tile> startTiles = new ArrayList<Tile>();
        for (Tile tile : getTiles())
            if (tile.getContent() instanceof StartBase)
                startTiles.add(tile);
        return startTiles;
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
            if (x < minMax[0])
                minMax[0] = x;
            else if (x > minMax[2])
                minMax[2] = x;
            if (y < minMax[1])
                minMax[1] = y;
            else if (y > minMax[3])
                minMax[3] = y;
        }
        return new Point(Math.abs(minMax[0] - minMax[2]), Math.abs(minMax[1]
                - minMax[3]));
    }

    public Point[] getMapExpanse() {
        Point minPoint = getTiles().iterator().next().getPosition();
        Point maxPoint = getTiles().iterator().next().getPosition();

        for (Tile tile : getTiles()) {
            Point tilePosition = tile.getPosition();
            if (tilePosition.x < minPoint.x)
                minPoint = new Point(tilePosition.x, minPoint.y);
            else if (tilePosition.x > maxPoint.x)
                maxPoint = new Point(tilePosition.x, maxPoint.y);
            if (tilePosition.y < minPoint.y)
                minPoint = new Point(minPoint.x, tilePosition.y);
            else if (tilePosition.y > maxPoint.y)
                maxPoint = new Point(maxPoint.x, tilePosition.y);
        }

        Point[] expanse = { minPoint, maxPoint };

        return expanse;
    }

    public void addTile(final Point point) {
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
            if (orientation != null)
                tile.replaceEdge(orientation, neighbourTile
                        .getEdgeAt(orientation.getOppositeOrientation()));
        }
    }

    private void mergeMap(Vector<Tile> map2, Point map1tile1, Point map1tile2,
            Point map2tile1, Point map2tile2) {
        int translatedxmap1 = (int) (map1tile2.getX() - map1tile1.getX());
        int translatedymap1 = (int) (map1tile2.getY() - map1tile1.getY());
        int translatedxmap2 = (int) (map2tile2.getX() - map2tile1.getX());
        int translatedymap2 = (int) (map2tile2.getY() - map2tile1.getY());
        float sinA;
        float cosA;
        if(translatedxmap2 == 0){
        	sinA = - translatedxmap1/translatedymap2;
        	cosA = translatedymap1/translatedymap2;
        }
        else if(translatedymap2 == 0){
        	cosA = translatedxmap1/translatedxmap2;
        	sinA = translatedymap1/translatedxmap2;
        }
        else{
        sinA = (float) ((translatedymap1 - translatedxmap1
                / translatedxmap2 * translatedymap2) / ((Math.pow(
                translatedymap2, 2)) / translatedxmap2 + translatedxmap2));
        cosA = (float) ((translatedymap1 - translatedxmap2 * sinA) / translatedymap2);
        }
        for (Tile tile : map2) {
            int convertedX = (int) (cosA * tile.getPosition().getX() - sinA
                    * tile.getPosition().getY() - cosA * map2tile1.getX()
                    + sinA * map2tile1.getY() + map1tile1.getX());
            int convertedY = (int) (cosA * tile.getPosition().getY() + sinA
                    * tile.getPosition().getX() - sinA * map2tile1.getX()
                    - cosA * map2tile1.getY() + map1tile1.getY());

            if (!this.tiles.containsKey(new Point(convertedX, convertedY))) {
                Tile copiedTile = tile.clone();
                for(Orientation orientation : Orientation.values()){
                	copiedTile.getEdgeAt(orientation.orientationRotatedOver(sinA, cosA)).setObstruction(tile.getEdgeAt(orientation).getObstruction());
                }
                copiedTile.getPosition().x = convertedX;
                copiedTile.getPosition().y = convertedY;
                setExistingTile(copiedTile);
            } else
                for (Orientation orientation : Orientation.values())
                    if (this.getTile(new Point(convertedX, convertedY))
                            .getEdgeAt(orientation).getObstruction() == null)
                        this.getTile(new Point(convertedX, convertedY))
                                .getEdgeAt(orientation)
                                .setObstruction(
                                        tile.getEdgeAt(orientation.orientationRotatedOver(sinA, cosA))
                                                .getObstruction());
        }
    }

    private void setExistingTile(Tile tile) {
        Point point = tile.getPosition();
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
                if (neighbourTile.getEdgeAt(orientation).getObstruction() != null)
                    tile.replaceEdge(orientation, neighbourTile
                            .getEdgeAt(orientation.getOppositeOrientation()));
                else
                    neighbourTile.replaceEdge(
                            orientation.getOppositeOrientation(),
                            neighbourTile.getEdgeAt(orientation));
            }
        }
    }

    @Override
    public String toString() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Point point : tiles.keySet()) {
            final int x = (int) point.getX();
            final int y = (int) point.getY();
            if (x < minX)
                minX = x;
            else if (x > maxX)
                maxX = x;
            if (y < minY)
                minY = y;
            else if (y > maxY)
                maxY = y;
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
                } else
                    for (String s : tile.toString().split("\n"))
                        tileString.add(s);
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
            for (List<String> column : columnListList)
                mapGraphString += column.get(row);
            mapGraphString += "\n";
        }
        return mapGraphString;
    }
    
    public static void main(String[] args) {
		MapGraph map = new MapGraph();
		for(int i = 0; i < 4; i++){
			for(int j=0; j<4; j++){
				map.addTile(new Point(i,j));
			}
		}
		map.getTile(new Point(0,0)).getEdgeAt(Orientation.NORTH).setObstruction(Obstruction.WALL);
		map.getTile(new Point(0,0)).getEdgeAt(Orientation.WEST).setObstruction(Obstruction.WALL);
		map.getTile(new Point(1,0)).getEdgeAt(Orientation.NORTH).setObstruction(Obstruction.WALL);
		map.getTile(new Point(2,0)).getEdgeAt(Orientation.NORTH).setObstruction(Obstruction.WALL);
		map.getTile(new Point(2,0)).getEdgeAt(Orientation.EAST).setObstruction(Obstruction.WALL);
		map.getTile(new Point(0,1)).getEdgeAt(Orientation.WEST).setObstruction(Obstruction.WALL);
		map.getTile(new Point(0,2)).getEdgeAt(Orientation.WEST).setObstruction(Obstruction.WALL);
		map.getTile(new Point(0,2)).getEdgeAt(Orientation.SOUTH).setObstruction(Obstruction.WALL);
		map.getTile(new Point(0,3)).getEdgeAt(Orientation.WEST).setObstruction(Obstruction.WALL);
		
		for(int i = 0; i < 4; i++){
			for(int j=0; j<4; j++){
				System.out.println(i + " en " + j);
				System.out.println(map.getTile(new Point(i,j)).getEdgeAt(Orientation.NORTH).getObstruction());
				System.out.println(map.getTile(new Point(i,j)).getEdgeAt(Orientation.EAST).getObstruction());
				System.out.println(map.getTile(new Point(i,j)).getEdgeAt(Orientation.SOUTH).getObstruction());
				System.out.println(map.getTile(new Point(i,j)).getEdgeAt(Orientation.WEST).getObstruction());
				System.out.println("  ");

			}
		}
		Vector<Tile> tiles = new Vector<Tile>();
		Tile tile1 = new Tile(new Point(0,0));
		Tile tile2 = new Tile(new Point(1,0));
		Tile tile3 = new Tile(new Point(0,1));
		Tile tile4 = new Tile(new Point(0,-1));
		Tile tile5 = new Tile(new Point(0,-2));
		tile1.getEdgeAt(Orientation.NORTH).setObstruction(Obstruction.WHITE_LINE);
		tile1.getEdgeAt(Orientation.WEST).setObstruction(Obstruction.WALL);
		tile1.getEdgeAt(Orientation.EAST).setObstruction(Obstruction.WHITE_LINE);
		tile1.getEdgeAt(Orientation.SOUTH).setObstruction(Obstruction.WHITE_LINE);
		tile2.getEdgeAt(Orientation.NORTH).setObstruction(Obstruction.WALL);
		tile2.getEdgeAt(Orientation.EAST).setObstruction(Obstruction.WALL);
		tile2.getEdgeAt(Orientation.WEST).setObstruction(Obstruction.WHITE_LINE);
		tile2.getEdgeAt(Orientation.SOUTH).setObstruction(Obstruction.WALL);
		tile3.getEdgeAt(Orientation.NORTH).setObstruction(Obstruction.WHITE_LINE);
		tile3.getEdgeAt(Orientation.WEST).setObstruction(Obstruction.WALL);
		tile3.getEdgeAt(Orientation.EAST).setObstruction(Obstruction.WALL);
		tile3.getEdgeAt(Orientation.SOUTH).setObstruction(Obstruction.WALL);
		tile5.getEdgeAt(Orientation.NORTH).setObstruction(Obstruction.WALL);
		tile5.getEdgeAt(Orientation.WEST).setObstruction(Obstruction.WALL);
		tile5.getEdgeAt(Orientation.EAST).setObstruction(Obstruction.WALL);
		tile5.getEdgeAt(Orientation.SOUTH).setObstruction(Obstruction.WHITE_LINE);
		tile4.getEdgeAt(Orientation.NORTH).setObstruction(Obstruction.WHITE_LINE);
		tile4.getEdgeAt(Orientation.WEST).setObstruction(Obstruction.WALL);
		tile4.getEdgeAt(Orientation.EAST).setObstruction(Obstruction.WALL);
		tile4.getEdgeAt(Orientation.SOUTH).setObstruction(Obstruction.WHITE_LINE);
		tiles.add(tile3);
		tiles.add(tile1);
		tiles.add(tile2);
		tiles.add(tile4);
		tiles.add(tile5);
		
		map.mergeMap(tiles, new Point(1,3), new Point(2,3), new Point(1,0), new Point(0,0));

		System.out.println("=================================================");
		
		for(Tile tile:map.getTiles()){
				System.out.println(tile.getPosition().getX() + " en " + tile.getPosition().getY());
				System.out.println(tile.getEdgeAt(Orientation.NORTH).getObstruction());
				System.out.println(tile.getEdgeAt(Orientation.EAST).getObstruction());
				System.out.println(tile.getEdgeAt(Orientation.SOUTH).getObstruction());
				System.out.println(tile.getEdgeAt(Orientation.WEST).getObstruction());
				System.out.println("  ");
		}

		
    }
}