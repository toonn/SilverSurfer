package simulator.pilot;

import java.awt.Point;

import javax.print.attribute.standard.Sides;

import mapping.Barcode;
import mapping.Obstruction;
import mapping.Orientation;
import mapping.TreasureObject;

import commands.BarcodeCommand;

/**
 * A class of actions which AbstractPilots can undertake.
 * @author Beheerder
 *
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
	 * @pre	:	Right after an allignOnWall() and allignOnWhiteLine()
	 * 			(the robot is standing on the white line right behind the barcode)
	 */
	private void pickUpItem(int barcode) {
		getPilot().stopReadingBarcodes();
		System.out.println("Robot " + getPilot().getTeamNumber() + ": pickup");
		
		// the last bit of the barcode matches the new team; the pilot is given its team
		int i = 0;
		if(barcode % 2 == 1)
		{
			i = 1;
		}
		getPilot().setTeamNumber(4 + i);
		
		// TODO: travel en rotate interferreertmet het explore-algoritme!! het verennnen loopt dan mis
//		getPilot().travel(getPilot().sizeTile());
//		getPilot().getMessageCenter().sendMessage("Demo1Silver", "demo.silver", "Object picked up!");
//		getPilot().travel(-getPilot().sizeTile()/4);
//		getPilot().rotate(180);
//		getPilot().travel(getPilot().sizeTile() * (1+(3/4))); //17cm tot vorige tegel, 12 tot barcode, 16cm barcode, 5cm speling.
//		getPilot().alignOnWhiteLine();
//		getPilot().travel(getPilot().sizeTile() * 29/40); // waarom is dit nodig?
		
		getPilot().startReadingBarcodes();
	}

	/**
	 * @pre	:	Right after an allignOnWall() and allignOnWhiteLine()
	 * 			(the robot is standing on the white line right behind the barcode)
	 */
	private void doNotPickUpItem() {
		getPilot().stopReadingBarcodes();
		System.out.println("Robot " + getPilot().getTeamNumber() + ": not pickup");
		
		// TODO: travel en rotate interferreert met het explore-algoritme!! het verennnen loopt dan mis
//		getPilot().rotate(180);
//		getPilot().alignOnWhiteLine();
//		getPilot().travel(24);
		
		getPilot().startReadingBarcodes();
	}
	
	/**
	 * Decides what should be done when a given barcode is found
	 * @param barcode
	 */
	public void executeBarcode(int barcode) {
		if(barcode != -1)
		{			
			//TODO: alle andere mogelijkheden moeten hiervoor komen (bvb wip) zodat 'kijken of het het voorwerp is' als laatste wordt gedaan
			
			// seesaw (wip)
			if(barcode == BarcodeCommand.SEESAW || barcode == BarcodeCommand.SEESAW_INVERSE)
			{
				
			}
			// if no other barcode, the barcode must represent a treasure
			else
			{
				treasureFound(barcode/2);

				// the first bits of the barcode match the teamnumer and the pilot does not yet know what team it is in
				if(barcode/2 == getPilot().getTeamNumber() && getPilot().getTeamNumber() < 5)
				{
					pickUpItem(barcode);
					
				}
				// a treasure is found, but not he right one
				else
				{
					doNotPickUpItem();
				}
			}
			
		}
		// else the barcode has not been read right and will not be executed
	}
	
	/**
	 * Change the current tile into a dead-end, mark it as explored and place a barcode on the tile.
	 * Allign on walls and on the white line behind the barcode, then execute the barcode.
	 */
	public void barcodeFound()
	 {
		 getPilot().stopReadingBarcodes();
		 
		 // change the current tile into a straight and mark it explored
		 for(Orientation orientation: Orientation.values())
			{
				if(orientation != getPilot().getOrientation()
					&&	orientation != getPilot().getOrientation().getOppositeOrientation())
				{
					getPilot().getMapGraphConstructed().addObstruction(getPilot().getMatrixPosition(), orientation, Obstruction.WALL);
				}
			}
		 getPilot().getMapGraphConstructed().getTile(getPilot().getMatrixPosition()).setMarkingExploreMaze(true);
		 
		 // add the tile at the end of the straight to the map
		 Point nextPoint = getPilot().getOrientation().getNext(getPilot().getMatrixPosition());
		 getPilot().getMapGraphConstructed().addTileXY(nextPoint);
		 
		 // add the barcode to the map 
		 int value = getPilot().readBarcode();
		 Barcode barcode = new Barcode(getPilot().getMapGraphConstructed().getTile(getPilot().getMatrixPosition()), value, getPilot().getOrientation());
		 getPilot().getMapGraphConstructed().addContentToCurrentTile(getPilot().getMatrixPosition(), barcode);
		 
		 // allign
		 // TODO: maakt de exploreThread() in de war!
		 // getPilot().alignOnWalls();
		 // getPilot().alignOnWhiteLine();
		 
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
		{
			if(orientation != getPilot().getOrientation().getOppositeOrientation())
			{
				getPilot().getMapGraphConstructed().addObstruction(nextPoint, orientation, Obstruction.WALL);
			}
		}
		getPilot().getMapGraphConstructed().getTile(nextPoint).setMarkingExploreMaze(true);
		
		// add the found treasure to the map
		TreasureObject treasure = new TreasureObject(getPilot().getMapGraphConstructed().getTile(nextPoint), value);
		getPilot().getMapGraphConstructed().addContentToCurrentTile(nextPoint, treasure);
	}
}
