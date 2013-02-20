package simulator;

import gui.SilverSurferGUI;
import mapping.*;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Random;

import datastructures.Tuple;

public class SimulationPilot {
    private SimulatorViewPort simulationPanel;
    /**
     * verandert wanneer een nieuwe map wordt ingeladen naar de positie waar het
     * pijltje staat wanneer de map ingeladen wordt
     */
    private double startPositionAbsoluteX = 5 * sizeTile() + sizeTile() / 2;
    private double startPositionAbsoluteY = 5 * sizeTile() + sizeTile() / 2;
    /**
     * coordinaat in het echte assenstelsel van de robot
     */
    private double currentPositionAbsoluteX = 5 * sizeTile() + sizeTile() / 2;
    private double currentPositionAbsoluteY = 5 * sizeTile() + sizeTile() / 2;

    private double alpha = 270;
    private int speed = 10;
    private File mapFile;
    private MapGraph mapGraphLoaded;
    private MapGraph mapGraphConstructed = new MapGraph();

    // private int amtToSendToBuffer = 50;

    /**
     * waarde die afhangt van de robot!
     */
    private final double detectionDistanceUltrasonicSensorRobot = 28;
    private boolean robotControllable = true;
    private boolean robotSimulated = true;

    public SimulationPilot(SimulatorViewPort simulationPanel) {
        this.simulationPanel = simulationPanel;
        getSimulationPanel().addPathPoint(getCurrentPositionAbsoluteX(),
                getCurrentPositionAbsoluteY());

    }

    public double getCurrentPositionAbsoluteX() {
        return currentPositionAbsoluteX;
    }

    public void setCurrentPositionAbsoluteX(double x) {
        this.currentPositionAbsoluteX = x;
    }

    public double getCurrentPositionAbsoluteY() {
        return currentPositionAbsoluteY;
    }

    public void setCurrentPositionAbsoluteY(double y) {
        this.currentPositionAbsoluteY = y;
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
        if (!isRobotSimulated())
            return SilverSurferGUI.getStatusInfoBuffer()
                    .getXCoordinateRelative();
        return this.getMapGraphLoaded().getCurrentTileCoordinates()[0];
    }

    public int getCurrentPositionRelativeY() {
        if (!isRobotSimulated())
            return SilverSurferGUI.getStatusInfoBuffer()
                    .getYCoordinateRelative();
        return this.getMapGraphLoaded().getCurrentTileCoordinates()[1];
    }

    public int getStartPositionRelativeX() {
        if (!isRobotSimulated())
            return 0;
        return this.getMapGraphLoaded().getStartingTileCoordinates()[0];
    }

    public int getStartPositionRelativeY() {
        if (!isRobotSimulated())
            return 0;
        return this.getMapGraphLoaded().getStartingTileCoordinates()[1];
    }

    /**
     * The lightsensor is not attached on the middle point of the robot, but
     * more in front of that point. This value gives the x-coordinate of the
     * lightsensor.
     */
    public double getLightsensorPositionX() {
        return (this.getCurrentPositionAbsoluteX() + scalingfactor() * 7.5
                * Math.cos(Math.toRadians(this.getAlpha())));
    }

    /**
     * The lightsensor is not attached on the middle point of the robot, but
     * more in front of that point. This value gives they -coordinate of the
     * lightsensor.
     */
    public double getLightsensorPositionY() {
        return (this.getCurrentPositionAbsoluteY() + scalingfactor() * 7.5
                * Math.sin(Math.toRadians(this.getAlpha())));
    }

    /**
     * The ultrasonic sensor is not attached on the middle point of the robot,
     * but a little behind that point. This value gives the x-coordinate of the
     * ultrasonic sensor.
     */
    public double getUltrasonicSensorPositionX() {
        return (this.getCurrentPositionAbsoluteX() - scalingfactor() * 5.5
                * Math.cos(Math.toRadians(this.getAlpha())));
    }

