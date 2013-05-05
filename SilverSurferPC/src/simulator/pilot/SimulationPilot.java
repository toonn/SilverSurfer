package simulator.pilot;

import java.awt.Point;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import mapping.Barcode;
import mapping.Edge;
import mapping.MapGraph;
import mapping.Obstruction;
import mapping.Orientation;
import mapping.Seesaw;
import mapping.StartBase;
import mapping.Tile;
import mazeAlgorithm.CollisionAvoidedException;
import simulator.viewport.SimulatorPanel;

public class SimulationPilot extends AbstractPilot {

    private static class SimulationSensorData {
        // Whether there the light is bright or quite dark. influences the light
        // sensor
        private static boolean isBrightLight = true;
        private static boolean isDriving = true;

        // Standard Deviation of the light sensor, when standing on a panel
        // containing a barcode under given circumstances.
        public static final double getMBarcodeTileLS(final int color) {
            // Black
            if (color == 0) {
                if (isBrightLight) {
                    if (isDriving) {
                        return 33.05;
                    } else {
                        return 34;
                    }
                } else {
                    if (isDriving) {
                        return 33.42;
                    } else {
                        return 33;
                    }
                }
            }
            // White
            else if (color == 1) {
                return SimulationSensorData.getMWhiteLineLS();
                // Not on the code itself, but on the brown panel next to it
            } else {
                return SimulationSensorData.getMEmptyPanelLS();
            }
        }

        // Mean value of the light sensor, when standing on an empty panel under
        // given circumstances.
        public static final double getMEmptyPanelLS() {
            if (isBrightLight) {
                if (isDriving) {
                    return 49.3863;
                } else {
                    return 49.99688474;
                }
            } else {
                if (isDriving) {
                    return 49.46829;
                } else {
                    return 49;
                }
            }
        }

        // Mean value of the infrared sensor, when the robot has no infrared in
        // view
        public static final double getMNoInfraRedIS() {
            return 2;
        }

        // Mean value of the infrared sensor, when having an open seesaw in view
        public static final double getMSeesawIS() {
            return 120;
        }

        // Mean value of the light sensor, when standing a white line under
        // given circumstances.
        public static final double getMWhiteLineLS() {
            if (isBrightLight) {
                if (isDriving) {
                    return 55.02606;
                } else {
                    return 54.99377;
                }
            } else {
                if (isDriving) {
                    return 54.98442;
                } else {
                    return 55;
                }
            }
        }

        // Standard Deviation of the light sensor, when standing on a panel
        // containing a barcode under given circumstances.
        // The color should be 0 when standing on a black part, 1 when standing
        // on a white part or something else when standing next to the panel.
        public static final double getSDBarcodeTileLS(final int color) {
            // Black
            if (color == 0) {
                if (isBrightLight) {
                    if (isDriving) {
                        return 0.272790608;
                    } else {
                        return 0;
                    }
                } else {
                    if (isDriving) {
                        return 0.575648536;
                    } else {
                        return 0;
                    }
                }
            }
            // White
            else if (color == 1) {
                return SimulationSensorData.getSDWhiteLineLS();
                // Not on the code itself, but on the brown panel next to it
            } else {
                return SimulationSensorData.getSDEmptyPanelLS();
            }
        }

        // Standard Deviation of the light sensor, when standing on an empty
        // panel under given circumstances.
        public static final double getSDEmptyPanelLS() {
            if (isBrightLight) {
                if (isDriving) {
                    return 0.6425;
                } else {
                    return 0.055814557;
                }
            } else {
                if (isDriving) {
                    return 1.218249;
                } else {
                    return 0;
                }
            }
        }

        // Standard Deviation of the ultrasonic sensor under given
        // circumstances.
        public static final double getSDUS() {
            return 0.523148364;
        }

        // Standard Deviation of the light sensor, when standing a white line
        // under given circumstances.
        public static final double getSDWhiteLineLS() {
            if (isBrightLight) {
                if (isDriving) {
                    return 0.966416;
                } else {
                    return 0.111629;
                }
            } else {
                if (isDriving) {
                    return 1.1218249;
                } else {
                    return 0;
                }
            }
        }
    }

    private MapGraph mapGraphLoaded;
    private final double lightSensorDistanceFromAxis = 7.5;
    private final double ultrasonicSensorDistanceFromAxis = 4;

