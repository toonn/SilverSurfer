package mapping;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapGraph {

    private final Map<Point, Tile> tiles = new HashMap<Point, Tile>();
    private boolean mapsMerged = false;
    private float cosAToConvert;
    private float sinAToConvert;
    private Point point1NeededToConvert;
    private Point point2NeededToConvert;
    
    public Collection<Tile> getTiles() {
        return tiles.values();
    }

    public Tile getTile(final Point point) {
        return tiles.get(point);
    }

    public void addTile(final Point point) {
        Tile tile = new Tile(point);
        tiles.put(point, tile);

        Set<Tile> neighbourTiles = new HashSet<Tile>();
        neighbourTiles.add(tiles.get(new Point((int) point.getX() - 1, (int) point.getY())));
        neighbourTiles.add(tiles.get(new Point((int) point.getX() + 1, (int) point.getY())));
        neighbourTiles.add(tiles.get(new Point((int) point.getX(), (int) point.getY() - 1)));
        neighbourTiles.add(tiles.get(new Point((int) point.getX(), (int) point.getY() + 1)));

        for (final Tile neighbourTile : neighbourTiles) {
            Orientation orientation = null;
            if (neighbourTile != null) {
                if (tile.getPosition().getX() < neighbourTile.getPosition().getX())
                    orientation = Orientation.EAST;
                else if (tile.getPosition().getX() > neighbourTile.getPosition().getX())
                    orientation = Orientation.WEST;
                else if (tile.getPosition().getY() < neighbourTile.getPosition().getY())
                    orientation = Orientation.SOUTH;
                else if (tile.getPosition().getY() > neighbourTile.getPosition().getY())
                    orientation = Orientation.NORTH;
            }
            if (orientation != null)
                tile.replaceEdge(orientation, neighbourTile.getEdgeAt(orientation.getOppositeOrientation()));
        }
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
        return new Point(Math.abs(minMax[0] - minMax[2]), Math.abs(minMax[1] - minMax[3]));
    }

    public Point[] getMapExpanse() {
        Point minPoint = getTiles().iterator().next().getPosition();
        Point maxPoint = getTiles().iterator().next().getPosition();
        try {
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
            Point[] expanse = {minPoint, maxPoint};
            return expanse;
        } catch(ConcurrentModificationException e) {
        	return getMapExpanse();
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
                }
                else
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
    

	public void mergeMap(List<peno.htttp.Tile> map2, Point map1tile1,
			Point map1tile2, Point map2tile1, Point map2tile2, Orientation ori) {

		int translatedxmap1 = (int) (map1tile2.getX() - map1tile1.getX());
		int translatedymap1 = (int) (map1tile2.getY() - map1tile1.getY());
		int translatedxmap2 = (int) (map2tile2.getX() - map2tile1.getX());
		int translatedymap2 = (int) (map2tile2.getY() - map2tile1.getY());
		
		if (translatedxmap2 == 0) {
			sinAToConvert = -translatedxmap1 / translatedymap2;
			cosAToConvert = translatedymap1 / translatedymap2;
		} else if (translatedymap2 == 0) {
			cosAToConvert = translatedxmap1 / translatedxmap2;
			sinAToConvert = translatedymap1 / translatedxmap2;
		} else {
			sinAToConvert = (float) ((translatedymap1 - translatedxmap1
					/ translatedxmap2 * translatedymap2) / ((Math.pow(
					translatedymap2, 2)) / translatedxmap2 + translatedxmap2));
			cosAToConvert = (float) ((translatedymap1 - translatedxmap2 * sinAToConvert) / translatedymap2);
		}
		
		point1NeededToConvert = map2tile1;
		point2NeededToConvert = map1tile1;
		
		for (peno.htttp.Tile tile : map2) {
			int convertedX = (int) (cosAToConvert * tile.getX() - sinAToConvert * tile.getY()
					- cosAToConvert * map2tile1.getX() + sinAToConvert * map2tile1.getY() + map1tile1
					.getX());
			int convertedY = (int) (cosAToConvert * tile.getY() + sinAToConvert * tile.getX()
					- sinAToConvert * map2tile1.getX() - cosAToConvert * map2tile1.getY() + map1tile1
					.getY());
			
			String[] info = tile.getToken().split("\\.");
			Tile madeTileFromString = new Tile(
					new Point(convertedX, convertedY));
			MapReader.generateStructures(info, madeTileFromString);

			if (!this.tiles.containsKey(new Point(convertedX, convertedY))) {
				Tile copiedTile = new Tile(new Point(convertedX, convertedY));
				for (Orientation orientation : Orientation.values()) {
					copiedTile.getEdgeAt(orientation).setObstruction(
							madeTileFromString.getEdgeAt(
									orientation.orientationRotatedOver(ori))
									.getObstruction());
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
										madeTileFromString
												.getEdgeAt(
														orientation
																.orientationRotatedOver(ori))
												.getObstruction());
		}
		mapsMerged = true;
		
		for(Tile t: getTiles()) {
			for(Orientation or: Orientation.values()){
				if(t.getEdgeAt(or).getObstruction() == Obstruction.WHITE_LINE && (t.getEdgeAt(or).getTile1() == null || t.getEdgeAt(or).getTile2() == null)){
					//System.out.println("in mergeMap bij tile : " + t.getPosition().getX() + " " + t.getPosition().getY() + "in orientation : " + or);
					Point p = new Point(t.getPosition());
					if(or.equals(Orientation.EAST)){
						p.x = (int) (p.getX() + 1); 
					}
					if(or.equals(Orientation.WEST)){
						p.x = (int) (p.getX() - 1); 
					}
					if(or.equals(Orientation.NORTH)){
						p.y = (int) (p.getY() + 1); 
					}
					if(or.equals(Orientation.SOUTH)){
						p.y = (int) (p.getY() - 1); 
					}
					Tile buurTile = getTile(p);
					//System.out.println(buurTile.getNeighbour(or.getOppositeOrientation()));
				}
			}
		}
	}
	
	public boolean mapsAreMerged() {
		return mapsMerged;
	}
	
	public Point convertPoint(Point2D point){
		if(mapsMerged){
			int convertedX = (int) (cosAToConvert * point.getX() - sinAToConvert * point.getY()
					- cosAToConvert * point1NeededToConvert.getX() + sinAToConvert * point1NeededToConvert.getY() + point2NeededToConvert
					.getX());
			int convertedY = (int) (cosAToConvert * point.getY() + sinAToConvert * point.getX()
					- sinAToConvert * point1NeededToConvert.getX() - cosAToConvert * point1NeededToConvert.getY() + point2NeededToConvert
					.getY());		
			
			return new Point(convertedX, convertedY);
		}
		return null;
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
				if (neighbourTile.getEdgeAt(orientation.getOppositeOrientation()).getObstruction() != null)
					tile.replaceEdge(orientation, neighbourTile
							.getEdgeAt(orientation.getOppositeOrientation()));
				else
					neighbourTile.replaceEdge(
							orientation.getOppositeOrientation(),
							tile.getEdgeAt(orientation));
			}
		}
	}
}