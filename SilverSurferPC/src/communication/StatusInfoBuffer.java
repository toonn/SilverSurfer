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

    public void addInfraRedSensorInfo(int infraSensorInfo) {
        this.infraSensorInfo = infraSensorInfo;
    }

    public void addLightSensorInfo(int lightSensorInfo) {
        this.lightSensorInfo = lightSensorInfo;
    }

    public void addUltraSensorInfo(int ultraSensorInfo) {
        this.ultraSensorInfo = ultraSensorInfo;
    }

    // BARCODE
    public int getBarcode() {
        return barcode;
    }

    // EXTRAULTRASONICSENSOR
    public double getExtraUltrasonicSensorValue() {
        return extraUltraSensorInfo;
    }

    // INFRAREDSENSOR
    public int getLatestInfraRedSensorInfo() {
        return infraSensorInfo;
    }

    // LIGHT SENSOR
    public int getLatestLightSensorInfo() {
        return lightSensorInfo;
    }

    // ULTRASONIC SENSOR
    public int getLatestUltraSensorInfo() {
        return ultraSensorInfo;
    }

    // LEFT MOTOR
    public boolean getLeftMotorMoving() {
        return leftMotorMoving;
    }

    public int getLeftMotorSpeed() {
        return leftMotorSpeed;
    }

    // RIGHT MOTOR
    public boolean getRightMotorMoving() {
        return rightMotorMoving;
    }

    public int getRightMotorSpeed() {
        return rightMotorSpeed;
    }

    public void robotDone() {
        pilot.robotDone();
    }

    // ANGLE
    public void setAngle(final double angle) {
        pilot.setAngle(angle);
    }

    public void setBarcode(int barcode) {
        this.barcode = barcode;
        pilot.setBusyExecutingBarcode(true);
    }

    public void setExtraUltrasonicSensorValue(int extraUltrasonicSensorValue) {
        extraUltraSensorInfo = extraUltrasonicSensorValue;
    }

    public void setLeftMotorMoving(final boolean leftMotorMoving) {
        this.leftMotorMoving = leftMotorMoving;
    }

    public void setLeftMotorSpeed(final int leftMotorSpeed) {
        this.leftMotorSpeed = leftMotorSpeed;
    }

    // POSITION
    public void setPosition(final double[] coordinates) {
        pilot.setPosition(coordinates[0], coordinates[1]);
    }

    public void setRightMotorMoving(final boolean rightMotorMoving) {
        this.rightMotorMoving = rightMotorMoving;
    }

    public void setRightMotorSpeed(final int rightMotorSpeed) {
        this.rightMotorSpeed = rightMotorSpeed;
    }
}

/*
 * private final int BUFFER_SIZE = 300; private boolean isBufferUsed = false;
 * public class LSInfoNode { public int info; public LSInfoNode next; } private
 * LSInfoNode startLSInfo = new LSInfoNode(); private LSInfoNode endLSInfo = new
 * LSInfoNode(); private int amtLSUpdated = 0;
 * 
 * public class USInfoNode { public int info; public USInfoNode next; } private
 * USInfoNode startUSInfo = new USInfoNode(); private USInfoNode endUSInfo = new
 * USInfoNode(); private int amtUSUpdated = 0;
 * 
 * public void claimBuffer() { isBufferUsed = true;
 * 
 * }
 * 
 * public void freeBuffer() { isBufferUsed = false;
 * 
 * }
 * 
 * 
 * 
 * /*if (!isBufferUsed) { if (amtLSUpdated < BUFFER_SIZE) { if (amtLSUpdated !=
 * 0) { final LSInfoNode temp = new LSInfoNode(); temp.info = lightSensorInfo;
 * temp.next = null; endLSInfo.next = temp; endLSInfo = temp; } else {
 * startLSInfo = new LSInfoNode(); startLSInfo.info = lightSensorInfo;
 * startLSInfo.next = null; endLSInfo = startLSInfo; } } else { startLSInfo =
 * startLSInfo.next; final LSInfoNode temp = new LSInfoNode(); temp.info =
 * lightSensorInfo; temp.next = null; endLSInfo.next = temp; endLSInfo = temp; }
 * amtLSUpdated++; }
 */

/*
 * if (!isBufferUsed) { if (amtUSUpdated < BUFFER_SIZE) { if (amtUSUpdated != 0)
 * { final USInfoNode temp = new USInfoNode(); temp.info = ultraSensorInfo;
 * temp.next = null; endUSInfo.next = temp; endUSInfo = temp; } else {
 * startUSInfo = new USInfoNode(); startUSInfo.info = ultraSensorInfo;
 * startUSInfo.next = null; endUSInfo = startUSInfo; } } else { startUSInfo =
 * startUSInfo.next; final USInfoNode temp = new USInfoNode(); temp.info =
 * ultraSensorInfo; temp.next = null; endUSInfo.next = temp; endUSInfo = temp; }
 * amtUSUpdated++; }
 */