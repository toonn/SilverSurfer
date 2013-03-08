package simulator.pilot;

import commands.BarcodeCommand;

/**
 * A class of actions which AbstractPilots can undertake.
 * @author Beheerder
 *
 */
public class PilotActions {

	private AbstractPilot pilot;
	private boolean executingBarcode;

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
	 * @pre	:	Execute methode right after reading (and aligning) the right barcode.
	 */
	public void pickUpItem() {
		getPilot().stopReadingBarcodes();
		getPilot().alignOnWhiteLine();
		getPilot().travel(48);
		getPilot().getMessageCenter().sendMessage("Demo1Silver", "demo.silver", "Object picked up!");
		getPilot().travel(-10);
		getPilot().rotate(180);
		getPilot().travel(17+12+16+5); //17cm tot vorige tegel, 12 tot barcode, 16cm barcode, 5cm speling.
		getPilot().alignOnWhiteLine();
		getPilot().travel(29);
		getPilot().startReadingBarcodes();
	}

	public void doNotPickUpItem() {
		getPilot().stopReadingBarcodes();
		getPilot().rotate(180);
		getPilot().alignOnWhiteLine();
		getPilot().travel(24);
		getPilot().startReadingBarcodes();
	}

	public void executeBarcode(int barcode) {
		if(barcode != -1)
		{
			executingBarcode = true;
			
			//TODO: alle andere mogelijkheden moeten hiervoor komen (bvb wip) zodat 'kijken of het het voorwerp is' als laatste wordt gedaan
			
			// the first bits of the barcode match the teamnumer and the pilot does not yet know what team it is in
			if(barcode/2 == getPilot().getTeamNumber() && getPilot().getTeamNumber() < 5)
			{
				System.out.println("robot " + getPilot().getTeamNumber() + "vindt barcode " + barcode + " en actie: pickup");
				// TODO: zorg dat het voorwerp wordt opgeslagen op volgende tegel in MapGraphConstructed!
				// pickUpItem();
				
				// the last bit of the barcode matches the new team; the pilot is given its team
				int i = 0;
				if(barcode % 2 == 1)
				{
					i = 1;
				}
				getPilot().setTeamNumber(4 + i);
			}
			else
			{
				// TODO: zorg dat het voorwerp wordt opgeslagen op volgende tegel in MapGraphConstructed!
				System.out.println("robot " + getPilot().getTeamNumber() + "vindt barcode " + barcode + " en actie: not pickup");
				//doNotPickUpItem();
			}
			
//			if(barcode == BarcodeCommand.PICKUP_OBJECT || barcode == BarcodeCommand.PICKUP_OBJECT_INVERSE)
//			{
//				System.out.println("pickup");
//				// TODO: zorg dat het voorwerp wordt opgeslagen op volgende tegel in MapGraphConstructed!
//				//pickUpItem();
//			}
//			else
//			{
//				// TODO: zorg dat het voorwerp wordt opgeslagen op volgende tegel in MapGraphConstructed!
//				System.out.println("not pick up");
//				//doNotPickUpItem();
//			}
			executingBarcode = false;
		}
		// else the barcode has not been read right and will not be executed
	}
}
