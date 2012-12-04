package communication;

import audio.SongThread;
import commands.Command;
import simulator.BarcodeThread;
import simulator.SimulationPilot;
import mapping.*;
import mazeAlgorithm.MazeExplorer;

import java.awt.geom.Rectangle2D;
import java.io.*;

import lejos.nxt.Motor;
import lejos.pc.comm.*;

public class Communicator {
	private StatusInfoBuffer statusInfoBuffer;
	private SimulationPilot simulationPilot = new SimulationPilot();
	private boolean robotConnected = false;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private static NXTConnector connection;
	private static String deviceURL = "00:16:53:0A:04:5A";
	private static String deviceName = "Silver";
	private static InfoReceiverThread IRT;
	private boolean buzy = false;
	private int tilesBeforeAllign = 0;
	private int tilesRidden = 0;
	private boolean mustAllign = true;
	private MazeExplorer explorer;

	public Communicator(StatusInfoBuffer statusInfoBuffer) {
		setStatusInfoBuffer(statusInfoBuffer);
		setSpeed(2);
	}
	
	public StatusInfoBuffer getStatusInfoBuffer() {
		return statusInfoBuffer;
	}
	
	public void setStatusInfoBuffer(StatusInfoBuffer statusInfoBuffer) {
		this.statusInfoBuffer = statusInfoBuffer;
	}
	
	public SimulationPilot getSimulationPilot() {
		return simulationPilot;
	}
	
	public boolean getRobotConnected() {
		return robotConnected;
	}
	
	public void setRobotConnected(boolean robotConnected) throws Exception {
		if(robotConnected)
			openRobotConnection();
		else
			closeRobotConnection();
		this.robotConnected = robotConnected;
		setSpeed(2);
	}
	
	public void openRobotConnection() throws Exception {	
		connection = new NXTConnector();
		connection.connectTo(deviceName, deviceURL, NXTCommFactory.BLUETOOTH, NXTComm.PACKET);
		dis = connection.getDataIn();
		dos = connection.getDataOut();
		if(dis == null || dos == null)
			throw new IOException();
		IRT = new InfoReceiverThread(statusInfoBuffer);
		IRT.setDis(dis);
		IRT.setDos(dos);
		IRT.start();
	}
	
	public void closeRobotConnection() throws Exception {
		dos.writeInt(Command.CLOSE_CONNECTION);
		dos.flush();
		IRT.setQuit(true);
		dis.close();
		dos.close();
		connection.close();
	}
	
	public void setBuzy(boolean buzy) {
		this.buzy = buzy;
	}
	
	public void sendCommand(int command) {
		try {
			if(robotConnected) {
				dos.writeInt(command);
				dos.flush();
				buzy = true;
			}
			if(command == Command.SLOW_SPEED)
				simulationPilot.setSpeed(194);
			else if(command == Command.NORMAL_SPEED)
				simulationPilot.setSpeed(86);
			else if(command == Command.FAST_SPEED)
				simulationPilot.setSpeed(58);
			else if(command == Command.VERY_FAST_SPEED)
				simulationPilot.setSpeed(48);				
			else if(command == Command.ALIGN_PERPENDICULAR)
				simulationPilot.allignOnWhiteLine();
			else if(command == Command.ALIGN_WALL)
				simulationPilot.allignOnWalls();
			else if(command == Command.LOOK_AROUND)
	            simulationPilot.checkForObstructions();		
			else if(command == Command.PLAY_SONG) {
	    			SongThread ST = new SongThread(); 
	    			ST.start();
			}
			else if(command == Command.checkObstructionsAndSetTile && !robotConnected){
				simulationPilot.checkForObstructionAndSetTile();
			}
			else if(command%100 == Command.AUTOMATIC_MOVE_FORWARD) {
				if(!getRobotConnected()){
					try {
						BarcodeThread BT = new BarcodeThread("BT",getSimulationPilot().getSSG());
			        	BT.start();
			        	int amount = (command-Command.AUTOMATIC_MOVE_FORWARD)/100;
						while(amount-- != 0)
							simulationPilot.travel(1);
			        	boolean found = BT.getFound();
			        	BT.setQuit(true);
			        	if(found) {
			        		readBarcode();
			        	}
			        } catch(Exception e) {
			        	e.printStackTrace();
			        	System.out.println("Error in CommandUnit.moveForward(int angle)!");
			        }
				}
				else{
					int amount = (command-Command.AUTOMATIC_MOVE_FORWARD)/100;
					while(amount-- != 0)
						simulationPilot.travel(1);
				}

				
				
			}
			else if(command%100 == Command.AUTOMATIC_TURN_ANGLE) {
				double amount = (double) (command-Command.AUTOMATIC_TURN_ANGLE)/100;
				while(amount-- > 0)
					simulationPilot.rotate(1);
			}
			else if(command%100 == -(100-Command.AUTOMATIC_TURN_ANGLE)) {
				double amount = (double) (command-Command.AUTOMATIC_TURN_ANGLE)/100;
				while(amount++ < 0)
					simulationPilot.rotate(-1);
			}
			if(robotConnected) {
				while(buzy)
					Thread.sleep(100);
			}
		} catch(Exception e) {
			System.out.println("Error in Communicator.sendCommand(int command)!");
		}
	}

