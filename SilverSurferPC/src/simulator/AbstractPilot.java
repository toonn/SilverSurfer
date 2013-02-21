package simulator;

import mapping.*;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.*;

import datastructures.Tuple;

public abstract class AbstractPilot {
    /**
     * verandert wanneer een nieuwe map wordt ingeladen naar de positie waar het
     * pijltje staat wanneer de map ingeladen wordt
     */
    private double startPositionAbsoluteX = 5 * sizeTile() + sizeTile() / 2;
    private double startPositionAbsoluteY = 5 * sizeTile() + sizeTile() / 2;
    /**
     * coordinaat in het echte assenstelsel van de robot
     */
    private double absoluteX = 5 * sizeTile() + sizeTile() / 2;
    private double absoluteY = 5 * sizeTile() + sizeTile() / 2;
    private int relativeX;
    private int relativeY;

    private double angle = 270;
    private int speed = 10;
    private File mapFile;
    private MapGraph mapGraphLoaded;
    private MapGraph mapGraphConstructed;
    private Set<Barcode> barcodes;

    // private int amtToSendToBuffer = 50;

    /**
     * waarde die afhangt van de robot!
     */
    private final double detectionDistanceUltrasonicSensorRobot = 28;
    private boolean robotControllable = true;
    private boolean robotSimulated = true;

    public AbstractPilot() {
        barcodes = new HashSet<Barcode>();
    }

    public double getCurrentPositionAbsoluteX() {
        return absoluteX;
    }

    public void setCurrentPositionAbsoluteX(double x) {
        absoluteX = x;
    }

    public double getCurrentPositionAbsoluteY() {
        return absoluteY;
    }

    public void setCurrentPositionAbsoluteY(double y) {
        absoluteY = y;
    }

    public double getStartPositionAbsoluteX() {
        return startPositionAbsoluteX;
    }

    public void setStartPositionAbsoluteX(double startPositionX) {
        startPositionAbsoluteX = startPositionX;
    }

    public double getStartPositionAbsoluteY() {
        return startPositionAbsoluteY;
    }

    public void setStartPositionAbsoluteY(double startPositionY) {
        startPositionAbsoluteY = startPositionY;
    }

    public int getCurrentPositionRelativeX() {
        return relativeX;
    }

    public int getCurrentPositionRelativeY() {
        return relativeY;
    }

    public int getStartPositionRelativeX() {
        if (!isRobotSimulated())
            return 0;
        return getMapGraphLoaded().getStartingTileCoordinates()[0];
    }

