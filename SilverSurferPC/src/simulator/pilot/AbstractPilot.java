package simulator.pilot;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import mapping.Barcode;
import mapping.MapGraph;
import mapping.MapReader;
import mapping.Obstruction;
import mapping.Orientation;
import mapping.Tile;
import simulator.ExtMath;
import simulator.viewport.ViewPort;
import datastructures.Tuple;

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
    private int relativeX;
    private int relativeY;

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

    /**
     * Voegt een barcode toe aan de bag met barcodes.
     * 
     * @param simulatorPanel
     *            TODO
     * @param barcode
     *            TODO
     * @param visual
     *            TODO
     * @pre De posities van de rectangles etc moeten nu al helemaal ingevuld
     *      zijn.
     */
    public void addBarcode(final ViewPort simulatorPanel,
            final Barcode barcode, final Rectangle2D[] visual) {
        simulatorPanel.getVisibleBarcode().add(
                new Tuple<Barcode, Rectangle2D[]>(barcode, visual));
    }

    public void addWall() {
        final Orientation currentOrientation = Orientation
                .calculateOrientation(getAngle());

        /* TODO panel moet pilot pollen voor ALLES(positie, muren...) */
        // getSimulationPanel().addWall(currentOrientation,
        // getCurrentPositionAbsoluteX(), getCurrentPositionAbsoluteY());
        setWallOnTile(getPositionRelativeX(), getPositionRelativeY(),
                currentOrientation);
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

    public void alignOnWhiteLine() {
        // TODO Commando geven aan de robot om alignOnWhiteLine() te doen?
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

    public void checkForObstructionAndSetTile() {
        if (checkForObstruction()) {
            addWall();
        } else {
            final Orientation currentOrientation = Orientation
                    .calculateOrientation(getAngle());
            int xCoordinate;
            int yCoordinate;
            if (isRobotControllable()) {
                xCoordinate = relativeX
                        + currentOrientation.getArrayToFindNeighbourRelative()[0];
                yCoordinate = relativeY
                        + currentOrientation.getArrayToFindNeighbourRelative()[1];
            } else {
                xCoordinate = mapGraphLoaded.getCurrentTileCoordinates()[0]
                        + currentOrientation.getArrayToFindNeighbourRelative()[0];
                yCoordinate = mapGraphLoaded.getCurrentTileCoordinates()[1]
                        + currentOrientation.getArrayToFindNeighbourRelative()[1];
            }
            if (mapGraphConstructed.getTileWithCoordinates(xCoordinate,
                    yCoordinate) == null) {
                setTile(xCoordinate, yCoordinate);
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
            if (checkForObstruction()) {
                addWall();
            } else {
                // removeWall();
                final Orientation currentOrientation = Orientation
                        .calculateOrientation(getAngle());
                final int xCoordinate = mapGraphLoaded
                        .getCurrentTileCoordinates()[0]
                        + currentOrientation.getArrayToFindNeighbourRelative()[0];
                final int yCoordinate = mapGraphLoaded
                        .getCurrentTileCoordinates()[1]
                        + currentOrientation.getArrayToFindNeighbourRelative()[1];
                if (mapGraphConstructed.getTileWithCoordinates(xCoordinate,
                        yCoordinate) == null) {
                    setTile(xCoordinate, yCoordinate);
                }

            }
            rotate(90);
        }

        if (checkForObstruction()) {
            addWall();
        } else {
            // removeWall();
            final Orientation currentOrientation = Orientation
                    .calculateOrientation(getAngle());
            final int xCoordinate = mapGraphLoaded.getCurrentTileCoordinates()[0]
                    + currentOrientation.getArrayToFindNeighbourRelative()[0];
            final int yCoordinate = mapGraphLoaded.getCurrentTileCoordinates()[1]
                    + currentOrientation.getArrayToFindNeighbourRelative()[1];
            if (mapGraphConstructed.getTileWithCoordinates(xCoordinate,
                    yCoordinate) == null) {
                setTile(xCoordinate, yCoordinate);
            }

        }

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

    // /* (non-Javadoc)
    // * @see
    // simulator.pilot.PilotInterface#getCenterAbsoluteCurrentTile(double)
    // */
    // @Override
    // public int[] getCenterAbsoluteCurrentTile(final double scalingFactor) {
    // // TOON scalingfactor volledig uit pilots houden
    // final int[] coord = new int[] { 0, 0 };
    // coord[0] = (int) (((Double) (getCurrentPositionAbsoluteX() -
    // getCurrentPositionAbsoluteX()
    // % sizeTile())).intValue()
    // * scalingFactor + (scalingFactor * sizeTile()) / 2);
    // coord[1] = (int) (((Double) (getCurrentPositionAbsoluteY() -
    // getCurrentPositionAbsoluteY()
    // % sizeTile())).intValue()
    // * scalingFactor + (scalingFactor * sizeTile()) / 2);
    // return coord;
    // }

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#getCurrentOrientation()
     */
    @Override
    public Orientation getOrientation() {
        return Orientation.calculateOrientation(getAngle());
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
     * @see simulator.pilot.PilotInterface#getCurrentPositionRelativeX()
     */
    @Override
    public int getPositionRelativeX() {
        return relativeX;
    }

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#getCurrentPositionRelativeY()
     */
    @Override
    public int getPositionRelativeY() {
        return relativeY;
    }

    /**
     * Dit is de marge ten opzichte van de edge wordt gebruikt in travel :
     * wanneer de robot op 1 pixel verwijderd van de edge is begint hij zijn
     * currentPositionRelative aan te passen afhankelijk van de
     * currentPositionAbsolute dit gebeurt in setCurrentTileCoordinates
     */
    private double getEdgeMarge() {
        // TODO dit moet nagekeken worden
        // return (double) 1.2 * scalingfactor
        return 0;
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
         * TODO pilot mag niets weten van absolute coordinaten. Deze methode
         * geeft de positie van de lichtsensor relatief ten opzichte van het
         * midden van de aandrijfas als een array [(naar voor positief in cm),
         * (naar rechts positief in cm)]
         */
        // return (getCurrentPositionAbsoluteX() + scalingfactor() * 7.5
        // * Math.cos(Math.toRadians(getAlpha())));
        return new double[2];
    }

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#getLightSensorValue()
     */
    @Override
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

    /*
     * public void setMapFile(File mapFile) { setMapFile(mapFile, 0, 0);
     * SSG.getInformationBuffer().setXCoordinateRelative(0);
     * SSG.getInformationBuffer().setYCoordinateRelative(0);
     * 
     * }
     */

    public MapGraph getMapGraphLoaded() {
        return mapGraphLoaded;
    }

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#getMapString()
     */
    @Override
    public String getMapString() {
        if (getMapGraphLoaded() == null) {
            return "/";
        }
        return mapFile.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#getSpeed()
     */
    @Override
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

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#getStartPositionAbsoluteX()
     */
    @Override
    public double getStartPositionAbsoluteX() {
        return startAbsolutePosition.getX();
    }

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#getStartPositionAbsoluteY()
     */
    @Override
    public double getStartPositionAbsoluteY() {
        return startAbsolutePosition.getY();
    }

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#getStartPositionRelativeX()
     */
    @Override
    public int getStartPositionRelativeX() {
        // TOON waarom 0 i.p.v. getstarting... als het gaat over de echte robot?
        // if (!isRobotSimulated()) {
        // return 0;
        // }
        return getMapGraphLoaded().getStartingTileCoordinates()[0];
    }

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#getStartPositionRelativeY()
     */
    @Override
    public int getStartPositionRelativeY() {
        // TOON waarom 0 i.p.v. getstarting... als het gaat over de echte robot?
        // if (!isRobotSimulated()) {
        // return 0;
        // }
        return getMapGraphLoaded().getStartingTileCoordinates()[1];
    }

    /*
     * (non-Javadoc)
     * 
     * @see simulator.pilot.PilotInterface#getUltraSensorValue()
     */
    @Override
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
         * TODO pilot mag niets weten van absolute coordinaten. Deze methode
         * geeft de positie van de lichtsensor relatief ten opzichte van het
         * midden van de aandrijfas als een array [(naar voor positief in cm),
         * (naar rechts positief in cm)]
         */
        // return (getCurrentPositionAbsoluteX() - scalingfactor() * 5.5
        // * Math.cos(Math.toRadians(getAlpha())));
        return new double[2];
    }

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
        // TOON 5 * sizeTile() komt nog van de versmelting met tekenpanel
        // (gewoon sizetile()/2?)
        absolutePosition.setLocation(5 * sizeTile() + sizeTile() / 2, 5
                * sizeTile() + sizeTile() / 2);
        startAbsolutePosition.setLocation(5 * sizeTile() + sizeTile() / 2, 5
                * sizeTile() + sizeTile() / 2);
        // startPositionAbsoluteX = 5 * sizeTile() + sizeTile() / 2;
        // startPositionAbsoluteY = 5 * sizeTile() + sizeTile() / 2;
        angle = 270;

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

            // TODO panel pollt pilot
            // getSimulationPanel().moveRobot(getCurrentPositionAbsoluteX(),
            // getCurrentPositionAbsoluteY(), alphaTemp);
            setAngle(alphaTemp);

            // getSSG().updateStatus();

            try {
                Thread.sleep(getRotateSleepTime());
            } catch (final InterruptedException e) {
            }
        }
    }

    protected abstract int getRotateSleepTime();

    /**
     * Deze methode zet de coordinaten van het echte systeem om in de
     * coordinaten van de matrix
     */
    public int[] setAbsoluteToRelative(final double x, final double y) {
        final double a = x
                - setToMultipleOfTileSize(startAbsolutePosition.getX());
        final double b = y
                - setToMultipleOfTileSize(startAbsolutePosition.getY());

        int c;
        int d;
        c = (int) Math.floor(a / sizeTile());
        d = (int) Math.floor(b / sizeTile());

        final int[] array = new int[2];
        array[0] = getStartPositionRelativeX() + c;
        array[1] = getStartPositionRelativeY() + d;

        return array;
    }

    public void setAngle(final double angle) {
        this.angle = angle;
    }

    public void setBarcode(final int barcode) {

        final Barcode scanned = new Barcode(barcode, getOrientation(),
                mapGraphConstructed.getCurrentTile());

        getMapGraphConstructed().getTileWithCoordinates(getPositionRelativeX(),
                getPositionRelativeY()).setContent(scanned);

        barcodes.add(scanned);
    }

    public void setCurrentAbsolutePosition(final double x, final double y) {
        absolutePosition.setLocation(x, y);
    }

    /**
     * zet dus de map terug op zijn juiste currenttilecoorinates berekend uit de
     * xOld en yOld xOld en yOld mogen eig enkel de huidige positie
     * voorstellen!!!!!!!!!!!!!! moeten hier ingegeven worden omdat bij de
     * travelmethode je de currentabsoluteposition pas terug juist zet op het
     * einde van de lus
     */
    public void setCurrentTileCoordinates(final MapGraph map,
            final double xOld, final double yOld) {
        final int[] relativePosition = setAbsoluteToRelative(xOld, yOld);
        map.setCurrentTileCoordinates(relativePosition[0], relativePosition[1]);
        setRelativeX(relativePosition[0]);
        setRelativeY(relativePosition[1]);
    }

    public void setCurrentTileCoordinatesRobot(final double xOld,
            final double yOld) {
        final int[] relativePosition = setAbsoluteToRelative(xOld, yOld);
        setRelativeX(relativePosition[0]);
        setRelativeY(relativePosition[1]);
    }

    public void setMapFile(File mapFile, final int xCo, final int yCo) {
        // TODO Hoort in simulatorPanel(overkoepelende)
        setMapGraph(MapReader.createMapFromFile(mapFile, xCo, yCo));
        startAbsolutePosition.setLocation(getAbsolutePosition().getX(),
                getAbsolutePosition().getY());
        // getSimulationPanel().clearTotal();
        setTile(xCo, yCo);
        setRelativeX(xCo);
        setRelativeY(yCo);

    }

    /**
     * Use this method only intern! If you want to change the map, use the
     * setMapFile-method! only used when you delete the map
     */
    public void setMapGraph(final MapGraph mapGraph) {
        if (mapGraph == null) {
            mapGraphLoaded = null;
            // getSimulationPanel().clearTotal();
        }
        mapGraphLoaded = mapGraph;
    }

    /**
     * Deze methode wordt voor het moment nog nergens gebruikt dus ook niet echt
     * veel getest kunnen fouten inzitten geeft het middelpunt van het vak weer
     * da overeenkomt met de coordinaten van de matrix die je moet ingeven als
     * argumenten
     */
    public double[] setRelativeToAbsolute(final int x, final int y) {
        final int a = x - getStartPositionRelativeX();
        final int b = y - getStartPositionRelativeY();
        final double c = a * 40;
        final double d = b * 40;
        final double[] array = new double[2];
        array[0] = startAbsolutePosition.getX() + c;
        // Moet dit niet getY() zijn?
        array[1] = startAbsolutePosition.getX() + d;
        return array;
    }

    private void setRelativeX(final int xCo) {
        relativeX = xCo;
    }

    private void setRelativeY(final int yCo) {
        relativeY = yCo;
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

    public void setTile(final int x, final int y) {
        getMapGraphConstructed().setTileXY(x, y, new Tile());
    }

    // zet een double om in een veelvoud van 40 kleiner dan de double (ook bij
    // negatief
    // maar doet normaal niet ter zake aangezien de coordinaten in het echte
    // coordinatensysteem
    // niet negatief kunnen zijn
    public int setToMultipleOfTileSize(final double a) {
        return (int) (Math.floor(a / sizeTile()) * sizeTile());
    }

    public void setWallOnTile(final int x, final int y,
            final Orientation orientation) {
        if (getMapGraphConstructed().getTileWithCoordinates(x, y) == null) {
            throw new IllegalArgumentException(
                    "in simulationPanel bij methode SetWallOnTile "
                            + "zijn coordinaten meegegeven die de mapgraph niet bevat nl"
                            + x + " en " + y);
        }
        getMapGraphConstructed().getTileWithCoordinates(x, y)
                .getEdge(orientation).setObstruction(Obstruction.WALL);
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
         * TODO travel: pilot in panel(viewport) maar panel niet in pilot -> dus
         * panel moet pollen
         */
        /*
         * TODO pilots mogen niets weten over scalingfactors (1 pilot kan in
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
                            && !getMapGraphLoaded().getCurrentTile()
                                    .getEdge(travelOrientation).isPassable()) {
                        setCurrentAbsolutePosition(
                                (xTemp - i
                                        * Math.cos(Math.toRadians(getAngle()))),
                                (yTemp - i
                                        * Math.sin(Math.toRadians(getAngle()))));
                        // getSSG().updateStatus();

                        System.out.println("Er staat een muur in de weg");
                        return;
                    } else {
                        travelToNextTileIfNeeded(xTemp, yTemp,
                                travelOrientation);
                    }
                }
            }
            setCurrentAbsolutePosition(xTemp, yTemp);

            distanceToGo = distanceToGo - i;
            if (distanceToGo < 1) {
                i = distanceToGo;
            }
            try {
                if (isRobotControllable()) {
                    Thread.sleep(speed / ((int) Math.ceil(Math.abs(distance))));
                } else {
                    if (getSpeed() == 1) {
                        Thread.sleep(10);
                    } else if (getSpeed() == 2) {
                        Thread.sleep(7);
                    }
                    if (getSpeed() == 3) {
                        Thread.sleep(5);
                    } else {
                        Thread.sleep(3);
                    }
                }
            } catch (final InterruptedException e) {
            }
        }

        // getSSG().updateStatus();
    }
    
    

    /**
     * Checkt of het een edge is gepasseerd zoja past hij zijn
     * currenttileCoordinates aan
     */
    private void travelToNextTileIfNeeded(final double xTemp,
            final double yTemp, final Orientation travelOrientation) {
        if (pointOnEdge(xTemp, yTemp)
                && getMapGraphLoaded().getCurrentTile()
                        .getEdge(travelOrientation).isPassable()) {
            setCurrentTileCoordinates(mapGraphLoaded, xTemp, yTemp);
        }
    }

    @Override
    public boolean isRobotControllable() {
        return true;
    }
}
