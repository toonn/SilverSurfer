package communication;

import simulator.SimulationPilot;
import mapping.Barcode;
import gui.SilverSurferGUI;

public class StatusInfoBuffer {

    private int lightSensorInfo;
    private int ultraSensorInfo;
    private boolean leftMotorMoving;
    private boolean rightMotorMoving;
    private int leftMotorSpeed;
    private int rightMotorSpeed;
    private boolean busy;
    private int barcode;
    private double angle;
    private double[] coordinatesAbsolute = new double[2];
    private int xCoordinateRelative;
    private int yCoordinateRelative;
    private double startPositionAbsoluteX;
    private double startPositionAbsoluteY;
    private int startPositionRelativeX;
    private int startPostitionRelativeY;

    private SimulationPilot pilot;
    private boolean isBufferUsed = false; // Check if the buffer can be updated
                                          // (can be updated when false).
    private final int BUFFER_SIZE = 300; // Amount of datapoints kept by this
                                         // buffer.

    private LSInfoNode startLSInfo = new LSInfoNode();
    private LSInfoNode endLSInfo = new LSInfoNode();
    private USInfoNode startUSInfo = new USInfoNode();
    private USInfoNode endUSInfo = new USInfoNode();

    private int amtLSUpdated = 0;
    private int amtUSUpdated = 0;

    /**
     * Used to make a linked-list stack of LS-information.
     */
    public class LSInfoNode {
        public int info;
        public LSInfoNode next;
    }

    /**
     * Used to make a linked-list stack of US-information.
     */
    public class USInfoNode {
        public int info;
        public USInfoNode next;
    }

    /**
     * Set a claim on the buffer so that information in it can be used to
     * calculate. Buffer won't update as long as claim has been withdraw.
     */
    public void claimBuffer() {
        isBufferUsed = true;

    }

    /**
     * Makes sure the buffer can be updated again.
     */
    public void freeBuffer() {
        isBufferUsed = false;

    }

    /**
     * Returns the latest info available for the Light Sensor
     */
    public int getLatestLightSensorInfo() {
        return lightSensorInfo;
    }

    /**
     * Add new info for the light Sensor.
     */
    public void addLightSensorInfo(int lightSensorInfo) {
        this.lightSensorInfo = lightSensorInfo;
        // SSG.updateStatus();
        if (!isBufferUsed) {
            // Voeg toe aan buffer.
            if (amtLSUpdated < BUFFER_SIZE) {
                if (amtLSUpdated != 0) {
                    LSInfoNode temp = new LSInfoNode();
                    temp.info = lightSensorInfo;
                    temp.next = null;
                    endLSInfo.next = temp;
                    endLSInfo = temp;
                } else {
                    startLSInfo = new LSInfoNode();
                    startLSInfo.info = lightSensorInfo;
                    startLSInfo.next = null;
                    endLSInfo = startLSInfo;
                }
            }
            // Vewijder eerste element en voeg ��n toe.
            else {
                startLSInfo = startLSInfo.next;
                LSInfoNode temp = new LSInfoNode();
                temp.info = lightSensorInfo;
                temp.next = null;
                endLSInfo.next = temp;
                endLSInfo = temp;
            }
            amtLSUpdated++;

        }
    }

    /**
     * Returns the latest info available for the Ultrasonic Sensor
     */
    public int getLatestUltraSensorInfo() {
        return ultraSensorInfo;
    }

    /**
     * Add new info for the Ulstrasonic Sensor.
     */
    public void addUltraSensorInfo(int ultraSensorInfo) {
        this.ultraSensorInfo = ultraSensorInfo;

        if (!isBufferUsed) {
            // Voeg toe aan buffer.
            if (amtUSUpdated < BUFFER_SIZE) {
                if (amtUSUpdated != 0) {
                    USInfoNode temp = new USInfoNode();
                    temp.info = ultraSensorInfo;
                    temp.next = null;
                    endUSInfo.next = temp;
                    endUSInfo = temp;
                } else {
                    startUSInfo = new USInfoNode();
                    startUSInfo.info = ultraSensorInfo;
                    startUSInfo.next = null;
                    endUSInfo = startUSInfo;
                }
            }
            // Vewijder eerste element en voeg ��n toe.
            else {
                startUSInfo = startUSInfo.next;
                USInfoNode temp = new USInfoNode();
                temp.info = ultraSensorInfo;
                temp.next = null;
                endUSInfo.next = temp;
                endUSInfo = temp;
            }
            amtUSUpdated++;
        }
        // SSG.updateStatus();

    }

    public boolean getLeftMotorMoving() {
        return leftMotorMoving;
    }

