package simulator;

import java.awt.Rectangle;

public class Wall extends Rectangle {

    private State state;
    /**
     * dit is de x positie van de rechthoek in het midden van de breedte aan het
     * linkeruiteinde van de rechthoek als de wall horizontaal ligt, en boven
     * aan het uiteinde als de wall verticaal staat.
     */
    private double xPosition;
    /**
     * dit is de y positie van de rechthoek in het midden van de breedte
     * helemaal links aan het uiteinde als de wall horizontaal ligt, en boven
     * aan het uiteinde als de wall verticaal staat.
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

            x = xPosition;
            y = yPosition - (standardWidth * scalingfactor)/2;
        } else {
        	setSize((int) (standardWidth * scalingfactor), (int) (standardLength * scalingfactor));

        	
            x = xPosition - (standardWidth * scalingfactor)/2;
            y = yPosition;
        }
        setLocation((int) x, (int) y);

    }

    public State getState() {
        return state;
    }

}
