package simulator.pilot;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import simulator.viewport.SimulatorPanel;

import mapping.MapGraph;
import mapping.Orientation;

public class DummyPilot implements PilotInterface {

	private Point2D.Double position;
	private double angle;
	private int teamNumber;
	private SimulatorPanel simulatorPanel;
	private MapGraph mapGraphConstructed;
	private boolean active = false;
	
	public DummyPilot(int teamNumber) {
		this.teamNumber = teamNumber;
		position = new Point2D.Double(sizeTile() / 2, sizeTile() / 2);
		reset();
	}

	@Override
	public Double getPosition() {
		return position;
	}

	@Override
	public double getAngle() {
		return angle;
	}

	@Override
	public MapGraph getMapGraphConstructed() {
		return mapGraphConstructed;
	}

	@Override
	public boolean isRobotControllable() {
		return false;
	}

	@Override
	public double sizeTile() {
		return 40;
	}

	@Override
	public void reset() {
		angle = 270;
		mapGraphConstructed = new MapGraph();
		mapGraphConstructed.addTileXY(getMatrixPosition());
	}

	@Override
	public Orientation getOrientation() {
		return Orientation.calculateOrientation(getAngle());
	}

	@Override
	public int getTeamNumber() {
		// TODO Auto-generated method stub
		return teamNumber;
	}

	@Override
	public boolean isExecutingBarcode() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setSimulatorPanel(SimulatorPanel simulatorPanel) {
		this.simulatorPanel = simulatorPanel;
	}
	
	public boolean isActive() {
		return active;
	}

	public Point getMatrixPosition() {
		return toMatrixPosition(getPosition());
	}

	public Point toMatrixPosition(Point2D.Double point) {
		return new Point((int) (point.getX() / sizeTile()),
				(int) (point.getY() / sizeTile()));
	}
}