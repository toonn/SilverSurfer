package simulator.pilot;

import java.awt.Point;

import mapping.Barcode;
import mapping.Obstruction;
import mapping.Orientation;
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
			for(int i = 0; i < BarcodeCommand.SEESAW_START.length; i++) {
				if(barcode == BarcodeCommand.SEESAW_START[i] || barcode == BarcodeCommand.SEESAW_START_INVERSE[i])
					seesawFound(barcode, BarcodeCommand.SEESAW_END[i]);
				else if(barcode == BarcodeCommand.SEESAW_END[i] || barcode == BarcodeCommand.SEESAW_END_INVERSE[i])
					seesawFound(barcode, BarcodeCommand.SEESAW_START[i]);
			}
			//Check if the barcode is a treasure.
			for(int i = 0; i < BarcodeCommand.TREASURE_TEAM0.length; i++) {
				if(barcode == BarcodeCommand.TREASURE_TEAM0[i]  || barcode == BarcodeCommand.TREASURE_TEAM1[i] || barcode == BarcodeCommand.TREASURE_TEAM0_INVERSE[i]  || barcode == BarcodeCommand.TREASURE_TEAM1_INVERSE[i]) {
					treasureFound(barcode);
					//The barcode and object belongs to the robot with number i.
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
	
	private void seesawFound(int currentBarcode, int otherBarcode) {
		//TODO
	}
	
	private void treasureFound(int value) {
		//Change the next tile into a dead end and mark the tile as explored.
		Point nextPoint = pilot.getOrientation().getNext(pilot.getMatrixPosition());
		for(Orientation orientation: Orientation.values())
			if(orientation != pilot.getOrientation().getOppositeOrientation())
				pilot.getMapGraphConstructed().addObstruction(nextPoint, orientation, Obstruction.WALL);
		pilot.getMapGraphConstructed().getTile(nextPoint).setMarkingExploreMaze(true);

		//Add the found treasure to the map.
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
