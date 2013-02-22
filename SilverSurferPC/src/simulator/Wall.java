package simulator;

import java.awt.Rectangle;

public class Wall extends Rectangle {

    private final State state;
    /**
     * als ge deze standaardbreedte wilt veranderen moet je in de methode
     * hieronder de -1 in xposition-1 en yposition-1 ook aanpassen! ge hebt hier
     * bevoorbeeld 3 pixels dus het midden is op pixel 2 en je moet om een
     * rechthoek te initialiseren de linkerbovenhoek meegeven dus dat wordt -1
     * voor x en -1 voor y als je 5 hebt wordt dit -2 enzovoort
     */
    private static int standardWidth = 3;
    private static int standardLength = 40;

    public static int getStandardWidth() {
        return standardWidth;
    }

    public Wall(final State state, final double xPosition,
            final double yPosition, final double scalingfactor) {
        super(standardWidth, standardLength);
        this.state = state;
        if (state == State.HORIZONTAL) {
            setSize((int) (standardLength * scalingfactor),
                    (int) (standardWidth * scalingfactor));
        } else {
            setSize((int) (standardWidth * scalingfactor),
                    (int) (standardLength * scalingfactor));
        }
        setLocation((int) xPosition, (int) yPosition);
    }

    public Wall(final State state, final double newXCoordinate,
            final double newYCoordinate, final int newWidth, final int newHeight) {
        setLocation((int) newXCoordinate, (int) newYCoordinate);
        setSize(newWidth, newHeight);
        this.state = state;
    }

    public State getState() {
        return state;
    }
}
