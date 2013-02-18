package communication;

import java.io.*;

public class InfoReceiverThread extends Thread {
	
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private StatusInfoBuffer statusInfoBuffer;
	private boolean quit = false;
	private double[] coordinates = new double[2];
	
	public InfoReceiverThread(StatusInfoBuffer statusInfoBuffer) {
		this.statusInfoBuffer = statusInfoBuffer;
	}
	
	@Override
	public void run() {
		byte[] b = new byte[500];

		while(!quit) {
			try {
				b = new byte[500];
				dis.read(b);
				String a = new String(b);
				if(a.startsWith("[LS]"))
					statusInfoBuffer.addLightSensorInfo(Integer.parseInt(a.substring(5).trim()));
				else if(a.startsWith("[US]"))
					statusInfoBuffer.addUltraSensorInfo(Integer.parseInt(a.substring(5).trim()));
				else if(a.startsWith("[LM]")) {
					if(a.substring(5).startsWith("true")) {
						statusInfoBuffer.setLeftMotorMoving(true);
						statusInfoBuffer.setLeftMotorSpeed(Integer.parseInt(a.substring(10).trim()));
					}
					else if(a.substring(5).startsWith("false")) {
						statusInfoBuffer.setLeftMotorMoving(false);
						statusInfoBuffer.setLeftMotorSpeed(Integer.parseInt(a.substring(11).trim()));
					}
				}
				else if(a.startsWith("[RM]")) {
					if(a.substring(5).startsWith("true")) {
						statusInfoBuffer.setRightMotorMoving(true);
						statusInfoBuffer.setRightMotorSpeed(Integer.parseInt(a.substring(10).trim()));
					}
					else if(a.substring(5).startsWith("false")) {
						statusInfoBuffer.setRightMotorMoving(false);
						statusInfoBuffer.setRightMotorSpeed(Integer.parseInt(a.substring(11).trim()));
					}
				}
				else if(a.startsWith("[B]")) {
					statusInfoBuffer.getSSG().getCommunicator().setBusy(false);
					statusInfoBuffer.setBusy(Boolean.valueOf(a.substring(4).trim()));
				}
				else if(a.startsWith("[X]"))
					coordinates[0] = Double.valueOf(a.substring(4).trim());
				else if(a.startsWith("[Y]")) {
					coordinates[1] = Double.valueOf(a.substring(4).trim());
					statusInfoBuffer.setCoordinatesAbsolute(coordinates);
				}
				else if(a.startsWith("[ANG]"))
					statusInfoBuffer.setAngle(Double.valueOf(a.substring(6).trim()));
				else if(a.startsWith("[BC]")) 
					statusInfoBuffer.setBarcode(Integer.parseInt(a.substring(5).trim()));
				else if(a.startsWith("[CH]")) {
					statusInfoBuffer.addUltraSensorInfo(Integer.parseInt(a.substring(5).trim()));
					statusInfoBuffer.getSSG().getCommunicator().getSimulationPilot().setCurrentTileCoordinatesRobot(statusInfoBuffer.getSSG().getCommunicator().getSimulationPilot().getCurrentPositionAbsoluteX(), statusInfoBuffer.getSSG().getCommunicator().getSimulationPilot().getCurrentPositionAbsoluteY());	
					statusInfoBuffer.getSSG().getCommunicator().getSimulationPilot().checkForObstructionAndSetTile();
				}
			} catch (Exception e) {
				System.out.println("Error in InfoReceiverThread.run()!");
				e.printStackTrace();
			}
		}
	}
	
	public DataInputStream getDis() {
		return dis;
	}
	
	public void setDis(DataInputStream dis) {
		InfoReceiverThread.dis = dis;
	}
	
	public DataOutputStream getDos() {
		return dos;
	}
	
	public void setDos(DataOutputStream dos) {
		InfoReceiverThread.dos = dos;
	}
	
	public void setQuit(boolean quit) {
		this.quit = quit;
	}
}