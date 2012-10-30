package communication;

import gui.SilverSurferGUI;

public class StatusInfoBuffer {

	private int lightSensorInfo;
	private int ultraSensorInfo;
	private boolean pushSensor1Info;
	private boolean pushSensor2Info;
	private boolean leftMotorMoving;
	private boolean rightMotorMoving;
	private int leftMotorSpeed;
	private int rightMotorSpeed;
	private SilverSurferGUI SSG;

	
	
	public int getLightSensorInfo() {
		return lightSensorInfo;
	}
	
	public void setLightSensorInfo(int lightSensorInfo) {
		this.lightSensorInfo = lightSensorInfo;
		SSG.updateStatus();
	}
	
	public int getUltraSensorInfo() {
		return ultraSensorInfo;
	}
	
	public void setUltraSensorInfo(int ultraSensorInfo) {
		this.ultraSensorInfo = ultraSensorInfo;
		SSG.updateStatus();
	}
	
	public boolean getPushSensor1Info() {
		return pushSensor1Info;
	}
	
	public void setPushSensor1Info(boolean pushSensor1Info) {
		this.pushSensor1Info = pushSensor1Info;
		SSG.updateStatus();
	}
	
	public boolean getPushSensor2Info() {
		return pushSensor2Info;
	}
	
	public void setPushSensor2Info(boolean pushSensor2Info) {
		this.pushSensor2Info = pushSensor2Info;
		SSG.updateStatus();
	}
	
	public boolean getLeftMotorMoving() {
		return leftMotorMoving;
	}
	
	public void setLeftMotorMoving(boolean leftMotorMoving) {
		this.leftMotorMoving = leftMotorMoving;
		SSG.updateStatus();
	}
	
	public boolean getRightMotorMoving() {
		return rightMotorMoving;
	}
	
	public void setRightMotorMoving(boolean rightMotorMoving) {
		this.rightMotorMoving = rightMotorMoving;
		SSG.updateStatus();
	}
	
	public int getLeftMotorSpeed() {
		return leftMotorSpeed;
	}
	
	public void setLeftMotorSpeed(int leftMotorSpeed) {
		this.leftMotorSpeed = leftMotorSpeed;
		SSG.updateStatus();
	}
	
	public int getRightMotorSpeed() {
		return rightMotorSpeed;
	}
	
	public void setRightMotorSpeed(int rightMotorSpeed) {
		this.rightMotorSpeed = rightMotorSpeed;
		SSG.updateStatus();
	}
	
	public void setSSG(SilverSurferGUI sSG) {
		SSG = sSG;
	}
	
	public SilverSurferGUI getSSG() {
		return SSG;
	}
}
