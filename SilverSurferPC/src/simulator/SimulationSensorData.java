/**
 * 
 */
package simulator;

/**
 * @author Nele
 *
 */
public class SimulationSensorData
{
	// whether there the light is bright or quite dark. influences the light sensor
	private static boolean isBrightLight = true;
	private static boolean isDriving = false;

	private static boolean isBrightLight()
	{
		return SimulationSensorData.isBrightLight;
	}

	private static void setBrightLight(boolean isBrightLight) 
	{
		SimulationSensorData.isBrightLight = isBrightLight;
	}
	
	public static boolean isDriving()
	{
		return isDriving;
	}

	public static void setDriving(boolean isDriving)
	{
		SimulationSensorData.isDriving = isDriving;
	}

	/**
	 * Mean value of the light sensor, when standing a white line under given circumstances.
	 */
	public static final double getMWhiteLineLS()
	{
		if(isBrightLight())
		{
			if(isDriving())
			{
				return 55.02606;
			}
			else
			{
				return 54.99377;
			}
		}
		else
		{
			if(isDriving())
			{
				return 54.98442;
			}
			else
			{
				return 55;
			}
		}
	}
	
	/**
	 * Standard Deviation of the light sensor, when standing a white line under given circumstances.
	 */
	public static final double getSDWhiteLineLS()
	{
		if(isBrightLight())
		{
			if(isDriving())
			{
				return 0.966416;
			}
			else
			{
				return 0.111629;
			}
		}
		else
		{
			if(isDriving())
			{
				return 1.1218249;
			}
			else
			{
				return 0;
			}
		}
	}
	
	/**
	 * Mean value of the light sensor, when standing on an empty panel under given circumstances.
	 */
	public static final double getMEmptyPanelLS()
	{
		if(isBrightLight())
		{
			if(isDriving())
			{
				return 49.3863;
			}
			else
			{
				return 49.99688474;
			}
		}
		else
		{
			if(isDriving())
			{
				return 49.46829;
			}
			else
			{
				return 49;
			}
		}
	}
	
	/**
	 * Standard Deviation of the light sensor, when standing on an empty panel under given circumstances.
	 */
	public static final double getSDEmptyPanelLS()
	{
		if(isBrightLight())
		{
			if(isDriving())
			{
				return 0.6425;
			}
			else
			{
				return 0.055814557;
			}
		}
		else
		{
			if(isDriving())
			{
				return 1.218249;
			}
			else
			{
				return 0;
			}
		}
	}
	
	/**
	 * Standard Deviation of the light sensor, when standing on a panel containing a barcode under given circumstances.
	 */
	public static final double getMBarcodeTileLS(int color)
	{
		// black
		if(color == 0)
		{
			if(isBrightLight())
			{
				if(isDriving())
				{
					// to be implemented!
					return 0;
				}
				else
				{
					// to be implemented!
					return 0;
				}
			}
			else
			{
				if(isDriving())
				{
					// to be implemented!
					return 0;
				}
				else
				{
					// to be implemented!
					return 0;
				}
			}
		}
		// white
		else if(color == 1)
		{
			return SimulationSensorData.getMWhiteLineLS();
		}
		// not on the code itself, but on the brown panel next to it
		else
		{
			return SimulationSensorData.getMEmptyPanelLS();
		}
	}
	
	/**
	 * Standard Deviation of the light sensor, when standing on a panel containing a barcode under given circumstances.
	 * The color should be 0 when standing on a black part, 1 when standing on a white part or something else when standing next to the panel.
	 */
	public static final double getSDBarcodeTileLS(int color)
	{
		// black
		if(color == 0)
		{
			if(isBrightLight())
			{
				if(isDriving())
				{
					// to be implemented!
					return 0;
				}
				else
				{
					// to be implemented!
					return 0;
				}
			}
			else
			{
				if(isDriving())
				{
					// to be implemented!
					return 0;
				}
				else
				{
					// to be implemented!
					return 0;
				}
			}
		}
		// white
		else if(color == 1)
		{
			return SimulationSensorData.getSDWhiteLineLS();
		}
		// not on the code itself, but on the brown panel next to it
		else
		{
			return SimulationSensorData.getSDEmptyPanelLS();
		}
	}

	/**
	 * Standard Deviation of the ultrasonic sensor under given circumstances.
	 */
	public static final double getSDUS()
	{
		// to be implemented!
		return 0;
	}
}