    public void setLeftMotorMoving(boolean leftMotorMoving) {
        this.leftMotorMoving = leftMotorMoving;
        // SSG.updateStatus();
    }

    public boolean getRightMotorMoving() {
        return rightMotorMoving;
    }

    public void setRightMotorMoving(boolean rightMotorMoving) {
        this.rightMotorMoving = rightMotorMoving;
        // SSG.updateStatus();
    }

    public int getLeftMotorSpeed() {
        return leftMotorSpeed;
    }

    public void setLeftMotorSpeed(int leftMotorSpeed) {
        this.leftMotorSpeed = leftMotorSpeed;
        // SSG.updateStatus();
    }

    public int getRightMotorSpeed() {
        return rightMotorSpeed;
    }

    public void setRightMotorSpeed(int rightMotorSpeed) {
        this.rightMotorSpeed = rightMotorSpeed;
        // SSG.updateStatus();
    }

    /**
     * Get the head of the LS-infostack.
     */
    public LSInfoNode getStartLSInfo() {
        return startLSInfo;
    }

    /**
     * Get the head of the US-infostack.
     */
    public USInfoNode getStartUSInfo() {
        return startUSInfo;
    }

    public boolean getBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
        // SSG.updateStatus();
    }

    public int getBarcode() {
        return barcode;
    }

    public void setBarcode(int barcode) {
        pilot.setBarcode(barcode);

        this.barcode = barcode;
        SSG.getCommunicator().setExecutingBarcodes(true);
        BarcodeExecuterThread BET = new BarcodeExecuterThread("BET", SSG,
                this.barcode);
        BET.start();
    }

    //
    // public double getAngle() {
    // return angle;
    // }
    //
    // public void setAngle(double angle) {
    // this.angle = angle;
    // // SSG.updateStatus();
    // }
    //
    // public void setStartPositionAbsoluteX(double startPositionAbsoluteX) {
    // this.startPositionAbsoluteX = startPositionAbsoluteX;
    // }
    //
    // public void setStartPositionAbsoluteY(double startPositionAbsoluteY) {
    // this.startPositionAbsoluteY = startPositionAbsoluteY;
    // }
    //
    // public void setStartPositionRelativeX(int startPositionRelativeX) {
    // this.startPositionRelativeX = startPositionRelativeX;
    // }
    //
    // public void setStartPositionRelativeY(int startPositionAbsoluteY) {
    // this.startPositionAbsoluteY = startPositionAbsoluteY;
    // }
    //
    // public int getXCoordinateRelative() {
    // return xCoordinateRelative;
    // }
    //
    // public void setXCoordinateRelative(int x) {
    // xCoordinateRelative = x;
    // }
    //
    // public int getYCoordinateRelative() {
    // return yCoordinateRelative;
    // }
    //
    // public void setYCoordinateRelative(int y) {
    // yCoordinateRelative = y;
    // }
    //
    // public double[] getCoordinatesAbsolute() {
    // return coordinatesAbsolute;
    // }
    //
    // public void setCoordinatesAbsolute(double[] coordinates) {
    // coordinatesAbsolute[0] = coordinates[0];
    // coordinatesAbsolute[1] = coordinates[1];
    // SSG.getSimulationPanel().moveRobot(coordinates[0], coordinates[1],
    // getAngle());
    //
    // // SSG.updateStatus();
    // }
    //
    // private void setCurrentTileCoordinatesRelative(double xOld, double yOld)
    // {
    // int[] relativePosition = setAbsoluteToRelative(xOld, yOld);
    // setXCoordinateRelative(relativePosition[0]);
    // setYCoordinateRelative(relativePosition[1]);
    // }
    //
    // /**
    // * Deze methode zet de coordinaten van het echte systeem om in de
    // * coordinaten van de matrix
    // */
    // private int[] setAbsoluteToRelative(double x, double y) {
    // double a = x - setToMultipleOf40(startPositionAbsoluteX);
    // double b = y - setToMultipleOf40(startPositionAbsoluteY);
    // int c;
    // int d;
    // c = (int) Math.floor(a / 40);
    // d = (int) Math.floor(b / 40);
    //
    // int[] array = new int[2];
    // array[0] = getStartPositionRelativeX() + c;
    // array[1] = getStartPositionRelativeX() + d;
    // return array;
    // }
    //
    // private int getStartPositionRelativeX() {
    // return startPositionRelativeX;
    // }
    //
    // private int setToMultipleOf40(double a) {
    // return (int) (Math.floor(a / 40) * 40);
    // }

    public void resetBuffer() {
        xCoordinateRelative = 0;
        yCoordinateRelative = 0;
        angle = 0;
    }
}
