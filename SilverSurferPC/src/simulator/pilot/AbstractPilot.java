package simulator.pilot;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import javax.swing.Timer;

import mapping.MapGraph;
import mapping.Obstruction;
import mapping.Orientation;
import mapping.Tile;
import mazeAlgorithm.ExploreThread;
import mazeAlgorithm.MazeExplorer;
import mazeAlgorithm.ShortestPath;
import mq.communicator.APHandler;
import mq.communicator.MQCenter;
import simulator.viewport.SimulatorPanel;

public abstract class AbstractPilot implements PilotInterface {
	
    private int playerNumber = -1;
    private int teamNumber = -1;
    private MapGraph mapGraphConstructed;
    private Point2D.Double position;
    private double angle;
    private Point2D teammatePosition;
    private boolean stillApproximating;
    private int tilesToGoToTeammate = 2;
    private int tilesAwayFromTeammate;
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
    private DummyPilot teamPilot = new DummyPilot();
    private int updateTilesAndPositionFPS = 3;
    private boolean won = false;
    private ActionListener updateTilesAndPosition = new ActionListener() {

        @Override
        public void actionPerformed(final ActionEvent arg0) {
        	updateTilesAndPosition();
        }
    };

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
    
    public void activateTeamPilot(String teamMemberName) {
    	teamMemberFound = true;
    	this.teamMemberName = teamMemberName;
    	teamPilot.activate();
    	teamPilot.setTeamNumber(teamNumber);
        new Timer(1000 / updateTilesAndPositionFPS, updateTilesAndPosition).start();
    }
    
    private void updateTilesAndPosition() {
    	ArrayList<peno.htttp.Tile> vector = new ArrayList<peno.htttp.Tile>();
    	for (mapping.Tile tile : getMapGraphConstructed().getTiles()) 
    		vector.add(new peno.htttp.Tile((long) tile.getPosition().getX(), (long) tile.getPosition().getY(), tile.getToken()));
    	try {
    		getCenter().getClient().sendTiles(vector);
    		getCenter().updatePosition((int)getPosition().x, (int)getPosition().y, (int)getAngle());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public DummyPilot getTeamPilot() {
    	return teamPilot;
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
    public APHandler getDefaultHandler() {
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
        teamPilot.reset();
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
            	playerName = "SILVER" + getPlayerNumber() + "_" + System.currentTimeMillis();
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
    
	public void startLookingYourTeammate() {
		stillApproximating = true;

		Tile EndTile = mapGraphConstructed.getTile(mapGraphConstructed
				.convertPoint(getTeammatePosition()));
		Vector<Tile> v = new Vector<Tile>();
		v.addAll(mapGraphConstructed.getTiles());
		ShortestPath shortestPath = new ShortestPath(null, this,
				mapGraphConstructed.getTile(getMatrixPosition()), EndTile, v);

		int tilesAway = shortestPath.getTilesAwayFromTargetPosition();

		if ((tilesAway == 1 || tilesAway == 0)
				&& mapGraphConstructed.convertPoint(getTeammatePosition())
						.getX() == EndTile.getPosition().getX()
				&& mapGraphConstructed.convertPoint(getTeammatePosition())
						.getY() == EndTile.getPosition().getY()) {
			System.out.println(mapGraphConstructed
				.convertPoint(getTeammatePosition()).x + " " + mapGraphConstructed
				.convertPoint(getTeammatePosition()).y);
			System.out.println(getMatrixPosition().x + " " + getMatrixPosition().y);
			won();
			return;
		} else if (tilesAway <= 4) {
			tilesToGoToTeammate = 1;
		}

		if (tilesAway != tilesAwayFromTeammate) {
			shortestPath.goNumberTilesShortestPath(tilesToGoToTeammate);
		} else {
			Random random = new Random();
			try {
				Thread.sleep(random.nextInt(10000) + 5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			EndTile = mapGraphConstructed.getTile(mapGraphConstructed
					.convertPoint(getTeammatePosition()));
			shortestPath = new ShortestPath(null, this,
					mapGraphConstructed.getTile(getMatrixPosition()), EndTile,
					v);

			shortestPath.goNumberTilesShortestPath(tilesToGoToTeammate);
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		stillApproximating = false;
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
    
    public Point2D getTeammatePosition(){
    	return teammatePosition;
    }
    
	public void setTeammatePosition(Point2D teammatePosition) {

		if (this.teammatePosition == null
				|| this.teammatePosition.getX() != teammatePosition.getX()
				|| this.teammatePosition.getY() != teammatePosition.getY()) {
			this.teammatePosition = teammatePosition;
			if (exploreThread.getExplorer().isReallyQuit() && !won
					&& !stillApproximating
					&& mapGraphConstructed.mapsAreMerged()) {
				startLookingYourTeammate();
			}
		}
	}
    
    public void won(){
    	System.out.println("you win");
		won = true;
    }
}