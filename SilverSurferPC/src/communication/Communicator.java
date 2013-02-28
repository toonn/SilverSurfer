package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import mapping.ExtMath;
import mapping.Orientation;
import mazeAlgorithm.MazeExplorer;
import simulator.BarcodeThread;
import simulator.pilot.AbstractPilot;

import commands.Command;

public class Communicator {
    private StatusInfoBuffer statusInfoBuffer;
    private final AbstractPilot pilot;
    private boolean robotConnected = false;
    private static DataInputStream dis;
    private static DataOutputStream dos;
    private static NXTConnector connection;
    private static String deviceURL = "00:16:53:0A:04:5A";
    private static String deviceName = "Silver";
    private static InfoReceiverThread IRT;
    private boolean busy = false;
    private int tilesBeforeAllign = 5;
    private int tilesRidden = 0;
    private boolean mustAllign = true;
    private MazeExplorer explorer;
    private boolean readBarcodes = true;
    private boolean permaBarcodeStop = false;
    private boolean executingBarcode = false;
    private BarcodeThread BT;

    public Communicator(final AbstractPilot simulationPilot,
            final StatusInfoBuffer statusInfoBuffer) {
        pilot = simulationPilot;
        setSpeed(2);
    }

    public void clear() {
        pilot.clear();
    }

    public void closeRobotConnection() throws Exception {
        dos.writeInt(Command.CLOSE_CONNECTION);
        dos.flush();
        IRT.setQuit(true);
        dis.close();
        dos.close();
        connection.close();
    }

