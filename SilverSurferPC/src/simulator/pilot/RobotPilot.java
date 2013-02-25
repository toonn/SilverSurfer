package simulator.pilot;

import communication.StatusInfoBuffer;

public class RobotPilot extends AbstractPilot {
    private StatusInfoBuffer statusInfoBuffer;

    @Override
    public int getLightSensorValue() {
        return statusInfoBuffer.getLatestLightSensorInfo();
    }

    @Override
    public int getUltraSensorValue() {
        return statusInfoBuffer.getLatestUltraSensorInfo();
    }

    @Override
    protected int getRotateSleepTime() {
        return speed / 10;
    }
}
