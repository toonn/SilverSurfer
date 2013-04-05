package simulator.pilot;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import mapping.MapGraph;
import mapping.Obstruction;
import mapping.Orientation;
import mapping.Tile;
import mazeAlgorithm.ExploreThread;
import mq.communicator.MQCenter;
import peno.htttp.GameHandler;
import simulator.viewport.SimulatorPanel;

public abstract class AbstractPilot implements PilotInterface {
    private int playerNumber = -1;
    private int teamNumber = -1;
    private MapGraph mapGraphConstructed;
    private Point2D.Double position;
    private double angle;
    private int speed;
    private boolean busyExecutingBarcode = false;
    protected boolean readBarcodes = true;
    protected boolean permaBarcodeStop = false;
    protected PilotActions pilotActions = new PilotActions(this);
    private ExploreThread exploreThread;
    private Vector<Tile> seesawBarcodeTiles = new Vector<Tile>();
    private boolean gameOn = false;
    private MQCenter center;
    protected final double detectionDistanceUltrasonicSensorRobot = 26;
    private boolean teamMemberFound = false;
    private String playerName = "/";
    private String teamMemberName = "/";
    private Tile startingPositionOfTeamMember;

    public AbstractPilot(int playerNumber) {
        if (playerNumber < 0 || playerNumber > 3) {
            this.playerNumber = -1;
        } else {
            this.playerNumber = playerNumber;
        }
        position = new Point2D.Double(sizeTile() / 2, sizeTile() / 2);
        angle = 270;
        reset();
    }

    public void alignOnWalls() {
        rotate(90);
        rotate(-90);
        rotate(-90);
        rotate(90);
    }

    public void alignOnWhiteLine() {
        travel(40);
    }

    protected boolean checkForObstruction() {
        if (getUltraSensorValue() < detectionDistanceUltrasonicSensorRobot) {
            return true;
        }
        return false;
    }

    protected abstract boolean crashImminent();

    @Override
    public double getAngle() {
        return angle;
    }

    public MQCenter getCenter() {
        return center;
    }

    public abstract String getConsoleTag();

    @Override
    public GameHandler getDefaultHandler() {
        return getCenter().getHandler();
    }

    public abstract int getInfraRedSensorValue();

    public abstract int getLightSensorValue();

    @Override
    public MapGraph getMapGraphConstructed() {
        return mapGraphConstructed;
    }

    @Override
    public Point getMatrixPosition() {
        return new Point((int) Math.floor(getPosition().getX() / sizeTile()),
                (int) Math.floor(getPosition().getY() / sizeTile()));
    }

    public Orientation getOrientation() {
        return Orientation.calculateOrientation(getAngle());
    }

    public boolean getPermaStopReadingBarcodes() {
        return permaBarcodeStop;
    }

    /**
     * Returns 0,1,2 or 3 indicating which treasure the pilot is looking for
     */
    @Override
    public int getPlayerNumber() {
        return playerNumber;
    }

    @Override
    public Point2D.Double getPosition() {
        return position;
    }

    public boolean getReadBarcodes() {
        return readBarcodes;
    }

    private int getRotateSleepTime() {
        return 5 - speed;
    }

    public Vector<Tile> getSeesawBarcodeTiles() {
        return seesawBarcodeTiles;
    }

    public Tile getStartingPositionOfTeamMember() {
        return startingPositionOfTeamMember;
    }

