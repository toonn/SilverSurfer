package simulator;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.ImageIcon;

public class Wall extends Rectangle{

	private State state;
	/**
	 * dit is de x positie van de rechthoek in het midden van de breedte aan het
	 * linkeruiteinde van de rechthoek als de wall horizontaal ligt, en boven aan het uiteinde als 
	 * de wall verticaal staat.
	 */
	private float xPosition;
	/**
	 * dit is de y positie van de rechthoek in het midden van de breedte helemaal links
	 * aan het uiteinde als de wall horizontaal ligt, en boven aan het uiteinde als 
	 * de wall verticaal staat.
	 */
	private float yPosition;
	
	/**
	 * als ge deze standaardbreedte wilt veranderen moet je in 
	 * de methode hieronder
	 * de -1 in xposition-1 en yposition-1 ook aanpassen!
	 * ge hebt hier bevoorbeeld 3 pixels dus het midden is op pixel 2 en je moet
	 * om een rechthoek te initialiseren de linkerbovenhoek meegeven
	 * dus dat wordt -1 voor x en -1 voor y
	 * als je 5 hebt wordt dit -2 enzovoort
	 */
	private static final int standardWidth = 3;
	private static final int standardLength = 40;
	
	public Wall(State state, float xPosition, float yPosition){
		super(standardWidth, standardLength);
		this.state = state;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		float x;
		float y;
		if(state == State.HORIZONTAL){
			
			setSize(standardLength, standardWidth);
			
		x = xPosition;
		y = yPosition -1;
				}
		else{
			x = xPosition -1;
			y = yPosition;
		}
		setLocation((int) x, (int) y);
		
	}
	
	
	public State getState() {
		return state;
	}
	
	

}
