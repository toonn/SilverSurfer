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

    /**
     * Deze methode exploreert het doolhof
     */
    private void algorithm(final Tile currentTile) {

        // kijkt eerst of er muren zijn, deze methode zet ook al tiles waar er
        // zowiezo liggen
        // (dus waar geen muur staat),
        // zodat deze al in de map zitten en de robot er naartoe kan gaan

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
        // opnieuw in de queu
        // terecht kan komen
        currentTile.setMarkingExploreMaze(true);

        // voegt buurtiles van de currentTile toe aan de queu, enkel als deze
        // nog niet begaan
        // zijn (niet gemarkeerd)
        for (final Object neighbourTile : currentTile.getReachableNeighbours()) {
            if (neighbourTile != null
                    && !(((Tile) neighbourTile).isMarkedExploreMaze())) {
                queue.add((Tile) neighbourTile);
            }
        }

        // returnt als er geen tiles meer in de wachtrij zitten (algoritme is
        // afgelopen)
        if (queue.isEmpty()) {
            return;
        }

        Tile nextTile = getPriorityNextTile(currentTile);

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

    // /**
    // * Set the endtile that's the ending of the shortest-path algorithm.
    // */
    // public void setEndTile(Tile endTile) {
    // this.endTile = endTile;
    // }
    //
    // /**
    // * Get the endtile that's the ending of the shortest-path algorithm.
    // */
    // public Tile getEndTile() {
    // return endTile;
    // }
    //
    // /**
    // * Set the checktile that's the beginning of the shortest-path algorithm.
    // */
    // public void setCheckTile(Tile checkTile) {
    // this.checkTile = checkTile;
    // }
    //
    // /**
    // * Get the checktile that's the beginning of the shortest-path algorithm.
    // */
    // public Tile getCheckTile() {
    // return checkTile;
    // }
    //
    // public void setCheckTileFound(boolean check) {
    // checkTileFound = check;
    // }
    //
    // public void setEndTileFound(boolean check) {
    // endTileFound = check;
    // }

    /**
     * Kijkt naar elke neighbourTile van de currentTile: wanneer nog niet
     * explored en geen obstruction wordt een tile gezet.
     */
    private void checkForNeighboursNotYetExplored(final Tile currentTile) {

        Orientation currentOrientation = communicator.getPilot()
                .getOrientation();
        int numberVariable = currentOrientation.getNumberArray();

        for (int i = 0; i < 4; i++) {
            final ArrayList<Tile> array = currentTile.getAllNeighbours();
            if (array.get(numberVariable) != null
                    && (array.get(numberVariable).isMarkedExploreMaze())) {
            } else {
                double angle = (((numberVariable - currentOrientation
                        .getNumberArray()) * 90) + 360) % 360;
                angle = ExtMath.getSmallestAngle(angle);
                communicator.sendCommand((int) (angle * 10) * 10
                        + Command.AUTOMATIC_TURN_ANGLE);
                communicator
                        .sendCommand(Command.CHECK_OBSTRUCTIONS_AND_SET_TILE);
                currentOrientation = communicator.getPilot().getOrientation();
            }

            numberVariable = numberVariable + 1;
            if (numberVariable == 4) {
                numberVariable = 0;
            }
        }
    }

    /**
     * checkt of alle neighbourTiles al gecheckt zijn, indien ja niet meer nodig
     * om nextTile nog te checken.
     */
    public boolean doesHaveOtherNeighboursToCheck(final Tile nextTile) {
        int j = 0;
        if (!nextTile.isStraightTile()) {
            for (final Object neighbourTile : nextTile.getAllNeighbours()) {
                if (neighbourTile != null
                        && ((Tile) neighbourTile).isMarkedExploreMaze()) {
                    j++;
                }
            }
        }
        if (j == 4) {
            return false;
        }
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
        // TODO test shortestPath op't einde.
        // if (getCheckTile() != null && getEndTile() != null) {
        // communicator.sendCommand(
        // Command.PERMA_STOP_READING_BARCODES);
        // SilverSurferGUI.changeSpeed(3);
        // // Drive to checkpoint.
        // //TODO
        // // gui.getSimulationPanel().clearPath();
        // ShortestPath almostFinalPath = new ShortestPath(gui, gui
        // .getCommunicator()
        // .getSimulationPilot()
        // .getMapGraphConstructed()
        // .getTileWithCoordinates(
        // (int) SilverSurferGUI.getStatusInfoBuffer()
        // .getXCoordinateRelative(),
        // (int) SilverSurferGUI.getStatusInfoBuffer()
        // .getYCoordinateRelative()), getCheckTile(),
        // allTiles);
        // almostFinalPath.goShortestPath();

        // Drive to endpoint.
        // TODO
        // gui.getSimulationPanel().clearPath();
        // ShortestPath finalPath = new ShortestPath(gui, getCheckTile(),
        // getEndTile(), allTiles);
        // finalPath.goShortestPath();
        // }
        for (final Object tile : allTiles) {
            ((Tile) tile).setMarkingExploreMaze(false);
        }
        communicator.mustAllign(false);

    }
}