    public SimulationPilot(int teamNumber, MapGraph mapGraphLoaded, Point mapSize) {
        super(teamNumber, mapSize);
        this.mapGraphLoaded = mapGraphLoaded;
    }

    private double calculateDistanceToObstacle() {
        if (mapGraphLoaded == null) {
            return 250;
        }

        Set<Rectangle2D> obstacles = new HashSet<Rectangle2D>();

        for (Tile tile : mapGraphLoaded.getTiles()) {
            for (Edge wall : tile.getEdges()) {
                if (wall.getObstruction() != null
                        && wall.getObstruction() == Obstruction.WALL) {
                    Point2D.Double[] endPoints = wall.getEndPoints();
                    endPoints[0].setLocation(endPoints[0].getX() * sizeTile(),
                            endPoints[0].getY() * sizeTile());
                    endPoints[1].setLocation(endPoints[1].getX() * sizeTile(),
                            endPoints[1].getY() * sizeTile());
                    double x, y, w, h;
                    if (endPoints[0].getX() < endPoints[1].getX()
                            || endPoints[0].getY() < endPoints[1].getY()) {
                        w = endPoints[1].getX() - endPoints[0].getX();
                        h = endPoints[1].getY() - endPoints[0].getY();
                        if (w == 0) {
                            x = endPoints[0].getX() - 1;
                            w = 2;
                        } else
                            x = endPoints[0].getX();
                        if (h == 0)
                            y = endPoints[0].getY() - 1;
                        else
                            y = endPoints[0].getY();
                    } else {
                        w = endPoints[0].getX() - endPoints[1].getX();
                        h = endPoints[0].getY() - endPoints[1].getY();
                        if (w == 0)
                            x = endPoints[1].getX() - 1;
                        else
                            x = endPoints[1].getX();
                        if (h == 0)
                            y = endPoints[1].getY() - 1;
                        else
                            y = endPoints[0].getY();
                    }
                    if (w == 0)
                        w = 2;
                    if (h == 0)
                        h = 2;
                    obstacles.add(new Rectangle2D.Double(x, y, w, h));
                }
            }
        }

        // TODO htttp robotdimensions?
        Point2D robotDimensions = new Point2D.Double(20, 20);
        for (Point2D robotPosition : SimulatorPanel.getAllRobotPositions()) {
            if (!robotPosition.equals(getMatrixPosition())) {
                double x = (sizeTile() * robotPosition.getX()) + sizeTile() / 2
                        - (robotDimensions.getX() / 2);
                double y = (sizeTile() * robotPosition.getY()) + sizeTile() / 2
                        - (robotDimensions.getY() / 2);
                obstacles.add(new Rectangle2D.Double(x, y, robotDimensions
                        .getX(), robotDimensions.getY()));
            }
        }

        for (int distance = 1; distance < 250; distance++) {
            Arc2D sonarArc = new Arc2D.Double();
            sonarArc.setArc(getPosition().getX() - distance, getPosition()
                    .getY() - distance, 2 * (distance), 2 * (distance),
                    360 - getAngle() - 15, 30, Arc2D.PIE);
            for (Rectangle2D obstacle : obstacles)
                if (sonarArc.intersects(obstacle.getX(), obstacle.getY(),
                        obstacle.getWidth(), obstacle.getHeight()))
                    return distance;
        }

        return 250;
    }
   
    @Override
    public String getConsoleTag() {
        return "[SIMULATOR]";
    }

    @Override
    public int getInfraRedSensorValue() {
        double mean = 0;
        double standardDeviation = 0.5;
        if (mapGraphLoaded == null) {
            mean = (int) SimulationSensorData.getMNoInfraRedIS();
        } else {
            mean = recursiveInfraRed(getMatrixPosition(), 3);
        }
        return (int) Math.round(mean
                + (new Random().nextGaussian() * standardDeviation));
    }

    private double[] getLightSensorCoordinates() {
        final double[] coordinates = new double[2];
        coordinates[0] = (getPosition().getX() + lightSensorDistanceFromAxis
                * Math.cos(Math.toRadians(getAngle())));
        coordinates[1] = (getPosition().getX() + lightSensorDistanceFromAxis
                * Math.sin(Math.toRadians(getAngle())));
        return coordinates;
    }

