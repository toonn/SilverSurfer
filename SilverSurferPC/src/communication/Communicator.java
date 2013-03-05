package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;

import commands.Command;

public class Communicator {
	
	private StatusInfoBuffer statusInfoBuffer;
    private static DataInputStream dis;
    private static DataOutputStream dos;
    private static NXTConnector connection;
    private static String deviceURL = "00:16:53:0A:04:5A";
    private static String deviceName = "Silver";

    public Communicator(StatusInfoBuffer statusInfoBuffer) {
        this.statusInfoBuffer = statusInfoBuffer;
    }
    
    public void sendCommand(final int command) {
    	try {
    		dos.writeInt(command);
    		dos.flush();
    	} catch(Exception e) {
            System.out.println("Error in Communicator.sendCommand(" + command + ")!");
    	}
    }

    public void openRobotConnection(InfoReceiverThread IRT) throws Exception {
        connection = new NXTConnector();
        connection.connectTo(deviceName, deviceURL, NXTCommFactory.BLUETOOTH, NXTComm.PACKET);
        dis = connection.getDataIn();
        dos = connection.getDataOut();
        if (dis == null || dos == null)
            throw new IOException();
        IRT = new InfoReceiverThread(statusInfoBuffer, dis, dos);
        IRT.start();
    }

    public void closeRobotConnection(InfoReceiverThread IRT) throws Exception {
        dos.writeInt(Command.CLOSE_CONNECTION);
        dos.flush();
        IRT.setQuit(true);
        dis.close();
        dos.close();
        connection.close();
    }
}



/*


    public void goToNextTile(final Orientation orientation) throws IOException {
        final double currentAngle = pilot.getAngle();
        final int angleToRotate = (int) ExtMath.getSmallestAngle((int) (orientation.getRightAngle() - currentAngle));
        sendCommand(angleToRotate * 100 + Command.AUTOMATIC_TURN_ANGLE);

        if (mustAllign) {
            tilesRidden++;
            if (getTilesRidden() == getTilesBeforeAllign()) {
                sendCommand(Command.ALIGN_PERPENDICULAR);
                sendCommand(24 * 100 + Command.AUTOMATIC_MOVE_FORWARD);
                setTilesRidden(0);
            }
            else
                sendCommand(40 * 100 + Command.AUTOMATIC_MOVE_FORWARD);
        }
        else
            sendCommand(40 * 100 + Command.AUTOMATIC_MOVE_FORWARD);
    }
    
    
    
    
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
        pilot.setObstructionOrTile();
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
*/