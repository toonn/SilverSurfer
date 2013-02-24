package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class InfoReceiverThread extends Thread {

    private static DataInputStream dis;
    private static DataOutputStream dos;
    private final StatusInfoBuffer statusInfoBuffer;
    private boolean quit = false;
    private final double[] coordinates = new double[2];

    public InfoReceiverThread(final StatusInfoBuffer statusInfoBuffer,
            final DataInputStream dis, final DataOutputStream dos) {
        this.statusInfoBuffer = statusInfoBuffer;
        InfoReceiverThread.dis = dis;
        InfoReceiverThread.dos = dos;
    }

    public DataInputStream getDis() {
        return dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    @Override
    public void run() {
        byte[] b;

        while (!quit) {
            try {
                b = new byte[500];
                dis.read(b);
                final String a = new String(b);
                if (a.startsWith("[LS]")) {
                    statusInfoBuffer.addLightSensorInfo(Integer.parseInt(a
                            .substring(5).trim()));
                } else if (a.startsWith("[US]")) {
                    statusInfoBuffer.addUltraSensorInfo(Integer.parseInt(a
                            .substring(5).trim()));
                } else if (a.startsWith("[LM]")) {
                    if (a.substring(5).startsWith("true")) {
                        statusInfoBuffer.setLeftMotorMoving(true);
                        statusInfoBuffer.setLeftMotorSpeed(Integer.parseInt(a
                                .substring(10).trim()));
                    } else if (a.substring(5).startsWith("false")) {
                        statusInfoBuffer.setLeftMotorMoving(false);
                        statusInfoBuffer.setLeftMotorSpeed(Integer.parseInt(a
                                .substring(11).trim()));
                    }
                } else if (a.startsWith("[RM]")) {
                    if (a.substring(5).startsWith("true")) {
                        statusInfoBuffer.setRightMotorMoving(true);
                        statusInfoBuffer.setRightMotorSpeed(Integer.parseInt(a
                                .substring(10).trim()));
                    } else if (a.substring(5).startsWith("false")) {
                        statusInfoBuffer.setRightMotorMoving(false);
                        statusInfoBuffer.setRightMotorSpeed(Integer.parseInt(a
                                .substring(11).trim()));
                    }
                } else if (a.startsWith("[B]")) {
                    statusInfoBuffer.getCommunicator().setBusy(false);
                    statusInfoBuffer.setBusy(Boolean.valueOf(a.substring(4)
                            .trim()));
                } else if (a.startsWith("[X]")) {
                    coordinates[0] = Double.valueOf(a.substring(4).trim());
                } else if (a.startsWith("[Y]")) {
                    coordinates[1] = Double.valueOf(a.substring(4).trim());
                    statusInfoBuffer.setCoordinatesAbsolute(coordinates);
                } else if (a.startsWith("[ANG]")) {
                    statusInfoBuffer.setAngle(Double.valueOf(a.substring(6)
                            .trim()));
                } else if (a.startsWith("[BC]")) {
                    statusInfoBuffer.setBarcode(Integer.parseInt(a.substring(5)
                            .trim()));
                } else if (a.startsWith("[CH]")) {
                    statusInfoBuffer.addUltraSensorInfo(Integer.parseInt(a
                            .substring(5).trim()));
                    statusInfoBuffer.getCommunicator().getPilot()
                            .checkForObstructionAndSetTile();
                }
            } catch (final Exception e) {
                System.out.println("Error in InfoReceiverThread.run()!");
                e.printStackTrace();
            }
        }
    }

    public void setQuit(final boolean quit) {
        this.quit = quit;
    }
}