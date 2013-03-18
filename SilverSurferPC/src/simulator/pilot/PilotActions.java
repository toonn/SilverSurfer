package simulator.pilot;

import java.awt.Point;

import mapping.Barcode;
import mapping.Obstruction;
import mapping.Orientation;
import mapping.Seesaw;
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
					seesawFound(BarcodeCommand.SEESAW_END[i], i, false);
				}
				else if(barcode == BarcodeCommand.SEESAW_END[i])
				{
					seesawFound(BarcodeCommand.SEESAW_START[i], i, true);
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
	 * @param barcode, the value of the barcode on the other side of the seesaw
	 * @param value, the value of the seesaw
	 * @param right, true if the seesaw lies partitally to the left of this tile (given the orientation of the robot)
	 */
	private void seesawFound(int otherBarcode, int value, boolean right) {
		System.out.println("Robot " + getPilot().getTeamNumber() + ": seesaw " + value);

		// add the four next tiles to the map (the tile on nextPoint1 is allready added by addBarcode())
		Point nextPoint1 = getPilot().getOrientation().getNext(getPilot().getMatrixPosition());
		Point nextPoint2 = getPilot().getOrientation().getNext(nextPoint1);
		Point nextPoint3 = getPilot().getOrientation().getNext(nextPoint2);
		Point nextPoint4 = getPilot().getOrientation().getNext(nextPoint3);
		getPilot().getMapGraphConstructed().addTileXY(nextPoint2);
		getPilot().getMapGraphConstructed().addTileXY(nextPoint3);
		getPilot().getMapGraphConstructed().addTileXY(nextPoint4);
		
		// add the other two seesaw-tiles to the map
		Orientation sideOrientation = getPilot().getOrientation().getOtherOrientationCorner();
		if(right)
		{
			sideOrientation = sideOrientation.getOppositeOrientation();
		}
		Point sidePoint1 = sideOrientation.getNext(nextPoint1);
		getPilot().getMapGraphConstructed().addTileXY(sidePoint1);
		Point sidePoint2 = sideOrientation.getNext(nextPoint2);
		getPilot().getMapGraphConstructed().addTileXY(sidePoint2);
		
		// add the seesaw to the seesaw-tiles
		Seesaw seesaw1 = new Seesaw(getPilot().getMapGraphConstructed().getTile(nextPoint1), value);
		Seesaw seesaw2 = new Seesaw(getPilot().getMapGraphConstructed().getTile(nextPoint2), value);
		Seesaw seesaw3 = new Seesaw(getPilot().getMapGraphConstructed().getTile(sidePoint1), value);
		Seesaw seesaw4 = new Seesaw(getPilot().getMapGraphConstructed().getTile(sidePoint2), value);
		getPilot().getMapGraphConstructed().addContentToCurrentTile(nextPoint1, seesaw1);
		getPilot().getMapGraphConstructed().addContentToCurrentTile(nextPoint2, seesaw2);
		getPilot().getMapGraphConstructed().addContentToCurrentTile(sidePoint1, seesaw3);
		getPilot().getMapGraphConstructed().addContentToCurrentTile(sidePoint2, seesaw4);
		
		// add the right edges to the seesaw tiles and mark as explored
		Obstruction closeObstruction = Obstruction.SEESAW_DOWN;
		Obstruction farObstruction = Obstruction.SEESAW_UP;
		if(getPilot().getInfraRedSensorValue() > 40 && getPilot().getInfraRedSensorValue() < 60)
		{
			closeObstruction = Obstruction.SEESAW_UP;
			farObstruction = Obstruction.SEESAW_DOWN;
		}
		getPilot().getMapGraphConstructed().getTile(nextPoint1).getEdge(getPilot().getOrientation()).replaceObstruction(Obstruction.SEESAW_FLIP);
		getPilot().getMapGraphConstructed().getTile(nextPoint1).getEdge(getPilot().getOrientation().getOppositeOrientation()).replaceObstruction(closeObstruction);
		getPilot().getMapGraphConstructed().getTile(nextPoint1).getEdge(sideOrientation.getOppositeOrientation()).replaceObstruction(Obstruction.WALL);
		getPilot().getMapGraphConstructed().getTile(nextPoint1).setMarkingExploreMaze(true);
		
		getPilot().getMapGraphConstructed().getTile(sidePoint1).getEdge(getPilot().getOrientation()).replaceObstruction(Obstruction.SEESAW_FLIP);
		getPilot().getMapGraphConstructed().getTile(sidePoint1).getEdge(sideOrientation).replaceObstruction(Obstruction.WALL);
		getPilot().getMapGraphConstructed().getTile(sidePoint1).getEdge(getPilot().getOrientation().getOppositeOrientation()).replaceObstruction(Obstruction.WALL);
		getPilot().getMapGraphConstructed().getTile(sidePoint1).setMarkingExploreMaze(true);
		
		getPilot().getMapGraphConstructed().getTile(nextPoint2).getEdge(getPilot().getOrientation()).replaceObstruction(farObstruction);
		getPilot().getMapGraphConstructed().getTile(nextPoint2).getEdge(getPilot().getOrientation().getOppositeOrientation()).replaceObstruction(Obstruction.SEESAW_FLIP);
		getPilot().getMapGraphConstructed().getTile(nextPoint2).getEdge(sideOrientation.getOppositeOrientation()).replaceObstruction(Obstruction.WALL);
		getPilot().getMapGraphConstructed().getTile(nextPoint2).setMarkingExploreMaze(true);
		
		getPilot().getMapGraphConstructed().getTile(sidePoint2).getEdge(getPilot().getOrientation()).replaceObstruction(Obstruction.WALL);
		getPilot().getMapGraphConstructed().getTile(sidePoint2).getEdge(sideOrientation).replaceObstruction(Obstruction.WALL);
		getPilot().getMapGraphConstructed().getTile(sidePoint2).getEdge(getPilot().getOrientation().getOppositeOrientation()).replaceObstruction(Obstruction.SEESAW_FLIP);
		getPilot().getMapGraphConstructed().getTile(sidePoint2).setMarkingExploreMaze(true);
		
		// change the third tile into a straight, add the other barcode and mark as explored
		for(Orientation orientation: Orientation.values())
			if(orientation != getPilot().getOrientation() && orientation != getPilot().getOrientation().getOppositeOrientation())
				getPilot().getMapGraphConstructed().addObstruction(nextPoint3, orientation, Obstruction.WALL);
		getPilot().getMapGraphConstructed().addObstruction(nextPoint3, getPilot().getOrientation(), farObstruction);
		Barcode barcode = new Barcode(getPilot().getMapGraphConstructed().getTile(nextPoint3), otherBarcode, getPilot().getOrientation());
		getPilot().getMapGraphConstructed().addContentToCurrentTile(nextPoint3, barcode);
		getPilot().getMapGraphConstructed().getTile(nextPoint3).setMarkingExploreMaze(true);
	}

	/**
	 * Change the current tile into a dead-end, mark it as explored and place a barcode on the tile.
	 * Allign on walls and on the white line behind the barcode, then execute the barcode.
	 */
	public void barcodeFound() {
		getPilot().stopReadingBarcodes();

		// change the current tile into a straight and mark it explored
		for(Orientation orientation: Orientation.values())
			if(orientation != getPilot().getOrientation() && orientation != getPilot().getOrientation().getOppositeOrientation())
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
