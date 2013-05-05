package mazeAlgorithm;

import java.awt.Point;
import java.util.Collections;
import java.util.Vector;

import mapping.Barcode;
import mapping.Obstruction;
import mapping.Orientation;
import mapping.Seesaw;
import mapping.Tile;
import simulator.pilot.AbstractPilot;
import commands.Sleep;

public class MazeExplorer {

    private final Vector<Tile> allTiles = new Vector<Tile>();
    private final Vector<Tile> queue = new Vector<Tile>();
    private Tile startTile;
    private AbstractPilot pilot;
    private boolean quit = false;
    private boolean lastTurnRight = false;
    private boolean openSeesawIfClosed = false;
    private boolean shuffled = false;

    public MazeExplorer(final Tile startTile, final AbstractPilot pilot) {
        this.startTile = startTile;
        this.pilot = pilot;
    }

    private Tile algorithm(Tile currentTile) {
        updateVectors(currentTile);
        if (!currentTile.isMarkedExploreMaze())
            exploreTileAndUpdateQueue(currentTile);
        if (queue.isEmpty() || quit) {
            System.out.println("[EXPLORE] Robot " + pilot.getPlayerNumber() + " has finished exploring.");
            return null;
        }
        Tile nextTile;
        if(!shuffled)
        	nextTile = getPriorityNextTile(currentTile);
        else
        	nextTile = getPriorityNextTileWithShuffle(currentTile);
        if (nextTile == null) { // Null if next tile is not reachable without taking a seesaw.
            Tile seesawTile = searchOpenSeesaw(currentTile);
            if(seesawTile != null) {
            	currentTile = seesawTile;
                int seesawValue = getSeesawValue(currentTile);
                nextTile = getOtherEndOfSeesaw(currentTile);
                if(!((Seesaw)(currentTile.getNeighbour(pilot.getOrientation()).getContent())).isClosed()) //If seesaw is open
                    pilot.crossOpenSeesaw(seesawValue);
                else //If seesaw is closed
                    pilot.crossClosedSeesaw(seesawValue);
            }
            else //Alleen als quit
            	nextTile = currentTile;
        } else {
            updateVectors(nextTile);
            if(!isUseful(nextTile)) {
                nextTile.setMarkingExploreMaze(true);
                nextTile = currentTile;
            } else {
            	final ShortestPath shortestPath = new ShortestPath(this, pilot, currentTile, nextTile, allTiles);
                int totalTilesToGo = shortestPath.getTilesAwayFromTargetPosition();
            	
            	while(totalTilesToGo != 0)
            		if(!quit) {
            			try {
                			shortestPath.goShortestPath1Tile(false);
                			totalTilesToGo--;
            			} catch(CollisionAvoidedException e) {
            				undoUpdateVectors(nextTile);
            				nextTile = shortestPath.getCurrentTileDuringException();
            				Collections.shuffle(queue);
            				shuffled = true;
            				return nextTile;
            			}
            		}
                if (nextTile.getContent() instanceof Barcode) {
                    while (pilot.isExecutingBarcode())
                        new Sleep().sleepFor(100);
                    if(pilot.getTeamNumber() != -1) {
                        System.out.println("[EXPLORE] Robot " + pilot.getPlayerNumber() + " has found his object and will now wait on the other player on a safe location.");
                    	return null; //Algoritme stopt: object is gevonden
                    }
                }
            }
        }
        return algorithm(nextTile);
    }

