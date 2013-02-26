package gui;

import communication.Communicator;

//Thread to move and turn real-time, only needed for manual testing with the arrow-buttons (forward, left, right).
public class MoveTurnThread extends Thread {

    private final Communicator communicator;
    private final int length;
    private final int angles;
    private final int amtOfAngles;
    private final int command;

    public MoveTurnThread(final String str, final Communicator communicator,
            final int length, final int angles, final int amtOfAngles,
            final int command) {
        super(str);
        this.communicator = communicator;
        this.length = length;
        this.angles = angles;
        this.amtOfAngles = amtOfAngles;
        this.command = command;
    }

    @Override
    public void run() {
        if (length == 0 && angles == 0 && amtOfAngles == 0) {
            communicator.sendCommand(command);
        } else {
            communicator.moveTurn(length, angles, amtOfAngles);
        }
    }
}