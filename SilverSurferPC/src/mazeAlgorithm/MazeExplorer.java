package mazeAlgorithm;

import java.util.Collections;
import java.util.Vector;

import commands.Sleep;
import mapping.Barcode;
import mapping.Obstruction;
import mapping.Orientation;
import mapping.Seesaw;
import mapping.Tile;
import simulator.pilot.AbstractPilot;

public class MazeExplorer {

    private final Vector<Tile> allTiles = new Vector<Tile>();
    private final Vector<Tile> queue = new Vector<Tile>();
    private Tile startTile;
    private AbstractPilot pilot;
    private boolean align;
    private final int amountOfTilesUntilAlign = 0;
    private int currentAmount;
    private boolean quit = false;
    private boolean lastTurnRight = false;

    public MazeExplorer(final Tile startTile, final AbstractPilot pilot, boolean align) {
        this.startTile = startTile;
        this.pilot = pilot;
        this.align = align;
        currentAmount = amountOfTilesUntilAlign;
    }

    public void quit() {
        quit = true;
    }
    
    public double getSmallestAngle(double angle) {
        if (angle < -180)
            angle = angle + 360;
        else if (angle > 180)
            angle = angle - 360;
        else if (lastTurnRight && angle == 180)
            angle = -angle;
        else if (!lastTurnRight && angle == -180)
            angle = -angle;
        
        if (angle >= 0)
            lastTurnRight = true;
        else
            lastTurnRight = false;
        
        return angle;
    }

    public void startExploringMaze() {
        try {
            algorithm(startTile);
            for (final Object tile : allTiles)
                ((Tile)tile).setMarkingExploreMaze(false);
        } catch (Exception e) {
            if(!quit)
            	e.printStackTrace();
        }
    }
    
    private void algorithm(Tile currentTile) {
        updateVectors(currentTile);
        if (!currentTile.isMarkedExploreMaze())
        	exploreTileAndUpdateQueue(currentTile);
        if (queue.isEmpty() || quit) {
            System.out.println("[EXPLORE] Robot " + pilot.getPlayerNumber() + " has finished exploring.");
            return;
        }
        Tile nextTile = getPriorityNextTile(currentTile);
        if(nextTile == null) { //Null if next tile is not reachable without taking a seesaw.
        	Tile seesawTile = searchOpenSeesaw(currentTile);
        	nextTile = crossSeesaw(seesawTile);
        } else {
            updateVectors(nextTile);
        	if(!isUseful(nextTile)) {
                nextTile.setMarkingExploreMaze(true);
                nextTile = currentTile;
        	} else {
                final ShortestPath shortestPath = new ShortestPath(this, pilot, currentTile, nextTile, allTiles);
                currentAmount = shortestPath.goShortestPath(align, currentAmount, amountOfTilesUntilAlign);
                if (nextTile.getContent() instanceof Barcode)
                    while (pilot.isExecutingBarcode())
                        new Sleep().sleepFor(100);
        	}
        }
        algorithm(nextTile);
    }
    
    private void updateVectors(Tile currentTile) {
    	if(!allTiles.contains(currentTile))
    		allTiles.add(currentTile);
       	queue.remove(currentTile);
        for (Tile tile : pilot.getMapGraphConstructed().getTiles()) 
            if (!allTiles.contains(tile) && !queue.contains(tile)) {
                if (tile.isMarkedExploreMaze())
                    allTiles.add(tile);
                else 
                    queue.add(tile);
            }
    }
    
    private void exploreTileAndUpdateQueue(Tile currentTile) {
    	Orientation orientation = pilot.getOrientation();
        Orientation[] orientationArray = {orientation, orientation.getClockwiseOrientation(), orientation.getOppositeOrientation(), orientation.getCounterClockwiseOrientation()};
        for(Orientation orientationValue : orientationArray) 
        	if(currentTile.getNeighbour(orientationValue) == null || !currentTile.getNeighbour(orientationValue).isMarkedExploreMaze()) {
                pilot.rotate(getSmallestAngle(((orientationValue.ordinal() - pilot.getOrientation().ordinal())*90 + 360)%360));
                pilot.setObstructionOrTile();
        	}
        currentTile.setMarkingExploreMaze(true);
        for (final Tile neighbourTile : currentTile.getReachableNeighbours())
            if (neighbourTile != null && !neighbourTile.isMarkedExploreMaze() && !queue.contains(neighbourTile))
                queue.add(neighbourTile);


    }
    
