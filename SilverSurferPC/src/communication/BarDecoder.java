package communication;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

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
			double scalingfactor = 1;
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
				case(13):    
					scalingfactor =  communicator.getSimulationPilot().getSSG().getSimulationPanel().getScalingfactor();
					communicator.getSimulationPilot().getSSG().getSimulationPanel().checkHighlight = new Rectangle(((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[0]*scalingfactor-20*scalingfactor)).intValue(),((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[1]*scalingfactor-20*scalingfactor)).intValue(),((Double)(40*scalingfactor)).intValue(),(((Double)(40*scalingfactor)).intValue()));
					xCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeX();
					yCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeY();;
					communicator.getExplorer().setCheckTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates(xCoordinate, yCoordinate));
					break;
				case(44):  
					scalingfactor = (int) communicator.getSimulationPilot().getSSG().getSimulationPanel().getScalingfactor();
					communicator.getSimulationPilot().getSSG().getSimulationPanel().checkHighlight = new Rectangle(((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[0]*scalingfactor-20*scalingfactor)).intValue(),((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[1]*scalingfactor-20*scalingfactor)).intValue(),((Double)(40*scalingfactor)).intValue(),(((Double)(40*scalingfactor)).intValue()));
					xCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeX();
					yCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeY();;
					communicator.getExplorer().setCheckTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates(xCoordinate, yCoordinate));
					break;
				case(55):    
					scalingfactor = (int) communicator.getSimulationPilot().getSSG().getSimulationPanel().getScalingfactor();
					communicator.getSimulationPilot().getSSG().getSimulationPanel().endHighlight = new Rectangle(((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[0]*scalingfactor-20*scalingfactor)).intValue(),((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[1]*scalingfactor-20*scalingfactor)).intValue(),((Double)(40*scalingfactor)).intValue(),(((Double)(40*scalingfactor)).intValue()));
					xCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeX();
					yCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeY();;
					communicator.getExplorer().setEndTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates(xCoordinate, yCoordinate));
					break;
				case(59):    
					scalingfactor = (int) communicator.getSimulationPilot().getSSG().getSimulationPanel().getScalingfactor();
					communicator.getSimulationPilot().getSSG().getSimulationPanel().endHighlight = new Rectangle(((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[0]*scalingfactor-20*scalingfactor)).intValue(),((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[1]*scalingfactor-20*scalingfactor)).intValue(),((Double)(40*scalingfactor)).intValue(),(((Double)(40*scalingfactor)).intValue()));
					xCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeX();
					yCoordinate = communicator.getSimulationPilot().getCurrentPositionRelativeY();;
					communicator.getExplorer().setEndTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates(xCoordinate, yCoordinate));
					break;
				default:
					break;
			}
		} catch(Exception e) {
			System.out.println("Error in BarDecoder.decode()!");
		}
	}
}