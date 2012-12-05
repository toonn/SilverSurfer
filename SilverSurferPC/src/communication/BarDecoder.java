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
			int xCoordinate;
			int yCoordinate;
			switch(value) {
				case(13):	//5
					communicator.moveTurn(0, -360, 0);
					break;
				case(44):	//40
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
	            	break;
				case(38):	//38
					SilverSurferGUI.changeSpeed(1);
	            	break;
				case(37):	//37
					SilverSurferGUI.changeSpeed(3);
	            	break;
				case(41):	//41
					SilverSurferGUI.changeSpeed(3);
	            	break;
				case(5):	//13
					System.out.println("start found");
					//xCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeX();
					//yCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeY();
					communicator.getExplorer().setCheckTileFound(true);
					//communicator.getExplorer().setCheckTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates(xCoordinate, yCoordinate));
					break;
				case(40):    //44   
					System.out.println("start found");
					//xCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeX();
					//yCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeY();
					communicator.getExplorer().setCheckTileFound(true);
					//communicator.getExplorer().setCheckTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates(xCoordinate, yCoordinate));
					break;
				case(10):    //55    
					System.out.println("end found");    
					//xCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeX();
					//yCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeY();
					communicator.getExplorer().setEndTileFound(true);
					//communicator.getExplorer().setEndTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates(xCoordinate, yCoordinate));
					break;
				case(20):    //59   
					System.out.println("end found");
					//xCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeX();
					//yCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeY();
					communicator.getExplorer().setEndTileFound(true);
					//communicator.getExplorer().setEndTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates(xCoordinate, yCoordinate));
					break;
				default:
					break;
			}
		} catch(Exception e) {
			System.out.println("Error in BarDecoder.decode()!");
		}
	}
}