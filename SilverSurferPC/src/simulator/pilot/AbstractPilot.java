package simulator.pilot;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.security.AllPermission;
import java.util.HashSet;
import java.util.Set;

import commands.BarcodeCommand;

import mapping.Barcode;
import simulator.viewport.SimulatorPanel;
import mapping.MapGraph;
import mapping.Obstruction;
import mapping.Orientation;
import mazeAlgorithm.MazeExplorer;
import mq.communicator.MessageCenter;

public abstract class AbstractPilot implements PilotInterface {

	private Point2D.Double position = new Point2D.Double(sizeTile() / 2,
			sizeTile() / 2);
	private double angle = 270;
	protected int speed = 10;
	private int teamNumber;
	private MapGraph mapGraphConstructed;
	private SimulatorPanel simulatorPanel;
	private MessageCenter messageCenter;
	private boolean readBarcodes = true;
	private boolean busyExecutingBarcode = false;
	private boolean permaBarcodeStop = false;
	protected PilotActions pilotActions = new PilotActions(this);

	protected final double lengthOfRobot = 24;
	protected final double widthOfRobot = 26;
	protected final double lightSensorDistanceFromAxis = 7.5;
	protected final double ultrasonicSensorDistanceFromAxis = 5.5;
	protected final double detectionDistanceUltrasonicSensorRobot = 28;

	public AbstractPilot(int teamNumber) {
		this.teamNumber = teamNumber;
		mapGraphConstructed = new MapGraph();
		mapGraphConstructed.addTileXY(new Point(0, 0));

		try {
			messageCenter = new MessageCenter(this);
		} catch (Exception e) {
			System.out.println("MessageCenter problem!");
		}
	}

	/**
	 * Returns 0,1,2 or 3 indicating which treasure the pilot is looking for
	 * Returns 4 or 5 when the treasure is found and the pilot knows what team it is in
	 * Returns -1 if no valid team number is available
	 */
	@Override
	public int getTeamNumber() {
		if(teamNumber >= 0 && teamNumber <= 5)
			return teamNumber;
		return -1;
	}

	/**
	 * The team number can only change when a robot has found its treasure and knows what team it is in
	 * This means the team number can only be set to 4 or 5
	 */
	public void setTeamNumber(int teamNumber) {
		if(teamNumber != 4 && teamNumber != 5)
			System.out.println("the teamnumber can only be set to 4 or 5");
		else
			this.teamNumber = teamNumber;
	}

	@Override
	public Point2D.Double getPosition() {
		return position;
	}

	public void setPosition(final double x, final double y) {
		position.setLocation(x, y);
	}

	public Point getMatrixPosition() {
		return toMatrixPosition(getPosition());
	}

	public Point toMatrixPosition(Point2D.Double point) {
		return new Point((int) (point.getX() / sizeTile()),
				(int) (point.getY() / sizeTile()));
	}

	@Override
	public double getAngle() {
		return angle;
	}

	public void setAngle(final double angle) {
		if (angle > 360)
			this.angle = angle - 360;
		else if (angle < 0)
			this.angle = angle + 360;
		else
			this.angle = angle;
	}

	@Override
	public double sizeTile() {
		return 40;
	}

	public int getSpeed() {
		if (speed == 48)
			return 4;
		else if (speed == 58)
			return 3;
		else if (speed == 86)
			return 2;
		else
			return 1;
	}

	public void setSpeed(int speed) {
		if (speed == 4)
			this.speed = 48;
		else if (speed == 3)
			this.speed = 58;
		else if (speed == 2)
			this.speed = 86;
		else
			this.speed = 194;
	}

	public MapGraph getMapGraphLoaded() {
		// TODO: change back (piloot mag niet aan simulatorpanel)
		return simulatorPanel.getMapGraphLoaded();
	}

	@Override
	public MapGraph getMapGraphConstructed() {
		return mapGraphConstructed;
	}

	public void setSimulatorPanel(SimulatorPanel simulatorPanel) {
		this.simulatorPanel = simulatorPanel;
	}

	@Override
	public boolean isRobotControllable() {
		return true;
	}

	public MessageCenter getMessageCenter() {
		return messageCenter;
	}

	public abstract void recieveMessage(String message);

	public abstract String getConsoleTag();

	@Override
	public void reset() {
		angle = 270;
		speed = 10;
		mapGraphConstructed = new MapGraph();
		mapGraphConstructed.addTileXY(getMatrixPosition());
	}

	public Orientation getOrientation() {
		return Orientation.calculateOrientation(getAngle());
	}

	public double[] getLightSensorCoordinates() {
		final double[] coordinates = new double[2];
		coordinates[0] = (getPosition().getX() + lightSensorDistanceFromAxis
                * Math.cos(Math.toRadians(this.getAngle())));
		coordinates[1] = (getPosition().getX() + lightSensorDistanceFromAxis
                * Math.sin(Math.toRadians(this.getAngle())));
		return coordinates;
	}

	public abstract int getLightSensorValue();

	public double[] getUltrasonicSensorCoordinates() {
		final double[] coordinates = new double[2];
		coordinates[0] = (getPosition().getX() - ultrasonicSensorDistanceFromAxis
                * Math.cos(Math.toRadians(this.getAngle())));
		coordinates[1] = (getPosition().getX() - ultrasonicSensorDistanceFromAxis
                * Math.sin(Math.toRadians(this.getAngle())));
		return coordinates;
	}

	public abstract int getUltraSensorValue();

	protected boolean checkForObstruction() {
		if (getUltraSensorValue() < detectionDistanceUltrasonicSensorRobot)
			return true;
		return false;
	}