    /**
     * The ultrasonic sensor is not attached on the middle point of the robot,
     * but a little behind that point. This value gives the y-coordinate of the
     * ultrasonic sensor.
     */
    public double getUltrasonicSensorPositionY() {
        return (this.getCurrentPositionAbsoluteY() - scalingfactor() * 5.5
                * Math.sin(Math.toRadians(this.getAlpha())));
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
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

    /**
     * Returns the center of the currentTile in absolutes.
     */
    public int[] getCenterAbsoluteCurrentTile() {

        int[] coord = new int[] { 0, 0 };
        coord[0] = (int) (((Double) (getCurrentPositionAbsoluteX() - getCurrentPositionAbsoluteX()
                % sizeTile())).intValue()
                * getSimulationPanel().getScalingfactor() + getSimulationPanel()
                .getSizeTile() / 2);
        coord[1] = (int) (((Double) (getCurrentPositionAbsoluteY() - getCurrentPositionAbsoluteY()
                % sizeTile())).intValue()
                * getSimulationPanel().getScalingfactor() + getSimulationPanel()
                .getSizeTile() / 2);
        return coord;
    }

    public File getMapFile() {
        return this.mapFile;
    }

    /*
     * public void setMapFile(File mapFile) { this.setMapFile(mapFile, 0, 0);
     * SSG.getInformationBuffer().setXCoordinateRelative(0);
     * SSG.getInformationBuffer().setYCoordinateRelative(0);
     * 
     * }
     */

    public void setMapFile(File mapFile, int xCo, int yCo) {
        this.mapFile = mapFile;
        this.setMapGraph(MapReader.createMapFromFile(mapFile, xCo, yCo));
        this.startPositionAbsoluteX = getCurrentPositionAbsoluteX();
        this.startPositionAbsoluteY = getCurrentPositionAbsoluteY();
        getSimulationPanel().clearTotal();
        setTile(xCo, yCo);
        SilverSurferGUI.getStatusInfoBuffer().setXCoordinateRelative(xCo);
        SilverSurferGUI.getStatusInfoBuffer().setYCoordinateRelative(yCo);

    }

    public MapGraph getMapGraphLoaded() {
        return this.mapGraphLoaded;
    }

    public MapGraph getMapGraphConstructed() {
        return this.mapGraphConstructed;
    }

    public String getMapString() {
        if (this.getMapGraphLoaded() == null) {
            return "/";
        }
        return this.mapFile.getName();
    }

    /**
     * Use this method only intern! If you want to change the map, use the
     * setMapFile-method! only used when you delete the map
     */
    public void setMapGraph(MapGraph mapGraph) {
        if (mapGraph == null) {
            this.mapGraphLoaded = null;
            getSimulationPanel().clearTotal();
        }
        this.mapGraphLoaded = mapGraph;
    }

    /**
     * Dit is de marge ten opzichte van de edge wordt gebruikt in travel :
     * wanneer de robot op 1 pixel verwijderd van de edge is begint hij zijn
     * currentPositionRelative aan te passen afhankelijk van de
     * currentPositionAbsolute dit gebeurt in setCurrentTileCoordinates
     */
    private double getEdgeMarge() {
        return (double) 1.2 * scalingfactor();
    }

    public SimulatorViewPort getSimulationPanel() {
        return simulationPanel;
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
        distance = distance * scalingfactor();
        // travelledInTotal = travelledInTotal + Math.abs(distance);
        // System.out.println("travelledInTotal : " + travelledInTotal);
        // double xOriginal = this.getCurrentPositionAbsoluteX();
        // double yOriginal = this.getCurrentPositionAbsoluteY();
        double xTemp = this.getCurrentPositionAbsoluteX();
        double yTemp = this.getCurrentPositionAbsoluteY();

        double j = 1;
        Orientation travelOrientation = Orientation.calculateOrientation(xTemp,
                yTemp, this.getAlpha(), sizeTile());

        // if you are traveling backwards, the orientation you are facing is the
        // opposite to the orientation you are traveling.
        if (distance < 0) {
            j = -1;
            travelOrientation = travelOrientation.getOppositeOrientation();
        }

        double distanceToGo = distance;
        double i = j;

        getSimulationPanel().addPathPoint(xTemp, yTemp);

        while (distanceToGo != 0)
        // for (double i = j; i*j <= distance*j; i+=j)
        {
            xTemp = (double) (xTemp + i
                    * Math.cos(Math.toRadians(this.getAlpha())));
            yTemp = (double) (yTemp + i
                    * Math.sin(Math.toRadians(this.getAlpha())));

            if (mapGraphLoaded != null) {

                if (robotOnEdge(xTemp, yTemp, this.getAlpha())) {
                    Orientation edgeOrientation = this.pointOnWichSideOfTile(
                            xTemp, yTemp, travelOrientation);

                    // the edge you are standing on contains a wall
                    if (travelOrientation == edgeOrientation
                            && !this.getMapGraphLoaded().getCurrentTile()
                                    .getEdge(travelOrientation).isPassable()) {
                        this.setCurrentPositionAbsoluteX((xTemp - i
                                * Math.cos(Math.toRadians(this.getAlpha()))));
                        this.setCurrentPositionAbsoluteY((yTemp - i
                                * Math.sin(Math.toRadians(this.getAlpha()))));
                        // this.getSSG().updateStatus();

                        System.out.println("Er staat een muur in de weg");
                        return;
                    } else {
                        this.travelToNextTileIfNeeded(xTemp, yTemp,
                                travelOrientation);
                    }
                }
            }
            getSimulationPanel().moveRobot(xTemp, yTemp, this.getAlpha());
            this.setCurrentPositionAbsoluteX(xTemp);
            this.setCurrentPositionAbsoluteY(yTemp);

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

        // this.getSSG().updateStatus();
    }

    /**
     * Checkt of het een edge is gepasseerd zoja past hij zijn
     * currenttileCoordinates aan
     */
    private void travelToNextTileIfNeeded(double xTemp, double yTemp,
            Orientation travelOrientation) {
        if (pointOnEdge(xTemp, yTemp)
                && this.getMapGraphLoaded().getCurrentTile()
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
                        .calculateOrientation(
                                this.getCurrentPositionAbsoluteX(),
                                this.getCurrentPositionAbsoluteY(),
                                this.getAlpha(), sizeTile());
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
            Orientation currentOrientation = Orientation.calculateOrientation(
                    this.getCurrentPositionAbsoluteX(),
                    this.getCurrentPositionAbsoluteY(), this.getAlpha(),
                    sizeTile());
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
        /* Orientation currentOrientation = */Orientation
                .calculateOrientation(this.getCurrentPositionAbsoluteX(),
                        this.getCurrentPositionAbsoluteY(), this.getAlpha(),
                        sizeTile());

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
            Orientation currentOrientation = Orientation.calculateOrientation(
                    this.getCurrentPositionAbsoluteX(),
                    this.getCurrentPositionAbsoluteY(), this.getAlpha(),
                    sizeTile());
            int xCoordinate;
            int yCoordinate;
            if (isRobotControllable()) {
                xCoordinate = SilverSurferGUI.getStatusInfoBuffer()
                        .getXCoordinateRelative()
                        + currentOrientation.getArrayToFindNeighbourRelative()[0];
                yCoordinate = SilverSurferGUI.getStatusInfoBuffer()
                        .getYCoordinateRelative()
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
                .calculateOrientation(this.getCurrentPositionAbsoluteX(),
                        this.getCurrentPositionAbsoluteY(), this.getAlpha(),
                        sizeTile());

        getSimulationPanel().addWall(currentOrientation,
                getCurrentPositionAbsoluteX(), getCurrentPositionAbsoluteY());
        setWallOnTile(getCurrentPositionRelativeX(),
                getCurrentPositionRelativeY(), currentOrientation);
    }

    // public void removeWall(){
    //
    // Orientation currentOrientation = Orientation.calculateOrientation(
    // this.getCurrentPositionAbsoluteX(),
    // this.getCurrentPositionAbsoluteY(), this.getAlpha(), sizeTile());
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

        double alphaOriginal = this.getAlpha();
        double alphaTemp = this.getAlpha();

        int j = 1;
        if (alpha < 0)
            j = -1;

        for (int i = j; i * j <= alpha * j; i += j) {
            alphaTemp = (double) ExtMath.addDegree(alphaOriginal, i);

            /*
             * if (this.getMapGraph() != null) { if
             * (robotOnEdge(this.getCurrentPositionAbsoluteX(),
             * this.getCurrentPositionAbsoluteY(), alphaTemp)) { // the edge you
             * are standing on contains a wall // weet niet goed hoe je dit kan
             * checken //
             * if(!(this.getMapGraph().canMoveTo(Orientation.calculateOrientation
             * (this.getCurrentPositionAbsoluteX(), //
             * this.getCurrentPositionAbsoluteY(),
             * ExtMath.addDegree(alphaTemp,j*30))) // &&
             * this.getMapGraph().canMoveTo
             * (Orientation.calculateOrientation(this
             * .getCurrentPositionAbsoluteX(), //
             * this.getCurrentPositionAbsoluteY(),
             * ExtMath.addDegree(alphaTemp,j*210))))) // { //
             * this.setAlpha((double) ExtMath.addDegree(alphaOriginal,i-j)); //
             * this.getSSG().updateStatus(); // //
             * System.out.println("Er staat een muur in de weg"); // return; //
             * } } }
             */

            getSimulationPanel().moveRobot(this.getCurrentPositionAbsoluteX(),
                    this.getCurrentPositionAbsoluteY(), alphaTemp);
            this.setAlpha(alphaTemp);
            SilverSurferGUI.getStatusInfoBuffer().setAngle(alphaTemp);

            // this.getSSG().updateStatus();

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
        return (x % sizeTile()) > sizeTile() - this.getEdgeMarge()
                || (x % sizeTile()) < this.getEdgeMarge()
                || (y % sizeTile()) > sizeTile() - this.getEdgeMarge()
                || (y % sizeTile()) < this.getEdgeMarge();
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
     * True if the robot is on an edge and this edge is not a wall
     */
    public boolean onWhiteLine(double x, double y) {
        // System.out.println("w: " + (this.onEdge(x,y) && (this.getMapGraph()
        // == null ||
        // this.getMapGraph().getObstruction(Orientation.calculateOrientation(x,
        // y, this.getAlpha())) != Obstruction.WALL)));
        return this.pointOnEdge(x, y)
                && (this.getMapGraphLoaded() == null || this
                        .getMapGraphLoaded().getObstruction(
                                Orientation.calculateOrientation(x, y,
                                        this.getAlpha(), sizeTile())) != Obstruction.WALL);

    }

    /**
     * True if the robot is not on an edge, but on a tile without a content.
     */
    public boolean onEmptyTile(double x, double y) {

        return (!this.pointOnEdge(x, y) && this.getMapGraphLoaded() == null)
                || (!this.pointOnEdge(x, y) && this.getMapGraphLoaded()
                        .getContentCurrentTile() == null);

    }

    /**
     * True if the robot is not on an edge, but on a tile containing a barcode.
     */
    public boolean onBarcodeTile(double x, double y) {
        if (this.getMapGraphLoaded() == null) {
            // System.out.println("b: /");
            return false;
        } else {
            // System.out.println("b: " + (!this.onEdge(x,y) &&
            // (this.getMapGraph().getContentCurrentTile() instanceof
            // Barcode)));
            return !this.pointOnEdge(x, y)
                    && (this.getMapGraphLoaded().getContentCurrentTile() instanceof Barcode);
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
        SilverSurferGUI.getStatusInfoBuffer().setXCoordinateRelative(
                relativePosition[0]);
        SilverSurferGUI.getStatusInfoBuffer().setYCoordinateRelative(
                relativePosition[1]);
    }

    public void setCurrentTileCoordinatesRobot(double xOld, double yOld) {
        int[] relativePosition = setAbsoluteToRelative(xOld, yOld);
        SilverSurferGUI.getStatusInfoBuffer().setXCoordinateRelative(
                relativePosition[0]);
        SilverSurferGUI.getStatusInfoBuffer().setYCoordinateRelative(
                relativePosition[1]);
    }

    public void clear() {
        getSimulationPanel().clearPath();
    }

    /**
     * Resets the currentPositionAbsolute's and the startPositionAbsolute's to
     * 220. Resets alpha to 270, speed to 10;
     */
    public void reset() {
        currentPositionAbsoluteX = 5 * sizeTile() + sizeTile() / 2;
        currentPositionAbsoluteY = 5 * sizeTile() + sizeTile() / 2;
        startPositionAbsoluteX = 5 * sizeTile() + sizeTile() / 2;
        startPositionAbsoluteY = 5 * sizeTile() + sizeTile() / 2;
        alpha = 270;

    }

    public void updateArc(int distance) {
        getSimulationPanel().updateArc(getCurrentPositionAbsoluteX(),
                getCurrentPositionAbsoluteY(), getAlpha(), distance);
    }

    public void setRobotControllable(boolean isRealRobot) {
        this.robotControllable = isRealRobot;
    }

    /**
     * Returns a number from a normal districution that represents a lightsensor
     * value.
     */
    public int getLightSensorValue() {
        if (!this.isRobotSimulated())
            return SilverSurferGUI.getStatusInfoBuffer()
                    .getLatestLightSensorInfo();
        else {
            // initialisation
            Random random = new Random();
            double mean = 0;
            double standardDeviation = 1;

            // check on which sort of underground your are standing
            // and adjust the mean and standardDeviation accordingly
            if (onEmptyTile(getLightsensorPositionX(),
                    getLightsensorPositionY())) {
                mean = SimulationSensorData.getMEmptyPanelLS();
                standardDeviation = SimulationSensorData.getSDEmptyPanelLS();
            } else if (onWhiteLine(getLightsensorPositionX(),
                    getLightsensorPositionY())) {
                mean = SimulationSensorData.getMWhiteLineLS();
                standardDeviation = SimulationSensorData.getSDWhiteLineLS();
            } else if (onBarcodeTile(getLightsensorPositionX(),
                    getLightsensorPositionY())) {
                int color = ((Barcode) this.getMapGraphLoaded()
                        .getContentCurrentTile()).getColorValue(
                        getLightsensorPositionX() % 40,
                        getLightsensorPositionY() % 40);
                mean = SimulationSensorData.getMBarcodeTileLS(color);
                standardDeviation = SimulationSensorData
                        .getSDBarcodeTileLS(color);
            }
            return (int) Math.round(mean
                    + (random.nextGaussian() * standardDeviation));

        }
    }

    public int getUltraSensorValue() {
        if (!this.isRobotSimulated()) {
            return SilverSurferGUI.getStatusInfoBuffer()
                    .getLatestUltraSensorInfo();
        } else {
            Random random = new Random();

            double mean = this.calculateDistanceToWall();
            double standardDeviation = SimulationSensorData.getSDUS();

            return (int) Math.round(mean
                    + (random.nextGaussian() * standardDeviation));
        }
    }

    /**
     * Calculates the distance to the first wall the robot will encounter facing
     * its current orientation. Returns 250 if no map is loaded or no wall is
     * found whithin the range of the sensor. The maximum range of the sensor is
     * 120 cm.
     * 
     * By virtually moving forward (the temporary coordinates) en on every
     * border checking whether there is a wall. If so, you calculate the
     * distance to is. If not, keep om moving (the robot doesn't move!)
     */
    private double calculateDistanceToWall() {
        // current temporary position; to check whether there are walls in the
        // direction the robot is facing
        double xTemp = this.getUltrasonicSensorPositionX();
        double yTemp = this.getUltrasonicSensorPositionY();
        // keep the last temporary position, so you can compare with the current
        // temporary position
        double xTempPrev = this.getUltrasonicSensorPositionX();
        double yTempPrev = this.getUltrasonicSensorPositionY();

        // there is no map loaded, so the sensor will detect no walls en returns
        // the maximum value.
        if (this.getMapGraphLoaded() == null) {
            return 250;
        }
        Tile tileTemp = this.getMapGraphLoaded().getCurrentTile();
        int i = 1;

        while (i < 148) {
            while (!(Math.abs(xTempPrev % sizeTile() - xTemp % sizeTile()) > 5)
                    && !(Math.abs(yTempPrev % sizeTile() - yTemp % sizeTile()) > 5)) {
                xTempPrev = xTemp;
                yTempPrev = yTemp;

                xTemp = (double) (this.getUltrasonicSensorPositionX() + i
                        * Math.cos(Math.toRadians(this.getAlpha())));
                yTemp = (double) (this.getUltrasonicSensorPositionY() + i
                        * Math.sin(Math.toRadians(this.getAlpha())));
                i++;
            }

            Orientation oriTemp = Orientation.defineBorderCrossed(xTemp, yTemp,
                    xTempPrev, yTempPrev, sizeTile());

            // the edge you have found, does not contain a wall, you can look
            // right over it.
            // change the current tile to the next tile en move a few steps
            // foreward (with the temporary coordinates).
            if (tileTemp.getEdge(oriTemp).isPassable()) {
                tileTemp = tileTemp.getEdge(oriTemp).getNeighbour(tileTemp);
                for (int j = 0; j < 35; j++) {
                    xTempPrev = xTemp;
                    yTempPrev = yTemp;

                    xTemp = (double) (this.getUltrasonicSensorPositionX() + i
                            * Math.cos(Math.toRadians(this.getAlpha())));
                    yTemp = (double) (this.getUltrasonicSensorPositionY() + i
                            * Math.sin(Math.toRadians(this.getAlpha())));
                    i++;
                }
            } else {
                return Math.sqrt(Math.pow(
                        xTemp - this.getUltrasonicSensorPositionX(), 2)
                        + Math.pow(yTemp - this.getUltrasonicSensorPositionY(),
                                2))
                        / scalingfactor();
            }
        }

        // no wall is found within the range of the ultrasonic sensor
        return 250;
    }

    public void allignOnWhiteLine() {
        if (isRobotControllable()) {
            /* Orientation orientation = */Orientation.calculateOrientation(
                    getCurrentPositionAbsoluteX(),
                    getCurrentPositionAbsoluteY(), getAlpha(), sizeTile());

            while (!pointOnEdge(getLightsensorPositionX(),
                    getLightsensorPositionY())) {
                travel(1);
            }

            travel(5);

            while (!pointOnEdge(getLightsensorPositionX(),
                    getLightsensorPositionY())) {
                rotate(-1);
            }

            rotate(90);

            int i = 0;

            while (!pointOnEdge(getLightsensorPositionX(),
                    getLightsensorPositionY())) {
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
        return Orientation.calculateOrientation(getCurrentPositionAbsoluteX(),
                getCurrentPositionAbsoluteY(), getAlpha(), sizeTile());
    }

    public double sizeTile() {
        return 40;
    }

    public double scalingfactor() {
        return 1;
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