    private Tile getPriorityNextTile(final Tile currentTile) {
        if (isGoodNextTile(currentTile, pilot.getOrientation()))
            return currentTile.getEdgeAt(pilot.getOrientation()).getNeighbour(currentTile);
        else if(isGoodNextTile(currentTile, pilot.getOrientation().getCounterClockwiseOrientation()))
            return currentTile.getEdgeAt(pilot.getOrientation().getCounterClockwiseOrientation()).getNeighbour(currentTile);
        else if (isGoodNextTile(currentTile, pilot.getOrientation().getClockwiseOrientation()))
            return currentTile.getEdgeAt(pilot.getOrientation().getClockwiseOrientation()).getNeighbour(currentTile);
        else if (isGoodNextTile(currentTile, pilot.getOrientation().getOppositeOrientation()))
            return currentTile.getEdgeAt(pilot.getOrientation().getOppositeOrientation()).getNeighbour(currentTile);
        else {
            Tile loopdetect = queue.lastElement();
            Tile tile = queue.lastElement();
            while (!isReachableWithoutWip(currentTile, tile, new Vector<Tile>())) {
                queue.remove(tile);
                queue.add(0, tile);
                tile = queue.lastElement();
                if (tile.equals(loopdetect))
                    return null;
            }
            return tile;
        }
    }
    
    private boolean isGoodNextTile(final Tile currentTile, final Orientation orientation) {
        return currentTile.getEdgeAt(orientation).isPassable() && currentTile.getEdgeAt(orientation).getNeighbour(currentTile) != null && queue.contains(currentTile.getEdgeAt(orientation).getNeighbour(currentTile));
    }
    
    private Tile searchOpenSeesaw(Tile currentTile) {
        Vector<Tile> seesawBarcodeTiles = pilot.getSeesawBarcodeTiles();
        Collections.shuffle(seesawBarcodeTiles);
        for(Tile tile : seesawBarcodeTiles) {
        	if(isReachableWithoutWip(currentTile, tile, new Vector<Tile>())) {
                ShortestPath shortestPath = new ShortestPath(this, pilot, currentTile, tile, allTiles);
                currentAmount = shortestPath.goShortestPath(align, currentAmount, amountOfTilesUntilAlign);
                while (pilot.isExecutingBarcode())
                    new Sleep().sleepFor(100);
                Orientation orientation = pilot.getOrientation();
                Tile seesaw = tile.getNeighbour(orientation);
                if(!((Seesaw)seesaw.getContent()).isClosed())
                    return tile;
                else {
                	Tile otherEnd = tile.getNeighbour(orientation.getOppositeOrientation());
                	shortestPath = new ShortestPath(this, pilot, tile, otherEnd, allTiles);
                	currentAmount = shortestPath.goShortestPath(align, currentAmount, amountOfTilesUntilAlign);
                	return searchOpenSeesaw(otherEnd);
                }
        	}
        }
        return null;
    }
    
    private Tile crossSeesaw(Tile currentTile) {
    	int seesawValue = getSeesawValue(currentTile);
    	Tile endTile = getOtherEndOfSeesaw(currentTile);
        pilot.setReadBarcodes(false);
        pilot.travel(60);
        for (Tile tile : pilot.getMapGraphConstructed().getTiles())
            if (tile.getContent() instanceof Seesaw && tile.getContent().getValue() == seesawValue)
                ((Seesaw)tile.getContent()).flipSeesaw();
        pilot.travel(80);
        pilot.setReadBarcodes(true);
        pilot.travel(20);
        //TODO: whiteline! want onnauwkeurig na wip!
        return endTile;
    }

    private int getSeesawValue(Tile tile) {
    	if(tile.getEdgeAt(Orientation.EAST).getObstruction() == Obstruction.WALL && tile.getSouthNeighbour().getContent() instanceof Seesaw)
    		return tile.getSouthNeighbour().getContent().getValue();
    	else if(tile.getEdgeAt(Orientation.EAST).getObstruction() == Obstruction.WALL && tile.getNorthNeighbour().getContent() instanceof Seesaw)
    		return tile.getNorthNeighbour().getContent().getValue();
    	else if(tile.getEdgeAt(Orientation.NORTH).getObstruction() == Obstruction.WALL && tile.getEastNeighbour().getContent() instanceof Seesaw)
    		return tile.getEastNeighbour().getContent().getValue();
    	else
    		return tile.getWestNeighbour().getContent().getValue();
    }

    private Tile getOtherEndOfSeesaw(Tile tile) {
    	if(tile.getEdgeAt(Orientation.EAST).getObstruction() == Obstruction.WALL && tile.getSouthNeighbour().getContent() instanceof Seesaw)
    		return tile.getSouthNeighbour().getSouthNeighbour().getSouthNeighbour().getSouthNeighbour();
    	else if(tile.getEdgeAt(Orientation.EAST).getObstruction() == Obstruction.WALL && tile.getNorthNeighbour().getContent() instanceof Seesaw)
    		return tile.getNorthNeighbour().getNorthNeighbour().getNorthNeighbour().getNorthNeighbour();
    	else if(tile.getEdgeAt(Orientation.NORTH).getObstruction() == Obstruction.WALL && tile.getEastNeighbour().getContent() instanceof Seesaw)
    		return tile.getEastNeighbour().getEastNeighbour().getEastNeighbour().getEastNeighbour();
    	else
    		return tile.getWestNeighbour().getWestNeighbour().getWestNeighbour().getWestNeighbour();
    }
    
