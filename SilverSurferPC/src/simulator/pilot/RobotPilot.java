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
    private int barcode;

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

    public void endConnection() {
        try {
            communicator.closeRobotConnection();
        } catch (Exception e) {
            System.out.println("[CONNECTION] Error disconnecting the robot!");
        }
    }

    public boolean getBusy() {
        return busy;
    }

    public void robotDone() {
        busy = false;
    }

    @Override
    public String getConsoleTag() {
        return "[ROBOT]";
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

    @Override
    public int getLightSensorValue() {
        return statusInfoBuffer.getLatestLightSensorInfo();
    }

    @Override
    public int getUltraSensorValue() {
        return statusInfoBuffer.getLatestUltraSensorInfo();
    }

    @Override
    public int getInfraRedSensorValue() {
        return statusInfoBuffer.getLatestInfraRedSensorInfo();
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
    public void travel(final double distance) {
        busy = true;
        communicator
                .sendCommand((int) (distance * 100 + Command.AUTOMATIC_MOVE_FORWARD));
        super.travel(distance);
        waitUntilDone();
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
    protected int readBarcode() {
        return barcode;
    }

    public void setLatestBarcode(int barcode) {
        this.barcode = barcode;
    }

    @Override
    public void setReadBarcodes(boolean readBarcodes) {
        busy = true;
        if (readBarcodes)
            communicator.sendCommand(Command.START_READING_BARCODES);
        else
            communicator.sendCommand(Command.STOP_READING_BARCODES);
        super.setReadBarcodes(readBarcodes);
        waitUntilDone();
    }

    @Override
    public void permaStopReadingBarcodes() {
        busy = true;
        communicator.sendCommand(Command.PERMA_STOP_READING_BARCODES);
        super.permaStopReadingBarcodes();
        waitUntilDone();
    }

    private void waitUntilDone() {
        try {
            while (busy || isExecutingBarcode())
                Thread.sleep(100);
        } catch (Exception e) {

        }
    }
}