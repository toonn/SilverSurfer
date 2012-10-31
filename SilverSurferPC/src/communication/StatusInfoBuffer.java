package communication;

import gui.SilverSurferGUI;

public class StatusInfoBuffer {

	private int lightSensorInfo;
	private int ultraSensorInfo;
	private boolean touchSensor1Info;
	private boolean touchSensor2Info;
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
	
	public boolean getTouchSensor1Info() {
		return touchSensor1Info;
	}
	
	public void setTouchSensor1Info(boolean touchSensor1Info) {
		this.touchSensor1Info = touchSensor1Info;
		SSG.updateStatus();
	}
	
	public boolean getTouchSensor2Info() {
		return touchSensor2Info;
	}
	
	public void setTouchSensor2Info(boolean touchSensor2Info) {
		this.touchSensor2Info = touchSensor2Info;
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
	
	public SilverSurferGUI getSSG() {
		return SSG;
	}
	
	public void setSSG(SilverSurferGUI SSG) {
		this.SSG = SSG;
	}
}
