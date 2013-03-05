package simulator.pilot;

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
}
