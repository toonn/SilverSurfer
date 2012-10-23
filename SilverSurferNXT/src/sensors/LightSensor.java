package sensors;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.RCXLightSensor;

public class LightSensor {

	public static void main(String[] args) {
		RCXLightSensor light = new RCXLightSensor(SensorPort.S2);
		light.setFloodlight(true);
		while(true){
			//Motor.A.forward();
			//Motor.B.backward();
			System.out.println(light.getFloodlight());
			System.out.println(light.getLightValue());
		}
	}
}
