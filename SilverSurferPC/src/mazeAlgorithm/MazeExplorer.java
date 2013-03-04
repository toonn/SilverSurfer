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

    private void algorithm(final Tile currentTile) {
        checkForNeighboursNotYetExplored(currentTile);

        // zet het mark-veld van de currentTile op true zodat deze niet meer
        // opnieuw in de queue terecht kan komen
        currentTile.setMarkingExploreMaze(true);

        // voegt buurtiles van de currentTile toe aan de queue, enkel als deze
        // nog niet begaan zijn (niet gemarkeerd)
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

        // TODO ?wachten tot de barcode is uitgevoerd.

        // voert methode opnieuw uit met nextTile
        algorithm(nextTile);
    }

    // Checkt voor alle neighbours of ze al behandeld zijn.
    // Indien niet, checkt of er een muur tussen staat.
    // Indien wel, plaats een muur.
    // Indien niet, voeg een nieuwe lege tile toe op de juiste plaats.
    private void checkForNeighboursNotYetExplored(final Tile currentTile) {
        int numberVariable = pilot.getOrientation().getNumberArray();
        // numbervariable met als inhoud het getal van de orientatie (N = 1,
        // ...)
        // Array met alle buren van deze tile
        final ArrayList<Tile> array = currentTile.getAllNeighbours();
        for (int i = 0; i < 4; i++) {
            // Do nothing if tile with numbervariable as orientation is already
            // done
            if (array.get(numberVariable) != null
                    && (array.get(numberVariable).isMarkedExploreMaze())) {
                ;
            } else {
                // TODO +360 zinloos?
                double angle = (((numberVariable - pilot.getOrientation()
                        .getNumberArray()) * 90) + 360) % 360;
                angle = ExtMath.getSmallestAngle(angle);
                pilot.rotate(angle);
                pilot.setObstructionOrTile();
                // communicator.sendCommand((int) (angle * 10) * 10
                // + Command.AUTOMATIC_TURN_ANGLE);
                // communicator
                // .sendCommand(Command.CHECK_OBSTRUCTIONS_AND_SET_TILE);
                // currentOrientation =
                // communicator.getPilot().getOrientation();
            }
            // Next orientation
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

    public void goToNextTile(final Tile currentTile, final Tile nextTile) {
        // voert een shortestPath uit om van currentTile naar nextTile te gaan.
        final ShortestPath shortestPath = new ShortestPath(pilot, currentTile,
                nextTile, allTiles);
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
        // TODO aligning
        // communicator.setTilesBeforeAllign(3);
        // communicator.mustAllign(mustAllign);
        allTiles.add(startTile);
        algorithm(startTile);
        for (final Object tile : allTiles) {
            ((Tile) tile).setMarkingExploreMaze(false);
        }
        // communicator.mustAllign(false);

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