    private void exploreTileAndUpdateQueue(Tile currentTile) {
        Orientation orientation = pilot.getOrientation();
        Orientation[] orientationArray = { orientation,
                orientation.getClockwiseOrientation(),
                orientation.getOppositeOrientation(),
                orientation.getCounterClockwiseOrientation() };
        for (Orientation orientationValue : orientationArray)
            if (currentTile.getNeighbour(orientationValue) == null || !currentTile.getNeighbour(orientationValue).isMarkedExploreMaze()) {
                pilot.rotate(getSmallestAngle(((orientationValue.ordinal() - pilot.getOrientation().ordinal()) * 90 + 360) % 360));
        		pilot.updateTilesAndPosition();
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
        else if (isGoodNextTile(currentTile, pilot.getOrientation()
                .getCounterClockwiseOrientation()))
            return currentTile.getEdgeAt(
                    pilot.getOrientation().getCounterClockwiseOrientation())
                    .getNeighbour(currentTile);
        else if (isGoodNextTile(currentTile, pilot.getOrientation()
                .getClockwiseOrientation()))
            return currentTile.getEdgeAt(
                    pilot.getOrientation().getClockwiseOrientation())
                    .getNeighbour(currentTile);
        else if (isGoodNextTile(currentTile, pilot.getOrientation()
                .getOppositeOrientation()))
            return currentTile.getEdgeAt(
                    pilot.getOrientation().getOppositeOrientation())
                    .getNeighbour(currentTile);
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

    private Tile getPriorityNextTileWithShuffle(final Tile currentTile) {
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
  
    private boolean isGoodNextTile(final Tile currentTile, final Orientation orientation) {
    	return currentTile.getEdgeAt(orientation).isPassable() && currentTile.getEdgeAt(orientation).getNeighbour(currentTile) != null
    			&& queue.contains(currentTile.getEdgeAt(orientation).getNeighbour(currentTile));
    }
    
    private Tile searchOpenSeesaw(Tile currentTile) {
        Vector<Tile> seesawBarcodeTiles = pilot.getSeesawBarcodeTiles();
        Collections.shuffle(seesawBarcodeTiles);
        if(!quit) {
	        for(Tile tile : seesawBarcodeTiles) {
	        	if(isReachableWithoutWip(currentTile, tile, new Vector<Tile>())) {
	                ShortestPath shortestPath = new ShortestPath(this, pilot, currentTile, tile, allTiles);
                	int totalTilesToGo = shortestPath.getTilesAwayFromTargetPosition();
                	
                	while(totalTilesToGo != 0)
                		if(!quit) {
                			try {
                    			shortestPath.goShortestPath1Tile(false);
                    			totalTilesToGo--;
                			} catch(CollisionAvoidedException e) {
                				return searchOpenSeesaw(shortestPath.getCurrentTileDuringException());
                			}
                		}
                	
	                while (pilot.isExecutingBarcode())
	                    new Sleep().sleepFor(100);
	                
	                Orientation orientation = pilot.getOrientation();
	                Tile seesaw = tile.getNeighbour(orientation);
	                if((!((Seesaw)seesaw.getContent()).isClosed() || openSeesawIfClosed))
	                	return tile;
	                else {
	                	Tile otherEnd = tile.getNeighbour(orientation.getOppositeOrientation());
	                	shortestPath = new ShortestPath(this, pilot, tile, otherEnd, allTiles);
	                	totalTilesToGo = shortestPath.getTilesAwayFromTargetPosition();
	                	
	                	while(totalTilesToGo != 0)
	                    	if(!quit) {
	                			try {
	                    			shortestPath.goShortestPath1Tile(false);
	                    			totalTilesToGo--;
	                			} catch(CollisionAvoidedException e) {
	                				searchOpenSeesawCollisionRollback();
	                				pilot.updateTilesAndPosition();
	                			}
	                    	}
	                	
	                	return searchOpenSeesaw(otherEnd);
	                }
	        	}
	        }
        }
        return null;
    }
	
	private void searchOpenSeesawCollisionRollback() {
		new Sleep().sleepFor(1000);
		try {
			pilot.alignOnWhiteLine();
        } catch(CollisionAvoidedException e) {
        	searchOpenSeesawCollisionRollback();
        }
	}

    private int getSeesawValue(Tile tile) {
        if (tile.getEdgeAt(Orientation.EAST).getObstruction() == Obstruction.WALL
                && tile.getSouthNeighbour().getContent() instanceof Seesaw)
            return tile.getSouthNeighbour().getContent().getValue();
        else if (tile.getEdgeAt(Orientation.EAST).getObstruction() == Obstruction.WALL
                && tile.getNorthNeighbour().getContent() instanceof Seesaw)
            return tile.getNorthNeighbour().getContent().getValue();
        else if (tile.getEdgeAt(Orientation.NORTH).getObstruction() == Obstruction.WALL
                && tile.getEastNeighbour().getContent() instanceof Seesaw)
            return tile.getEastNeighbour().getContent().getValue();
        else
            return tile.getWestNeighbour().getContent().getValue();
    }
    
    private Tile getOtherEndOfSeesaw(Tile tile) {
        if (tile.getEdgeAt(Orientation.EAST).getObstruction() == Obstruction.WALL
                && tile.getSouthNeighbour().getContent() instanceof Seesaw)
            return tile.getSouthNeighbour().getSouthNeighbour()
                    .getSouthNeighbour().getSouthNeighbour();
        else if (tile.getEdgeAt(Orientation.EAST).getObstruction() == Obstruction.WALL
                && tile.getNorthNeighbour().getContent() instanceof Seesaw)
            return tile.getNorthNeighbour().getNorthNeighbour()
                    .getNorthNeighbour().getNorthNeighbour();
        else if (tile.getEdgeAt(Orientation.NORTH).getObstruction() == Obstruction.WALL
                && tile.getEastNeighbour().getContent() instanceof Seesaw)
            return tile.getEastNeighbour().getEastNeighbour()
                    .getEastNeighbour().getEastNeighbour();
        else
            return tile.getWestNeighbour().getWestNeighbour()
                    .getWestNeighbour().getWestNeighbour();
    }

    protected double getSmallestAngle(double angle) {
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
            if (!tilesPath.contains(neighbourTile)
                    && allTiles.contains(neighbourTile)
                    && !(neighbourTile.getContent() instanceof Seesaw)
                    && isReachableWithoutWip(neighbourTile, endTile, tilesPath))
                return true;
        }
        return false;
    }  
    
    public void startExploringMaze() {
        try {
        	Tile returnTile = startTile;
        	while(returnTile != null) //als returnTile geen null is, is er collision, dus hernemen met returnTile
        		returnTile = algorithm(returnTile);
            if(pilot.isInGameModus()) {
                try {
                	pilot.getCenter().getPlayerClient().foundObject();
                	pilot.getCenter().getPlayerClient().joinTeam(pilot.getTeamNumber());
                } catch (Exception e) {
                    System.out.println("Exception! Cannot join team!");
                }
            }
            while(!pilot.getTeamMemberFound()) { //Zolang teammember niet gevonden is, stuur coordinaten en map door
                new Sleep().sleepFor(1000);
                pilot.updateTilesAndPosition();
            }
            for(int i = 0; i < 3; i++) { //Stuur daarna nog 3 keer door zodat de andere robot zeker verbonden is en alles kan ontvangen
                new Sleep().sleepFor(1000);
                pilot.updateTilesAndPosition();
            }
            if(!pilot.getMapGraphConstructed().mapsAreMerged()) //Verken verder wanneer er geen gelijke tiles zijn
            	;
            else //Rij naar elkaar
    			while(!pilot.hasWon())
    				goToTeammateOneStep();
            for (final Object tile : allTiles)
            	((Tile) tile).setMarkingExploreMaze(false);
        } catch (Exception e) {
            if (!quit)
                e.printStackTrace();
        }
    }
    
    private void updateVectors(Tile currentTile) {
        if (!allTiles.contains(currentTile))
            allTiles.add(currentTile);
        queue.remove(currentTile);
        for (Tile tile : pilot.getMapGraphConstructed().getTiles())
            if (!allTiles.contains(tile) && !queue.contains(tile))
                if (tile.isMarkedExploreMaze())
                    allTiles.add(tile);
                else
                    queue.add(tile);
    }
    
    private void undoUpdateVectors(Tile currentTile) {
        if (allTiles.contains(currentTile))
            allTiles.remove(currentTile);
        queue.add(currentTile);
    }
    
    public void quit() {
        quit = true;
    }

	private void goToTeammateOneStep() {
		Point endTilePoint = new Point((int)(pilot.getTeamPilot().getPosition().x/40), (int)(pilot.getTeamPilot().getPosition().y/40));
		Tile endTile = pilot.getMapGraphConstructed().getTile(endTilePoint);

		ShortestPath shortestPath = new ShortestPath(this, pilot, pilot.getMapGraphConstructed().getTile(pilot.getMatrixPosition()), endTile, pilot.getAllTileVector());
		if (shortestPath.getTilesAwayFromTargetPosition() <= 1) {
			System.out.println("VICTORY! YOU WON THE GAME!"); //TODO: vuurwerk en een fanfare
			pilot.setWon();
			return;
		}
		try {
			shortestPath.goShortestPath1Tile(true);
		} catch(CollisionAvoidedException e) {
			new Sleep().sleepFor(1000); //wacht 1 second
		}
	}
}