    public int getStartPositionRelativeY() {
        if (!isRobotSimulated())
            return 0;
        return getMapGraphLoaded().getStartingTileCoordinates()[1];
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

    public double[] getLightSensorAbsolute() {
        double[] absLS = new double[2];
        absLS[0] = getCurrentPositionAbsoluteX()
                + Math.cos(getLightsensorPlacement()[0])
                + Math.sin(getLightsensorPlacement()[1]);
        absLS[1] = getCurrentPositionAbsoluteY()
                + Math.sin(getLightsensorPlacement()[0])
                + Math.cos(getLightsensorPlacement()[1]);

        return absLS;
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

    public double[] getUltrasonicSensorAbsolute() {
        double[] absUS = new double[2];
        absUS[0] = getCurrentPositionAbsoluteX()
                + Math.cos(getUltrasonicSensorPlacement()[0])
                + Math.sin(getUltrasonicSensorPlacement()[1]);
        absUS[1] = getCurrentPositionAbsoluteY()
                + Math.sin(getUltrasonicSensorPlacement()[0])
                + Math.cos(getUltrasonicSensorPlacement()[1]);

        return absUS;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
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
            speed = 48;
        else if (speed == 3)
            speed = 58;
        else if (speed == 2)
            speed = 86;
        else
            speed = 194;
    }

    /**
     * Returns the center of the currentTile in absolutes.
     */
    public int[] getCenterAbsoluteCurrentTile(double scalingFactor) {
        // TOON scalingfactor volledig uit pilots houden
        int[] coord = new int[] { 0, 0 };
        coord[0] = (int) (((Double) (getCurrentPositionAbsoluteX() - getCurrentPositionAbsoluteX()
                % sizeTile())).intValue()
                * scalingFactor + (scalingFactor * sizeTile()) / 2);
        coord[1] = (int) (((Double) (getCurrentPositionAbsoluteY() - getCurrentPositionAbsoluteY()
                % sizeTile())).intValue()
                * scalingFactor + (scalingFactor * sizeTile()) / 2);
        return coord;
    }

    public File getMapFile() {
        return mapFile;
    }

    /*
     * public void setMapFile(File mapFile) { setMapFile(mapFile, 0, 0);
     * SSG.getInformationBuffer().setXCoordinateRelative(0);
     * SSG.getInformationBuffer().setYCoordinateRelative(0);
     * 
     * }
     */

    public void setMapFile(File mapFile, int xCo, int yCo) {
        // TODO Hoort in simulatorPanel(overkoepelende)
        mapFile = mapFile;
        setMapGraph(MapReader.createMapFromFile(mapFile, xCo, yCo));
        startPositionAbsoluteX = getCurrentPositionAbsoluteX();
        startPositionAbsoluteY = getCurrentPositionAbsoluteY();
        // getSimulationPanel().clearTotal();
        setTile(xCo, yCo);
        setRelativeX(xCo);
        setRelativeY(yCo);

    }

    private void setRelativeX(int xCo) {
        relativeX = xCo;
    }

    private void setRelativeY(int yCo) {
        relativeY = yCo;
    }

    public MapGraph getMapGraphLoaded() {
        return mapGraphLoaded;
    }

    public MapGraph getMapGraphConstructed() {
        return mapGraphConstructed;
    }

    public String getMapString() {
        if (getMapGraphLoaded() == null) {
            return "/";
        }
        return mapFile.getName();
    }

    /**
     * Use this method only intern! If you want to change the map, use the
     * setMapFile-method! only used when you delete the map
     */
    public void setMapGraph(MapGraph mapGraph) {
        if (mapGraph == null) {
            mapGraphLoaded = null;
            // getSimulationPanel().clearTotal();
        }
        mapGraphLoaded = mapGraph;
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

    /**
     * Moet deze Pilot de robot aansturen? (echte robot of zelfgesimuleerde
     * robot)
     */
    public boolean isRobotControllable() {
        return robotControllable;
    }

    /**
     * Zelf simuleren sensoren?
     */
    public boolean isRobotSimulated() {
        return robotSimulated;
    }

    public void travel(double distance) {
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
        double xTemp = getCurrentPositionAbsoluteX();
        double yTemp = getCurrentPositionAbsoluteY();

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
            xTemp = (double) (xTemp + i * Math.cos(Math.toRadians(getAngle())));
            yTemp = (double) (yTemp + i * Math.sin(Math.toRadians(getAngle())));

            if (mapGraphLoaded != null) {

                if (robotOnEdge(xTemp, yTemp, getAngle())) {
                    Orientation edgeOrientation = pointOnWichSideOfTile(xTemp,
                            yTemp, travelOrientation);

                    // the edge you are standing on contains a wall
                    if (travelOrientation == edgeOrientation
                            && !getMapGraphLoaded().getCurrentTile()
                                    .getEdge(travelOrientation).isPassable()) {
                        setCurrentPositionAbsoluteX((xTemp - i
                                * Math.cos(Math.toRadians(getAngle()))));
                        setCurrentPositionAbsoluteY((yTemp - i
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
            setCurrentPositionAbsoluteX(xTemp);
            setCurrentPositionAbsoluteY(yTemp);

            distanceToGo = distanceToGo - i;
            if (distanceToGo < 1) {
                i = distanceToGo;
            }
            try {
                if (isRobotControllable())
                    Thread.sleep(speed / ((int) Math.ceil(Math.abs(distance))));
                else {
                    if (getSpeed() == 1)
                        Thread.sleep(10);
                    else if (getSpeed() == 2)
                        Thread.sleep(7);
                    if (getSpeed() == 3)
                        Thread.sleep(5);
                    else
                        Thread.sleep(3);
                }
            } catch (InterruptedException e) {
            }
        }

        // getSSG().updateStatus();
    }

    /**
     * Checkt of het een edge is gepasseerd zoja past hij zijn
     * currenttileCoordinates aan
     */
    private void travelToNextTileIfNeeded(double xTemp, double yTemp,
            Orientation travelOrientation) {
        if (pointOnEdge(xTemp, yTemp)
                && getMapGraphLoaded().getCurrentTile()
                        .getEdge(travelOrientation).isPassable()) {
            setCurrentTileCoordinates(mapGraphLoaded, xTemp, yTemp);
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
                Orientation currentOrientation = Orientation
                        .calculateOrientation(getAngle());
                int xCoordinate = mapGraphLoaded.getCurrentTileCoordinates()[0]
                        + currentOrientation.getArrayToFindNeighbourRelative()[0];
                int yCoordinate = mapGraphLoaded.getCurrentTileCoordinates()[1]
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
            Orientation currentOrientation = Orientation
                    .calculateOrientation(getAngle());
            int xCoordinate = mapGraphLoaded.getCurrentTileCoordinates()[0]
                    + currentOrientation.getArrayToFindNeighbourRelative()[0];
            int yCoordinate = mapGraphLoaded.getCurrentTileCoordinates()[1]
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

    /**
     * checkt of de robot een obstruction ZIET
     */

    public boolean checkForObstruction() {
        int distance = getUltraSensorValue();

        if (distance < detectionDistanceUltrasonicSensorRobot) {
            return true;
        }

        return false;
    }

    public void checkForObstructionAndSetTile() {
        if (checkForObstruction())
            addWall();
        else {
            Orientation currentOrientation = Orientation
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
                    yCoordinate) == null)
                setTile(xCoordinate, yCoordinate);
        }
    }

    public void addWall() {
        Orientation currentOrientation = Orientation
                .calculateOrientation(getAngle());

        /* TODO panel moet pilot pollen voor ALLES(positie, muren...) */
        // getSimulationPanel().addWall(currentOrientation,
        // getCurrentPositionAbsoluteX(), getCurrentPositionAbsoluteY());
        setWallOnTile(getCurrentPositionRelativeX(),
                getCurrentPositionRelativeY(), currentOrientation);
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

    public void rotate(double alpha) {

        // rotatedInTotal = rotatedInTotal + Math.abs(alpha);
        // System.out.println("rotatedInTotal : " + rotatedInTotal);

        double alphaOriginal = getAngle();
        double alphaTemp = getAngle();

        int j = 1;
        if (alpha < 0)
            j = -1;

        for (int i = j; i * j <= alpha * j; i += j) {
            alphaTemp = (double) ExtMath.addDegree(alphaOriginal, i);

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
                if (isRobotControllable())
                    Thread.sleep(speed / 10);
                else {
                    if (getSpeed() == 1)
                        Thread.sleep(4);
                    else if (getSpeed() == 2)
                        Thread.sleep(3);
                    if (getSpeed() == 3)
                        Thread.sleep(2);
                    else
                        Thread.sleep(1);
                }
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * checkt of de robot zich binnen de marge van een edge bevindt
     */
    public boolean pointOnEdge(double x, double y) {
        return (x % sizeTile()) > sizeTile() - getEdgeMarge()
                || (x % sizeTile()) < getEdgeMarge()
                || (y % sizeTile()) > sizeTile() - getEdgeMarge()
                || (y % sizeTile()) < getEdgeMarge();
    }

    /**
     * Checks whether the given point is on the edge of a tile.
     */
    public Orientation pointOnWichSideOfTile(double x, double y,
            Orientation travelOrientation) {
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

    /**
     * Checks whether the robot, standig on the given point, is on the edge of a
     * tile. The robot is interpreted as a rectangle around the given position.
     */
    public boolean robotOnEdge(double x, double y, double alpha) {
        double leftFrontX = (x - 12 * Math.cos(Math.toRadians(alpha - 45)));
        double leftFrontY = (y + 12 * Math.sin(Math.toRadians(alpha - 45)));

        double rightFrontX = (x - 12 * Math.cos(Math.toRadians(alpha + 45)));
        double rightFrontY = (y + 12 * Math.sin(Math.toRadians(alpha + 45)));

        double leftBackX = (x - 13 * Math.cos(Math.toRadians(alpha - 180 + 30)));
        double leftBackY = (y + 13 * Math.sin(Math.toRadians(alpha - 180 + 30)));

        double rightBackX = (x - 13 * Math
                .cos(Math.toRadians(alpha - 180 - 30)));
        double rightBackY = (y + 13 * Math
                .sin(Math.toRadians(alpha - 180 - 30)));

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

    /**
     * True if the robot is not on an edge, but on a tile containing a barcode.
     */
    public boolean onBarcodeTile(double x, double y) {
        if (getMapGraphLoaded() == null) {
            // System.out.println("b: /");
            return false;
        } else {
            // System.out.println("b: " + (!onEdge(x,y) &&
            // (getMapGraph().getContentCurrentTile() instanceof
            // Barcode)));
            return !pointOnEdge(x, y)
                    && (getMapGraphLoaded().getContentCurrentTile() instanceof Barcode);
        }
    }

    // zet een double om in een veelvoud van 40 kleiner dan de double (ook bij
    // negatief
    // maar doet normaal niet ter zake aangezien de coordinaten in het echte
    // coordinatensysteem
    // niet negatief kunnen zijn
    public int setToMultipleOfTileSize(double a) {
        return (int) (Math.floor(a / sizeTile()) * sizeTile());
    }

    /**
     * Deze methode zet de coordinaten van het echte systeem om in de
     * coordinaten van de matrix
     */
    public int[] setAbsoluteToRelative(double x, double y) {
        double a = x - setToMultipleOfTileSize(startPositionAbsoluteX);
        double b = y - setToMultipleOfTileSize(startPositionAbsoluteY);

        int c;
        int d;
        c = (int) Math.floor(a / sizeTile());
        d = (int) Math.floor(b / sizeTile());

        int[] array = new int[2];
        array[0] = getStartPositionRelativeX() + c;
        array[1] = getStartPositionRelativeY() + d;

        return array;
    }

    /**
     * Deze methode wordt voor het moment nog nergens gebruikt dus ook niet echt
     * veel getest kunnen fouten inzitten geeft het middelpunt van het vak weer
     * da overeenkomt met de coordinaten van de matrix die je moet ingeven als
     * argumenten
     */
    public double[] setRelativeToAbsolute(int x, int y) {
        int a = x - getStartPositionRelativeX();
        int b = y - getStartPositionRelativeY();
        double c = a * 40;
        double d = b * 40;
        double[] array = new double[2];
        array[0] = startPositionAbsoluteX + c;
        array[1] = startPositionAbsoluteX + d;
        return array;
    }

    /**
     * zet dus de map terug op zijn juiste currenttilecoorinates berekend uit de
     * xOld en yOld xOld en yOld mogen eig enkel de huidige positie
     * voorstellen!!!!!!!!!!!!!! moeten hier ingegeven worden omdat bij de
     * travelmethode je de currentabsoluteposition pas terug juist zet op het
     * einde van de lus
     */
    public void setCurrentTileCoordinates(MapGraph map, double xOld, double yOld) {
        int[] relativePosition = setAbsoluteToRelative(xOld, yOld);
        map.setCurrentTileCoordinates(relativePosition[0], relativePosition[1]);
        setRelativeX(relativePosition[0]);
        setRelativeY(relativePosition[1]);
    }

    public void setCurrentTileCoordinatesRobot(double xOld, double yOld) {
        int[] relativePosition = setAbsoluteToRelative(xOld, yOld);
        setRelativeX(relativePosition[0]);
        setRelativeY(relativePosition[1]);
    }

    public void clear() {
        /*
         * TOON clear hoort niet in pilot maar in simulatorpanel(clear
         * mapgraphconstructed?)
         */
        // getSimulationPanel().clearPath();
    }

    /**
     * Resets the currentPositionAbsolute's and the startPositionAbsolute's to
     * 220. Resets alpha to 270, speed to 10;
     */
    public void reset() {
        absoluteX = 5 * sizeTile() + sizeTile() / 2;
        absoluteY = 5 * sizeTile() + sizeTile() / 2;
        startPositionAbsoluteX = 5 * sizeTile() + sizeTile() / 2;
        startPositionAbsoluteY = 5 * sizeTile() + sizeTile() / 2;
        angle = 270;

    }

    public void updateArc(int distance) {
        /* TODO panel pollt pilot */
        // getSimulationPanel().updateArc(getCurrentPositionAbsoluteX(),
        // getCurrentPositionAbsoluteY(), getAlpha(), distance);
    }

    public void setRobotControllable(boolean isRealRobot) {
        robotControllable = isRealRobot;
    }

    /**
     * Returns a number from a normal districution that represents a lightsensor
     * value.
     */
    public abstract int getLightSensorValue();

    public abstract int getUltraSensorValue();

    public void allignOnWhiteLine() {
        double[] absLS = getLightSensorAbsolute();
        if (isRobotControllable()) {
            while (!pointOnEdge(absLS[0], absLS[1])) {
                travel(1);
            }

            travel(5);

            while (!pointOnEdge(absLS[0], absLS[1])) {
                rotate(-1);
            }

            rotate(90);

            int i = 0;

            while (!pointOnEdge(absLS[0], absLS[1])) {
                rotate(1);
                i++;
            }

            rotate(-(90 + i) / 2);
        } else
            travel(16);
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

    public Orientation getCurrentOrientation() {
        return Orientation.calculateOrientation(getAngle());
    }

    public double sizeTile() {
        return 40;
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
    public void addBarcode(ViewPort simulatorPanel, Barcode barcode,
            Rectangle2D[] visual) {
        simulatorPanel.getVisibleBarcode().add(
                new Tuple<Barcode, Rectangle2D[]>(barcode, visual));
    }

    public Set<Barcode> getBarcodes() {
        return barcodes;
    }

    public void setBarcode(int barcode) {

        Barcode scanned = new Barcode(barcode, getCurrentOrientation(),
                mapGraphConstructed.getCurrentTile());

        getMapGraphConstructed().getTileWithCoordinates(
                getCurrentPositionRelativeX(), getCurrentPositionRelativeY())
                .setContent(scanned);

        barcodes.add(scanned);
    }

    public void setWallOnTile(int x, int y, Orientation orientation) {
        if (getMapGraphConstructed().getTileWithCoordinates(x, y) == null)
            throw new IllegalArgumentException(
                    "in simulationPanel bij methode SetWallOnTile "
                            + "zijn coordinaten meegegeven die de mapgraph niet bevat nl"
                            + x + " en " + y);
        getMapGraphConstructed().getTileWithCoordinates(x, y)
                .getEdge(orientation).setObstruction(Obstruction.WALL);
    }

    public void setTile(int x, int y) {
        getMapGraphConstructed().setTileXY(x, y, new Tile());
    }
}
