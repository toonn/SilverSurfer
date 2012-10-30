package sensors;

import lejos.nxt.*;

public class LightSensor {

	public static void main(String[] args) {		
		lejos.nxt.LightSensor light = new lejos.nxt.LightSensor(SensorPort.S2);
		lejos.nxt.UltrasonicSensor ultra = new lejos.nxt.UltrasonicSensor(SensorPort.S1);
		Motor.A.setSpeed(180);
		Motor.B.setSpeed(180);
		while(true) {
			System.out.println(light.getLightValue());
			//System.out.println(ultra.getDistance());
			/*if(light.getLightValue() < 53) {
				Motor.A.forward();
				Motor.B.forward();
			}
			if(light.getLightValue() >= 53) {
				Motor.A.stop();
				Motor.B.stop();
			}*/
		}
	}
}

//wit: 55-56
//bord: 49-50
//zwart: <40