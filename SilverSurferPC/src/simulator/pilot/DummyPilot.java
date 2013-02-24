package simulator.pilot;

public class DummyPilot extends AbstractPilot {

	@Override
	public int getNewLightSensorValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNewUltraSensorValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNewInfraSensorValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getConsoleTag() {
		return "[DUMMY]";
	}
}