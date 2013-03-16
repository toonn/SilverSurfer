package mazeAlgorithm;

import java.util.ArrayList;
import java.util.Vector;

import simulator.pilot.AbstractPilot;

import mapping.ExtMath;
import mapping.Orientation;
import mapping.Tile;

public class MazeExplorer {

    /**
     * Hierin komt elke tile die bezocht is, op het einde dus alle tiles van het
     * doolhof
     */
    private final Vector<Tile> allTiles = new Vector<Tile>();
    
    /**
     * hierin worden de tiles in de wachtrij gezet
     */
    private final Vector<Tile> queue = new Vector<Tile>();
    private Tile startTile = null;

    private AbstractPilot pilot;
    
    private boolean align;
    private final int amountOfTilesUntilAlign = 5;
    private int currentAmount;
    private boolean quit = false;

    public MazeExplorer(final Tile startTile, final AbstractPilot pilot, boolean align) {
        this.startTile = startTile;
        this.pilot = pilot;
        this.align = align;
        this.currentAmount = amountOfTilesUntilAlign;
    }

    /**
     * Deze methode wordt opgeroepen als het object het algoritme moet uitvoeren
     */
    public void startExploringMaze() {
    	try {
            // TODO aligning
            // communicator.setTilesBeforeAllign(3);
            // communicator.mustAllign(mustAllign);
            allTiles.add(startTile);
            algorithm(startTile);
            for (final Object tile : allTiles)
                ((Tile) tile).setMarkingExploreMaze(false);
            // communicator.mustAllign(false);    		
    	} catch(NullPointerException e) {
    		System.out.println("Algoritm incorrectly closed.");
    	}
    }

    private void algorithm(final Tile currentTile) {
    	// Explore tile and set current tile on "Explored".
        exploreTile(currentTile);
        
        if(quit) {
    		System.out.println("Algoritm correctly closed.");
        	return;
        }

        // Update queue.
        for (final Object neighbourTile : currentTile.getReachableNeighbours())
            if (neighbourTile != null && !(((Tile) neighbourTile).isMarkedExploreMaze()))
                queue.add((Tile) neighbourTile);

        // Algorithm finished?
        if (queue.isEmpty())
            return;

        // Get next optimal tile.
        Tile nextTile = getPriorityNextTile(currentTile);

        // Is the next tile useful?
        while (!doesHaveOtherNeighboursToCheck(nextTile)) {
            nextTile.setMarkingExploreMaze(true);
            allTiles.add(nextTile);
            removeTileFromQueue(nextTile);

            if (queue.isEmpty())
                return;

            nextTile = getPriorityNextTile(currentTile);
        }
        
        // Add the current tile to the finish-queue and remove it from the todo-queue.
        allTiles.add(nextTile);
        removeTileFromQueue(nextTile);
        
        // Go to the next tile.
        goToNextTile(currentTile, nextTile);

        // Wait until the barcode is executed. This is the barcode on the nextTile you just went to.
        // Reading a barcode means that you are standing on a straight, these walls are automatically added.
        while(pilot.isExecutingBarcode()) {
        	try {
        		Thread.sleep(100);
        	} catch(Exception e) {

        	}
        }
		checkExploredQueue();
        
        if(quit) {
    		System.out.println("Algoritm correctly closed.");
        	return;
        }

        // Repeat with next tile.
        algorithm(nextTile);
    }
    
    /**
     * Checks whether the queue only contains unexplored tiles.
     * For, it is possible that a pilot explores some tiles by itself.
     * For example, when he finds a barcode that says that there is a treasure - and a dead-end - on the next tile.
     */
    private void checkExploredQueue() {
    	Tile toDelete = null;
    	for(Tile tile: queue)
    		if(tile.isMarkedExploreMaze())
    			toDelete = tile;
    	if(toDelete != null) {
    		allTiles.add(toDelete);
    		removeTileFromQueue(toDelete);
    	}
    }
   

