package simulator.pilot;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import mapping.Barcode;
import mapping.MapGraph;
import mapping.Obstruction;
import mapping.Orientation;
import simulator.ExtMath;

public abstract class AbstractPilot implements PilotInterface {
    /**
     * verandert wanneer een nieuwe map wordt ingeladen naar de positie waar het
     * pijltje staat wanneer de map ingeladen wordt
     */
    private Point2D.Double startAbsolutePosition = new Point2D.Double(5
            * sizeTile() + sizeTile() / 2, 5 * sizeTile() + sizeTile() / 2);
    // private double startPositionAbsoluteX = 5 * sizeTile() + sizeTile() / 2;
    // private double startPositionAbsoluteY = 5 * sizeTile() + sizeTile() / 2;
    /**
     * coordinaat in het echte assenstelsel van de robot
     */
    private Point2D.Double absolutePosition = new Point2D.Double(5 * sizeTile()
            + sizeTile() / 2, 5 * sizeTile() + sizeTile() / 2);
    // private double absoluteX = 5 * sizeTile() + sizeTile() / 2;
    // private double absoluteY = 5 * sizeTile() + sizeTile() / 2;

    private double angle = 270;
    protected int speed = 10;
    private File mapFile;
    private MapGraph mapGraphLoaded;
    private MapGraph mapGraphConstructed;
    private final Set<Barcode> barcodes;

    // private int amtToSendToBuffer = 50;

    /**
     * waarde die afhangt van de robot!
     */
    protected final double detectionDistanceUltrasonicSensorRobot = 28;

    public AbstractPilot() {
        barcodes = new HashSet<Barcode>();
    }

    public void addWall() {
        final Orientation currentOrientation = Orientation
                .calculateOrientation(getAngle());

        /* TOON panel moet pilot pollen voor ALLES(positie, muren...) */
        // getSimulationPanel().addWall(currentOrientation,
        // getCurrentPositionAbsoluteX(), getCurrentPositionAbsoluteY());
        setWallOnTile(getRelativePosition(), currentOrientation);
    }

    public void alignOnWhiteLine() {
        // TOON Commando geven aan de robot om alignOnWhiteLine() te doen?
        // Momenteel wordt er in communicator gecheckt hoelang het geleden is.
        while (!pointOnEdge(getLightSensorAbsolute()[0],
                getLightSensorAbsolute()[1])) {
            travel(1);
        }

        travel(5);

        while (!pointOnEdge(getLightSensorAbsolute()[0],
                getLightSensorAbsolute()[1])) {
            rotate(-1);
        }

        rotate(90);

        int i = 0;

        while (!pointOnEdge(getLightSensorAbsolute()[0],
                getLightSensorAbsolute()[1])) {
            rotate(1);
            i++;
        }

        rotate(-(90 + i) / 2);
    }

    // checkt deze afstand ook en doet hetzelfde (alleen als de afstand kleiner
    // is dan 30 en niet tussen 22 en 24 want da zou al goe genoeg zijn), dan
    // terug 90 graden naar rechts zodat em zoals int begin staat. Als er alleen
    // links een muur staat, doet em dus niks rechts en dan links wat hem anders
    // eerst rechts zou doen (dus < 30 enige voorwaarde).
    public void allignOnWalls() {
        rotate(90);
        if (getUltraSensorValue() < 30 && getUltraSensorValue() > 23) {
            while (!(getUltraSensorValue() < 23)) {
                travel(1);
            }
        }

        rotate(-90);
        rotate(-90);

        if (getUltraSensorValue() < 30 && getUltraSensorValue() > 24) {
            while (!(getUltraSensorValue() < 23)) {
                travel(1);
            }
        }

        rotate(90);
    }

    /**
     * checkt of de robot een obstruction ZIET
     */

    public boolean checkForObstruction() {
        final int distance = getUltraSensorValue();

        if (distance < detectionDistanceUltrasonicSensorRobot) {
            return true;
        }

        return false;
    }

    // voegt ofwel muur toe ofwel tile toe
    public void checkForObstructionAndSetTile() {
        if (checkForObstruction()) {
            addWall();
        } else {
            final Orientation currentOrientation = Orientation
                    .calculateOrientation(getAngle());
            Point nextPoint = currentOrientation.getNext(getRelativePosition());
            if (mapGraphConstructed.getTile(nextPoint) == null) {
                getMapGraphConstructed().addTileXY(nextPoint);
            }
        }
    }

