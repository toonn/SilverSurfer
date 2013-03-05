package simulator.pilot;

import commands.Command;
import communication.Communicator;
import communication.StatusInfoBuffer;

public class RobotPilot extends AbstractPilot {

    private StatusInfoBuffer statusInfoBuffer;
    private Communicator communicator;

    public RobotPilot() {
        statusInfoBuffer = new StatusInfoBuffer();
        communicator = new Communicator(this, statusInfoBuffer);
        statusInfoBuffer.setCommunicator(communicator);
    }

    @Override
    public void setSpeed(int speed) {
        super.setSpeed(speed);
        communicator.setSpeed(speed);
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
    protected int getRotateSleepTime(double angle) {
        return speed / 10;
    }

    @Override
    protected int getTravelSleepTime(double distance) {
        return speed / ((int) Math.ceil(Math.abs(distance)));
    }

    @Override
    public void rotate(final double alpha) {
        /*
         * final MoveTurnThread MTT = new MoveTurnThread("MTT", communicator, 0,
         * (int) alpha, 0, 0); MTT.start();
         */
    }

    @Override
    public void travel(final double distance) {
        /*
         * final MoveTurnThread MTT = new MoveTurnThread("MTT", communicator,
         * (int) distance, 0, 0, 0); MTT.start();
         */
    }

    public void stopReadingBarcodes() {
        communicator.sendCommand(Command.STOP_READING_BARCODES);
    }

    public void startReadingBarcodes() {
        communicator.sendCommand(Command.START_READING_BARCODES);
    }

    public boolean getExecutingBarcodes() {
        return communicator.getExecutingBarcodes();
    }
}