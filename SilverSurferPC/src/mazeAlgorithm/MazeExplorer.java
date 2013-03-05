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

    /**
     * is true when robot must allign during the algorithm.
     */
    // private final boolean mustAllign = false;

    public MazeExplorer(final Tile startTile, final AbstractPilot pilot) {
        this.startTile = startTile;
        this.pilot = pilot;
    }

    /**
     * Deze methode wordt opgeroepen als het object het algoritme moet uitvoeren
     */
    public void startExploringMaze() {
        // TODO aligning
        // communicator.setTilesBeforeAllign(3);
        // communicator.mustAllign(mustAllign);
        allTiles.add(startTile);
        algorithm(startTile);
        for (final Object tile : allTiles)
            ((Tile) tile).setMarkingExploreMaze(false);
        // communicator.mustAllign(false);

    }

    private void algorithm(final Tile currentTile) {
    	// Explore tile and set current tile on "Explored".
        exploreTile(currentTile);

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

        // Wait until the barcode is executed
        // TODO:

        // Repeat with next tile.
        algorithm(nextTile);
    }

    // Checkt voor alle neighbours of ze al behandeld zijn.
    // Indien niet, checkt of er een muur tussen staat.
    // Indien wel, plaats een muur.
    // Indien niet, voeg een nieuwe lege tile toe op de juiste plaats.
    private void exploreTile(final Tile currentTile) {
        int numberVariable = pilot.getOrientation().getNumberArray();
        // numbervariable met als inhoud het getal van de orientatie (N = 1,
        // ...)
        // Array met alle buren van deze tile
        final ArrayList<Tile> array = currentTile.getAllNeighbours();
        for (int i = 0; i < 4; i++) {
            // Do nothing if tile with numbervariable as orientation is already
            // done
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
        } else {
            return queue.lastElement();
        }
    }

    private void goToNextTile(final Tile currentTile, final Tile nextTile) {
        // voert een shortestPath uit om van currentTile naar nextTile te gaan.
        final ShortestPath shortestPath = new ShortestPath(pilot, currentTile, nextTile, allTiles);
        shortestPath.goShortestPath();
    }

    private boolean isGoodNextTile(final Tile currentTile,
            final Orientation orientation) {
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
}