    /**
     * Bij checkForObstructions ook direct tiles toevoegen aangrenzend aan de
     * current, nodig voor het algoritme! die worden dus hier toegevoegd en niet
     * meer wanneer je naar een volgende tile gaat.
     */
    public void checkForObstructions() {
        for (int i = 0; i < 3; i++) {
            checkForObstructionAndSetTile();
            rotate(90);
        }

        checkForObstructionAndSetTile();

        for (int i = 0; i < 3; i++) {
            rotate(-90);
        }

    }

    public void clear() {
        /*
         * TOON clear hoort niet in pilot maar in simulatorpanel(clear
         * mapgraphconstructed?)
         */
        // getSimulationPanel().clearPath();
    }

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#getCurrentPositionAbsolute()
     */
    @Override
    public Point2D.Double getAbsolutePosition() {
        return absolutePosition;
    }

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#getAngle()
     */
    @Override
    public double getAngle() {
        return angle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#getBarcodes()
     */
    @Override
    public Set<Barcode> getBarcodes() {
        return barcodes;
    }

    public abstract String getConsoleTag();

    /**
     * Dit is de marge ten opzichte van de edge wordt gebruikt in travel :
     * wanneer de robot op 1 pixel verwijderd van de edge is begint hij zijn
     * currentPositionRelative aan te passen afhankelijk van de
     * currentPositionAbsolute dit gebeurt in setCurrentTileCoordinates
     */
    private double getEdgeMarge() {
        // return (double) 1.2 * scalingfactor
        return 1.2;
    }

    public double[] getLightSensorAbsolute() {
        final double[] absLS = new double[2];
        absLS[0] = getAbsolutePosition().getX()
                + Math.cos(getLightsensorPlacement()[0])
                + Math.sin(getLightsensorPlacement()[1]);
        absLS[1] = getAbsolutePosition().getY()
                + Math.sin(getLightsensorPlacement()[0])
                + Math.cos(getLightsensorPlacement()[1]);

        return absLS;
    }

    /**
     * The lightsensor is not attached on the middle point of the robot, but
     * more in front of that point. This value gives the distance of the
     * lightsensor with respect to the center of the drive axle.
     */
    private double[] getLightsensorPlacement() {
        /*
         * TOON pilot mag niets weten van absolute coordinaten. Deze methode
         * geeft de positie van de lichtsensor relatief ten opzichte van het
         * midden van de aandrijfas als een array [(naar voor positief in cm),
         * (naar rechts positief in cm)]
         */
        // return (getCurrentPositionAbsoluteX() + scalingfactor() * 7.5
        // * Math.cos(Math.toRadians(getAlpha())));
        return new double[2];
    }

    /**
     * Returns a number from a normal distribution that represents a lightsensor
     * value.
     */
    public abstract int getLightSensorValue();

    public File getMapFile() {
        return mapFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#getMapGraphConstructed()
     */
    @Override
    public MapGraph getMapGraphConstructed() {
        return mapGraphConstructed;
    }

    public MapGraph getMapGraphLoaded() {
        return mapGraphLoaded;
    }

    // TOON Dit hoort eerder in mapgraph?
    public String getMapString() {
        if (getMapGraphLoaded() == null) {
            return "/";
        }
        return mapFile.getName();
    }

    /*
     * public void setMapFile(File mapFile) { setMapFile(mapFile, 0, 0);
     * SSG.getInformationBuffer().setXCoordinateRelative(0);
     * SSG.getInformationBuffer().setYCoordinateRelative(0);
     * 
     * }
     */

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#getCurrentOrientation()
     */
    @Override
    public Orientation getOrientation() {
        return Orientation.calculateOrientation(getAngle());
    }

    public Point getRelativePosition() {
        return absToRelPosition(getAbsolutePosition());
    }

    protected abstract int getRotateSleepTime(double angle);

    public int getSpeed() {
        if (speed == 48) {
            return 4;
        } else if (speed == 58) {
            return 3;
        } else if (speed == 86) {
            return 2;
        } else {
            return 1;
        }
    }

    protected abstract int getTravelSleepTime(double distance);

    /**
     * Returns a number from a normal distribution that represents a lightsensor
     * value.
     */
    public abstract int getUltraSensorValue();

    public double[] getUltrasonicSensorAbsolute() {
        final double[] absUS = new double[2];
        absUS[0] = getAbsolutePosition().getX()
                + Math.cos(getUltrasonicSensorPlacement()[0])
                + Math.sin(getUltrasonicSensorPlacement()[1]);
        absUS[1] = getAbsolutePosition().getY()
                + Math.sin(getUltrasonicSensorPlacement()[0])
                + Math.cos(getUltrasonicSensorPlacement()[1]);

        return absUS;
    }

    /**
     * The ultrasonic sensor is not attached on the middle point of the robot,
     * but a little behind that point. This value gives the x-coordinate of the
     * ultrasonic sensor.
     */
    public double[] getUltrasonicSensorPlacement() {
        /*
         * TOON pilot mag niets weten van absolute coordinaten. Deze methode
         * geeft de positie van de lichtsensor relatief ten opzichte van het
         * midden van de aandrijfas als een array [(naar voor positief in cm),
         * (naar rechts positief in cm)]
         * 
         * Herziening, pilot heeft 'absolute' coordinaten(die niets met tekenen
         * te maken hebben.) De relatieve gaan verwijderd worden, die kunnen
         * immers berekend worden uit de absolute.
         */
        // return (getCurrentPositionAbsoluteX() - scalingfactor() * 5.5
        // * Math.cos(Math.toRadians(getAlpha())));
        return new double[2];
    }

    @Override
    public boolean isRobotControllable() {
        return true;
    }

    // public void removeWall(){
    //
    // Orientation currentOrientation = Orientation.calculateOrientation(
    // getCurrentPositionAbsoluteX(),
    // getCurrentPositionAbsoluteY(), getAlpha(), sizeTile());
    //
    // // roept addwhiteline op, deze methode verwijdert de muur terug uit
    // // het panel
    // SSG.getSimulationPanel().addWhiteLine(currentOrientation,
    // getCurrentPositionAbsoluteX(),
    // getCurrentPositionAbsoluteY());
    // SSG.getSimulationPanel().removeWallFromTile(getCurrentPositionRelativeX(),
    // getCurrentPositionRelativeY(), currentOrientation);
    // }

    /**
     * checkt of de robot zich binnen de marge van een edge bevindt
     */
    public boolean pointOnEdge(final double x, final double y) {
        return (x % sizeTile()) > sizeTile() - getEdgeMarge()
                || (x % sizeTile()) < getEdgeMarge()
                || (y % sizeTile()) > sizeTile() - getEdgeMarge()
                || (y % sizeTile()) < getEdgeMarge();
    }

    /**
     * Checks whether the given point is on the edge of a tile.
     */
    public Orientation pointOnWichSideOfTile(final double x, final double y,
            final Orientation travelOrientation) {
        if (travelOrientation == Orientation.NORTH
                || travelOrientation == Orientation.SOUTH) {
            if ((y % sizeTile()) > sizeTile() / 2) {
                return Orientation.SOUTH;
            }
            // if((y % 40) < 20)
            else {
                return Orientation.NORTH;
            }
        }
        // if(travelOrientation == Orientation.EAST || travelOrientation ==
        // Orientation.WEST)
        else {
            if ((x % sizeTile()) > sizeTile() / 2) {
                return Orientation.EAST;
            }
            // if((x % 40) < 20)
            else {
                return Orientation.WEST;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#reset()
     */
    @Override
    public void reset() {
        // TOON 5*sizeTile() komt nog van de versmelting met tekenpanel
        // (gewoon sizetile()/2?)
        absolutePosition.setLocation(5 * sizeTile() + sizeTile() / 2, 5
                * sizeTile() + sizeTile() / 2);
        startAbsolutePosition.setLocation(5 * sizeTile() + sizeTile() / 2, 5
                * sizeTile() + sizeTile() / 2);
        // startPositionAbsoluteX = 5 * sizeTile() + sizeTile() / 2;
        // startPositionAbsoluteY = 5 * sizeTile() + sizeTile() / 2;
        angle = 270;

    }

    /**
     * Checks whether the robot, standig on the given point, is on the edge of a
     * tile. The robot is interpreted as a rectangle around the given position.
     */
    public boolean robotOnEdge(final double x, final double y,
            final double alpha) {
        final double leftFrontX = (x - 12 * Math
                .cos(Math.toRadians(alpha - 45)));
        final double leftFrontY = (y + 12 * Math
                .sin(Math.toRadians(alpha - 45)));

        final double rightFrontX = (x - 12 * Math.cos(Math
                .toRadians(alpha + 45)));
        final double rightFrontY = (y + 12 * Math.sin(Math
                .toRadians(alpha + 45)));

        final double leftBackX = (x - 13 * Math.cos(Math
                .toRadians(alpha - 180 + 30)));
        final double leftBackY = (y + 13 * Math.sin(Math
                .toRadians(alpha - 180 + 30)));

        final double rightBackX = (x - 13 * Math.cos(Math
                .toRadians(alpha - 180 - 30)));
        final double rightBackY = (y + 13 * Math.sin(Math
                .toRadians(alpha - 180 - 30)));

        return pointOnEdge(leftFrontX, leftFrontY)
                || pointOnEdge(rightFrontX, rightFrontY)
                || pointOnEdge(leftBackX, leftBackY)
                || pointOnEdge(rightBackX, rightBackY)
                || (Math.abs(leftFrontX % sizeTile() - rightFrontX % sizeTile()) > sizeTile() / 2)
                || (Math.abs(leftFrontX % sizeTile() - leftBackX % sizeTile()) > sizeTile() / 2)
                || (Math.abs(leftFrontX % sizeTile() - rightBackX % sizeTile()) > sizeTile() / 2)
                || (Math.abs(rightFrontX % sizeTile() - rightBackX % sizeTile()) > sizeTile() / 2)
                || (Math.abs(rightFrontX % sizeTile() - leftBackX % sizeTile()) > sizeTile() / 2)
                || (Math.abs(rightBackX % sizeTile() - leftBackX % sizeTile()) > sizeTile() / 2)
                || (Math.abs(leftFrontY % sizeTile() - rightFrontY % sizeTile()) > sizeTile() / 2)
                || (Math.abs(leftFrontY % sizeTile() - leftBackY % sizeTile()) > sizeTile() / 2)
                || (Math.abs(leftFrontY % sizeTile() - rightBackY % sizeTile()) > sizeTile() / 2)
                || (Math.abs(rightFrontY % sizeTile() - rightBackY % sizeTile()) > sizeTile() / 2)
                || (Math.abs(rightFrontY % sizeTile() - leftBackY % sizeTile()) > sizeTile() / 2)
                || (Math.abs(rightBackY % sizeTile() - leftBackY % sizeTile()) > sizeTile() / 2);
    }

    public void rotate(final double alpha) {

        // rotatedInTotal = rotatedInTotal + Math.abs(alpha);
        // System.out.println("rotatedInTotal : " + rotatedInTotal);

        final double alphaOriginal = getAngle();
        double alphaTemp = getAngle();

        int j = 1;
        if (alpha < 0) {
            j = -1;
        }

        for (int i = j; i * j <= alpha * j; i += j) {
            alphaTemp = ExtMath.addDegree(alphaOriginal, i);

            /*
             * if (getMapGraph() != null) { if
             * (robotOnEdge(getCurrentPositionAbsoluteX(),
             * getCurrentPositionAbsoluteY(), alphaTemp)) { // the edge you are
             * standing on contains a wall // weet niet goed hoe je dit kan
             * checken //
             * if(!(getMapGraph().canMoveTo(Orientation.calculateOrientation
             * (getCurrentPositionAbsoluteX(), // getCurrentPositionAbsoluteY(),
             * ExtMath.addDegree(alphaTemp,j*30))) // && getMapGraph().canMoveTo
             * (Orientation.calculateOrientation( getCurrentPositionAbsoluteX(),
             * // getCurrentPositionAbsoluteY(),
             * ExtMath.addDegree(alphaTemp,j*210))))) // { // setAlpha((double)
             * ExtMath.addDegree(alphaOriginal,i-j)); //
             * getSSG().updateStatus(); // //
             * System.out.println("Er staat een muur in de weg"); // return; //
             * } } }
             */

            // TOON panel pollt pilot
            // getSimulationPanel().moveRobot(getCurrentPositionAbsoluteX(),
            // getCurrentPositionAbsoluteY(), alphaTemp);
            setAngle(alphaTemp);

            // getSSG().updateStatus();

            try {
                Thread.sleep(getRotateSleepTime(alpha));
            } catch (final InterruptedException e) {
            }
        }
    }

    /**
     * Deze methode zet de coordinaten van het echte systeem om in de
     * coordinaten van de matrix
     */
    public Point absToRelPosition(Point2D.Double point) {
        Point relPoint = new Point();
        relPoint.setLocation((int) (point.getX() / sizeTile()),
                (int) (point.getY() / sizeTile()));

        return relPoint;
    }

    public void setAngle(final double angle) {
        this.angle = angle;
    }

    public void setBarcode(final int barcode) {

        final Barcode scanned = new Barcode(barcode, getOrientation(),
                mapGraphConstructed.getTile(new Point()));

        getMapGraphConstructed().getTile(getRelativePosition()).setContent(
                scanned);

        barcodes.add(scanned);
    }

    public void setCurrentAbsolutePosition(final double x, final double y) {
        absolutePosition.setLocation(x, y);
    }

    public void setSpeed(int speed) {
        if (speed == 4) {
            speed = 48;
        } else if (speed == 3) {
            speed = 58;
        } else if (speed == 2) {
            speed = 86;
        } else {
            speed = 194;
        }
    }

    public void setStartAbsolutePosition(final double startPositionX,
            final double startPositionY) {
        startAbsolutePosition.setLocation(startPositionX, startPositionY);
    }

    // zet een double om in een veelvoud van 40 kleiner dan de double (ook bij
    // negatief
    // maar doet normaal niet ter zake aangezien de coordinaten in het echte
    // coordinatensysteem
    // niet negatief kunnen zijn
    public int setToMultipleOfTileSize(final double a) {
        return (int) (Math.floor(a / sizeTile()) * sizeTile());
    }

    public void setWallOnTile(final Point point, final Orientation orientation) {
        if (getMapGraphConstructed().getTile(point) == null) {
            throw new IllegalArgumentException(
                    "in simulationPanel bij methode SetWallOnTile "
                            + "zijn coordinaten meegegeven die de mapgraph niet bevat nl"
                            + point.x + " en " + point.y);
        }
        getMapGraphConstructed().getTile(point).getEdge(orientation)
                .setObstruction(Obstruction.WALL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#sizeTile()
     */
    @Override
    public double sizeTile() {
        // TOON Moet dit 40 zijn? Misschien wel zodat travel() in cm werkt?
        return 40;
    }

    public void travel(final double distance) {
        /*
         * TOON travel: pilot in panel(viewport) maar panel niet in pilot -> dus
         * panel moet pollen
         */
        /*
         * TOON pilots mogen niets weten over scalingfactors (1 pilot kan in
         * verschillende panels met verschillende scalingfactors getoond worden)
         */
        // distance = distance * scalingfactor();

        // travelledInTotal = travelledInTotal + Math.abs(distance);
        // System.out.println("travelledInTotal : " + travelledInTotal);
        // double xOriginal = getCurrentPositionAbsoluteX();
        // double yOriginal = getCurrentPositionAbsoluteY();
        double xTemp = getAbsolutePosition().getX();
        double yTemp = getAbsolutePosition().getY();

        double j = 1;
        Orientation travelOrientation = Orientation
                .calculateOrientation(getAngle());

        // if you are traveling backwards, the orientation you are facing is the
        // opposite to the orientation you are traveling.
        if (distance < 0) {
            j = -1;
            travelOrientation = travelOrientation.getOppositeOrientation();
        }

        double distanceToGo = distance;
        double i = j;

        while (distanceToGo != 0)
        // for (double i = j; i*j <= distance*j; i+=j)
        {
            xTemp = (xTemp + i * Math.cos(Math.toRadians(getAngle())));
            yTemp = (yTemp + i * Math.sin(Math.toRadians(getAngle())));

            if (mapGraphLoaded != null) {
                if (robotOnEdge(xTemp, yTemp, getAngle())) {
                    final Orientation edgeOrientation = pointOnWichSideOfTile(
                            xTemp, yTemp, travelOrientation);

                    // the edge you are standing on contains a wall
                    if (travelOrientation == edgeOrientation
                            && !getMapGraphLoaded()
                                    .getTile(getRelativePosition())
                                    .getEdge(travelOrientation).isPassable()) {
                        setCurrentAbsolutePosition(
                                (xTemp - i
                                        * Math.cos(Math.toRadians(getAngle()))),
                                (yTemp - i
                                        * Math.sin(Math.toRadians(getAngle()))));
                        // getSSG().updateStatus();

                        System.out.println("Er staat een muur in de weg");
                        return;
                    }
                }
            }
            setCurrentAbsolutePosition(xTemp, yTemp);

            distanceToGo = distanceToGo - i;
            if (distanceToGo < 1) {
                i = distanceToGo;
            }
            try {
                Thread.sleep(getTravelSleepTime(distance));
            } catch (final InterruptedException e) {
            }
        }

        // getSSG().updateStatus();
    }
}
