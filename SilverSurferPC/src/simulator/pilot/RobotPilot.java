package simulator.pilot;

import java.awt.Point;

import mapping.MapGraph;
import mapping.Seesaw;
import mapping.Tile;
import mazeAlgorithm.CollisionAvoidedException;
import commands.Command;
import commands.Sleep;
import communication.Communicator;
import communication.InfoReceiverThread;
import communication.StatusInfoBuffer;

public class RobotPilot extends AbstractPilot {

    private StatusInfoBuffer statusInfoBuffer;
    private Communicator communicator;
    private static InfoReceiverThread IRT;
    private boolean busy = false;
    private boolean ready = false;

    public RobotPilot(int playerNumber, MapGraph mapGraphLoaded, Point mapSize) {
        super(playerNumber, mapGraphLoaded, mapSize);
        statusInfoBuffer = new StatusInfoBuffer(this);
        communicator = new Communicator();
        try {
            communicator.openRobotConnection(statusInfoBuffer, IRT);
        } catch (Exception e) {
            System.out.println("[CONNECTION] Error connecting the robot!");
        }
    }

    @Override
    public void alignOnWhiteLine() {
        busy = true;
        communicator.sendCommand(Command.ALIGN_WHITE_LINE);
        super.travel(40);
        waitUntilDone();
        if (readBarcodes && isExecutingBarcode()) {
            pilotActions.barcodeFound();
        }
        setBusyExecutingBarcode(false);
    }

    @Override
    protected boolean checkForObstruction() {
        busy = true;
        communicator.sendCommand(Command.CHECK_FOR_OBSTRUCTION);
        waitUntilDone();
        if (statusInfoBuffer.getExtraUltrasonicSensorValue() < DETECTION_DISTANCE_ULTRASONIC_SENSOR_ROBOT) {
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
    public void travel(final double distance, boolean ignoreCollision) throws CollisionAvoidedException {
        busy = true;
        boolean succes = true;
        communicator.sendCommand((int) (distance * 100 + Command.AUTOMATIC_MOVE_FORWARD));
        try {
            super.travel(distance, false);
        } catch(CollisionAvoidedException e) {
        	communicator.sendCommand(Command.UNDO_ACTION);
        	succes = false;
        }
        waitUntilDone();
        if(!succes || statusInfoBuffer.getUndidAction()) {
        	setPosition(statusInfoBuffer.getX(), statusInfoBuffer.getY());
        	throw new CollisionAvoidedException();     
        }   
        if (readBarcodes && isExecutingBarcode())
            pilotActions.barcodeFound();
        setBusyExecutingBarcode(false);
    }

    private void waitUntilDone() {
        while (busy)
            new Sleep().sleepFor(100);
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady() {
        ready = true;
    }

    @Override
    public void crossOpenSeesaw(int seesawValue) {
        try {
            if (isInGameModus()
                    && !getCenter().getPlayerClient().hasLockOnSeesaw(
                            seesawValue))
                getCenter().getPlayerClient().lockSeesaw(seesawValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        busy = true;
        communicator.sendCommand(Command.CROSS_OPEN_SEESAW);
        boolean readBarcodesBackup = readBarcodes;
        readBarcodes = false;
        super.travel(60);
        for (Tile tile : getMapGraphConstructed().getTiles())
            if (tile.getContent() instanceof Seesaw
                    && tile.getContent().getValue() == seesawValue)
                ((Seesaw) tile.getContent()).flipSeesaw();
        super.travel(60);
        try {
            if (isInGameModus()
                    && getCenter().getPlayerClient().hasLockOnSeesaw())
                getCenter().getPlayerClient().unlockSeesaw();
        } catch (Exception e) {
            e.printStackTrace();
        }
        readBarcodes = readBarcodesBackup;
        super.travel(40);
        waitUntilDone();
        if (readBarcodes && isExecutingBarcode())
            pilotActions.barcodeFound();
        setBusyExecutingBarcode(false);
    }

    @Override
    public void crossClosedSeesaw(int seesawValue) {
        try {
            if (isInGameModus())
                getCenter().getPlayerClient().lockSeesaw(seesawValue);
        } catch (Exception e) {

        }
        busy = true;
        communicator.sendCommand(Command.CROSS_CLOSED_SEESAW);
        for (Tile tile : getMapGraphConstructed().getTiles())
            if (tile.getContent() instanceof Seesaw
                    && tile.getContent().getValue() == seesawValue)
                ((Seesaw) tile.getContent()).flipSeesaw();
        boolean readBarcodesBackup = readBarcodes;
        readBarcodes = false;
        super.travel(120);
        readBarcodes = readBarcodesBackup;
        super.travel(40);
        waitUntilDone();
        if (readBarcodes && isExecutingBarcode())
            pilotActions.barcodeFound();
        setBusyExecutingBarcode(false);
    }

    @Override
    public void pickupObject(int team) {
        busy = true;
        communicator.sendCommand(Command.PICKUP_OBJECT);
        boolean readBarcodesBackup = readBarcodes;
        readBarcodes = false;
        super.rotate(180);
        super.travel(-30);
        setTeamNumber(team);
        super.travel(30); // Eerst naar voor zodat de arm naar boven kan komen
        super.travel(-40); // Daarna naar achter
        readBarcodes = readBarcodesBackup;
        waitUntilDone();
    }
}