    public boolean getTeamMemberFound() {
        return teamMemberFound;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getTeamMemberName() {
        return teamMemberName;
    }

    /**
     * Returns 0 or 1, indicating what team the robot is on. Returns -1 when the
     * team is not yet known.
     */
    @Override
    public int getTeamNumber() {
        return teamNumber;
    }

    private int getTravelSleepTime() {
        switch (speed) {
        case -1:
            return 50;
        case 1:
            return 10;
        case 2:
            return 7;
        case 3:
            return 5;
        case 4:
            return 3;
        }
        return 1;
    }

    public abstract int getUltraSensorValue();

    public boolean isExecutingBarcode() {
        return busyExecutingBarcode;
    }

    /**
     * Check if this Pilot is in gameModus (MQ is activated).
     */
    @Override
    public boolean isInGameModus() {
        return gameOn;
    }

    @Override
    public void makeReadyToPlay() {
        mapGraphConstructed = new MapGraph();
        mapGraphConstructed.addTile(getMatrixPosition());
    }

    public void permaStopReadingBarcodes() {
        permaBarcodeStop = true;
    }

    protected boolean pointOnEdge(final double x, final double y) {
        double edgeMarge = 1.2;
        return (x % sizeTile()) > sizeTile() - edgeMarge
                || (x % sizeTile()) < edgeMarge
                || (y % sizeTile()) > sizeTile() - edgeMarge
                || (y % sizeTile()) < edgeMarge;
    }

    protected abstract int readBarcode();

    @Override
    public void reset() {
        teamNumber = -1;
        speed = 2;
        mapGraphConstructed = new MapGraph();
        mapGraphConstructed.addTile(getMatrixPosition());
    }

    public void rotate(final double alpha) {
        double angle = getAngle();
        for (int i = 1; i <= Math.abs(alpha); i++) {
            if (alpha >= 0) {
                setAngle(angle + i);
            } else {
                setAngle(angle - i);
            }
            try {
                Thread.sleep(getRotateSleepTime());
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void setAngle(final double angle) {
        if (angle >= 360) {
            this.angle = angle - 360;
        } else if (angle < 0) {
            this.angle = angle + 360;
        } else {
            this.angle = angle;
        }
    }

    public void setBusyExecutingBarcode(boolean busy) {
        busyExecutingBarcode = busy;
    }

    /**
     * Set this Pilot in it's gameModus.
     */
    @Override
    public void setGameModus(boolean onOff) {
        gameOn = onOff;
    }

    public void setObstructionOrTile() {
        final Orientation currentOrientation = Orientation.calculateOrientation(getAngle());
        if (checkForObstruction())
            getMapGraphConstructed().getTile(getMatrixPosition()).getEdgeAt(currentOrientation).setObstruction(Obstruction.WALL);
        else {
            getMapGraphConstructed().getTile(getMatrixPosition()).getEdgeAt(currentOrientation).setObstruction(Obstruction.WHITE_LINE);
            Point nextPoint = currentOrientation.getNext(getMatrixPosition());
            if (mapGraphConstructed.getTile(nextPoint) == null)
                getMapGraphConstructed().addTile(nextPoint);
        }
    }

    @Override
    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    @Override
    public void setPosition(final double x, final double y) {
        position.setLocation(x, y);
    }

    public void setReadBarcodes(boolean readBarcodes) {
        this.readBarcodes = readBarcodes;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setTeamMemberFound(String teamMemberName) {
        teamMemberFound = true;
        this.teamMemberName = teamMemberName;
    }

    /**
     * Set teamNumber to 0 or 1. Other values are not accepted.
     */
    @Override
    public void setTeamNumber(int teamNumber) {
    	this.teamNumber = teamNumber;
    }

    @Override
    public void setupForGame(SimulatorPanel panel) {
        if (isInGameModus()) {
            try {
            	playerName = "SILVER" + getPlayerNumber();
                center = new MQCenter(this, playerName, panel);
                getCenter().join();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void shuffleSeesawBarcodeTiles() {
        Collections.shuffle(seesawBarcodeTiles);
    }

    @Override
    public double sizeTile() {
        return 40;
    }

    public void startExploring() {
        exploreThread = new ExploreThread(
                mapGraphConstructed.getTile(getMatrixPosition()), this);
        exploreThread.start();
    }

    public void stopExploring() {
        if (exploreThread != null && exploreThread.isAlive()) {
            exploreThread.quit();
        }
    }

    public void travel(final double distance) {
        double currentX = getPosition().getX();
        double currentY = getPosition().getY();
        double x;
        double y;
        Orientation travelOrientation = Orientation
                .calculateOrientation(getAngle());
        if (distance < 0) {
            travelOrientation = travelOrientation.getOppositeOrientation();
        }
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
            setPosition(x, y);
            try {
                Thread.sleep(getTravelSleepTime());
            } catch (final InterruptedException e) {

            }
        }
    }
}