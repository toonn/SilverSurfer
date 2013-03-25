package simulator.pilot;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import peno.htttp.GameHandler;
import peno.htttp.PlayerHandler;

import mapping.MapGraph;
import mapping.Obstruction;
import mapping.Orientation;
import mapping.Tile;
import mazeAlgorithm.ExploreThread;
import mq.communicator.MQCenter;

public abstract class AbstractPilot implements PilotInterface {

	private int playerNumber = -1;
	private int teamNumber = -1;
	private MapGraph mapGraphConstructed;
	private Point2D.Double position;
	private double angle;
	private int speed;
	private boolean busyExecutingBarcode = false;
	protected boolean readBarcodes = true;
	protected boolean permaBarcodeStop = false;
	protected PilotActions pilotActions = new PilotActions(this);
	private ExploreThread exploreThread;
    private PlayerHandler handler;
    private Vector<Tile> seesawBarcodeTiles = new Vector<Tile>();
	private Boolean gameOn;
	private MQCenter center;
	protected final double detectionDistanceUltrasonicSensorRobot = 26;
	private boolean teamMemberFound = false;
	private int teamMemberPlayerNumber;
	private Tile startingPositionOfTeamMember;

	public AbstractPilot(int playerNumber) {
		if(playerNumber < 0 || playerNumber > 3)
			this.playerNumber = -1;
		else
			this.playerNumber = playerNumber;
		position = new Point2D.Double(sizeTile() / 2, sizeTile() / 2);
		reset();
	}
	
	public Tile getStartingPositionOfTeamMember() {
		return startingPositionOfTeamMember;
	}
	
	public boolean getTeamMemberFound() {
		return teamMemberFound;
	}
	
	public int getTeamMemberPlayerNumber() {
		return teamMemberPlayerNumber;
	}
	
	public void setTeamMemberFound(int teamMemberPlayerNumber) {
		this.teamMemberFound = true;
		this.teamMemberPlayerNumber = teamMemberPlayerNumber;
	}
	
	public MQCenter getCenter() {
		return center;
	}
	
	public Vector<Tile> getSeesawBarcodeTiles() {
		return seesawBarcodeTiles;
	}
	
	public void shuffleSeesawBarcodeTiles() {
		Collections.shuffle(seesawBarcodeTiles);
	}

	@Override
	public double sizeTile() {
		return 40;
	}

	/**
	 * Returns 0,1,2 or 3 indicating which treasure the pilot is looking for
	 */
	@Override
	public int getPlayerNumber() {
		return playerNumber;
	}
	
	@Override
    public void setPlayerNumber(int playerNumber) {
    	this.playerNumber = playerNumber;
    }
	
	/**
	 * Returns 0 or 1, indicating what team the robot is on.
	 * Returns -1 when the team is not yet known.
	 */
	@Override
	public int getTeamNumber() {
		return teamNumber;
	}

	/**
	 * Set teamNumber to 0 or 1. Other values are not excepted.
	 */
	@Override
	public void setTeamNumber(int teamNumber) {
		if(teamNumber < 0 && teamNumber > 1)
			throw new IllegalStateException("The teamnumber can only be set to 4 or 5!");
		else
			this.teamNumber = teamNumber;
	}

	@Override
	public MapGraph getMapGraphConstructed() {
		return mapGraphConstructed;
	}

	@Override
	public Point2D.Double getPosition() {
		return position;
	}

	@Override
	public Point getMatrixPosition() {
		return new Point((int)Math.floor(getPosition().getX() / sizeTile()),
				(int)Math.floor(getPosition().getY() / sizeTile()));
	}

	/**
	 * Check if this Pilot is in gameModus (MQ is activated).
	 */
	public boolean isInGameModus() {
		return gameOn;
	}
	
	/**
	 * Set this Pilot in it's gameModus.
	 */
	public void setGameModus(boolean onOff){
		this.gameOn = onOff;
	}
	
	@Override
	public void setPosition(final double x, final double y) {
		position.setLocation(x, y);
	}

	@Override
	public double getAngle() {
		return angle;
	}

	@Override
	public void setAngle(final double angle) {
		if (angle > 360)
			this.angle = angle - 360;
		else if (angle < 0)
			this.angle = angle + 360;
		else
			this.angle = angle;
	}

	@Override
	public void reset() {
		teamNumber = -1;
		angle = 270;
		speed = 2;
		mapGraphConstructed = new MapGraph();
		mapGraphConstructed.addTile(getMatrixPosition());
	}

