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
    protected int getRotateSleepTime(double angle) {
        return speed / 10;
    }

    @Override
    protected int getTravelSleepTime(double distance) {
        return speed / ((int) Math.ceil(Math.abs(distance)));
    }
}
