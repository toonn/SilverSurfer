package simulator.pilot;

import java.awt.Point;

import mapping.Barcode;
import mapping.Obstruction;
import mapping.Orientation;
import mapping.TreasureObject;

import commands.BarcodeCommand;

/**
 * A class of actions which AbstractPilots can undertake.
 */
public class PilotActions {

	private AbstractPilot pilot;

	public PilotActions(AbstractPilot clientPilot){
		this.pilot = clientPilot;
	}

	/**
	 * @return	: The AbstractPilot that executes these actions.
	 */
	public AbstractPilot getPilot() {
		return pilot;
	}

	/**
	 * Executes the actions that should result in picking up the object.
	 * @pre team = 0 or 1, depending on which team the robot is on.
	 */
	private void pickUpItem(int team) {
		getPilot().stopReadingBarcodes();
		System.out.println("Robot " + getPilot().getTeamNumber() + ": pickup");

		getPilot().setTeamNumber(4 + team);

		getPilot().travel(50);
		try {
			Thread.sleep(500);
		} catch(Exception e) {

		}
		getPilot().travel(-50);

		getPilot().startReadingBarcodes();
	}

	private void doNotPickUpItem() {
		getPilot().stopReadingBarcodes();
		System.out.println("Robot " + getPilot().getTeamNumber() + ": not pickup");

		getPilot().startReadingBarcodes();
	}

	/**
	 * Decides what should be done when a given barcode is found
	 * @param barcode
	 */
	public void executeBarcode(int barcode) {
		if(barcode != -1) {
			for(int i = 0; i < BarcodeCommand.SEESAW_START.length; i++)
			{
				if(barcode == BarcodeCommand.SEESAW_START[i])
				{
					seesawFound(barcode, BarcodeCommand.SEESAW_END[i]);
				}
				else if(barcode == BarcodeCommand.SEESAW_END[i])
				{
					seesawFound(barcode, BarcodeCommand.SEESAW_START[i]);
				}
			}
			// check whether the barcode found is one of the treasures of team 0.
			for(int i = 0; i < BarcodeCommand.TREASURE_TEAM0.length; i++)
			{
				if(getPilot().getTeamNumber() < 5 && 
						(barcode == BarcodeCommand.TREASURE_TEAM0[i] 
						  || barcode == BarcodeCommand.TREASURE_TEAM1[i]))
				{
					treasureFound(barcode);
					
					// the found treasure is the one this pilot is searching for
					if(i == pilot.getTeamNumber())
					{
						int team = 0;
						if(barcode == BarcodeCommand.TREASURE_TEAM1[i])
						{
							team = 1;
						}
						pickUpItem(team);
					}
					else
					{
						doNotPickUpItem();
					}
				}
			}
		}
		// else the barcode has not been read right and will not be executed
	}

	/**
	 * @param barcode
	 * @param i
	 */
	private void seesawFound(int currentBarcode, int otherBarcode) {
		
		// TODO Auto-generated method stub
		
	}

	/**
	 * Change the current tile into a dead-end, mark it as explored and place a barcode on the tile.
	 * Allign on walls and on the white line behind the barcode, then execute the barcode.
	 */
	public void barcodeFound() {
		getPilot().stopReadingBarcodes();

		// change the current tile into a straight and mark it explored
		for(Orientation orientation: Orientation.values())
			if(orientation != getPilot().getOrientation() &&	orientation != getPilot().getOrientation().getOppositeOrientation())
				getPilot().getMapGraphConstructed().addObstruction(getPilot().getMatrixPosition(), orientation, Obstruction.WALL);
		getPilot().getMapGraphConstructed().getTile(getPilot().getMatrixPosition()).setMarkingExploreMaze(true);

		// add the tile at the end of the straight to the map
		Point nextPoint = getPilot().getOrientation().getNext(getPilot().getMatrixPosition());
		getPilot().getMapGraphConstructed().addTileXY(nextPoint);

		// add the barcode to the map 
		int value = getPilot().readBarcode();
		Barcode barcode = new Barcode(getPilot().getMapGraphConstructed().getTile(getPilot().getMatrixPosition()), value, getPilot().getOrientation());
		getPilot().getMapGraphConstructed().addContentToCurrentTile(getPilot().getMatrixPosition(), barcode);

		getPilot().alignOnWalls();

		// execute the action the barcode implies
		executeBarcode(value);

		getPilot().setBusyExecutingBarcode(false);
		getPilot().startReadingBarcodes();
	}

	/**
	 * Change the next tile into a dead-end, mark it as explored and place a treasure on it
	 * @param value	the value of the treasure
	 */
	private void treasureFound(int value)
	{
		// change the next tile into a dead end and mark the tile as explored
		Point nextPoint = getPilot().getOrientation().getNext(getPilot().getMatrixPosition());
		for(Orientation orientation: Orientation.values())
			if(orientation != getPilot().getOrientation().getOppositeOrientation())
				getPilot().getMapGraphConstructed().addObstruction(nextPoint, orientation, Obstruction.WALL);
		getPilot().getMapGraphConstructed().getTile(nextPoint).setMarkingExploreMaze(true);

		// add the found treasure to the map
		TreasureObject treasure = new TreasureObject(getPilot().getMapGraphConstructed().getTile(nextPoint), value);
		getPilot().getMapGraphConstructed().addContentToCurrentTile(nextPoint, treasure);
	}
}
