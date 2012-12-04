package communication;

import commands.Command;

import gui.SilverSurferGUI;

public class BarDecoder {
	
	private Communicator communicator;
	
	public BarDecoder(Communicator communicator) {
		this.communicator = communicator;
	}
	
	public void decode(int value) {
		try {
			switch(value) {
				case(5):
					communicator.moveTurn(0, -360, 0);
					break;
				case(40):
					communicator.moveTurn(0, -360, 0);
					break;
				case(9):
					communicator.moveTurn(0, 360, 0);
					break;
				case(36):
					communicator.moveTurn(0, 360, 0);
					break;
				case(15):
					communicator.sendCommand(Command.PLAY_SONG);
					break;
				case(60):
					communicator.sendCommand(Command.PLAY_SONG);
					break;
				case(19):
					Thread.sleep(5000);
					break;
				case(50):
					Thread.sleep(5000);
					break;
				case(25):  //25   
					SilverSurferGUI.changeSpeed(1);
					//communicator.getExplorer().setCheckTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getCurrentTile());
				
	            	break;
				//case(26):
					//communicator.getExplorer().setEndTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getCurrentTile());

				//	break;
				case(38):	//38
					SilverSurferGUI.changeSpeed(1);
	            	break;
				case(37):	//37
					SilverSurferGUI.changeSpeed(3);
	            	break;
				case(41):	//41
					SilverSurferGUI.changeSpeed(3);
	            	break;
				case(13):            
					communicator.getExplorer().setCheckTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getCurrentTile());
					break;
				case(44):       
					communicator.getExplorer().setCheckTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getCurrentTile());
					break;
				case(55):            
					communicator.getExplorer().setEndTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getCurrentTile());
	            	break;
				case(59):       
					communicator.getExplorer().setEndTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getCurrentTile());
					break;
				default:
					break;
			}
		} catch(Exception e) {
			System.out.println("Error in BarDecoder.decode()!");
		}
	}
}