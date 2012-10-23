package sensors;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.RCXLightSensor;

public class LightSensorManager {

	public static void main(String[] args) {
		RCXLightSensor light = new RCXLightSensor(SensorPort.S2);
		while(true){
			Motor.A.forward();
			Motor.B.backward();
			System.out.println(light.getLightValue());
		}
	}
}
