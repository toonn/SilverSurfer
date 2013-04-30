package mq.communicator;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.Timer;

import mapping.Barcode;
import mapping.MapReader;
import mapping.Orientation;
import mapping.Seesaw;
import mapping.TreasureObject;
import peno.htttp.DisconnectReason;
import peno.htttp.PlayerHandler;
import peno.htttp.Tile;
import simulator.pilot.AbstractPilot;
import simulator.pilot.RobotPilot;
import simulator.viewport.SimulatorPanel;

public class APHandler implements PlayerHandler {

    private AbstractPilot pilot;
    private SimulatorPanel panel;
    private Timer readyTimer;

    public APHandler(AbstractPilot pilot, SimulatorPanel panel) {
        this.pilot = pilot;
        this.panel = panel;
    }

    public AbstractPilot getPilot() {
        return pilot;
    }

    @Override
    public void gameStarted() {
        getPilot().startExploring();
        System.out.println("[HTTTP] Game started!");
    }

    @Override
    public void gameStopped() {
        System.out.println("[HTTTP] Game stopped!");
    }

    @Override
    public void gamePaused() {
        System.out.println("[HTTTP] Game paused!");
    }

    @Override
    public void playerJoined(String playerID) {
        System.out.println("[HTTTP] " + playerID + " has joined the game!");
    }

    @Override
    public void playerJoining(String playerID) {

    }

    @Override
    public void playerDisconnected(String playerID, DisconnectReason reason) {

    }

    @Override
    public void playerReady(String playerID, boolean isReady) {

    }

    @Override
    public void playerFoundObject(String playerID, int playerNumber) {
        System.out.println("[HTTTP] Player " + (playerNumber - 1) + " (" + playerID + ") has found his object!");
    }

    @Override
    public void gameWon(int teamNumber) {
        System.out.println("[HTTTP] Team " + teamNumber + " has won the game!");
    }

