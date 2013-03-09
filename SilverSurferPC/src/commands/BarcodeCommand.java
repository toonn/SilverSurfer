package commands;

public class BarcodeCommand {

   /**
    * De waarde van de barcode waarbij een voorwerp moet worden opgepikt hangt af van de pilot.
    * Dit kan daarom niet hier beslist worden (ik zie toch niet meteen hoe, wie wel iets weet mag het veranderen)
    * De bedoeling is dat een barcode eerst geckecked wordt op alle andere opdrachten (bvb wip): zie PilotActions.executBarcode()
    * Als laatste optie wordt in die methode gechecked of de barcode misschien een voorwerp-barcode is, deze check gebeurt daar.
    */
	//public static final int PICKUP_OBJECT = 1;
    //public static final int PICKUP_OBJECT_INVERSE = 32;
	
	public static final int SEESAW = 15; // TODO: voorlopig nummer!
	public static final int SEESAW_INVERSE = 15; // TODO: voorlopig nummer!
}