package simulator.pilot;

import java.awt.Point;
import java.awt.geom.Point2D;
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
    private Set<Barcode> barcodes;
    private MapGraph mapGraphConstructed;
    private SimulatorPanel simulatorPanel;
    private MessageCenter messageCenter;
    private boolean readBarcodes = true;
    private boolean permaBarcodeStop = false;
    protected PilotActions pilotActions = new PilotActions(this);

    protected final double lengthOfRobot = 24;
    protected final double widthOfRobot = 26;
    protected final double lightSensorDistanceFromAxis = 7.5;
    protected final double ultrasonicSensorDistanceFromAxis = 5.5;
    protected final double detectionDistanceUltrasonicSensorRobot = 28;

    public AbstractPilot() {
        mapGraphConstructed = new MapGraph();
        mapGraphConstructed.addTileXY(new Point(0, 0));
        barcodes = new HashSet<Barcode>();
        
        try {
        	 messageCenter = new MessageCenter(this);
        } catch (Exception e) {
        	System.out.println("MessageCenter problem!");
        }
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

    public void setBarcode(final int barcode) {
        final Barcode scanned = new Barcode(
                mapGraphConstructed.getTile(new Point()), barcode,
                getOrientation());
        getMapGraphConstructed().getTile(getMatrixPosition()).setContent(
                scanned);
        barcodes.add(scanned);
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
        barcodes = new HashSet<Barcode>();
        mapGraphConstructed = new MapGraph();
        mapGraphConstructed.addTileXY(getMatrixPosition());
    }

    public Orientation getOrientation() {
        return Orientation.calculateOrientation(getAngle());
    }

    public double[] getLightSensorCoordinates() {
        final double[] coordinates = new double[2];
        if (getOrientation() == Orientation.NORTH) {
            coordinates[0] = getPosition().getX() - lightSensorDistanceFromAxis;
            coordinates[1] = getPosition().getY();
        } else if (getOrientation() == Orientation.SOUTH) {
            coordinates[0] = getPosition().getX() + lightSensorDistanceFromAxis;
            coordinates[1] = getPosition().getY();
        } else if (getOrientation() == Orientation.EAST) {
            coordinates[0] = getPosition().getX();
            coordinates[1] = getPosition().getY() - lightSensorDistanceFromAxis;
        } else if (getOrientation() == Orientation.WEST) {
            coordinates[0] = getPosition().getX();
            coordinates[1] = getPosition().getY() + lightSensorDistanceFromAxis;
        }
        return coordinates;
    }

    public abstract int getLightSensorValue();

    public double[] getUltrasonicSensorCoordinates() {
        final double[] coordinates = new double[2];
        if (getOrientation() == Orientation.NORTH) {
            coordinates[0] = getPosition().getX()
                    + ultrasonicSensorDistanceFromAxis;
            coordinates[1] = getPosition().getY();
        } else if (getOrientation() == Orientation.SOUTH) {
            coordinates[0] = getPosition().getX()
                    - ultrasonicSensorDistanceFromAxis;
            coordinates[1] = getPosition().getY();
        } else if (getOrientation() == Orientation.EAST) {
            coordinates[0] = getPosition().getX();
            coordinates[1] = getPosition().getY()
                    + ultrasonicSensorDistanceFromAxis;
        } else if (getOrientation() == Orientation.WEST) {
            coordinates[0] = getPosition().getX();
            coordinates[1] = getPosition().getY()
                    - ultrasonicSensorDistanceFromAxis;
        }
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

    // /**
    // * Checks whether the robot, standing on the given point, is on the edge
    // of
    // * a tile. The robot is interpreted as a rectangle around the given
    // * position.
    // */
    // private boolean robotOnEdge(final double x, final double y,
    // final double alpha) {
    // Orientation orientation = Orientation.calculateOrientation(alpha);
    // Point backup;
    // // North
    // Point leftFront = new Point((int) (x - lengthOfRobot / 2),
    // (int) (y - widthOfRobot / 2));
    // Point rightFront = new Point((int) (x - lengthOfRobot / 2),
    // (int) (y + widthOfRobot / 2));
    // Point leftBack = new Point((int) (x + lengthOfRobot / 2),
    // (int) (y - widthOfRobot / 2));
    // Point rightBack = new Point((int) (x + lengthOfRobot / 2),
    // (int) (y + widthOfRobot / 2));
    // if (orientation == Orientation.SOUTH) {
    // backup = leftFront;
    // leftFront = rightBack;
    // rightBack = backup;
    // backup = rightFront;
    // rightFront = leftBack;
    // leftBack = backup;
    // } else if (orientation == Orientation.EAST) {
    // backup = leftFront;
    // leftFront = rightFront;
    // rightFront = rightBack;
    // rightBack = leftBack;
    // leftBack = backup;
    // } else if (orientation == Orientation.WEST) {
    // backup = leftFront;
    // leftFront = leftBack;
    // leftBack = rightBack;
    // rightBack = rightFront;
    // rightFront = backup;
    // }
    // return pointOnEdge(leftFront.getX(), leftFront.getY())
    // || pointOnEdge(rightFront.getX(), rightFront.getY())
    // || pointOnEdge(leftBack.getX(), leftBack.getY())
    // || pointOnEdge(rightBack.getX(), rightBack.getY())
    // || (Math.abs(leftFront.getX() % sizeTile() - rightFront.getX()
    // % sizeTile()) > sizeTile() / 2)
    // || (Math.abs(leftFront.getX() % sizeTile() - leftBack.getX()
    // % sizeTile()) > sizeTile() / 2)
    // || (Math.abs(leftFront.getX() % sizeTile() - rightBack.getX()
    // % sizeTile()) > sizeTile() / 2)
    // || (Math.abs(rightFront.getX() % sizeTile() - rightBack.getX()
    // % sizeTile()) > sizeTile() / 2)
    // || (Math.abs(rightFront.getX() % sizeTile() - leftBack.getX()
    // % sizeTile()) > sizeTile() / 2)
    // || (Math.abs(rightBack.getX() % sizeTile() - leftBack.getX()
    // % sizeTile()) > sizeTile() / 2)
    // || (Math.abs(leftFront.getY() % sizeTile() - rightFront.getY()
    // % sizeTile()) > sizeTile() / 2)
    // || (Math.abs(leftFront.getY() % sizeTile() - leftBack.getY()
    // % sizeTile()) > sizeTile() / 2)
    // || (Math.abs(leftFront.getY() % sizeTile() - rightBack.getY()
    // % sizeTile()) > sizeTile() / 2)
    // || (Math.abs(rightFront.getY() % sizeTile() - rightBack.getY()
    // % sizeTile()) > sizeTile() / 2)
    // || (Math.abs(rightFront.getY() % sizeTile() - leftBack.getY()
    // % sizeTile()) > sizeTile() / 2)
    // || (Math.abs(rightBack.getY() % sizeTile() - leftBack.getY()
    // % sizeTile()) > sizeTile() / 2);
    // }
    //
    // /**
    // * Checks on what side of the tile you are.
    // */
    // private Orientation pointOnWichSideOfTile(final double x, final double y,
    // final Orientation travelOrientation) {
    // if (travelOrientation == Orientation.NORTH
    // || travelOrientation == Orientation.SOUTH) {
    // if ((y % sizeTile()) > sizeTile() / 2)
    // return Orientation.SOUTH;
    // // if((y % 40) < 20)
    // else
    // return Orientation.NORTH;
    // } else if ((x % sizeTile()) > sizeTile() / 2)
    // return Orientation.EAST;
    // // if((x % 40) < 20)
    // else
    // return Orientation.WEST;
    // }

    public void alignOnWhiteLine() {
        // TODO aparte invulling voor sim en robot?
        // TODO Commando geven aan de robot om alignOnWhiteLine() te doen?
        // Momenteel wordt er in communicator gecheckt hoelang het geleden is.
        while (!pointOnEdge(getLightSensorCoordinates()[0],
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
        rotate(-(90 + i) / 2);
    }

    public void alignOnWalls() {
        // TODO aparte invulling voor sim en robot?
        rotate(90);
        if (getUltraSensorValue() < detectionDistanceUltrasonicSensorRobot
                && getUltraSensorValue() > 23)
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
            if(readBarcodes && getLightSensorValue() < 40 && getLightSensorValue() > 10)
            {
            	System.out.println("barcode found");
            	actionBarcodeFound();
            }
            try {
                Thread.sleep(getTravelSleepTime(distance));
            } catch (final InterruptedException e) {

            }
        }
    }
    
    protected abstract int readBarcode();
    
    private void actionBarcodeFound()
    {
    	int value = readBarcode();
    	Barcode barcode = new Barcode(getMapGraphConstructed().getTile(getMatrixPosition()), value, getOrientation());
    	getMapGraphConstructed().addContentToCurrentTile(getMatrixPosition(), barcode);
    	barcodes.add(barcode);
    	pilotActions.executeBarcode(value);
    }
    
    public void stopReadingBarcodes() {
        // TODO Deze methode wil ik weg uit pilot.
        readBarcodes = false;
    }

    public void startReadingBarcodes() {
        // TODO Deze methode wil ik weg uit pilot.
        readBarcodes = true;
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
                        AbstractPilot.this).startExploringMaze();
            }
        }.start();
    }
}