    /**
     * Gets the amount of angles the arrow should turn in one event to be at par
     * with the robot.
     */
    public double getAngularSpeed() {
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

    public boolean getExecutingBarcodes() {
        return executingBarcode;
    }

    public MazeExplorer getExplorer() {
        return explorer;
    }

    public AbstractPilot getPilot() {
        return pilot;
    }

    public boolean getRobotConnected() {
        return robotConnected;
    }

    public int getSpeed() {
        return pilot.getSpeed();
    }

    public StatusInfoBuffer getStatusInfoBuffer() {
        return statusInfoBuffer;
    }

    public int getTilesBeforeAllign() {
        return tilesBeforeAllign;
    }

    private int getTilesRidden() {
        return tilesRidden;
    }

    public void goToNextTile(final Orientation orientation) throws IOException {
        final double currentAngle = pilot.getAngle();
        final int angleToRotate = (int) ExtMath
                .getSmallestAngle((int) (orientation.getRightAngle() - currentAngle));
        sendCommand(angleToRotate * 100 + Command.AUTOMATIC_TURN_ANGLE);

        if (mustAllign) {
            tilesRidden++;
            if (getTilesRidden() == getTilesBeforeAllign()) {
                sendCommand(Command.ALIGN_PERPENDICULAR);
                sendCommand(24 * 100 + Command.AUTOMATIC_MOVE_FORWARD);
                setTilesRidden(0);
            } else {
                sendCommand(40 * 100 + Command.AUTOMATIC_MOVE_FORWARD);
            }
        } else {
            sendCommand(40 * 100 + Command.AUTOMATIC_MOVE_FORWARD);
        }

    }

    public void moveTurn(final int lengthInCM, final int anglesInDegrees,
            final int amtOfAngles) {
        final int length = lengthInCM * 100 + Command.AUTOMATIC_MOVE_FORWARD;
        int angles;
        if (amtOfAngles == 0) {
            angles = anglesInDegrees * 100 + Command.AUTOMATIC_TURN_ANGLE;
            sendCommand(length);
            sendCommand(angles);
        } else {
            angles = (int) Math.round(360.0 / amtOfAngles) * 100
                    + Command.AUTOMATIC_TURN_ANGLE;
            if (amtOfAngles == 1) {
                sendCommand(length);
            } else {
                for (int i = 0; i < amtOfAngles; i++) {
                    sendCommand(length);
                    sendCommand(angles);
                }
            }
        }
    }

    public void mustAllign(final boolean mustAllign) {
        this.mustAllign = mustAllign;
    }

    public void openRobotConnection() throws Exception {
        connection = new NXTConnector();
        connection.connectTo(deviceName, deviceURL, NXTCommFactory.BLUETOOTH,
                NXTComm.PACKET);
        dis = connection.getDataIn();
        dos = connection.getDataOut();
        if (dis == null || dos == null) {
            throw new IOException();
        }
        IRT = new InfoReceiverThread(statusInfoBuffer, dis, dos);
        IRT.start();
    }

    private void readBarcode() {
        final int value = pilot.getMapGraphLoaded()
                .getTile(pilot.getRelativePosition()).getContent().getValue();
        statusInfoBuffer.setBarcode(value);
    }

    public void sendCommand(final int command) {
        try {
            if (robotConnected) {
                busy = true;
                dos.writeInt(command);
                dos.flush();
            }
            if (command == Command.SLOW_SPEED) {
                pilot.setSpeed(1);
            } else if (command == Command.NORMAL_SPEED) {
                pilot.setSpeed(2);
            } else if (command == Command.FAST_SPEED) {
                pilot.setSpeed(3);
            } else if (command == Command.VERY_FAST_SPEED) {
                pilot.setSpeed(4);
            } else if (command == Command.ALIGN_PERPENDICULAR) {
                pilot.alignOnWhiteLine();
            } else if (command == Command.ALIGN_WALL) {
                pilot.allignOnWalls();
            } else if (command == Command.CHECK_OBSTRUCTIONS_AND_SET_TILE
                    && !robotConnected) {
                pilot.checkForObstructionAndSetTile();
            } else if (command == Command.STOP_READING_BARCODES) {
                readBarcodes = false;
            } else if (command == Command.START_READING_BARCODES) {
                readBarcodes = true;
            } else if (command == Command.PERMA_STOP_READING_BARCODES) {
                permaBarcodeStop = true;
            } else if (command % 100 == Command.AUTOMATIC_MOVE_FORWARD) {
                if (!getRobotConnected()) {
                    try {
                        if (readBarcodes && !permaBarcodeStop) {
                            BT = new BarcodeThread("BT", pilot);
                            BT.start();
                        }
                        final int amount = (command - Command.AUTOMATIC_MOVE_FORWARD) / 100;
                        pilot.travel(amount);
                        if (readBarcodes && !permaBarcodeStop) {
                            final boolean found = BT.getFound();
                            BT.setQuit(true);
                            if (found) {
                                readBarcode();
                            }
                        }
                    } catch (final Exception e) {
                        System.out.println("Error in Communicator.sendCommand("
                                + command + ")!");
                    }
                } else {
                    final int amount = (command - Command.AUTOMATIC_MOVE_FORWARD) / 100;
                    pilot.travel(amount);
                }
            } else if (command % 100 == Command.AUTOMATIC_TURN_ANGLE) {
                double amount = (double) (command - Command.AUTOMATIC_TURN_ANGLE) / 100;
                while (amount-- > 0) {
                    pilot.rotate(1);
                }
            } else if (command % 100 == -(100 - Command.AUTOMATIC_TURN_ANGLE)) {
                double amount = (double) (command - Command.AUTOMATIC_TURN_ANGLE) / 100;
                while (amount++ < 0) {
                    pilot.rotate(-1);
                }
            }
            if (robotConnected) {
                while (busy) {
                    Thread.sleep(100);
                }
            }
        } catch (final Exception e) {
            System.out.println("Error in Communicator.sendCommand(" + command
                    + ")!");
        }
    }

    public void setBusy(final boolean busy) {
        this.busy = busy;
    }

    public void setExecutingBarcodes(final boolean executing) {
        executingBarcode = executing;
    }

    public void setExplorer(final MazeExplorer explorer) {
        this.explorer = explorer;
    }

    public void setRobotConnected(final boolean robotConnected)
            throws Exception {
        if (robotConnected) {
            openRobotConnection();
        } else {
            closeRobotConnection();
        }
        this.robotConnected = robotConnected;
        setSpeed(2);
    }

    public void setSpeed(final int speed) {
        if (speed == 1) {
            sendCommand(Command.SLOW_SPEED);
        } else if (speed == 2) {
            sendCommand(Command.NORMAL_SPEED);
        } else if (speed == 3) {
            sendCommand(Command.FAST_SPEED);
        } else {
            sendCommand(Command.VERY_FAST_SPEED);
        }
    }

    public void setTilesBeforeAllign(final int tilesBeforeAllign) {
        this.tilesBeforeAllign = tilesBeforeAllign;
    }

    private void setTilesRidden(final int tilesRidden) {
        this.tilesRidden = tilesRidden;
    }

}