    /**
     * Checkt voor alle neighbours of ze al behandeld zijn.
     * Indien niet, checkt of er een muur tussen staat.
     * Indien wel, plaats een muur.
     * Indien niet, voeg een nieuwe lege tile toe op de juiste plaats.
     */
    private void exploreTile(final Tile currentTile) {
        // numbervariable met als inhoud het getal van de orientatie (N = 1,...)
    	int numberVariable = pilot.getOrientation().getNumberArray();
        
        // Array met alle buren van deze tile
        final ArrayList<Tile> array = currentTile.getAllNeighbours();
        
        for (int i = 0; i < 4; i++) {
            // Do nothing if tile with numbervariable as orientation is already done
            if (array.get(numberVariable) != null && (array.get(numberVariable).isMarkedExploreMaze()))
                ;
            else {
                // TODO +360 zinloos? Zodat angle altijd >= 0 is?
                double angle = (((numberVariable - pilot.getOrientation() .getNumberArray()) * 90) + 360) % 360;
                angle = ExtMath.getSmallestAngle(angle);
                pilot.rotate(angle);
                pilot.setObstructionOrTile();
            }
            // Next orientation
            numberVariable = numberVariable + 1;
            if (numberVariable == 4)
                numberVariable = 0;
        }

        // zet het mark-veld van de currentTile op true zodat deze niet meer
        // opnieuw in de queue terecht kan komen
        currentTile.setMarkingExploreMaze(true);
    }

    /**
     * checkt of alle neighbourTiles al gecheckt zijn, indien ja niet meer nodig
     * om nextTile nog te checken.
     */
    private boolean doesHaveOtherNeighboursToCheck(final Tile nextTile) {
        int j = 0;
        if (!nextTile.isStraightTile())
            for (final Object neighbourTile : nextTile.getAllNeighbours())
                if (neighbourTile != null && ((Tile) neighbourTile).isMarkedExploreMaze())
                    j++;
        if (j == 4)
            return false;
        return true;
    }

    private Tile getPriorityNextTile(final Tile currentTile) {
        if (isGoodNextTile(currentTile, pilot.getOrientation())) {
            return currentTile.getEdge(pilot.getOrientation()).getNeighbour(
                    currentTile);
        } else if (isGoodNextTile(currentTile, pilot.getOrientation()
                .getOtherOrientationCorner())) {
            return currentTile.getEdge(
                    pilot.getOrientation().getOtherOrientationCorner())
                    .getNeighbour(currentTile);
        } else if (isGoodNextTile(currentTile, pilot.getOrientation()
                .getOtherOrientationCorner().getOppositeOrientation())) {
            return currentTile.getEdge(
                    pilot.getOrientation().getOtherOrientationCorner()
                            .getOppositeOrientation())
                    .getNeighbour(currentTile);
        } else if (isGoodNextTile(currentTile, pilot.getOrientation()
                .getOppositeOrientation())) {
            return currentTile.getEdge(
                    pilot.getOrientation().getOppositeOrientation())
                    .getNeighbour(currentTile);
        }
        else
            return queue.lastElement();
    }

    private void goToNextTile(final Tile currentTile, final Tile nextTile) {
        // voert een shortestPath uit om van currentTile naar nextTile te gaan.
        final ShortestPath shortestPath = new ShortestPath(pilot, currentTile, nextTile, allTiles);
        currentAmount = shortestPath.goShortestPath(align, currentAmount, amountOfTilesUntilAlign);
    }

    private boolean isGoodNextTile(final Tile currentTile,final Orientation orientation) {
        return currentTile.getEdge(orientation).isPassable()
                && currentTile.getEdge(orientation).getNeighbour(currentTile) != null
                && queue.contains(currentTile.getEdge(orientation)
                        .getNeighbour(currentTile));
    }

    private void removeTileFromQueue(final Tile tile) {
    	// Multiple times in queue so multiple times remove.
        while (queue.contains(tile))
            queue.remove(tile);
    }

    // Gebruikt door nieuw algoritme (bijhouden voor later)
    @SuppressWarnings("unused")
    private void updateQueue(Tile currentTile) {
        ArrayList<Tile> array = currentTile.getAllNeighbours();
        int numberVariable = pilot.getOrientation().getNumberArray();
        for (int i = 0; i < 4; i++) {
            if (!allTiles.contains(array.get(numberVariable))) {
                double angle = ExtMath
                        .getSmallestAngle((((numberVariable - pilot
                                .getOrientation().getNumberArray()) * 90) + 360) % 360);
                pilot.rotate(angle);
                // // TODO: robot checkforobstruction
                // if (communicator.getPilot().checkForObstruction())
                // communicator.getPilot().addWall();
                // else {
                // int xCoordinate = 0;
                // int yCoordinate = 0;
                // // TODO: find coordinates
                // if (communicator.getPilot().getMapGraphConstructed()
                // .getTileWithCoordinates(xCoordinate, yCoordinate) == null) {
                // communicator.getPilot().setTile(xCoordinate,
                // yCoordinate);
                // }
                // queue.add(array.get(numberVariable));
                // }
            }
            numberVariable = numberVariable + 1;
            if (numberVariable == 4) {
                numberVariable = 0;
            }
        }
    }
    
    public void setQuit(boolean quit) {
    	this.quit = quit;
    }
}