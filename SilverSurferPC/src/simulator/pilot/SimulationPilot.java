package simulator.pilot;

import gui.SilverSurferGUI;

import java.awt.geom.Rectangle2D;
import java.util.Random;

import mapping.Barcode;
import mapping.Obstruction;
import mapping.Orientation;
import mapping.Tile;
import simulator.ExtMath;
import simulator.SimulationSensorData;
import simulator.viewport.ViewPort;
import datastructures.Tuple;

public class SimulationPilot extends AbstractPilot {
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
    @Override
    public void addBarcode(final ViewPort simulatorPanel,
            final Barcode barcode, final Rectangle2D[] visual) {
        simulatorPanel.getVisibleBarcode().add(
                new Tuple<Barcode, Rectangle2D[]>(barcode, visual));
    }

    @Override
    public void addWall() {
        final Orientation currentOrientation = Orientation
                .calculateOrientation(getAngle());

        setWallOnTile(getCurrentPositionRelativeX(),
                getCurrentPositionRelativeY(), currentOrientation);
    }

    // checkt deze afstand ook en doet hetzelfde (alleen als de afstand kleiner
    // is dan 30 en niet tussen 22 en 24 want da zou al goe genoeg zijn), dan
    // terug 90 graden naar rechts zodat em zoals int begin staat. Als er alleen
    // links een muur staat, doet em dus niks rechts en dan links wat hem anders
    // eerst rechts zou doen (dus < 30 enige voorwaarde).
    @Override
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

    @Override
    public void allignOnWhiteLine() {
        final double[] absLS = getLightSensorAbsolute();

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
        }/*
          * Geen idee waarvoor dit diende? else travel(16);
          */
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
        double xTemp = getUltrasonicSensorAbsolute()[0];
        double yTemp = getUltrasonicSensorAbsolute()[1];
        // keep the last temporary position, so you can compare with the current
        // temporary position
        double xTempPrev = xTemp;
        double yTempPrev = yTemp;

        // there is no map loaded, so the sensor will detect no walls en returns
        // the maximum value.
        if (getMapGraphLoaded() == null) {
            return 250;
        }
        Tile tileTemp = getMapGraphLoaded().getCurrentTile();
        int i = 1;

        while (i < 148) {
            while (!(Math.abs(xTempPrev % sizeTile() - xTemp % sizeTile()) > 5)
                    && !(Math.abs(yTempPrev % sizeTile() - yTemp % sizeTile()) > 5)) {
                xTempPrev = xTemp;
                yTempPrev = yTemp;

                xTemp = (getUltrasonicSensorAbsolute()[0] + i
                        * Math.cos(Math.toRadians(getAngle())));
                yTemp = (getUltrasonicSensorAbsolute()[1] + i
                        * Math.sin(Math.toRadians(getAngle())));
                i++;
            }

            final Orientation oriTemp = Orientation.defineBorderCrossed(xTemp,
                    yTemp, xTempPrev, yTempPrev, sizeTile());

            // the edge you have found, does not contain a wall, you can look
            // right over it.
            // change the current tile to the next tile en move a few steps
            // foreward (with the temporary coordinates).
            if (tileTemp.getEdge(oriTemp).isPassable()) {
                tileTemp = tileTemp.getEdge(oriTemp).getNeighbour(tileTemp);
                for (int j = 0; j < 35; j++) {
                    xTempPrev = xTemp;
                    yTempPrev = yTemp;

                    xTemp = (getUltrasonicSensorAbsolute()[0] + i
                            * Math.cos(Math.toRadians(getAngle())));
                    yTemp = (getUltrasonicSensorAbsolute()[1] + i
                            * Math.sin(Math.toRadians(getAngle())));
                    i++;
                }
            } else {
                return Math
                        .sqrt(Math.pow(
                                xTemp - getUltrasonicSensorAbsolute()[0], 2)
                                + Math.pow(yTemp
                                        - getUltrasonicSensorAbsolute()[1], 2))
                        / scalingfactor();
            }
        }

