package mazeAlgorithm;

import java.util.ArrayList;
import java.util.Vector;

import mapping.ExtMath;
import mapping.Orientation;
import mapping.Tile;

import commands.Command;
import communication.Communicator;

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
    private final Communicator communicator;

    private Orientation currentOrientation;

    /**
     * is true when robot must allign during the algorithm.
     */
    private final boolean mustAllign = false;

    public MazeExplorer(final Communicator communicator) {
        this.communicator = communicator;
        startTile = communicator.getPilot().getMapGraphConstructed()
                .getTile(communicator.getPilot().getRelativePosition());
    }

    // Nieuw algoritme, equivalent met oud (bijhouden voor later)
    private void algoritm2(Tile currentTile) {
        allTiles.add(currentTile);
        updateQueue(currentTile);

        if (queue.isEmpty())
            return;

        Tile nextTile = getPriorityNextTile(currentTile);
        removeTileFromQueue(nextTile);
        goToNextTile(currentTile, nextTile);

        while (communicator.getExecutingBarcodes()) {
            try {
                Thread.sleep(100);
            } catch (final Exception e) {

            }
        }
        algorithm(nextTile);
    }

    // Gebruikt door nieuw algoritme (bijhouden voor later)
    private void updateQueue(Tile currentTile) {
        ArrayList<Tile> array = currentTile.getAllNeighbours();
        Orientation currentOrientation = communicator.getPilot()
                .getOrientation();
        int numberVariable = currentOrientation.getNumberArray();
        for (int i = 0; i < 4; i++) {
            if (!allTiles.contains(array.get(numberVariable))) {
                double angle = ExtMath
                        .getSmallestAngle((((numberVariable - currentOrientation
                                .getNumberArray()) * 90) + 360) % 360);
                communicator.sendCommand((int) (angle * 10) * 10
                        + Command.AUTOMATIC_TURN_ANGLE);
                currentOrientation = communicator.getPilot().getOrientation();
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
            if (numberVariable == 4)
                numberVariable = 0;
        }
    }

    private void algorithm(final Tile currentTile) {
        checkForNeighboursNotYetExplored(currentTile);

        currentOrientation = communicator.getPilot().getOrientation();

        // checkForObstructionsAndSetTile(whichTilesAllreadyBeen);

        // if (checkTileFound) {
        // setCheckTile(gui
        // .getCommunicator()
        // .getSimulationPilot()
        // .getMapGraphConstructed()
        // .getTileWithCoordinates(
        // communicator.getSimulationPilot()
        // .getCurrentPositionRelativeX(),
        // communicator.getSimulationPilot()
        // .getCurrentPositionRelativeY()));
        // setCheckTileFound(false);
        // }
        // if (endTileFound) {
        // setEndTile(gui
        // .getCommunicator()
        // .getSimulationPilot()
        // .getMapGraphConstructed()
        // .getTileWithCoordinates(
        // communicator.getSimulationPilot()
        // .getCurrentPositionRelativeX(),
        // communicator.getSimulationPilot()
        // .getCurrentPositionRelativeY()));
        // setEndTileFound(false);
        // }

        // zet het mark-veld van de currentTile op true zodat deze niet meer
        // opnieuw in de queue terecht kan komen
        currentTile.setMarkingExploreMaze(true);

        // voegt buurtiles van de currentTile toe aan de queue, enkel als deze
        // nog niet begaan zijn (niet gemarkeerd)
        for (final Object neighbourTile : currentTile.getReachableNeighbours()) {
            if (neighbourTile != null
                    && !(((Tile) neighbourTile).isMarkedExploreMaze()))
                queue.add((Tile) neighbourTile);
        }

        // returnt als er geen tiles meer in de wachtrij zitten (algoritme is
        // afgelopen)
        if (queue.isEmpty())
            return;

        Tile nextTile = getPriorityNextTile(currentTile);

        // Als de volgende tile geen onontdekte buren heeft, slaag deze tile
        // over --> MAG WEG!
        while (!doesHaveOtherNeighboursToCheck(nextTile)) {
            nextTile.setMarkingExploreMaze(true);
            allTiles.add(nextTile);
            removeTileFromQueue(nextTile);

            if (queue.isEmpty()) {
                return;
            }

            nextTile = getPriorityNextTile(currentTile);
        }
        allTiles.add(nextTile);
        removeTileFromQueue(nextTile);

        goToNextTile(currentTile, nextTile);

        // wachten tot de barcode is uitgevoerd.
        while (communicator.getExecutingBarcodes()) {
            try {
                Thread.sleep(100);
            } catch (final Exception e) {

            }
        }
        // voert methode opnieuw uit met nextTile
        algorithm(nextTile);
    }

    // Checkt voor alle neighbours of ze al behandeld zijn.
    // Indien niet, checkt of er een muur tussen staat.
    // Indien wel, plaats een muur.
    // Indien niet, voeg een nieuwe lege tile toe op de juiste plaats.
    private void checkForNeighboursNotYetExplored(final Tile currentTile) {

        Orientation currentOrientation = communicator.getPilot()
                .getOrientation();
        int numberVariable = currentOrientation.getNumberArray();
        // numbervariable met als inhoud het getal van de orientatie (N = 1,
        // ...)
        // Array met alle buren van deze tile
        final ArrayList<Tile> array = currentTile.getAllNeighbours();
        for (int i = 0; i < 4; i++) {
            // Do nothing if tile with numbervariable as orientation is already
            // done
            if (array.get(numberVariable) != null
                    && (array.get(numberVariable).isMarkedExploreMaze()))
                ;
            // Else turn to this direction and check obstruction and set tile
            else {
                double angle = (((numberVariable - currentOrientation
                        .getNumberArray()) * 90) + 360) % 360;
                angle = ExtMath.getSmallestAngle(angle);
                communicator.sendCommand((int) (angle * 10) * 10
                        + Command.AUTOMATIC_TURN_ANGLE);
                communicator
                        .sendCommand(Command.CHECK_OBSTRUCTIONS_AND_SET_TILE);
                currentOrientation = communicator.getPilot().getOrientation();
            }
            // Next orientation
            numberVariable = numberVariable + 1;
            if (numberVariable == 4)
                numberVariable = 0;
        }
    }

    /**
     * checkt of alle neighbourTiles al gecheckt zijn, indien ja niet meer nodig
     * om nextTile nog te checken.
     */
    public boolean doesHaveOtherNeighboursToCheck(final Tile nextTile) {
        int j = 0;
        if (!nextTile.isStraightTile())
            for (final Object neighbourTile : nextTile.getAllNeighbours())
                if (neighbourTile != null
                        && ((Tile) neighbourTile).isMarkedExploreMaze())
                    j++;
        if (j == 4)
            return false;
        return true;
    }

    private Tile getPriorityNextTile(final Tile currentTile) {

        currentOrientation = communicator.getPilot().getOrientation();

        if (isGoodNextTile(currentTile, currentOrientation)) {
            return currentTile.getEdge(currentOrientation).getNeighbour(
                    currentTile);
        } else if (isGoodNextTile(currentTile,
                currentOrientation.getOtherOrientationCorner())) {
            return currentTile.getEdge(
                    currentOrientation.getOtherOrientationCorner())
                    .getNeighbour(currentTile);
        } else if (isGoodNextTile(currentTile, currentOrientation
                .getOtherOrientationCorner().getOppositeOrientation())) {
            return currentTile.getEdge(
                    currentOrientation.getOtherOrientationCorner()
                            .getOppositeOrientation())
                    .getNeighbour(currentTile);
        } else if (isGoodNextTile(currentTile,
                currentOrientation.getOppositeOrientation())) {
            return currentTile.getEdge(
                    currentOrientation.getOppositeOrientation()).getNeighbour(
                    currentTile);
        } else {
            return queue.lastElement();
        }
    }

    public void goToNextTile(final Tile currentTile, final Tile nextTile) {
        // voert een shortestPath uit om van currentTile naar nextTile te gaan.
        final ShortestPath shortestPath = new ShortestPath(communicator,
                currentTile, nextTile, allTiles);
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
        // verwijdert alle nextTiles uit de queu. De reden waarom deze meermaals
        // in de queu
        // voorkomen is omdat het voordeliger is om de laatste versie te pakken
        // omdat deze
        // het dichts bij de currentTile ligt, zodat de robot niet voor elke
        // nextTile massa's
        // omweg moet doen
        while (queue.contains(tile)) {
            queue.remove(tile);
        }
    }

    /**
     * Deze methode wordt opgeroepen als het object het algoritme moet uitvoeren
     */
    public void startExploringMaze() {
        communicator.setTilesBeforeAllign(3);
        communicator.mustAllign(mustAllign);
        allTiles.add(startTile);
        algorithm(startTile);
        for (final Object tile : allTiles) {
            ((Tile) tile).setMarkingExploreMaze(false);
        }
        communicator.mustAllign(false);

    }
}