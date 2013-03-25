/**
 * een object van deze klasse kan maar 1x gebruikt worden!
 * velden worden ingevuld bij aanmaak object en kunnen niet meer veranderen.
 */

package mazeAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import simulator.pilot.AbstractPilot;

import mapping.ExtMath;
import mapping.Orientation;
import mapping.Seesaw;
import mapping.Tile;

public class ShortestPath {

    private final ArrayList<Tile> queue = new ArrayList<Tile>();
    private final Vector<Tile> tilesPath = new Vector<Tile>();
    private Vector<Tile> tiles;
    private Tile startTile;
    private Tile endTile;
    private AbstractPilot pilot;
    private int extraCostSeesaw = 50000;

    public ShortestPath(final AbstractPilot pilot, final Tile startTile, final Tile endTile, final Vector<Tile> tiles) {
        this.pilot = pilot;
        this.startTile = startTile;
        this.endTile = endTile;
        this.tiles = tiles;
        for (final Tile tile : tiles)
            tile.setMarkingShortestPath(false);
    }

    /**
     * wordt opgeroepen als einddoel bereikt is in fillTilesPath. tilesPath
     * bevat nu alle tiles die afgegaan zijn dus diegene die niet naar het doel
     * leiden moeten nog verwijderd worden. dit gebeurt als volgt: ge begint
     * vanaf uw voorlaatste tile en checkt of deze buren is met de volgende EN
     * een kost heeft 1 minder als de kost van de volgende, indien dit niet zo
     * is , wordt deze tile verwijderd en checkt men de tile ervoor, enz...
     */
    private void deleteSuperfluousTiles() {
    	for (int i = tilesPath.size() - 2; i > 0; i--)
    		if ((tilesPath.get(i).getCost() != tilesPath.get(i + 1).getCost() - 1) || !tilesPath.get(i).areNeighbours(tilesPath.get(i + 1)))
    			tilesPath.remove(i);
    }
    
    private boolean seesawIsPassableFromTile(Tile seesaw) {
    	if(!((Seesaw) seesaw.getContent()).isUp())
    		return true;
    	else
    		return false;
    }
    
    private void fillTilesPath(final Tile currentTile) {
        tilesPath.add(currentTile);
        if (currentTile.getManhattanValue() == 0) {
            deleteSuperfluousTiles();
            return;
        }

        // voeg neighbourTiles van de currentTile toe aan de queue
        for (final Tile neighbourTile : currentTile.getReachableNeighbours()) {
            if (tiles.contains(neighbourTile) && !neighbourTile.isMarkedShortestPath()) {
            	if(! (neighbourTile.getContent() instanceof Seesaw) || seesawIsPassableFromTile(neighbourTile)) {
                    neighbourTile.setCost(currentTile.getCost() + 1);
                    queue.add(neighbourTile);
            	}
            }
        }

        // sorteer de queue: grootste vooraan
        Collections.sort(queue, new Comparator<Tile>() {
            @Override
            public int compare(final Tile o1, final Tile o2) {
                if (o1.getManhattanValue() + o1.getCost() < o2.getManhattanValue() + o2.getCost()) 
                    return 1;
                else if (o1.getManhattanValue() + o1.getCost() == o2.getManhattanValue() + o2.getCost()) 
                    return 0;
                else 
                    return -1;
            }
        });

        currentTile.setMarkingShortestPath(true);

        if(queue.size() > 0) {
        	// remove the last tile from the queue and add it to the path
        	final Tile nextTile = queue.get(queue.size() - 1);
        	while (queue.contains(nextTile))
                queue.remove(nextTile);
        	fillTilesPath(nextTile);
        }
    }

    /**
     * De methode die moet opgeroepen worden en alle methodes in de juiste
     * volgorde uitvoert. eerst worden de heuristieken gezet dan fillTilesPath,
     * en aan de hand hiervan wordt naar de robot/simulator het commando
     * gestuurd om deze tiles te "bewandelen". Op het einde wordt de kost van
     * alle tiles terug op hun initiele waarde gezet.
     */
    public int goShortestPath(boolean align, int amount, int amountOfTilesUntilAlign) {
        int currentAmount = amount;
        setHeuristics();
        startTile.setCost(0);
        fillTilesPath(startTile);
        if (tilesPath.size() == 1)
            return currentAmount;
        for (int i = 0; i < tilesPath.size() - 1; i++) {
        	Orientation orientation = null;
        	for(Orientation ori : Orientation.values())
        		if(tilesPath.get(i).getEdgeAt(ori) == tilesPath.get(i + 1).getEdgeAt(ori.getOppositeOrientation()))
        			orientation = ori;
            if (tilesPath.size() - i > 2)
                pilot.setReadBarcodes(false);
            else
                pilot.setReadBarcodes(true);
            pilot.rotate((int) ExtMath.getSmallestAngle((int) (orientation.getAngle() - pilot.getAngle())));
            if(align && currentAmount == 0) {
            	pilot.alignOnWhiteLine();
            	currentAmount = amountOfTilesUntilAlign;
            }
            else {
                pilot.travel(40);
                currentAmount--;
            }
            if(pilot.canUpdatePosition()) {
            	try {
                	pilot.getCenter().getClient().updatePosition(pilot.getPosition().x, pilot.getPosition().y, pilot.getAngle());
            	} catch(Exception e) {
            		
            	}
            }
            // TODO goToNextTile checkte of er geAligned moest worden.
            // communicator.goToNextTile(orientation);
        }

        for (final Object tile : tiles)
            ((Tile) tile).resetCost();
        return currentAmount;
    }
    
    /**
     * zet de heuristiek op elke tile afhankelijk van de endTile die heuristiekwaarde 0 krijgt.
     */
    private void setHeuristics() {
        for (final Tile tile : tiles) {
            int heuristic = (int) (Math.abs(endTile.getPosition().getX() - tile.getPosition().getX()) + Math.abs(endTile.getPosition().getY() - tile.getPosition().getY()));
            if(tile.getContent() instanceof Seesaw)
            	heuristic = heuristic + extraCostSeesaw;            
            tile.setManhattanValue(heuristic);
        }
    }
}