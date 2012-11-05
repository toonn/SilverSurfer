package statemachine;

import communication.CommandUnit;

import lejos.nxt.*;

public class Automatic extends State {

    public Automatic() {
        Motor.A.setSpeed(CommandUnit.NORMAL_SPEED);
        Motor.B.setSpeed(CommandUnit.NORMAL_SPEED);
        Motor.A.stop(true);
        Motor.B.stop();
    }

    public void moveForward(int angle) {
        Motor.A.rotate(angle, true);
        Motor.B.rotate(angle);
    }

    public void turnAngle(int angle) {
        Motor.A.rotate(-angle, true);
        Motor.B.rotate(angle);
    }

    /**
     * Move the robot forward untill it has passed a white line.
     */
    public void forwardToWhiteLine(LightSensor lightSensor) {
        boolean passedWhiteLine = false;
        int forwardStepAngle = 5;
        int passWhiteAngle = 36;
        int oldLightValue;
        int newLightValue = lightSensor.getLightValue();
        int threshold = 10; // Verschil tussen bruin en wit.

        while (!passedWhiteLine) {
            oldLightValue = newLightValue;
            newLightValue = lightSensor.getLightValue();

            if (oldLightValue - newLightValue > threshold) {
                moveForward(passWhiteAngle);
                passedWhiteLine = true;
            } else {
                moveForward(forwardStepAngle);
            }
        }
    }

    /**
     * The robot will set itself perpendicular to a white line (not on a
     * barcode!) This method assumes that the robot is near a white line (it can
     * reach it just by turning around its axis).
     */
    public void whiteLinePerpendicular(LightSensor lightSensor) {
        boolean alignedPerpendicular = false;
        boolean reverse = false;
        int wheelAngle = 5;
        int angleTurned = 0;
        int oldLightValue;
        int newLightValue = lightSensor.getLightValue();
        int threshold = 10; // Verschil tussen bruin en wit.

        while (!alignedPerpendicular) {
            oldLightValue = newLightValue;
            newLightValue = lightSensor.getLightValue();

            if (newLightValue - oldLightValue > threshold) {
                if (reverse) {
                    turnAngle(angleTurned / 2);
                    alignedPerpendicular = true;
                } else {
                    reverse = true;
                }
            } else {
                if (reverse) {
                    turnAngle(-wheelAngle);
                    angleTurned += wheelAngle;
                } else {
                    turnAngle(wheelAngle);
                }
            }
        }
        // // turn around your axis till you are on a line
        // while(robot.getUnderground() != "WHITE")
        // {
        // robot.turn(1);
        // }
        //
        // // turn further around your axis till you find the other side of the
        // line
        // robot.turn(-5);
        // int degreesTurnedNeg = 5;
        // while(robot.getUnderground() != "WHITE")
        // {
        // robot.turn(-1);
        // degreesTurnedNeg++;
        // }
        //
        // robot.turn(degreesTurnedNeg/2);
    }
}