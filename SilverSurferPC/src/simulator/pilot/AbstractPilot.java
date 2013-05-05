package simulator.pilot;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import commands.Sleep;
import mapping.MapGraph;
import mapping.Obstruction;
import mapping.Orientation;
import mapping.Seesaw;
import mapping.Tile;
import mazeAlgorithm.CollisionAvoidedException;
import mazeAlgorithm.ExploreThread;
import mazeAlgorithm.ShortestPath;
import mq.communicator.APHandler;
import mq.communicator.MQCenter;
import simulator.viewport.SimulatorPanel;

public abstract class AbstractPilot implements PilotInterface {

    private Point mapSize; //Startend van 0
    private int playerNumber = -1;
    private int teamNumber = -1;
    private MapGraph mapGraphConstructed;
    private Point2D.Double position;
    private double angle;
    private Point2D teammatePosition;
    private boolean stillApproximating;
    private int tilesToGoToTeammate = 1;
    private int tilesAwayFromTeammate = Integer.MAX_VALUE;
    private int speed;
    private boolean busyExecutingBarcode = false;
    protected boolean readBarcodes = true;
    protected boolean permaBarcodeStop = false;
    protected boolean objectBoolean = false;
    protected PilotActions pilotActions = new PilotActions(this);
    private ExploreThread exploreThread;
    private Vector<Tile> seesawBarcodeTiles = new Vector<Tile>();
    private boolean gameOn = false;
    private MQCenter center;
    protected final double DETECTION_DISTANCE_ULTRASONIC_SENSOR_ROBOT = 26;
    private boolean teamMemberFound = false;
    private String playerName = "/";
    private String teamMemberName = "/";
    private Tile startingPositionOfTeamMember;
    private DummyPilot teamPilot = new DummyPilot();
    private boolean won = false;
    private int nbOfTilesBetweenAlign = 0;
    private int tilesBeforeAlign = nbOfTilesBetweenAlign;
    private Vector<Tile> allTileVector = new Vector<Tile>();

    public AbstractPilot(int playerNumber, Point mapSize) {
        if (playerNumber < 0 || playerNumber > 3) {
            this.playerNumber = -1;
        } else {
            this.playerNumber = playerNumber;
        }
        if(mapSize != null)
        	this.mapSize = mapSize;
        position = new Point2D.Double(sizeTile() / 2, sizeTile() / 2);
        angle = 270;
        reset();
    }
    
    public void activateTeamPilot(String teamMemberName) {
    	teamMemberFound = true;
    	this.teamMemberName = teamMemberName;
    	teamPilot.activate();
    	teamPilot.setTeamNumber(teamNumber);
    }
    
    public Point getMapSize() {
    	return mapSize;
    }
    
    public void updateTilesAndPosition() {
    	if(gameOn)
    		try {
    			int angle = (int)getAngle();
    			if(angle == 270)
    				angle = -90;
    			getCenter().updatePosition((int)(getPosition().x/40), mapSize.y - (int)(getPosition().y/40), angle);
    			if(teamPilot.isActive()) {
    				ArrayList<peno.htttp.Tile> vector = new ArrayList<peno.htttp.Tile>();
    				for (mapping.Tile tile : getMapGraphConstructed().getTiles()) 
    					vector.add(new peno.htttp.Tile((long) tile.getPosition().getX(), (long) tile.getPosition().getY(), tile.getToken()));
    				getCenter().getPlayerClient().sendTiles(vector);
    			}
    		} catch(IOException e) {
			
    		}
    }
    
    public DummyPilot getTeamPilot() {
    	return teamPilot;
    }
    
    public int getTilesBeforeAlign() {
    	return tilesBeforeAlign;
    }
    
    public void decreaseTilesBeforeAlign() {
    	if(tilesBeforeAlign == 0)
    		tilesBeforeAlign = nbOfTilesBetweenAlign;
    	else
    		tilesBeforeAlign--;
    }

