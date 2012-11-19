package communication;

import gui.SilverSurferGUI;

public class BarDecoder {

	private SilverSurferGUI SSG;
	private UnitCommunicator unitCommunicator;
	
	public BarDecoder(SilverSurferGUI SSG, UnitCommunicator unitCommunicator) {
		this.SSG = SSG;
		this.unitCommunicator = unitCommunicator;
	}
	
	public void decode(int value) {
		try {
			switch(value) {
				case(5):
					unitCommunicator.moveTurn(-360, 0);
					break;
				case(40):
					unitCommunicator.moveTurn(-360, 0);
					break;
				case(9):
					unitCommunicator.moveTurn(360, 0);
					break;
				case(36):
					unitCommunicator.moveTurn(360, 0);
					break;
				case(15):
					unitCommunicator.playSong();
					break;
				case(60):
					unitCommunicator.playSong();
					break;
				case(19):
					Thread.sleep(5000);
					break;
				case(50):
					Thread.sleep(5000);
					break;
				case(25):    
					SSG.changeSpeed(1);
	            	break;
				case(38):    
					SSG.changeSpeed(1);
	            	break;
				case(37):
					SSG.changeSpeed(4);
	            	break;
				case(41):
					SSG.changeSpeed(4);
	            	break;
				case(55):            
					//TODO: finish
	            	break;
				case(59):       
					//TODO: finish
					break;
				default:
					break;
			}
		} catch(Exception e) {
			
		}
	}
}