    private boolean isUseful(final Tile nextTile) {
        int j = 0;
        for (final Tile neighbourTile : nextTile.getNeighbours())
            if (neighbourTile != null && neighbourTile.isMarkedExploreMaze())
                j++;
        if (j == 4) 
            return false;
        return true;
    }

    private boolean isReachableWithoutWip(Tile currentTile, Tile endTile, Vector<Tile> tilesPath) {
        tilesPath.add(currentTile);
        for (final Tile neighbourTile : currentTile.getReachableNeighbours()) {
            if (neighbourTile.equals(endTile))
                return true;
            if (!tilesPath.contains(neighbourTile) && allTiles.contains(neighbourTile) && !(neighbourTile.getContent() instanceof Seesaw) && isReachableWithoutWip(neighbourTile, endTile, tilesPath))
                    return true;
        }
        return false;
    }
}

/*


private void algorithm2(Tile currentTile) {
    // Explore tile and set current tile on "Explored".
    if (!currentTile.isMarkedExploreMaze())
    	exploreTile(currentTile);

    // Update queue.
    for (final Tile neighbourTile : currentTile.getReachableNeighbours())
        if (neighbourTile != null && !neighbourTile.isMarkedExploreMaze()) {
        	while(queue.contains(neighbourTile))
        		queue.remove(neighbourTile);
            queue.add(neighbourTile);
        }

    // Algorithm finished?
    if (queue.isEmpty() || quit) {
        System.out.println("[EXPLORE] Robot " + pilot.getPlayerNumber() + " has finished exploring.");
        return;
    }

    // Get next optimal tile.
    Tile nextTile = getPriorityNextTile(currentTile);
    while (allTiles.contains(nextTile)) {
        currentTile = nextTile;
        if (queue.isEmpty() || quit) {
            System.out.println("[EXPLORE] Robot " + (pilot.getPlayerNumber()+1) + " has finished exploring.");
            return;
        }
        nextTile = getPriorityNextTile(currentTile);
    }

    // Is the next tile useful?
    while(!isUseful(nextTile)) {
        nextTile.setMarkingExploreMaze(true);
        allTiles.add(nextTile);
        removeTileFromQueue(nextTile);

        if (queue.isEmpty() || quit) {
            System.out.println("[EXPLORE] Robot " + pilot.getPlayerNumber() + " has finished exploring.");
            return;
        }

        nextTile = getPriorityNextTile(currentTile);
        while (allTiles.contains(nextTile)) {
            currentTile = nextTile;
            if (queue.isEmpty() || quit) {
                System.out.println("[EXPLORE] Robot " + pilot.getPlayerNumber() + " has finished exploring.");
                return;
            }
            nextTile = getPriorityNextTile(currentTile);
        }
    }
    
    // Add the current tile to the finish-queue and remove it from the todo-queue.
    allTiles.add(nextTile);
    removeTileFromQueue(nextTile);
    
    // Go to the next tile.
    final ShortestPath shortestPath = new ShortestPath(this, pilot, currentTile, nextTile, allTiles);
    currentAmount = shortestPath.goShortestPath(align, currentAmount, amountOfTilesUntilAlign);

    // the next tile contains a barcode.
    // this means that the robot can alter its mapping. therefore, the queue
    // has to be updated
    if (nextTile.getContent() instanceof Barcode) {
        updateVectors();

        while (pilot.isExecutingBarcode()) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {

            }
        }
    }
    // Repeat with next tile.
    algorithm(nextTile);
}

    private Tile getPriorityNextTile(final Tile currentTile) {
        if (isGoodNextTile(currentTile, pilot.getOrientation())) {
            return currentTile.getEdgeAt(pilot.getOrientation()).getNeighbour(
                    currentTile);
        } else if (isGoodNextTile(currentTile, pilot.getOrientation()
                .getCounterClockwiseOrientation())) {
            return currentTile.getEdgeAt(
                    pilot.getOrientation().getCounterClockwiseOrientation())
                    .getNeighbour(currentTile);
        } else if (isGoodNextTile(currentTile, pilot.getOrientation()
                .getCounterClockwiseOrientation().getOppositeOrientation())) {
            return currentTile.getEdgeAt(
                    pilot.getOrientation().getCounterClockwiseOrientation()
                            .getOppositeOrientation())
                    .getNeighbour(currentTile);
        } else if (isGoodNextTile(currentTile, pilot.getOrientation()
                .getOppositeOrientation())) {
            return currentTile.getEdgeAt(
                    pilot.getOrientation().getOppositeOrientation())
                    .getNeighbour(currentTile);
        } else {
            Tile loopdetect = queue.lastElement();
            Tile tile = queue.lastElement();
            while (!isReachableWithoutWip(currentTile, tile, new Vector<Tile>())) {
                queue.remove(tile);
                queue.add(0, tile);
                tile = queue.lastElement();
                if (tile.equals(loopdetect)) {
                    return searchAndCrossOpenSeesaw(currentTile);
                }
            }
            return queue.lastElement();
        }
    }



    private Tile searchAndCrossOpenSeesaw(Tile currentTile) {
        while (true) {
            pilot.shuffleSeesawBarcodeTiles();
            for (Tile tile : pilot.getSeesawBarcodeTiles()) {
                if (isReachableWithoutWip(currentTile, tile, new Vector<Tile>())) {
                    ShortestPath shortestPath = new ShortestPath(this, pilot,
                            currentTile, tile, allTiles);
                    currentAmount = shortestPath.goShortestPath(align,
                            currentAmount, amountOfTilesUntilAlign);
                    currentTile = tile;
                    updateVectors();
                    while (pilot.isExecutingBarcode()) {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {

                        }
                    }
                    for (Tile neighbour : currentTile
                            .getReachableNeighboursIgnoringSeesaw()) {
                        if (neighbour.getContent() instanceof Seesaw
                                && !((Seesaw) neighbour.getContent())
                                        .isClosed()) {
                            Tile endTile = getOtherEndOfSeesaw(currentTile);
                            pilot.setReadBarcodes(false);
                            pilot.travel(140);
                            pilot.setReadBarcodes(true);
                            if (!allTiles.contains(endTile)) {
                                allTiles.add(endTile);
                            }
                            removeTileFromQueue(endTile);
                            pilot.travel(20); // Zodat een eventuele barcode op
                                              // volgende tile wel gelezen wordt
                                              // maar de laatste barcode van de
                                              // wip niet.
                            //TODO: whiteline! want onnauwkeurig na wip!
                            if (!endTile.isMarkedExploreMaze())
                                exploreTile(endTile);
                            for (final Tile neighbourTile : endTile.getReachableNeighbours())
                                if (neighbourTile != null && !neighbourTile.isMarkedExploreMaze())
                                    queue.add(neighbourTile);
                            return endTile;
                        }
                    }
                    // Wip is gesloten, dus rij 1 tegel achteruit
                    for (Tile neighbour : currentTile
                            .getReachableNeighboursIgnoringSeesaw()) {
                        if (!(neighbour.getContent() instanceof Seesaw)) {
                            shortestPath = new ShortestPath(this, pilot, currentTile,
                                    neighbour, allTiles);
                            currentAmount = shortestPath.goShortestPath(align,
                                    currentAmount, amountOfTilesUntilAlign);
                            currentTile = neighbour;
                            updateVectors();
                        }
                    }
                }
            }
        }
    }
    
    
    private void exploreTile(final Tile currentTile) {
        // numbervariable met als inhoud het getal van de orientatie (N = 1,...)
        int numberVariable = pilot.getOrientation().ordinal();

        // Array met alle buren van deze tile
        final ArrayList<Tile> array = currentTile.getNeighbours();

        for (int i = 0; i < 4; i++) {
            if (array.get(numberVariable) == null || !array.get(numberVariable).isMarkedExploreMaze()) {
                double angle = (((numberVariable - pilot.getOrientation().ordinal()) * 90) + 360) % 360;
                pilot.rotate(getSmallestAngle(angle));
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
    
    private void updateVectors() {
        for (Tile tile : pilot.getMapGraphConstructed().getTiles()) 
            if (!allTiles.contains(tile) && !queue.contains(tile)) {
                if (tile.isMarkedExploreMaze())
                    allTiles.add(tile);
                else
                    queue.add(tile);
            }
    }

    private void removeTileFromQueue(final Tile tile) {
        // Multiple times in queue so multiple times remove.
        while (queue.contains(tile)) {
            queue.remove(tile);
        }
        // TODO: Somehow some tiles do not get removed... Investigate! AND FIX THAT LAST DAMN THING :(
        for (Tile queuetile : queue) {
            if (queuetile.getPosition().x == tile.getPosition().x && queuetile.getPosition().y == tile.getPosition().y)
                queue.remove(queuetile);
        	if(queuetile.isMarkedExploreMaze())
        		queue.remove(queuetile);
        }
    	for(Tile allTilesTile : allTiles)
            for (Tile queuetile : queue) {
            	if(allTilesTile.getPosition().x == queuetile.getPosition().x && allTilesTile.getPosition().y == queuetile.getPosition().y) {
            		queue.remove(queuetile);
            		break;
            	}
            }
    }
*/