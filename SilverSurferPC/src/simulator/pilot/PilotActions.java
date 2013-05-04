package simulator.pilot;

import java.awt.Point;

import mapping.Barcode;
import mapping.Obstruction;
import mapping.Orientation;
import mapping.Seesaw;
import mapping.TreasureObject;

import commands.BarcodeCommand;

public class PilotActions {

    private AbstractPilot pilot;

    public PilotActions(AbstractPilot pilot) {
        this.pilot = pilot;
    }

    public void barcodeFound() {
        // Stop reading barcodes.
        pilot.setReadBarcodes(false);

        // Change the current tile into a straight and mark it explored.
        for (Orientation orientation : Orientation.values()) {
            if (orientation != pilot.getOrientation()
                    && orientation != pilot.getOrientation()
                            .getOppositeOrientation()) {
                pilot.getMapGraphConstructed()
                        .getTile(pilot.getMatrixPosition())
                        .getEdgeAt(orientation)
                        .setObstruction(Obstruction.WALL);
            } else {
                pilot.getMapGraphConstructed()
                        .getTile(pilot.getMatrixPosition())
                        .getEdgeAt(orientation)
                        .setObstruction(Obstruction.WHITE_LINE);
            }
        }
        pilot.getMapGraphConstructed().getTile(pilot.getMatrixPosition())
                .setMarkingExploreMaze(true);

        // Add the tile at the end of the straight to the map.
        Point nextPoint = pilot.getOrientation().getNext(
                pilot.getMatrixPosition());
        if (pilot.getMapGraphConstructed().getTile(nextPoint) == null) {
            pilot.getMapGraphConstructed().addTile(nextPoint);
        }

        // Add the barcode to the map.
        int value = pilot.readBarcode();
        Barcode barcode = new Barcode(pilot.getMapGraphConstructed().getTile(
                pilot.getMatrixPosition()), value, pilot.getOrientation());
        pilot.getMapGraphConstructed().getTile(pilot.getMatrixPosition())
                .setContent(barcode);

        // Execute the barcode action.
        executeBarcode(value);

        // Finish executing and start reading barcodes again.
        pilot.setBusyExecutingBarcode(false);
        pilot.setReadBarcodes(true);
    }

    private void executeBarcode(int barcode) {
        if (barcode != -1) {
            // Check if the barcode is a seesaw.
            for (int i = 0; i < BarcodeCommand.SEESAW_START.length; i++) {
                if (barcode == BarcodeCommand.SEESAW_START[i]
                        || barcode == BarcodeCommand.SEESAW_START_INVERSE[i]) {
                    seesawFound(BarcodeCommand.SEESAW_END[i], i);
                } else if (barcode == BarcodeCommand.SEESAW_END[i]
                        || barcode == BarcodeCommand.SEESAW_END_INVERSE[i]) {
                    seesawFound(BarcodeCommand.SEESAW_START[i], i);
                }
            }
            // Check if the barcode is a treasure.
            for (int i = 0; i < BarcodeCommand.TREASURE_TEAM0.length; i++) {
                if (barcode == BarcodeCommand.TREASURE_TEAM0[i]
                        || barcode == BarcodeCommand.TREASURE_TEAM1[i]
                        || barcode == BarcodeCommand.TREASURE_TEAM0_INVERSE[i]
                        || barcode == BarcodeCommand.TREASURE_TEAM1_INVERSE[i]) {
                    treasureFound(barcode);
                    // The barcode and object belongs to the robot with number
                    // i.
                    if (i == pilot.getPlayerNumber() && pilot.getTeamNumber() == -1) {
                        int team = 0;
                        if (barcode == BarcodeCommand.TREASURE_TEAM1[i]
                                || barcode == BarcodeCommand.TREASURE_TEAM1_INVERSE[i]) {
                            team = 1;
                        }
                        pickUpItem(team);
                    }
                }
            }
        }
    }

    private void pickUpItem(int team) {
        pilot.setTeamNumber(team);
        pilot.alignOnWhiteLine();
        pilot.travel(6);
        pilot.travel(-46);

        if (pilot.isInGameModus()) {
            try {
                pilot.getCenter().getPlayerClient().foundObject();
                pilot.getCenter().getPlayerClient().joinTeam(pilot.getTeamNumber());
            } catch (Exception e) {
                System.out.println("EXCEPTION! PILOTACTIONS!");
            }
        }
    }

