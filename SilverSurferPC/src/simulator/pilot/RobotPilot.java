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
    private boolean ready = false;

    public RobotPilot(int teamNumber) {
        super(teamNumber);
        statusInfoBuffer = new StatusInfoBuffer(this);
        communicator = new Communicator();
        try {
            communicator.openRobotConnection(statusInfoBuffer, IRT);
        } catch (Exception e) {
            System.out.println("[CONNECTION] Error connecting the robot!");
        }
    }

    @Override
    public void alignOnWalls() {
        busy = true;
        communicator.sendCommand(Command.ALIGN_WALL);
        super.rotate(90);
        super.rotate(-90);
        super.rotate(-90);
        super.rotate(90);
        waitUntilDone();
    }

    @Override
    public void alignOnWhiteLine() {
        busy = true;
        communicator.sendCommand(Command.ALIGN_WHITE_LINE);
        super.travel(40);
        waitUntilDone();
        if (readBarcodes && !permaBarcodeStop && isExecutingBarcode()) {
            pilotActions.barcodeFound();
        }
        setBusyExecutingBarcode(false);
    }

    @Override
    protected boolean checkForObstruction() {
        busy = true;
        communicator.sendCommand(Command.CHECK_FOR_OBSTRUCTION);
        waitUntilDone();
        if (statusInfoBuffer.getExtraUltrasonicSensorValue() < detectionDistanceUltrasonicSensorRobot) {
            return true;
        }
        return false;
    }

   
    public void endConnection() {
        try {
            communicator.closeRobotConnection();
        } catch (Exception e) {
            System.out.println("[CONNECTION] Error disconnecting the robot!");
        }
    }

    @Override
    public String getConsoleTag() {
        return "[ROBOT]";
    }

    @Override
    public int getInfraRedSensorValue() {
        return statusInfoBuffer.getLatestInfraRedSensorInfo();
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
    public void permaStopReadingBarcodes() {
        busy = true;
        communicator.sendCommand(Command.PERMA_STOP_READING_BARCODES);
        super.permaStopReadingBarcodes();
        waitUntilDone();
    }

    @Override
    protected int readBarcode() {
        return statusInfoBuffer.getBarcode();
    }

    public void robotDone() {
        busy = false;
    }

    @Override
    public void rotate(final double alpha) {
        busy = true;
        communicator
                .sendCommand((int) (alpha * 100 + Command.AUTOMATIC_TURN_ANGLE));
        super.rotate(alpha);
        waitUntilDone();
    }

    @Override
    public void setReadBarcodes(boolean readBarcodes) {
        busy = true;
        if (readBarcodes) {
            communicator.sendCommand(Command.START_READING_BARCODES);
        } else {
            communicator.sendCommand(Command.STOP_READING_BARCODES);
        }
        super.setReadBarcodes(readBarcodes);
        waitUntilDone();
    }

    @Override
    public void setSpeed(int speed) {
        busy = true;
        if (speed == 1) {
            communicator.sendCommand(Command.SLOW_SPEED);
        } else if (speed == 2) {
            communicator.sendCommand(Command.NORMAL_SPEED);
        } else if (speed == 3) {
            communicator.sendCommand(Command.FAST_SPEED);
        } else {
            communicator.sendCommand(Command.VERY_FAST_SPEED);
        }
        super.setSpeed(speed);
        waitUntilDone();
    }

    @Override
    public void travel(final double distance) {
        busy = true;
        communicator
                .sendCommand((int) (distance * 100 + Command.AUTOMATIC_MOVE_FORWARD));
        super.travel(distance);
        waitUntilDone();
        if (readBarcodes && !permaBarcodeStop && isExecutingBarcode())
            pilotActions.barcodeFound();
        setBusyExecutingBarcode(false);
    }

    private void waitUntilDone() {
        try {
            while (busy)
                Thread.sleep(100);
        } catch (Exception e) {

        }
    }
    
    public boolean isReady() {
    	return ready;
    }
    
    public void setReady() {
    	ready = true;
    }
}