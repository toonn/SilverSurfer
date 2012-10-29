package communication;

public class StatusInfoBuffer {

	private int lightSensorInfo;
	private int ultraSensorInfo;
	private boolean pushSensor1Info;
	private boolean pushSensor2Info;

	
	public int getLightSensorInfo() {
		return lightSensorInfo;
	}
	public void setLightSensorInfo(int lightSensorInfo) {
		this.lightSensorInfo = lightSensorInfo;
	}
	
	public int getUltraSensorInfo() {
		return ultraSensorInfo;
	}
	public void setUltraSensorInfo(int ultraSensorInfo) {
		this.ultraSensorInfo = ultraSensorInfo;
	}
	
	public boolean getPushSensor1Info() {
		return pushSensor1Info;
	}
	public void setPushSensor1Info(boolean pushSensor2Info) {
		this.pushSensor2Info = pushSensor2Info;
	}
	
	public boolean getPushSensor2Info() {
		return pushSensor1Info;
	}
	public void setPushSensor2Info(boolean pushSensor2Info) {
		this.pushSensor2Info = pushSensor2Info;
	}

}