	public void setObstructionOrTile() {
		final Orientation currentOrientation = Orientation
				.calculateOrientation(getAngle());
		if (checkForObstruction())
			getMapGraphConstructed().getTile(getMatrixPosition())
			.getEdge(currentOrientation)
			.setObstruction(Obstruction.WALL);
		else {
			Point nextPoint = currentOrientation.getNext(getMatrixPosition());
			if (mapGraphConstructed.getTile(nextPoint) == null)
				getMapGraphConstructed().addTileXY(nextPoint);
		}
	}

	/**
	 * checkt of het punt zich binnen de marge van een edge bevindt
	 */
	 protected boolean pointOnEdge(final double x, final double y) {
		double edgeMarge = 1.2;
		return (x % sizeTile()) > sizeTile() - edgeMarge
				|| (x % sizeTile()) < edgeMarge
				|| (y % sizeTile()) > sizeTile() - edgeMarge
				|| (y % sizeTile()) < edgeMarge;
	}

	 public void alignOnWhiteLine() {		 
		 //TODO: express fouten brengen op simulator?
		 /*while (!pointOnEdge(getLightSensorCoordinates()[0],
				 getLightSensorCoordinates()[1]))
			 travel(1);
		 travel(5);
		 while (!pointOnEdge(getLightSensorCoordinates()[0],
				 getLightSensorCoordinates()[1]))
			 rotate(-1);
		 rotate(90);
		 int i = 0;
		 while (!pointOnEdge(getLightSensorCoordinates()[0],
				 getLightSensorCoordinates()[1])) {
			 rotate(1);
			 i++;
		 }
		 rotate(-(90 + i) / 2);*/
		 
		 travel(13);
		 travel(5);
		 rotate(-90);
		 rotate(180);
		 rotate(-90);
	 }

	 public void alignOnWalls() {
		 rotate(90);
		 if (getUltraSensorValue() < detectionDistanceUltrasonicSensorRobot && getUltraSensorValue() > 23)
			 while (!(getUltraSensorValue() <= 23))
				 travel(1);
		 rotate(-90);
		 rotate(-90);
		 if (getUltraSensorValue() < detectionDistanceUltrasonicSensorRobot
				 && getUltraSensorValue() > 23)
			 while (!(getUltraSensorValue() <= 23))
				 travel(1);
		 rotate(90);
	 }

	 protected abstract int getRotateSleepTime(double angle);

	 protected abstract int getTravelSleepTime(double distance);

	 public void rotate(final double alpha) {
		 double angle = getAngle();
		 for (int i = 1; i <= Math.abs(alpha); i++) {
			 if (alpha >= 0)
				 setAngle(angle + i);
			 else
				 setAngle(angle - i);
			 try {
				 Thread.sleep(getRotateSleepTime(alpha));
			 } catch (Exception e) {

			 }
		 }
	 }

	 public void travel(final double distance) {
		 double currentX = getPosition().getX();
		 double currentY = getPosition().getY();
		 double x;
		 double y;
		 Orientation travelOrientation = Orientation
				 .calculateOrientation(getAngle());
		 if (distance < 0)
			 travelOrientation = travelOrientation.getOppositeOrientation();
		 for (int i = 1; i <= Math.abs(distance); i++) {
			 if (travelOrientation == Orientation.NORTH) {
				 x = currentX;
				 y = currentY - i;
			 } else if (travelOrientation == Orientation.SOUTH) {
				 x = currentX;
				 y = currentY + i;
			 } else if (travelOrientation == Orientation.EAST) {
				 x = currentX + i;
				 y = currentY;
			 } else {
				 x = currentX - i;
				 y = currentY;
			 }
			 // TODO: niet door muren rijden (maar op betere manier dan
					 // hieronder, dit geeft errors)
			 /*
			  * if (getMapGraphLoaded() != null && robotOnEdge(x, y, getAngle()))
			  * { final Orientation edgeOrientation = pointOnWichSideOfTile(x, y,
			  * travelOrientation); if (travelOrientation == edgeOrientation &&
			  * !getMapGraphLoaded().getTile(getMatrixPosition())
			  * .getEdge(travelOrientation).isPassable()) {
			  * System.out.println("Er staat een muur in de weg"); return; } }
			  */
			 setPosition(x, y);
			 
			 try {
				 Thread.sleep(getTravelSleepTime(distance));
			 } catch (final InterruptedException e) {

			 }
		 }
		 if(readBarcodes && !(getMapGraphConstructed().getTile(getMatrixPosition()).getContent() instanceof Barcode)
					&& getLightSensorValue() < 40 && getLightSensorValue() > 10) {
			 setBusyExecutingBarcode(true);
			 pilotActions.barcodeFound();
		 }
	 }

	 protected abstract int readBarcode();
	 
	 public boolean isExecutingBarcode() {
		 return busyExecutingBarcode;
	 }

	 public void executeBarcode(int barcode) {
		 pilotActions.executeBarcode(barcode);
	 }

	 public void stopReadingBarcodes() {
		 // TODO Deze methode wil ik weg uit pilot.
		 readBarcodes = false;
	 }

	 public void startReadingBarcodes() {
		 // TODO Deze methode wil ik weg uit pilot.
		 readBarcodes = true;
	 }
	 
	 public void setBusyExecutingBarcode(boolean busy) {
		 busyExecutingBarcode = busy;
	 }

	 public void permaStopReadingBarcodes() {
		 // TODO Deze methode wil ik weg uit pilot.
		 permaBarcodeStop = true;
	 }

	 public void startExploring() {
		 new Thread() {
			 public void run() {
				 new MazeExplorer(
						 mapGraphConstructed.getTile(getMatrixPosition()),
						 AbstractPilot.this, true).startExploringMaze();
			 }
		 }.start();
	 }
}