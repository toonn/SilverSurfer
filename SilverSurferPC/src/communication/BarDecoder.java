package communication;

import java.awt.Rectangle;

import commands.Command;

import gui.SilverSurferGUI;

public class BarDecoder {
	
	private Communicator communicator;
	
	public BarDecoder(Communicator communicator) {
		this.communicator = communicator;
	}
	
	public void decode(int value) {
		try {
			double scalingfactor = 1;
			switch(value) {
				case(5):	//5
					communicator.moveTurn(0, -360, 0);
					break;
				case(40):	//40
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
				case(13):	//13  //5
					scalingfactor =  communicator.getSimulationPilot().getSSG().getSimulationPanel().getScalingfactor();
					communicator.getSimulationPilot().getSSG().getSimulationPanel().checkHighlight = new Rectangle(((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[0]*scalingfactor-20*scalingfactor)).intValue(),((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[1]*scalingfactor-20*scalingfactor)).intValue(),((Double)(40*scalingfactor)).intValue(),(((Double)(40*scalingfactor)).intValue()));
					if(communicator.getRobotConnected())
						communicator.getExplorer().setCheckTileFound(true);
					else
						communicator.getExplorer().setCheckTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates(communicator.getSimulationPilot().getSSG().getCommunicator().getSimulationPilot().getCurrentPositionRelativeX(), communicator.getSimulationPilot().getSSG().getCommunicator().getSimulationPilot().getCurrentPositionRelativeY()));
					break;
				case(44):    //44   //40
					scalingfactor = (int) communicator.getSimulationPilot().getSSG().getSimulationPanel().getScalingfactor();
					communicator.getSimulationPilot().getSSG().getSimulationPanel().checkHighlight = new Rectangle(((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[0]*scalingfactor-20*scalingfactor)).intValue(),((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[1]*scalingfactor-20*scalingfactor)).intValue(),((Double)(40*scalingfactor)).intValue(),(((Double)(40*scalingfactor)).intValue()));
					if(communicator.getRobotConnected())
						communicator.getExplorer().setCheckTileFound(true);
					else
						communicator.getExplorer().setCheckTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates(communicator.getSimulationPilot().getSSG().getCommunicator().getSimulationPilot().getCurrentPositionRelativeX(), communicator.getSimulationPilot().getSSG().getCommunicator().getSimulationPilot().getCurrentPositionRelativeY()));
					break;
				case(55):    //55  //10
					scalingfactor = (int) communicator.getSimulationPilot().getSSG().getSimulationPanel().getScalingfactor();
					communicator.getSimulationPilot().getSSG().getSimulationPanel().endHighlight = new Rectangle(((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[0]*scalingfactor-20*scalingfactor)).intValue(),((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[1]*scalingfactor-20*scalingfactor)).intValue(),((Double)(40*scalingfactor)).intValue(),(((Double)(40*scalingfactor)).intValue()));
					if(communicator.getRobotConnected())
						communicator.getExplorer().setEndTileFound(true);
					else
						communicator.getExplorer().setEndTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates(communicator.getSimulationPilot().getSSG().getCommunicator().getSimulationPilot().getCurrentPositionRelativeX(), communicator.getSimulationPilot().getSSG().getCommunicator().getSimulationPilot().getCurrentPositionRelativeY()));
					break;
				case(59):    //59  //20
					scalingfactor = (int) communicator.getSimulationPilot().getSSG().getSimulationPanel().getScalingfactor();
					communicator.getSimulationPilot().getSSG().getSimulationPanel().endHighlight = new Rectangle(((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[0]*scalingfactor-20*scalingfactor)).intValue(),((Double)(communicator.getSimulationPilot().getCenterAbsoluteCurrentTile()[1]*scalingfactor-20*scalingfactor)).intValue(),((Double)(40*scalingfactor)).intValue(),(((Double)(40*scalingfactor)).intValue()));
					if(communicator.getRobotConnected())
						communicator.getExplorer().setEndTileFound(true);
					else
						communicator.getExplorer().setEndTile(communicator.getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates(communicator.getSimulationPilot().getSSG().getCommunicator().getSimulationPilot().getCurrentPositionRelativeX(), communicator.getSimulationPilot().getSSG().getCommunicator().getSimulationPilot().getCurrentPositionRelativeY()));
					break;
				default:
					break;
			}
		} catch(Exception e) {
			System.out.println("Error in BarDecoder.decode()!");
		}
	}
}