package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;

import commands.Command;

public class Communicator {

    private InfoReceiverThread IRT;
    private static DataInputStream dis;
    private static DataOutputStream dos;
    private static NXTConnector connection;
    private static String deviceURL = "00:16:53:0A:04:5A";
    private static String deviceName = "Silver";

    public void closeRobotConnection() throws Exception {
        dos.writeInt(Command.CLOSE_CONNECTION);
        dos.flush();
        IRT.setQuit(true);
        dis.close();
        dos.close();
        connection.close();
    }

    public void openRobotConnection(StatusInfoBuffer statusInfoBuffer,
            InfoReceiverThread IRT) throws Exception {
        connection = new NXTConnector();
        connection.connectTo(deviceName, deviceURL, NXTCommFactory.BLUETOOTH,
                NXTComm.PACKET);
        dis = connection.getDataIn();
        dos = connection.getDataOut();
        if (dis == null || dos == null) {
            throw new IOException();
        }
        this.IRT = IRT;
        this.IRT = new InfoReceiverThread(statusInfoBuffer, dis);
        this.IRT.start();
    }

    public void sendCommand(final int command) {
        try {
            dos.writeInt(command);
            dos.flush();
        } catch (Exception e) {
            System.out.println("Error in Communicator.sendCommand(" + command + ")!");
        }
    }
}