    @Override
    public void gameRolled(int playerNumber, int objectNumber) {
        pilot.setPlayerNumber(playerNumber - 1);
        System.out.println("[HTTTP] Game rolled, your number is "
                + (playerNumber - 1) + " and your objectnumber is "
                + objectNumber + ".");
        panel.makeReadyToPlay(pilot);
        panel.resetAllPaths();
        
    	if(pilot instanceof RobotPilot) {
            int checkIfReadyFPS = 50;
            ActionListener checkIfReady = new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent arg0) {
                	checkIfReady();
                }
            };
            readyTimer = new Timer(1000 / checkIfReadyFPS, checkIfReady);
            readyTimer.start();
    	}
    	else
    		try {
    			pilot.getCenter().setReady(true);
    		} catch (Exception e) {
    			
    		}
    }
    
    private void checkIfReady() {
    	if(((RobotPilot)pilot).isReady()) {
    		try {
    			pilot.getCenter().setReady(true);
    		} catch (Exception e) {
    			
    		}
    		readyTimer.stop();
    	}
    }

    @Override
    public void teamConnected(String partnerID) {
        System.out.println("[HTTTP] Partner (" + partnerID + ") has connected.");
        pilot.activateTeamPilot(partnerID);
    }

    @Override
    public void teamPosition(double x, double y, double angle) {
    	pilot.getTeamPilot().setPosition(x, y);
    	pilot.getTeamPilot().setAngle(angle);
    	int xTilePosition = (int) x/40;
    	int yTilePosition = (int) y/40;
    	Point2D point = new Point2D.Double(xTilePosition, yTilePosition);
    	pilot.setTeammatePosition(point);
    }

    @Override
    public void teamTilesReceived(List<Tile> tiles) {
        pilot.getTeamPilot().setMap(MapReader.createMapFromTiles(tiles));
        searchForSimilarTiles(tiles);
    }
    
    private void searchForSimilarTiles(List<Tile> tiles) {
    	Point point1 = null, point2 = null, ourPoint1 = null, ourPoint2 = null;
        String[] info;
        String ori1 = null;
        String ori2 = null;
        Orientation ori = null;

        for (peno.htttp.Tile tile : tiles) {
            info = tile.getToken().split("\\.");
            if (info.length == 3 && !info[2].equals("V"))
                for (mapping.Tile ourTile : pilot.getMapGraphConstructed()
                        .getTiles()) {
                    if (ourTile.getContent() instanceof Barcode
                            && ourTile.getContent().getValue() == Integer
                                    .valueOf(info[2])) {
                        point1 = new Point((int) tile.getX(), (int) tile.getY());
                        ourPoint1 = ourTile.getPosition();
                        Point pointN = new Point((int) point1.getX(),
                                (int) point1.getY() + 1);
                        Point pointZ = new Point((int) point1.getX(),
                                (int) point1.getY() - 1);
                        Point pointE = new Point((int) point1.getX() + 1,
                                (int) point1.getY());
                        Point pointW = new Point((int) point1.getX() - 1,
                                (int) point1.getY());
                        for (peno.htttp.Tile tileNZ : tiles) {
                            String[] infoTileNZ = tileNZ.getToken()
                                    .split("\\.");
                            if (tileNZ.getX() == pointN.x
                                    && tileNZ.getY() == pointN.y
                                    && (infoTileNZ[0].equals("Seesaw") || (infoTileNZ.length == 3 && infoTileNZ[2]
                                            .equals("V"))))
                                {point2 = pointN;
                            	ori2 = infoTileNZ[1];}
                            else if (tileNZ.getX() == pointZ.x
                                    && tileNZ.getY() == pointZ.y
                                    && (infoTileNZ[0].equals("Seesaw") || (infoTileNZ.length == 3 && infoTileNZ[2]
                                            .equals("V"))))
                                {point2 = pointZ;
                            	ori2 = infoTileNZ[1];}
                            else if (tileNZ.getX() == pointE.x
                                    && tileNZ.getY() == pointE.y
                                    && (infoTileNZ[0].equals("Seesaw") || (infoTileNZ.length == 3 && infoTileNZ[2]
                                            .equals("V"))))
                                {point2 = pointE;
                            	ori2 = infoTileNZ[1];}
                            else if (tileNZ.getX() == pointW.x
                                    && tileNZ.getY() == pointW.y
                                    && (infoTileNZ[0].equals("Seesaw") || (infoTileNZ.length == 3 && infoTileNZ[2]
                                            .equals("V"))))
                                {point2 = pointW;
                            	ori2 = infoTileNZ[1];}
                        }

                        for (mapping.Tile ourTileNZ : ourTile
                                .getReachableNeighboursIgnoringSeesaw()) {
                            if (ourTileNZ.getContent() instanceof Seesaw
                                    || ourTileNZ.getContent() instanceof TreasureObject){
                                ourPoint2 = ourTileNZ.getPosition();
                            	ori1 = ourTileNZ.getToken().split("\\.")[1];}
                        }
                        
                    }
                }
        }
        if (point1 != null && point2 != null && ourPoint1 != null && ourPoint2 != null){
        	ori = findOrientationEquivalentWithOurNorth(ori1,ori2);
            pilot.getMapGraphConstructed().mergeMap(tiles, ourPoint1, ourPoint2, point1 , point2, ori);
            pilot.stopExploring();
           }
        else
            System.out.println("[HTTTP] No similar tiles found yet!");
    }
    
	private Orientation findOrientationEquivalentWithOurNorth(String ori1,
			String ori2) {
		if (ori1.equals(ori2)) {
			return Orientation.NORTH;
		}
		if (ori1.equals("N")) {
			if (ori2.equals("S")) {
				return Orientation.SOUTH;
			} else if (ori2 == "W") {
				return Orientation.WEST;
			} else if (ori2 == "E") {
				return Orientation.EAST;
			}
		}
		if (ori1.equals("E")) {
			if (ori2.equals("S")) {
				return Orientation.EAST;
			} else if (ori2.equals("W")) {
				return Orientation.SOUTH;
			} else if (ori2.equals("N")) {
				return Orientation.WEST;
			}
		}
		if (ori1.equals("S")) {
			if (ori2.equals("N")) {
				return Orientation.SOUTH;
			} else if (ori2.equals("W")) {
				return Orientation.EAST;
			} else if (ori2.equals("E")) {
				return Orientation.WEST;
			}
		}
		if (ori1.equals("W")) {
			if (ori2.equals("S")) {
				return Orientation.WEST;
			} else if (ori2.equals("N")) {
				return Orientation.EAST;
			} else if (ori2.equals("E")) {
				return Orientation.SOUTH;
			}
		}
		return null;
	}   
}