	public abstract String getConsoleTag();

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public Orientation getOrientation() {
		return Orientation.calculateOrientation(getAngle());
	}

	public abstract int getLightSensorValue();

	public abstract int getUltraSensorValue();

	public abstract int getInfraRedSensorValue();

	protected boolean checkForObstruction() {
		if (getUltraSensorValue() < detectionDistanceUltrasonicSensorRobot)
			return true;
		return false;
	}

	public void setObstructionOrTile() {
		final Orientation currentOrientation = Orientation.calculateOrientation(getAngle());
		if (checkForObstruction())
			getMapGraphConstructed().getTile(getMatrixPosition())
			.getEdgeAt(currentOrientation)
			.setObstruction(Obstruction.WALL);
		else {
			getMapGraphConstructed().getTile(getMatrixPosition())
			.getEdgeAt(currentOrientation)
			.setObstruction(Obstruction.WHITE_LINE);
			Point nextPoint = currentOrientation.getNext(getMatrixPosition());
			if(mapGraphConstructed.getTile(nextPoint) == null)
				getMapGraphConstructed().addTile(nextPoint);
		}
	}

	protected boolean pointOnEdge(final double x, final double y) {
		double edgeMarge = 1.2;
		return (x % sizeTile()) > sizeTile() - edgeMarge
		|| (x % sizeTile()) < edgeMarge
		|| (y % sizeTile()) > sizeTile() - edgeMarge
		|| (y % sizeTile()) < edgeMarge;
	}

	public void alignOnWhiteLine() {
		travel(20);
		rotate(-90);
		rotate(90);
		travel(20);
	}

	public void alignOnWalls() {
		rotate(90);
		rotate(-90);
		rotate(-90);
		rotate(90);
	}

	public void travel(final double distance) {
		double currentX = getPosition().getX();
		double currentY = getPosition().getY();
		double x;
		double y;
		Orientation travelOrientation = Orientation.calculateOrientation(getAngle());
		if (distance < 0)
			travelOrientation = travelOrientation.getOppositeOrientation();
		for (int i = 1; i <= Math.abs(distance); i++) {
			if (travelOrientation == Orientation.NORTH) {
				x = currentX;
				y = currentY - i;
			}
			else if (travelOrientation == Orientation.SOUTH) {
				x = currentX;
				y = currentY + i;
			}
			else if (travelOrientation == Orientation.EAST) {
				x = currentX + i;
				y = currentY;
			}
			else {
				x = currentX - i;
				y = currentY;
			}
			setPosition(x, y);
			try {
				Thread.sleep(getTravelSleepTime());
			} catch (final InterruptedException e) {

			}
		}
	}

	private int getTravelSleepTime() {
		switch (speed) {
		case -1:
			return 50;
		case 1:
			return 10;
		case 2:
			return 7;
		case 3:
			return 5;
		case 4:
			return 3;
		}
		return 1;
	}

	public void rotate(final double alpha) {
		double angle = getAngle();
		for (int i = 1; i <= Math.abs(alpha); i++) {
			if (alpha >= 0)
				setAngle(angle + i);
			else
				setAngle(angle - i);
			try {
				Thread.sleep(getRotateSleepTime());
			} catch (Exception e) {

			}
		}
	}

	private int getRotateSleepTime() {
		return 5 - speed;
	}

	protected abstract int readBarcode();

	public boolean isExecutingBarcode() {
		return busyExecutingBarcode;
	}

	public void setBusyExecutingBarcode(boolean busy) {
		busyExecutingBarcode = busy;
	}

	public void setReadBarcodes(boolean readBarcodes) {
		this.readBarcodes = readBarcodes;
	}

	public boolean getPermaStopReadingBarcodes() {
		return permaBarcodeStop;
	}

	public void permaStopReadingBarcodes() {
		permaBarcodeStop = true;
	}

	public void startExploring() {
		exploreThread = new ExploreThread(mapGraphConstructed.getTile(getMatrixPosition()), this);
		exploreThread.start();
	}

	public void stopExploring() {
		if(exploreThread != null && exploreThread.isAlive())
			exploreThread.quit();
	}

    @Override
    public GameHandler getDefaultHandler() {
        return this.handler;
    }

    public void setupForGame(){
    	if (isInGameModus()){
    		try {
    			this.center = new MQCenter(this, "SILVER" + getPlayerNumber());
				getCenter().join();
				getCenter().setReady(true);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    protected abstract boolean crashImminent();
}