        // no wall is found within the range of the ultrasonic sensor
        return 250;
    }

    /**
     * checkt of de robot een obstruction ZIET
     */

    @Override
    public boolean checkForObstruction() {
        final int distance = getUltraSensorValue();

        if (distance < detectionDistanceUltrasonicSensorRobot) {
            return true;
        }

        return false;
    }

    @Override
    public void checkForObstructionAndSetTile() {
        if (checkForObstruction()) {
            addWall();
        } else {
            final Orientation currentOrientation = Orientation
                    .calculateOrientation(getAngle());
            int xCoordinate;
            int yCoordinate;
            if (isRobotControllable()) {
                xCoordinate = getCurrentPositionRelativeX()
                        + currentOrientation.getArrayToFindNeighbourRelative()[0];
                yCoordinate = getCurrentPositionRelativeY()
                        + currentOrientation.getArrayToFindNeighbourRelative()[1];
            } else {
                xCoordinate = getMapGraphLoaded().getCurrentTileCoordinates()[0]
                        + currentOrientation.getArrayToFindNeighbourRelative()[0];
                yCoordinate = getMapGraphLoaded().getCurrentTileCoordinates()[1]
                        + currentOrientation.getArrayToFindNeighbourRelative()[1];
            }
            if (getMapGraphConstructed().getTileWithCoordinates(xCoordinate,
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
    @Override
    public void checkForObstructions() {
        for (int i = 0; i < 3; i++) {
            if (checkForObstruction()) {
                addWall();
            } else {
                // removeWall();
                final Orientation currentOrientation = Orientation
                        .calculateOrientation(getAngle());
                final int xCoordinate = getMapGraphLoaded()
                        .getCurrentTileCoordinates()[0]
                        + currentOrientation.getArrayToFindNeighbourRelative()[0];
                final int yCoordinate = getMapGraphLoaded()
                        .getCurrentTileCoordinates()[1]
                        + currentOrientation.getArrayToFindNeighbourRelative()[1];
                if (getMapGraphConstructed().getTileWithCoordinates(
                        xCoordinate, yCoordinate) == null) {
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
            final int xCoordinate = getMapGraphLoaded()
                    .getCurrentTileCoordinates()[0]
                    + currentOrientation.getArrayToFindNeighbourRelative()[0];
            final int yCoordinate = getMapGraphLoaded()
                    .getCurrentTileCoordinates()[1]
                    + currentOrientation.getArrayToFindNeighbourRelative()[1];
            if (getMapGraphConstructed().getTileWithCoordinates(xCoordinate,
                    yCoordinate) == null) {
                setTile(xCoordinate, yCoordinate);
            }

        }

        for (int i = 0; i < 3; i++) {
            rotate(-90);
        }

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

    @Override
    public Orientation getCurrentOrientation() {
        return Orientation.calculateOrientation(getAngle());
    }

    /**
     * Returns a number from a normal districution that represents a lightsensor
     * value.
     */
    @Override
    public int getLightSensorValue() {
        // initialisation
        final Random random = new Random();
        double mean = 0;
        double standardDeviation = 1;
        final double[] absLS = getLightSensorAbsolute();

        // check on which sort of underground your are standing
        // and adjust the mean and standardDeviation accordingly
        if (onEmptyTile(absLS[0], absLS[1])) {
            mean = SimulationSensorData.getMEmptyPanelLS();
            standardDeviation = SimulationSensorData.getSDEmptyPanelLS();
        } else if (onWhiteLine(absLS[0], absLS[1])) {
            mean = SimulationSensorData.getMWhiteLineLS();
            standardDeviation = SimulationSensorData.getSDWhiteLineLS();
        } else if (onBarcodeTile(absLS[0], absLS[1])) {
            final int color = ((Barcode) getMapGraphLoaded()
                    .getContentCurrentTile()).getColorValue(absLS[0]
                    % sizeTile(), absLS[1] % sizeTile());
            mean = SimulationSensorData.getMBarcodeTileLS(color);
            standardDeviation = SimulationSensorData.getSDBarcodeTileLS(color);
        }
        return (int) Math.round(mean
                + (random.nextGaussian() * standardDeviation));
    }

    @Override
    public int getUltraSensorValue() {
        if (!isRobotSimulated()) {
            return SilverSurferGUI.getStatusInfoBuffer()
                    .getLatestUltraSensorInfo();
        } else {
            final Random random = new Random();

            final double mean = calculateDistanceToWall();
            final double standardDeviation = SimulationSensorData.getSDUS();

            return (int) Math.round(mean
                    + (random.nextGaussian() * standardDeviation));
        }
    }

    @Override
    public boolean isRobotControllable() {
        return true;
    }

    /**
     * Zelf simuleren sensoren?
     */
    @Override
    public boolean isRobotSimulated() {
        return true;
    }

    /**
     * True if the robot is not on an edge, but on a tile containing a barcode.
     */
    @Override
    public boolean onBarcodeTile(final double x, final double y) {
        if (getMapGraphLoaded() == null) {
            // System.out.println("b: /");
            return false;
        } else {
            // System.out.println("b: " + (!this.onEdge(x,y) &&
            // (this.getMapGraph().getContentCurrentTile() instanceof
            // Barcode)));
            return !pointOnEdge(x, y)
                    && (getMapGraphLoaded().getContentCurrentTile() instanceof Barcode);
        }
    }

    /**
     * True if the robot is not on an edge, but on a tile without a content.
     */
    public boolean onEmptyTile(final double x, final double y) {
        // TOON deze methode override abstractpilot
        return (!pointOnEdge(x, y) && getMapGraphLoaded() == null)
                || (!pointOnEdge(x, y) && getMapGraphLoaded()
                        .getContentCurrentTile() == null);

    }

    /**
     * True if the robot is on an edge and this edge is not a wall
     */
    public boolean onWhiteLine(final double x, final double y) {
        // TOON deze methode override abstractpilot
        // System.out.println("w: " + (this.onEdge(x,y) && (this.getMapGraph()
        // == null ||
        // this.getMapGraph().getObstruction(Orientation.calculateOrientation(x,
        // y, this.getAlpha())) != Obstruction.WALL)));
        return pointOnEdge(x, y)
                && (getMapGraphLoaded() == null || getMapGraphLoaded()
                        .getObstruction(
                                Orientation.calculateOrientation(getAngle())) != Obstruction.WALL);

    }

    /**
     * Checks whether the given point is on the edge of a tile.
     */
    @Override
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

    /**
     * Checks whether the robot, standig on the given point, is on the edge of a
     * tile. The robot is interpreted as a rectangle around the given position.
     */
    @Override
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

    @Override
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

            setAngle(alphaTemp);
            SilverSurferGUI.getStatusInfoBuffer().setAngle(alphaTemp);

            // this.getSSG().updateStatus();

            try {
                if (isRobotControllable()) {
                    Thread.sleep(getSpeed() / 10);
                } else {
                    if (getSpeed() == 1) {
                        Thread.sleep(4);
                    } else if (getSpeed() == 2) {
                        Thread.sleep(3);
                    }
                    if (getSpeed() == 3) {
                        Thread.sleep(2);
                    } else {
                        Thread.sleep(1);
                    }
                }
            } catch (final InterruptedException e) {
            }
        }
    }

    public double scalingfactor() {
        return 1;
    }

    @Override
    public void setTile(final int x, final int y) {
        getMapGraphConstructed().setTileXY(x, y, new Tile());
    }

    @Override
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

    @Override
    public double sizeTile() {
        return 40;
    }

    @Override
    public void travel(double distance) {
        distance = distance * scalingfactor();
        // travelledInTotal = travelledInTotal + Math.abs(distance);
        // System.out.println("travelledInTotal : " + travelledInTotal);
        // double xOriginal = this.getCurrentPositionAbsoluteX();
        // double yOriginal = this.getCurrentPositionAbsoluteY();
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
            xTemp = (xTemp + i * Math.cos(Math.toRadians(getAngle())));
            yTemp = (yTemp + i * Math.sin(Math.toRadians(getAngle())));

            if (getMapGraphLoaded() != null) {

                if (robotOnEdge(xTemp, yTemp, getAngle())) {
                    final Orientation edgeOrientation = pointOnWichSideOfTile(
                            xTemp, yTemp, travelOrientation);

                    // the edge you are standing on contains a wall
                    if (travelOrientation == edgeOrientation
                            && !getMapGraphLoaded().getCurrentTile()
                                    .getEdge(travelOrientation).isPassable()) {
                        setCurrentPositionAbsoluteX((xTemp - i
                                * Math.cos(Math.toRadians(getAngle()))));
                        setCurrentPositionAbsoluteY((yTemp - i
                                * Math.sin(Math.toRadians(getAngle()))));
                        // this.getSSG().updateStatus();

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
                if (isRobotControllable()) {
                    Thread.sleep(getSpeed()
                            / ((int) Math.ceil(Math.abs(distance))));
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

        // this.getSSG().updateStatus();
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
            setCurrentTileCoordinates(getMapGraphLoaded(), xTemp, yTemp);
        }
    }
}