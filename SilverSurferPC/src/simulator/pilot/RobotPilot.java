package simulator.pilot;

import commands.Command;
import communication.Communicator;
import communication.InfoReceiverThread;
import communication.StatusInfoBuffer;

public class RobotPilot extends AbstractPilot {

    private StatusInfoBuffer statusInfoBuffer;
    private Communicator communicator;
    private static InfoReceiverThread IRT;
    private boolean busy = false;

    public RobotPilot() {
        statusInfoBuffer = new StatusInfoBuffer(this);
        communicator = new Communicator();
        try {
            communicator.openRobotConnection(statusInfoBuffer, IRT);
        } catch(Exception e) {
        	
        }
    }
    
    public void endConnection() {
        try {
        	communicator.closeRobotConnection(IRT);
        } catch(Exception e) {
        	
        }
    }
    
    public boolean getBusy() {
    	return busy;
    }

    @Override
    public void setSpeed(int speed) {
    	busy = true;
        if (speed == 1)
        	communicator.sendCommand(Command.SLOW_SPEED);
        else if (speed == 2)
        	communicator.sendCommand(Command.NORMAL_SPEED);
        else if (speed == 3)
        	communicator.sendCommand(Command.FAST_SPEED);
        else
        	communicator.sendCommand(Command.VERY_FAST_SPEED);
        super.setSpeed(speed);
    	waitUntilDone();
    }

    /**
     * Gets the amount of angles the arrow should turn in one event to be at par
     * with the robot.
     */
    public double getAngularSpeed() {
    	//TODO: recalibrate + getrotatesleeptime is hetzelfde?
        switch (getSpeed()) {
        case 1:
            return 1.82;
        case 2:
            return 2.74;
        case 3:
            return 2.77;
        case 4:
            return 1.82;
        }
        return 2.74;
    }

    @Override
    public void recieveMessage(String message) {
        // TODO:
    }

    @Override
    public String getConsoleTag() {
        return "[ROBOT]";
    }

    @Override
    public int getLightSensorValue() {
        return statusInfoBuffer.getLatestLightSensorInfo();
    }

    @Override
    public int getUltraSensorValue() {
        return statusInfoBuffer.getLatestUltraSensorInfo();
    }

    @Override
    protected boolean checkForObstruction() {
    	busy = true;
    	communicator.sendCommand(Command.CHECK_FOR_OBSTRUCTION);
    	waitUntilDone();
        if (statusInfoBuffer.getExtraUltrasonicSensorValue() < detectionDistanceUltrasonicSensorRobot)
            return true;
        return false;
    }

    @Override
    public void alignOnWhiteLine() {
    	busy = true;
    	communicator.sendCommand(Command.ALIGN_PERPENDICULAR);
    	super.alignOnWhiteLine();
    	waitUntilDone();
    }

    @Override
    public void alignOnWalls() {
    	busy = true;
    	communicator.sendCommand(Command.ALIGN_WALL);
    	super.alignOnWalls();
    	waitUntilDone();
    }

    @Override
    protected int getRotateSleepTime(double angle) {
        return speed / 10;
    }

    @Override
    protected int getTravelSleepTime(double distance) {
        return speed / ((int) Math.ceil(Math.abs(distance)));
    }

    @Override
    public void rotate(final double alpha) {
    	busy = true;
    	communicator.sendCommand((int)(alpha * 100 + Command.AUTOMATIC_TURN_ANGLE));
    	super.rotate(alpha);
    	waitUntilDone();
    }

    @Override
    public void travel(final double distance) {
    	busy = true;
    	communicator.sendCommand((int)(distance * 100 + Command.AUTOMATIC_MOVE_FORWARD));
    	//TODO: barcodes
    	super.travel(distance);
    	waitUntilDone();
    }
    
    public void robotDone() {
    	busy = false;
    }
    
    private void waitUntilDone() {
    	try {
            while (busy)
                Thread.sleep(100);    		
    	} catch(Exception e) {
    		
    	}
    }

    @Override
    public void stopReadingBarcodes() {
    	busy = true;
        communicator.sendCommand(Command.STOP_READING_BARCODES);
        super.stopReadingBarcodes();
    	waitUntilDone();
    }

    @Override
    public void startReadingBarcodes() {
    	busy = true;
        communicator.sendCommand(Command.START_READING_BARCODES);
        super.startReadingBarcodes();
    	waitUntilDone();
    }
    
    @Override
    public void permaStopReadingBarcodes() {
    	busy = true;
        communicator.sendCommand(Command.PERMA_STOP_READING_BARCODES);
        super.permaStopReadingBarcodes();
    	waitUntilDone();
    }
}