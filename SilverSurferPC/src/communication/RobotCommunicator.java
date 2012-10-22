package communication;

import java.io.*;
import commands.Command;
import lejos.pc.comm.*;

public class RobotCommunicator extends UnitCommunicator{

	private static InputStream dis;
	private static OutputStream dos;
	private static NXTConnector connection = new NXTConnector();
	private static String deviceURL = "00:16:53:0A:04:5A";
	private static String deviceName = "Silver";
	
	@Override
	public void openUnitConnection() throws IOException {
		connection.connectTo(deviceName, deviceURL, NXTCommFactory.BLUETOOTH, NXTComm.PACKET);
    	dis = connection.getInputStream();
    	dos = connection.getOutputStream();
    	if(dis == null || dos == null)
    		throw new IOException();
	}
	
	@Override
	public void closeUnitConnection() throws Exception {
		dis.close();
		dos.close();
		connection.close();
	}
	
	@Override
	public void sendCommandToUnit(int command) throws IOException {
		dos.write(command);
		dos.flush();
	}

	@Override
	public void runPolygon(int amtOfAngles, int lengthInCM) throws IOException {
		int angle = (int)Math.round(ANGLE_COEF/amtOfAngles);
		int lengthInDeg = (int)Math.round(LENGTH_COEF * lengthInCM);
		for(int i = 0; i<amtOfAngles; i++) {
			sendCommandToUnit(lengthInDeg*100 + Command.AUTOMATIC_MOVE_FORWARD);
			sendCommandToUnit(angle*100 + Command.AUTOMATIC_TURN_ANGLE);
		}
	}
	
	public String getConsoleTag() {
		return "[ROBOT]";
	}
}