package communication;

import java.io.*;

import commands.Command;
import lejos.pc.comm.*;

public class RobotCommunicator extends UnitCommunicator{

	private static DataInputStream dis;
	private static DataOutputStream dos;
	private static NXTConnector connection = new NXTConnector();
	private static String deviceURL = "00:16:53:0A:04:5A";
	private static String deviceName = "Silver";
	
	//private static InfoReceiverThread CRT;
	
	@Override
	public void openUnitConnection() throws IOException {
		connection.connectTo(deviceName, deviceURL, NXTCommFactory.BLUETOOTH, NXTComm.PACKET);
    	dis = connection.getDataIn();
    	dos = connection.getDataOut();
    	if(dis == null || dos == null)
    		throw new IOException();
    	//CRT = new InfoReceiverThread();
    	//CRT.setDis(dis);
    	//CRT.setDos(dos);
    	//CRT.start();
	}
	
	@Override
	public void closeUnitConnection() throws Exception {
		//CRT.setQuit(true);
		dis.close();
		dos.close();
		connection.close();
	}
	
	@Override
	public void sendCommandToUnit(int command) throws IOException {
		dos.writeInt(command); //write(int command) is NIET goed!
		dos.flush();
	}

	private void turnCommandsListenerOn() {
		// TODO Auto-generated method stub

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
	
	@Override
	public int getSpeed() {
		return 0;
	}
	
	@Override
	public void setSpeed(int speed) {
		try {
			if(speed == 1)
				sendCommandToUnit(10);
			else if(speed == 2)
				sendCommandToUnit(11);
			else if(speed == 3)
				sendCommandToUnit(12);
			else
				sendCommandToUnit(13);
		} catch (IOException e) {

		}
	}
	
	public String getConsoleTag() {
		return "[ROBOT]";
	}
}