    private void readBarcode() {
    		int value = ((Barcode)getSimulationPilot().getMapGraph().getCurrentTile().getContent()).getValue();
    		//if (((Barcode)getSimulationPilot().getSSG().getSimulationPanel().getMapGraphConstructed().getContentCurrentTile()== null)){
				getSimulationPilot().getSSG().getInformationBuffer().setBarcode(value);
    		//}
  	
	}

	public void setExplorer(MazeExplorer explorer) {
		this.explorer = explorer;
	}
	
	public MazeExplorer getExplorer() {
		return explorer;
	}
	
	public void goToNextTile(Orientation orientation) throws IOException{
		double currentAngle = getStatusInfoBuffer().getAngle();
		int angleToRotate = (int)((double) orientation.getRightAngle() - currentAngle);
		angleToRotate = (int) ExtMath.getSmallestAngle(angleToRotate);
		sendCommand(angleToRotate*100 + Command.AUTOMATIC_TURN_ANGLE);
		
		if(mustAllign){
			tilesRidden++;
			if(getTilesRidden() == getTilesBeforeAllign()){
				sendCommand(Command.ALIGN_PERPENDICULAR);
				sendCommand(24*100 + Command.AUTOMATIC_MOVE_FORWARD);
				setTilesRidden(0);
			}	
		}
		else{
		sendCommand(40*100 + Command.AUTOMATIC_MOVE_FORWARD);}

		
		
	}

    public void moveTurn(int lengthInCM, int anglesInDegrees, int amtOfAngles) {
        int length = lengthInCM * 100 + Command.AUTOMATIC_MOVE_FORWARD;
        int angles;
        if (amtOfAngles == 0) {
            angles = anglesInDegrees * 100 + Command.AUTOMATIC_TURN_ANGLE;
            sendCommand(length);
            sendCommand(angles);
        } else {
            angles = (int) Math.round(360.0 / amtOfAngles) * 100
                    + Command.AUTOMATIC_TURN_ANGLE;
            if (amtOfAngles == 1)
                sendCommand(length);
            else
                for (int i = 0; i < amtOfAngles; i++) {
                    sendCommand(length);
                    sendCommand(angles);
                }
        }
    }

    public int getSpeed() {
        return simulationPilot.getSpeed();
    }

    public void setSpeed(int speed) {
        if (speed == 1)
            sendCommand(Command.SLOW_SPEED);
        else if (speed == 2)
            sendCommand(Command.NORMAL_SPEED);
        else if (speed == 3)
            sendCommand(Command.FAST_SPEED);
        else
            sendCommand(Command.VERY_FAST_SPEED);
    }

    public String getConsoleTag() {
        if (robotConnected)
            return "[ROBOT]";
        return "[SIMULATOR]";
    }

    public void clear() {
        simulationPilot.clear();
    }

    // TODO: vanaf hier!
    /**
     * Gets the amount of angles the arrow should turn in one event to be at par
     * with the robot.
     */
    public double getAngularSpeed() {
        switch (getSpeed()) {
        case 1:
            return (double) 1.82;
        case 2:
            return (double) 2.74;
        case 3:
            return (double) 2.77;
        case 4:
            return (double) 1.82;
        }
        return (double) 2.74;
    }

    public int getTilesBeforeAllign() {
        return tilesBeforeAllign;
    }

    public void setTilesBeforeAllign(int tilesBeforeAllign) {
        this.tilesBeforeAllign = tilesBeforeAllign;
    }

    private int getTilesRidden() {
        return tilesRidden;
    }

    private void setTilesRidden(int tilesRidden) {
        this.tilesRidden = tilesRidden;
    }

    public void mustAllign(boolean mustAllign) {
        this.mustAllign = mustAllign;
    }
}