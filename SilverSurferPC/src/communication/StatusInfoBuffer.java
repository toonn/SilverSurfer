package communication;

import simulator.pilot.RobotPilot;

public class StatusInfoBuffer {

    private RobotPilot pilot;
    private int lightSensorInfo;
    private int ultraSensorInfo;
    private int extraUltraSensorInfo;
    private int infraSensorInfo;
    private boolean leftMotorMoving;
    private int leftMotorSpeed;
    private boolean rightMotorMoving;
    private int rightMotorSpeed;
    private int barcode;

    public StatusInfoBuffer(RobotPilot pilot) {
        this.pilot = pilot;
    }

    public void robotDone() {
        pilot.robotDone();
    }

    // POSITION
    public void setPosition(final double[] coordinates) {
        pilot.setPosition(coordinates[0], coordinates[1]);
    }

    // ANGLE
    public void setAngle(final double angle) {
        pilot.setAngle(angle);
    }

    // INFRAREDSENSOR
    public int getLatestInfraRedSensorInfo() {
        return infraSensorInfo;
    }

    public void addInfraRedSensorInfo(int infraSensorInfo) {
        this.infraSensorInfo = infraSensorInfo;
    }

    // LIGHT SENSOR
    public int getLatestLightSensorInfo() {
        return lightSensorInfo;
    }

    public void addLightSensorInfo(int lightSensorInfo) {
        this.lightSensorInfo = lightSensorInfo;
    }

    // ULTRASONIC SENSOR
    public int getLatestUltraSensorInfo() {
        return ultraSensorInfo;
    }

    public void addUltraSensorInfo(int ultraSensorInfo) {
        this.ultraSensorInfo = ultraSensorInfo;
    }

    // EXTRAULTRASONICSENSOR
    public double getExtraUltrasonicSensorValue() {
        return extraUltraSensorInfo;
    }

    public void setExtraUltrasonicSensorValue(int extraUltrasonicSensorValue) {
        extraUltraSensorInfo = extraUltrasonicSensorValue;
    }

    // LEFT MOTOR
    public boolean getLeftMotorMoving() {
        return leftMotorMoving;
    }

    public void setLeftMotorMoving(final boolean leftMotorMoving) {
        this.leftMotorMoving = leftMotorMoving;
    }

    public int getLeftMotorSpeed() {
        return leftMotorSpeed;
    }

    public void setLeftMotorSpeed(final int leftMotorSpeed) {
        this.leftMotorSpeed = leftMotorSpeed;
    }

    // RIGHT MOTOR
    public boolean getRightMotorMoving() {
        return rightMotorMoving;
    }

    public void setRightMotorMoving(final boolean rightMotorMoving) {
        this.rightMotorMoving = rightMotorMoving;
    }

    public int getRightMotorSpeed() {
        return rightMotorSpeed;
    }

    public void setRightMotorSpeed(final int rightMotorSpeed) {
        this.rightMotorSpeed = rightMotorSpeed;
    }

    // BARCODE
    public int getBarcode() {
        return barcode;
    }

    public void setBarcode(int barcode) {
        this.barcode = barcode;
        pilot.setBusyExecutingBarcode(true);
    }
}