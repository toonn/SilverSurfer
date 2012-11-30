package simulator;

import java.awt.Rectangle;

import mapping.Orientation;

public class Wall extends Rectangle {

    private State state;
    /**
     * dit is de x positie van de linkerbovenhoek van de rechthoek
     */
    private double xPosition;
    /**
     * dit is de y positie van de linkerbovenhoek van de rechthoek
     */
    private double yPosition;

    /**
     * als ge deze standaardbreedte wilt veranderen moet je in de methode
     * hieronder de -1 in xposition-1 en yposition-1 ook aanpassen! ge hebt hier
     * bevoorbeeld 3 pixels dus het midden is op pixel 2 en je moet om een
     * rechthoek te initialiseren de linkerbovenhoek meegeven dus dat wordt -1
     * voor x en -1 voor y als je 5 hebt wordt dit -2 enzovoort
     */
    private static int standardWidth = 3;
    private static int standardLength = 40;

    public Wall(State state, double xPosition, double yPosition, double scalingfactor) {
        super(standardWidth, standardLength);
        this.state = state;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        double x;
        double y;
        if (state == State.HORIZONTAL) {
            setSize((int) (standardLength * scalingfactor), (int) (standardWidth * scalingfactor));
        } else {
        	setSize((int) (standardWidth * scalingfactor), (int) (standardLength * scalingfactor));
        }
        setLocation((int) xPosition, (int) yPosition);

    }

    public State getState() {
        return state;
    }
    
    public static int getStandardWidth(){
    	return standardWidth;
    }
}
