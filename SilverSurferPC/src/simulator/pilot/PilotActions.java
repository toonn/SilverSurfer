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
		//Stop reading barcodes.
		pilot.setReadBarcodes(false);
		
		//Change the current tile into a straight and mark it explored.
		for(Orientation orientation: Orientation.values())
			if(orientation != pilot.getOrientation() &&	orientation != pilot.getOrientation().getOppositeOrientation())
				pilot.getMapGraphConstructed().addObstruction(pilot.getMatrixPosition(), orientation, Obstruction.WALL);
		pilot.getMapGraphConstructed().getTile(pilot.getMatrixPosition()).setMarkingExploreMaze(true);
		
		//Add the tile at the end of the straight to the map.
		Point nextPoint = pilot.getOrientation().getNext(pilot.getMatrixPosition());
		if(pilot.getMapGraphConstructed().getTile(nextPoint) == null)
			pilot.getMapGraphConstructed().addTileXY(nextPoint);

		//Add the barcode to the map. 
		int value = pilot.readBarcode();
		Barcode barcode = new Barcode(pilot.getMapGraphConstructed().getTile(pilot.getMatrixPosition()), value, pilot.getOrientation());
		pilot.getMapGraphConstructed().addContentToCurrentTile(pilot.getMatrixPosition(), barcode);
				
		//Execute the barcode action.
		executeBarcode(value);

		//Finish executing and start reading barcodes again.
		pilot.setBusyExecutingBarcode(false);
		pilot.setReadBarcodes(true);
	}
	
	private void executeBarcode(int barcode) {
		if(barcode != -1) {
			//Check if the barcode is a seesaw.
			for(int i = 0; i < BarcodeCommand.SEESAW_START.length; i++)
			{
				if(barcode == BarcodeCommand.SEESAW_START[i] || barcode == BarcodeCommand.SEESAW_START_INVERSE[i])
				{
					seesawFound(BarcodeCommand.SEESAW_END[i], i, false);
				}
				else if(barcode == BarcodeCommand.SEESAW_END[i] || barcode == BarcodeCommand.SEESAW_END_INVERSE[i])
				{
					seesawFound(BarcodeCommand.SEESAW_START[i], i, true);
				}
			}
			//Check if the barcode is a treasure.
			for(int i = 0; i < BarcodeCommand.TREASURE_TEAM0.length; i++) {
				if(barcode == BarcodeCommand.TREASURE_TEAM0[i] || barcode == BarcodeCommand.TREASURE_TEAM1[i]
				      || barcode == BarcodeCommand.TREASURE_TEAM0_INVERSE[i]  || barcode == BarcodeCommand.TREASURE_TEAM1_INVERSE[i]) {
					treasureFound(barcode);
					// the barcode and object belongs to the robot with number i.
					if(i == pilot.getTeamNumber()) {
						int team = 0;
						if(barcode == BarcodeCommand.TREASURE_TEAM1[i] || barcode == BarcodeCommand.TREASURE_TEAM1_INVERSE[i])
							team = 1;
						pickUpItem(team);
					}
				}
			}
		}
	}

	/**
	 * @param barcode, the value of the barcode on the other side of the seesaw
	 * @param value, the value of the seesaw
	 * @param right, true if the seesaw lies partitally to the left of this tile (given the orientation of the robot)
	 */
	private void seesawFound(int otherBarcode, int value, boolean right) {
		System.out.println("Robot " + pilot.getTeamNumber() + ": seesaw " + value);

		// add the four next tiles to the map (the tile on nextPoint1 is allready added by addBarcode())
		Point nextPoint1 = pilot.getOrientation().getNext(pilot.getMatrixPosition());
		Point nextPoint2 = pilot.getOrientation().getNext(nextPoint1);
		Point nextPoint3 = pilot.getOrientation().getNext(nextPoint2);
		Point nextPoint4 = pilot.getOrientation().getNext(nextPoint3);
		pilot.getMapGraphConstructed().addTileXY(nextPoint2);
		pilot.getMapGraphConstructed().addTileXY(nextPoint3);
		pilot.getMapGraphConstructed().addTileXY(nextPoint4);
		
		// add the other two seesaw-tiles to the map
		Orientation sideOrientation = pilot.getOrientation().getOtherOrientationCorner();
		if(right)
		{
			sideOrientation = sideOrientation.getOppositeOrientation();
		}
		Point sidePoint1 = sideOrientation.getNext(nextPoint1);
		pilot.getMapGraphConstructed().addTileXY(sidePoint1);
		Point sidePoint2 = sideOrientation.getNext(nextPoint2);
		pilot.getMapGraphConstructed().addTileXY(sidePoint2);
		
		// add the seesaw to the seesaw-tiles
		Seesaw seesaw1 = new Seesaw(pilot.getMapGraphConstructed().getTile(nextPoint1), value);
		Seesaw seesaw2 = new Seesaw(pilot.getMapGraphConstructed().getTile(nextPoint2), value);
		Seesaw seesaw3 = new Seesaw(pilot.getMapGraphConstructed().getTile(sidePoint1), value);
		Seesaw seesaw4 = new Seesaw(pilot.getMapGraphConstructed().getTile(sidePoint2), value);
		pilot.getMapGraphConstructed().addContentToCurrentTile(nextPoint1, seesaw1);
		pilot.getMapGraphConstructed().addContentToCurrentTile(nextPoint2, seesaw2);
		pilot.getMapGraphConstructed().addContentToCurrentTile(sidePoint1, seesaw3);
		pilot.getMapGraphConstructed().addContentToCurrentTile(sidePoint2, seesaw4);
		
		// add the right edges to the seesaw tiles and mark as explored
		Obstruction closeObstruction = Obstruction.SEESAW_DOWN;
		Obstruction farObstruction = Obstruction.SEESAW_UP;
		if(pilot.getInfraRedSensorValue() > 40 && pilot.getInfraRedSensorValue() < 60)
		{
			closeObstruction = Obstruction.SEESAW_UP;
			farObstruction = Obstruction.SEESAW_DOWN;
		}
		pilot.getMapGraphConstructed().getTile(nextPoint1).getEdge(pilot.getOrientation()).replaceObstruction(Obstruction.SEESAW_FLIP);
		pilot.getMapGraphConstructed().getTile(nextPoint1).getEdge(pilot.getOrientation().getOppositeOrientation()).replaceObstruction(closeObstruction);
		pilot.getMapGraphConstructed().getTile(nextPoint1).getEdge(sideOrientation.getOppositeOrientation()).replaceObstruction(Obstruction.WALL);
		pilot.getMapGraphConstructed().getTile(nextPoint1).setMarkingExploreMaze(true);
		
		pilot.getMapGraphConstructed().getTile(sidePoint1).getEdge(pilot.getOrientation()).replaceObstruction(Obstruction.SEESAW_FLIP);
		pilot.getMapGraphConstructed().getTile(sidePoint1).getEdge(sideOrientation).replaceObstruction(Obstruction.WALL);
		pilot.getMapGraphConstructed().getTile(sidePoint1).getEdge(pilot.getOrientation().getOppositeOrientation()).replaceObstruction(Obstruction.WALL);
		pilot.getMapGraphConstructed().getTile(sidePoint1).setMarkingExploreMaze(true);
		
		pilot.getMapGraphConstructed().getTile(nextPoint2).getEdge(pilot.getOrientation()).replaceObstruction(farObstruction);
		pilot.getMapGraphConstructed().getTile(nextPoint2).getEdge(pilot.getOrientation().getOppositeOrientation()).replaceObstruction(Obstruction.SEESAW_FLIP);
		pilot.getMapGraphConstructed().getTile(nextPoint2).getEdge(sideOrientation.getOppositeOrientation()).replaceObstruction(Obstruction.WALL);
		pilot.getMapGraphConstructed().getTile(nextPoint2).setMarkingExploreMaze(true);
		
		pilot.getMapGraphConstructed().getTile(sidePoint2).getEdge(pilot.getOrientation()).replaceObstruction(Obstruction.WALL);
		pilot.getMapGraphConstructed().getTile(sidePoint2).getEdge(sideOrientation).replaceObstruction(Obstruction.WALL);
		pilot.getMapGraphConstructed().getTile(sidePoint2).getEdge(pilot.getOrientation().getOppositeOrientation()).replaceObstruction(Obstruction.SEESAW_FLIP);
		pilot.getMapGraphConstructed().getTile(sidePoint2).setMarkingExploreMaze(true);
		
		// change the third tile into a straight, add the other barcode and mark as explored
		for(Orientation orientation: Orientation.values())
			if(orientation != pilot.getOrientation() && orientation != pilot.getOrientation().getOppositeOrientation())
				pilot.getMapGraphConstructed().addObstruction(nextPoint3, orientation, Obstruction.WALL);
		pilot.getMapGraphConstructed().addObstruction(nextPoint3, pilot.getOrientation(), farObstruction);
		Barcode barcode = new Barcode(pilot.getMapGraphConstructed().getTile(nextPoint3), otherBarcode, pilot.getOrientation());
		pilot.getMapGraphConstructed().addContentToCurrentTile(nextPoint3, barcode);
		pilot.getMapGraphConstructed().getTile(nextPoint3).setMarkingExploreMaze(true);
	}
	
	private void treasureFound(int value) {
		//Change the next tile into a dead end and mark the tile as explored.
		Point nextPoint = pilot.getOrientation().getNext(pilot.getMatrixPosition());
		for(Orientation orientation: Orientation.values())
			if(orientation != pilot.getOrientation().getOppositeOrientation())
				pilot.getMapGraphConstructed().addObstruction(nextPoint, orientation, Obstruction.WALL);
		pilot.getMapGraphConstructed().getTile(nextPoint).setMarkingExploreMaze(true);

		// add the found treasure to the map.
		TreasureObject treasure = new TreasureObject(pilot.getMapGraphConstructed().getTile(nextPoint), value);
		pilot.getMapGraphConstructed().addContentToCurrentTile(nextPoint, treasure);
	}
	
	private void pickUpItem(int team) {
		pilot.setTeamNumber(4 + team);
		pilot.travel(50);
		try {
			Thread.sleep(500);
		} catch(Exception e) {

		}
		pilot.travel(-50);
	}
}