    public void alignOnWhiteLine() throws CollisionAvoidedException {
        travel(40, false);
    }

    protected boolean checkForObstruction() {
        if (getUltraSensorValue() < DETECTION_DISTANCE_ULTRASONIC_SENSOR_ROBOT)
            return true;
        return false;
    }

    @Override
    public double getAngle() {
        return angle;
    }

    public MQCenter getCenter() {
        return center;
    }

    public abstract String getConsoleTag();

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
        case -2:
        	return 100;
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
        case 5:
        	return 1;
        }
        return 0;
    }

    public abstract int getUltraSensorValue();

    public boolean isExecutingBarcode() {
        return busyExecutingBarcode;
    }

    /**
     * Check if this Pilot is in gameModus (MQ is activated).
     */
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
            new Sleep().sleepFor(getRotateSleepTime());
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

    public void setupForGame(SimulatorPanel panel) {
        if (isInGameModus()) {
            try {
            	playerName = "SILVER" + getPlayerNumber() + "_" + System.currentTimeMillis();
                center = new MQCenter(this, playerName, panel);
                center.join();
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

    public void fillVectorMapgraphTiles() {
		allTileVector.addAll(mapGraphConstructed.getTiles());
    }

	public void startLookingYourTeammate() {
		stillApproximating = true;
		Tile endTile = mapGraphConstructed.getTile(mapGraphConstructed.convertPoint(getTeammatePosition()));
		
		ShortestPath shortestPath = new ShortestPath(null, this, mapGraphConstructed.getTile(getMatrixPosition()), endTile, allTileVector);
		
		int tilesAway = shortestPath.getTilesAwayFromTargetPosition();
				
		if ((tilesAway == 1 || tilesAway == 0)
			&& mapGraphConstructed.convertPoint(getTeammatePosition()).equals(endTile.getPosition())){

//			System.out.println("Teammate = " + getTeamMemberName() + " op positie " +mapGraphConstructed
//				.convertPoint(getTeammatePosition()).x + " " + mapGraphConstructed
//				.convertPoint(getTeammatePosition()).y);
//			System.out.println("ik = " + getPlayerName() + " op positie " + getMatrixPosition().x + " " + getMatrixPosition().y);
			won();
			return;
		} 
				
		if (tilesAwayFromTeammate >= tilesAway && !(shortestPath.getTilesPath().get(1) instanceof Seesaw && ((Seesaw) (shortestPath.getTilesPath().get(1))).isClosed())) {
			//TODO: volgende methode geeft ofwel null terug (succes) ofwel een tile (collisiondetecion, robot staat op teruggegeven tile)
			shortestPath.goNumberTilesShortestPath(tilesToGoToTeammate);
		} else {
			System.out.println("Wacht een paar seconden");
			new Sleep().sleepFor(new Random().nextInt(10)*1000);
		}
		
		tilesAwayFromTeammate = tilesAway;
		stillApproximating = false;
	}

	public void travel(final double distance, boolean ignoreCollision) throws CollisionAvoidedException {
		int distTraveled = 0;
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
			distTraveled++;
			new Sleep().sleepFor(getTravelSleepTime());
			
			//Houd geen rekening als robot achteruit rijdt (kijkt de verkeerde richting uit)
			//op zich gewoon negeren want das alleen als het object wordt opgepakt en dan kan er geen crash zijn
			if (!ignoreCollision && crashImminent(distance-distTraveled)) {
				currentX = x;
				currentY = y;
				for (int j = 1; j <= Math.abs(distTraveled); j++) {
					if (travelOrientation == Orientation.NORTH) {
						x = currentX;
						y = currentY + j;
					} else if (travelOrientation == Orientation.SOUTH) {
						x = currentX;
						y = currentY - j;
					} else if (travelOrientation == Orientation.EAST) {
						x = currentX - j;
						y = currentY;
					} else {
						x = currentX + j;
						y = currentY;
					}
					setPosition(x, y);
					new Sleep().sleepFor(getTravelSleepTime());
				}
				throw new CollisionAvoidedException("Near-collision detected!");
			}
		}
	}

	protected boolean crashImminent(double distance) {
		return getUltraSensorValue() <= distance;
	}

	public Point2D getTeammatePosition() {
		return teammatePosition;
	}

	public void setTeammatePosition(Point2D teammatePosition) {

//		System.out.println("ik ben : " + getPlayerName() + " en mijn teammate " + getTeamMemberName() + " stuurt door" +
//				" dat hij op positie " + teammatePosition.getX() + " " + teammatePosition.getY() + " staat.");
		
		//if (this.teammatePosition == null || this.teammatePosition.getX() != teammatePosition.getX() || this.teammatePosition.getY() != teammatePosition.getY()) {
			this.teammatePosition = teammatePosition;
			if (!won && !stillApproximating && mapGraphConstructed.mapsAreMerged()) {
				startLookingYourTeammate();
			}
		//}
	}

	public void won() {
		System.out.println("VICTORY! YOU WON THE GAME!"); //TODO: vuurwerk en een fanfare
		won = true;
	}
	
	public void crossOpenSeesaw(int seesawValue) {
    	boolean readBarcodesBackup = readBarcodes;
    	readBarcodes = false;
    	try {
        	travel(60, true);
            for (Tile tile : getMapGraphConstructed().getTiles())
                if (tile.getContent() instanceof Seesaw
                        && tile.getContent().getValue() == seesawValue)
                    ((Seesaw) tile.getContent()).flipSeesaw();
        	travel(30, true);
    	} catch(CollisionAvoidedException e) {
        	//Do nothing, a collision cannot happen here
    	}
    	try {
        	travel(30, false);
    	} catch(CollisionAvoidedException e) {
    		travelThirtyCollisionRollback();
    	}
        readBarcodes = readBarcodesBackup;
        try {
        	alignOnWhiteLine();
        } catch(CollisionAvoidedException e) {
        	alignOnWhiteLineCollisionRollback();
        }
	}
	
	public void crossClosedSeesaw(int seesawValue) {
        for (Tile tile : getMapGraphConstructed().getTiles())
            if (tile.getContent() instanceof Seesaw
                    && tile.getContent().getValue() == seesawValue)
                ((Seesaw) tile.getContent()).flipSeesaw();
        crossOpenSeesaw(seesawValue);
	}
	
	public void pickupObject(int team) {
    	boolean readBarcodesBackup = readBarcodes;
    	readBarcodes = false;
        rotate(180);
        try {
        	travel(-30, true);
        } catch(CollisionAvoidedException e) {
        	//Do nothing, a collision cannot happen here
        }
        objectBoolean = true;
        setTeamNumber(team);
        if(isInGameModus()) {
            try {
                getCenter().getPlayerClient().foundObject();
                getCenter().getPlayerClient().joinTeam(getTeamNumber());
            } catch (Exception e) {
                System.out.println("EXCEPTION! PILOTACTIONS!");
            }
        }
        try {
        	travel(30, false);
        } catch(CollisionAvoidedException e) {
        	travelThirtyCollisionRollback();
        }
        readBarcodes = readBarcodesBackup;
	}
	
	private void travelThirtyCollisionRollback() {
		new Sleep().sleepFor(1000);
		try {
        	travel(30, false);
        } catch(CollisionAvoidedException e) {
        	travelThirtyCollisionRollback();
        }
	}
	
	private void alignOnWhiteLineCollisionRollback() {
		new Sleep().sleepFor(1000);
		try {
        	alignOnWhiteLine();
        } catch(CollisionAvoidedException e) {
        	alignOnWhiteLineCollisionRollback();
        }
	}
	
	public boolean getObjectBoolean() { //Nodig voor algoritme te signaleren
		return objectBoolean;
	}
	
	public void objectHandled() { //After object has been picked up and algorithm is aware of this, this is set to false so the algoritm stays correct
		objectBoolean = false;
	}
}