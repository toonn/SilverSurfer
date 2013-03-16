package simulator.pilot;

import java.util.Random;

import mapping.Barcode;
import mapping.TreasureObject;
import mapping.Obstruction;
import mapping.Orientation;
import mapping.Tile;
import simulator.SimulationSensorData;

public class SimulationPilot extends AbstractPilot {

	public SimulationPilot(int teamNumber) {
		super(teamNumber);
	}
	
	@Override
	public void recieveMessage(String message) {
		System.out.println("Simulator -> Message: \""+message+"\" recieved.");
	}

    @Override
    public String getConsoleTag() {
        return "[SIMULATOR]";
    }

    @Override
    public int getLightSensorValue() {
        final Random random = new Random();
        double mean = 0;
        double standardDeviation = 1;
        final double[] coordinates = getLightSensorCoordinates();

        // check on which sort of underground your are standing
        // and adjust the mean and standardDeviation accordingly
        if (onEmptyTile(coordinates[0], coordinates[1])) {
            mean = SimulationSensorData.getMEmptyPanelLS();
            standardDeviation = SimulationSensorData.getSDEmptyPanelLS();
        } else if (onWhiteLine(coordinates[0], coordinates[1])) {
            mean = SimulationSensorData.getMWhiteLineLS();
            standardDeviation = SimulationSensorData.getSDWhiteLineLS();
        } else if (onBarcodeTile(coordinates[0], coordinates[1])) {
            final int color = getMapGraphLoaded()
                    .getTile(getMatrixPosition())
                    .getContent()
                    .getColorValue(coordinates[0] % sizeTile(),
                            coordinates[1] % sizeTile());
            mean = SimulationSensorData.getMBarcodeTileLS(color);
            standardDeviation = SimulationSensorData.getSDBarcodeTileLS(color);
        }
        return (int) Math.round(mean
                + (random.nextGaussian() * standardDeviation));
    }

    /**
	 * True if the robot is not on an edge, but on a tile without a content.
	 */
	private boolean onEmptyTile(final double x, final double y) {
	    return !pointOnEdge(x, y)
	            && (getMapGraphLoaded() == null || getMapGraphLoaded().getTile(
	                    getMatrixPosition()).getContent() == null);
	}

	/**
	 * True if the robot is on an edge and this edge is not a wall
	 */
	private boolean onWhiteLine(final double x, final double y) {
	    return pointOnEdge(x, y)
	            && (getMapGraphLoaded() == null || getMapGraphLoaded()
	                    .getObstruction(getMatrixPosition(),
	                            Orientation.calculateOrientation(getAngle())) != Obstruction.WALL);
	}

	/**
	 * True if the robot is not on an edge, but on a tile containing a barcode.
	 */
	private boolean onBarcodeTile(final double x, final double y) {
	    return !pointOnEdge(x, y)
	            && getMapGraphLoaded() != null
	            && (getMapGraphLoaded().getTile(getMatrixPosition())
	                    .getContent() instanceof Barcode);
	}
	
	protected int readBarcode()
	{
		if(getMapGraphLoaded().getTile(getMatrixPosition()).getContent() == null
				|| !(getMapGraphLoaded().getTile(getMatrixPosition()).getContent() instanceof Barcode))
		{
			return -1;
		}
		int value = getMapGraphLoaded().getTile(getMatrixPosition()).getContent().getValue();
		return value;
	}

	@Override
    public int getUltraSensorValue() {
        final Random random = new Random();
        final double mean = calculateDistanceToWall();
        final double standardDeviation = SimulationSensorData.getSDUS();

        return (int) Math.round(mean
                + (random.nextGaussian() * standardDeviation));
    }

    private double calculateDistanceToWall() {
	    if (getMapGraphLoaded() == null)
	        return 250;
	    double distanceToFirstEdge;
	    int amountOfTilesVisible;
	    Orientation orientation = getOrientation();
	    Tile tile = getMapGraphLoaded().getTile(getMatrixPosition());
	    // Berekent de afstand van de UltraSensor tot de eerste edge
	    if (orientation == Orientation.NORTH)
	        distanceToFirstEdge = getUltrasonicSensorCoordinates()[0]
	                % sizeTile();
	    else if (orientation == Orientation.SOUTH)
	        distanceToFirstEdge = sizeTile()
	                - (getUltrasonicSensorCoordinates()[0] % sizeTile());
	    else if (orientation == Orientation.WEST)
	        distanceToFirstEdge = getUltrasonicSensorCoordinates()[1]
	                % sizeTile();
	    else
	        distanceToFirstEdge = sizeTile()
	                - (getUltrasonicSensorCoordinates()[1] % sizeTile());
	
	    // Berekent het aantal tegels die zichtbaar zijn na deze edge (tot een
	    // maximum van 3)
	    if (tile.getEdge(orientation).isPassable()) {
	        tile = tile.getEdge(orientation).getNeighbour(tile);
	        if (tile.getEdge(orientation).isPassable()) {
	            tile = tile.getEdge(orientation).getNeighbour(tile);
	            if (tile.getEdge(orientation).isPassable()) {
	                tile = tile.getEdge(orientation).getNeighbour(tile);
	                if (tile.getEdge(orientation).isPassable())
	                    return 250;
	                else
	                    amountOfTilesVisible = 3;
	            } else
	                amountOfTilesVisible = 2;
	        } else
	            amountOfTilesVisible = 1;
	    } else
	        amountOfTilesVisible = 0;
	
	    return distanceToFirstEdge + amountOfTilesVisible * sizeTile();
	}

	@Override
    protected int getRotateSleepTime(double angle) {
        return 5 - getSpeed();
    }

    @Override
    protected int getTravelSleepTime(double distance) {
        switch (getSpeed()) {
        case 1:
            return 10;
        case 2:
            return 7;
        case 3:
            return 5;
        case 4:
            return 3;
        }
        return 0;
    }
}