    private void seesawFound(int otherBarcode, int value) {
        // add the four next tiles to the map (the tile on nextPoint1 is already
        // added by addBarcode())
        Point nextPoint1 = pilot.getOrientation().getNext(
                pilot.getMatrixPosition());
        Point nextPoint2 = pilot.getOrientation().getNext(nextPoint1);
        Point nextPoint3 = pilot.getOrientation().getNext(nextPoint2);
        Point nextPoint4 = pilot.getOrientation().getNext(nextPoint3);
        // If the wip is discovered the first time
        if (!pilot.getMapGraphConstructed().getTiles().contains(pilot.getMapGraphConstructed().getTile(nextPoint2)))
            pilot.getMapGraphConstructed().addTile(nextPoint2);
        if (!pilot.getMapGraphConstructed().getTiles().contains(pilot.getMapGraphConstructed().getTile(nextPoint3)))
            pilot.getMapGraphConstructed().addTile(nextPoint3);
        if (!pilot.getMapGraphConstructed().getTiles().contains(pilot.getMapGraphConstructed().getTile(nextPoint4)))
            pilot.getMapGraphConstructed().addTile(nextPoint4);

        // add the seesaw to the seesaw-tiles
        Seesaw seesaw1 = new Seesaw(pilot.getMapGraphConstructed().getTile(
                nextPoint1), pilot.getOrientation().getOppositeOrientation());
        Seesaw seesaw2 = new Seesaw(pilot.getMapGraphConstructed().getTile(
                nextPoint2), pilot.getOrientation());
        seesaw1.setValue(value);
        seesaw2.setValue(value);
        seesaw1.setOtherSeesaw(seesaw2);
        seesaw2.setOtherSeesaw(seesaw1);
        pilot.getMapGraphConstructed().getTile(nextPoint1).setContent(seesaw1);
        pilot.getMapGraphConstructed().getTile(nextPoint2).setContent(seesaw2);

        // add the right edges to the seesaw tiles and mark as explored
        Obstruction closeObstruction = Obstruction.SEESAW_DOWN;
        Obstruction farObstruction = Obstruction.SEESAW_UP;
        seesaw2.switchClosed();
        if (pilot.getInfraRedSensorValue() > 70) {
            // The seesaw is up
            closeObstruction = Obstruction.SEESAW_UP;
            farObstruction = Obstruction.SEESAW_DOWN;
            seesaw1.switchClosed();
            seesaw2.switchClosed();
        }
        for (Orientation orientation : Orientation.values()) {
            if (orientation == pilot.getOrientation()) {
                pilot.getMapGraphConstructed().getTile(nextPoint1)
                        .getEdgeAt(orientation)
                        .setObstruction(Obstruction.SEESAW_FLIP);
                pilot.getMapGraphConstructed().getTile(nextPoint2)
                        .getEdgeAt(orientation).setObstruction(farObstruction);
            } else if (orientation == pilot.getOrientation()
                    .getOppositeOrientation()) {
                pilot.getMapGraphConstructed().getTile(nextPoint1)
                        .getEdgeAt(orientation)
                        .setObstruction(closeObstruction);
                pilot.getMapGraphConstructed().getTile(nextPoint2)
                        .getEdgeAt(orientation)
                        .setObstruction(Obstruction.SEESAW_FLIP);
            } else {
                pilot.getMapGraphConstructed().getTile(nextPoint1)
                        .getEdgeAt(orientation)
                        .setObstruction(Obstruction.WALL);
                pilot.getMapGraphConstructed().getTile(nextPoint2)
                        .getEdgeAt(orientation)
                        .setObstruction(Obstruction.WALL);
            }
        }
        pilot.getMapGraphConstructed().getTile(nextPoint1)
                .setMarkingExploreMaze(true);
        pilot.getMapGraphConstructed().getTile(nextPoint2)
                .setMarkingExploreMaze(true);

        // change the third tile into a straight, add the other barcode and mark
        // as explored
        for (Orientation orientation : Orientation.values()) {
            if (orientation != pilot.getOrientation()
                    && orientation != pilot.getOrientation()
                            .getOppositeOrientation()) {
                pilot.getMapGraphConstructed().getTile(nextPoint3)
                        .getEdgeAt(orientation)
                        .setObstruction(Obstruction.WALL);
            }
        }
        pilot.getMapGraphConstructed().getTile(nextPoint3)
                .getEdgeAt(pilot.getOrientation().getOppositeOrientation())
                .setObstruction(farObstruction);
        pilot.getMapGraphConstructed().getTile(nextPoint3)
                .getEdgeAt(pilot.getOrientation())
                .setObstruction(Obstruction.WHITE_LINE);
        Barcode barcode = new Barcode(pilot.getMapGraphConstructed().getTile(
                nextPoint3), otherBarcode, pilot.getOrientation());
        pilot.getMapGraphConstructed().getTile(nextPoint3).setContent(barcode);
        pilot.getMapGraphConstructed().getTile(nextPoint3)
                .setMarkingExploreMaze(true);

        if (!pilot.getSeesawBarcodeTiles().contains(
                pilot.getMapGraphConstructed().getTile(
                        pilot.getMatrixPosition()))) {
            pilot.getSeesawBarcodeTiles().add(
                    pilot.getMapGraphConstructed().getTile(
                            pilot.getMatrixPosition())); // Add current barcode
                                                         // to list
        }
        if (!pilot.getSeesawBarcodeTiles().contains(
                pilot.getMapGraphConstructed().getTile(nextPoint3))) {
            pilot.getSeesawBarcodeTiles().add(
                    pilot.getMapGraphConstructed().getTile(nextPoint3)); // Add
                                                                         // other
                                                                         // barcode
                                                                         // to
                                                                         // list
        }
    }

    private void treasureFound(int value) {
        // Change the next tile into a dead end and mark the tile as explored.
        Point nextPoint = pilot.getOrientation().getNext(
                pilot.getMatrixPosition());
        for (Orientation orientation : Orientation.values()) {
            if (orientation != pilot.getOrientation().getOppositeOrientation()) {
                pilot.getMapGraphConstructed().getTile(nextPoint)
                        .getEdgeAt(orientation)
                        .setObstruction(Obstruction.WALL);
            }
        }
        pilot.getMapGraphConstructed().getTile(nextPoint)
                .setMarkingExploreMaze(true);

        // add the found treasure to the map.
        TreasureObject treasure = new TreasureObject(pilot
                .getMapGraphConstructed().getTile(nextPoint), value);
        pilot.getMapGraphConstructed().getTile(nextPoint).setContent(treasure);
    }
}
