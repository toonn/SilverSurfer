package communication;

import java.io.IOException;

import mapping.Orientation;
import mapping.Tile;

import commands.Command;

public class UnitCommunicator {

	protected static final double LENGTH_COEF = 20.8; //Amount of degrees needed for 1 cm forward in a polygon.
	protected static final double ANGLE_COEF = 708; //Amount of degrees needed for a 360 degree turn in a polygon.
	protected static final double ANGLE_COEF_TURN = 716; //Amount of degrees needed for a 360 degree turn.
	protected StatusInfoBuffer statusInfo;
	
	public UnitCommunicator(StatusInfoBuffer status) {
		setStatusInfo(status);
	}
	
	/**
	 * Establishes a connection with an unit.
	 * @throws IOException 
	 */
	public void openUnitConnection() throws IOException {}

	/**
	 * Closes the connection with the unit.
	 * @throws Exception 
	 */
	public void closeUnitConnection() throws Exception {}
	
	/**
	 * Sends a given command to the unit.
	 * @Pre   make sure you've connected to the unit first by using connectToUnit().
	 * @param command:	An integer that corresponds with a command in the commands.Command class.
	 * @throws IOException
	 */
	public void sendCommandToUnit(int command) throws IOException {}
		
	/**
	 * Sends commands to a unit u so that when u executes these commands,
	 * it completes a polygon-shaped track with 'amtOfAngles' angles and 'lengthInCM'-long sides.
	 * 
	 * @param amtOfAngles:	The amount of angles the polygon has.
	 * @param lengthInCM:	The length of the sides the polygon has.
	 * @throws IOException
	 */
	public void runPolygon(int amtOfAngles, int lengthInCM) throws IOException {}
	
	public void moveTurn(int amtOfAngles, int lengthInCM) throws IOException {}
	
	public int getSpeed() {
		return 0; 
	}
	
	public void setSpeed(int speed) {}
	
	public StatusInfoBuffer getStatusInfo() {
		return statusInfo;
	}
	
	public void setStatusInfo(StatusInfoBuffer statusInfo) {
		this.statusInfo = statusInfo;
	}
	
	public void goToNextTile(Orientation orientation, Tile previousTile) throws IOException{
		double currentAngle = getStatusInfo().getAngle();
		int angleToRotate = (int)(((double) orientation.getRightAngle() - currentAngle)*10);
		sendCommandToUnit(angleToRotate*10 + Command.AUTOMATIC_TURN_ANGLE);
		sendCommandToUnit(40*100 + Command.AUTOMATIC_MOVE_FORWARD);
		int xCoordinate = orientation.getArrayToFindNeighbourAbsolute()[0] + previousTile.getxCoordinate();
		int yCoordinate = orientation.getArrayToFindNeighbourAbsolute()[1] + previousTile.getyCoordinate();
		setNewTileInMap(xCoordinate, yCoordinate);
	}
	
	//TODO
	public void setNewTileInMap(int xCoordinate, int yCoordinate){
		
	}

	
	public String getConsoleTag() {
		return "";
	}
	
	public void playSong() {
		try {
			sendCommandToUnit(Command.PLAY_SONG);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}