    @Override
    public int getLightSensorValue() {
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
            final int color = ((Barcode) (mapGraphLoaded
                    .getTile(getMatrixPosition()).getContent())).getColorValue(
                    coordinates[0] % sizeTile(), coordinates[1] % sizeTile());
            mean = SimulationSensorData.getMBarcodeTileLS(color);
            standardDeviation = SimulationSensorData.getSDBarcodeTileLS(color);
        } else if (onStartTile(coordinates[0], coordinates[1])) {
            mean = SimulationSensorData.getMEmptyPanelLS();
            standardDeviation = SimulationSensorData.getSDEmptyPanelLS();
        }
        return (int) Math.round(mean
                + (new Random().nextGaussian() * standardDeviation));
    }

    @Override
    public int getUltraSensorValue() {
        try {
            return (int) Math.round(calculateDistanceToObstacle()
                    + (new Random().nextGaussian() * SimulationSensorData
                            .getSDUS()));
        } catch (Exception e) {
            System.out
                    .println("Error in SimulationPilot.getUltraSensorValue()!");
            return 0;
        }
    }

    private double[] getUltrasonicSensorCoordinates() {
        final double[] coordinates = new double[2];
        coordinates[0] = (getPosition().getX() + ultrasonicSensorDistanceFromAxis
                * Math.cos(Math.toRadians(getAngle())));
        coordinates[1] = (getPosition().getX() + ultrasonicSensorDistanceFromAxis
                * Math.sin(Math.toRadians(getAngle())));
        return coordinates;
    }

    // True if the robot is not on an edge, but on a tile containing a barcode.
    private boolean onBarcodeTile(final double x, final double y) {
        return !pointOnEdge(x, y)
                && mapGraphLoaded != null
                && (mapGraphLoaded.getTile(getMatrixPosition()).getContent() instanceof Barcode);
    }

    // True if the robot is not on an edge, but on a tile without a content.
    private boolean onEmptyTile(final double x, final double y) {
        return !pointOnEdge(x, y)
                && (mapGraphLoaded == null || mapGraphLoaded.getTile(
                        getMatrixPosition()).getContent() == null);
    }

    // True if the robot is not on an edge, but on a tile with a startbase.
    private boolean onStartTile(final double x, final double y) {
        return !pointOnEdge(x, y)
                && (mapGraphLoaded == null || mapGraphLoaded.getTile(
                        getMatrixPosition()).getContent() instanceof StartBase);
    }

    // True if the robot is on an edge and this edge is not a wall
    private boolean onWhiteLine(final double x, final double y) {
        return pointOnEdge(x, y)
                && (mapGraphLoaded == null || mapGraphLoaded
                        .getTile(getMatrixPosition())
                        .getEdgeAt(Orientation.calculateOrientation(getAngle()))
                        .getObstruction() != Obstruction.WALL);
    }

    @Override
    protected int readBarcode() {
        if (mapGraphLoaded.getTile(getMatrixPosition()).getContent() == null
                || !(mapGraphLoaded.getTile(getMatrixPosition()).getContent() instanceof Barcode)) {
            return -1;
        }
        int value = mapGraphLoaded.getTile(getMatrixPosition()).getContent()
                .getValue();
        return value;
    }

    private int recursiveInfraRed(Point currentPoint, int nbOfTilesToLook) {
        if (nbOfTilesToLook < 0
                || mapGraphLoaded.getTile(currentPoint)
                        .getEdgeAt(getOrientation()).getObstruction() == Obstruction.WALL
                || mapGraphLoaded.getTile(currentPoint)
                        .getEdgeAt(getOrientation()).getObstruction() == Obstruction.SEESAW_DOWN) {
            return (int) SimulationSensorData.getMNoInfraRedIS();
        } else if (!(mapGraphLoaded.getTile(currentPoint).getContent() instanceof Seesaw)
                && mapGraphLoaded.getTile(currentPoint)
                        .getEdgeAt(getOrientation()).getObstruction() == Obstruction.SEESAW_UP) {
            return (int) SimulationSensorData.getMSeesawIS();
        } else {
            return recursiveInfraRed(getOrientation().getNext(currentPoint),
                    nbOfTilesToLook--);
        }
    }

    @Override
    public void travel(final double distance, boolean ignoreCollision) throws CollisionAvoidedException {
        super.travel(distance, ignoreCollision);
        if (readBarcodes
                && !permaBarcodeStop
                && mapGraphLoaded.getTile(getMatrixPosition()).getContent() instanceof Barcode) {
            setBusyExecutingBarcode(true);
            pilotActions.barcodeFound();
            setBusyExecutingBarcode(false);
        }
    }
}