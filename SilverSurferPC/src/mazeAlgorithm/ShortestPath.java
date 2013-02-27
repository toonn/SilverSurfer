/**
 * een object van deze klasse kan maar 1x gebruikt worden!
 * velden worden ingevuld bij aanmaak object en kunnen niet meer veranderen.
 */

package mazeAlgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import mapping.Orientation;
import mapping.Tile;

import commands.Command;
import communication.Communicator;

public class ShortestPath {

    /**
     * hierin worden de tiles in de wachtrij gezet
     */
    private final ArrayList<Tile> queue = new ArrayList<Tile>();
    /**
     * hierin worden de tiles opgeslagen die het uiteindelijk pad vormen van
     * startdoel tot einddoel, inclusief deze laatste 2
     */
    private final Vector<Tile> tilesPath = new Vector<Tile>();
    /**
     * zijn alle tiles die meegegeven worden wanneer het algoritme opgeroepen
     * wordt. dus de tiles ter beschikking om van start- naar einddoel te gaan
     */
    Vector<Tile> tiles;
    Tile startTile;
    Tile endTile;
    Communicator communicator;

    public ShortestPath(final Communicator communicator, final Tile startTile,
            final Tile endTile, final Vector<Tile> tiles) {
        this.communicator = communicator;
        this.tiles = tiles;
        this.startTile = startTile;
        this.endTile = endTile;
        for (final Tile tile : tiles) {
            tile.setMarkingShortestPath(false);
        }
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
        if (tilesPath.size() > 1) {
            for (int i = tilesPath.size() - 2; i != 0; i--) {
                if ((tilesPath.get(i).getCost() != tilesPath.get(i + 1)
                        .getCost() - 1)
                        || !tilesPath.get(i)
                                .areNeighbours(tilesPath.get(i + 1))) {
                    tilesPath.remove(i);
                }
            }
        }

    }

    /**
     * In deze methode wordt tilesPath gevuld.
     */
    private void fillTilesPath(final Tile currentTile) {
        tilesPath.add(currentTile);

        if (currentTile.getManhattanValue() == 0) {
            // endTile bereikt
            deleteSuperfluousTiles();
            return;
        }

        // voeg neighbourTiles van de currentTile toe aan de queu
        for (final Object neighbourTile : currentTile.getReachableNeighbours()) {
            if (neighbourTile != null && tiles.contains(neighbourTile)
                    && !((Tile) neighbourTile).isMarkedShortestPath()) {
                ((Tile) neighbourTile).setCost(currentTile.getCost() + 1);
                queue.add((Tile) neighbourTile);
            }
        }

        // sorteer de queu: kleinste vooraan, nog niet getest
        Collections.sort(queue, new Comparator<Tile>() {
            @Override
            public int compare(final Tile o1, final Tile o2) {
                if (o1.getManhattanValue() + o1.getCost() < o2
                        .getManhattanValue() + o2.getCost()) {
                    return 1;
                } else if (o1.getManhattanValue() + o1.getCost() == o2
                        .getManhattanValue() + o2.getCost()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        currentTile.setMarkingShortestPath(true);

        final Tile nextTile = queue.get(queue.size() - 1);
        while (queue.contains(nextTile)) {
            queue.remove(nextTile);
        }
        fillTilesPath(nextTile);
    }

    private Vector<Tile> getTiles() {
        return tiles;
    }

    /**
     * De methode die moet opgeroepen worden en alle methodes in de juiste
     * volgorde uitvoert. eerst worden de heuristieken gezet dan fillTilesPath,
     * en aan de hand hiervan wordt naar de robot/simulator het commando
     * gestuurd om deze tiles te "bewandelen". Op het einde wordt de kost van
     * alle tiles terug op hun initiele waarde gezet.
     */
    public void goShortestPath() {
        setHeuristics();
        startTile.setCost(0);
        fillTilesPath(startTile);
        if (tilesPath.size() == 1) {
            return;
        }
        for (int i = 0; i < tilesPath.size() - 1; i++) {
            final Orientation orientation = tilesPath.get(i + 1)
                    .getCommonOrientation(tilesPath.get(i));
            try {
                if (tilesPath.size() - i > 2) {
                    communicator.sendCommand(Command.STOP_READING_BARCODES);
                } else {
                    communicator.sendCommand(Command.START_READING_BARCODES);
                }
                communicator.goToNextTile(orientation);
            } catch (final IOException e) {
                System.err
                        .println("exception in shortestpad gui.getunitcommunicator.goTonextTile");
                e.printStackTrace();
            }
        }

        for (final Object tile : getTiles()) {
            ((Tile) tile).setCostBackToInitiatedValue();
        }

    }

    /**
     * zet de heuristiek op elke tile afhankelijk van de endTile die
     * heuristiekwaarde 0 krijgt.
     */
    private void setHeuristics() {
        for (final Tile tile : tiles) {
            final int heuristic = (int) (Math.abs(endTile.getPosition().getX()
                    - tile.getPosition().getX()) + Math.abs(endTile
                    .getPosition().getY() - tile.getPosition().getY()));
            tile.setManhattanValue(heuristic);
        }
    }
}