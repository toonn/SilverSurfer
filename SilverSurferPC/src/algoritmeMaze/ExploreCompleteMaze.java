package algoritmeMaze;

public class ExploreCompleteMaze {

}
 


/**
 * Hallo dit is the pseudocode van de completness van de maze:
 * 
 * Vakje houdt bij of het niet, wel of twee gemarkeerd (boolean) is en de coordinaten.
 * Gebruik van Tile in maping en Enumeratie voor marking (noemt Marking in package mapping).
 * 
 * 
 * robot start:
 * 			checked de kinderen (muren), die hou je bij in een lijst.
 * 			en check ook of ze gemarkeerd zijn. 
 * 			ga naar het laatste kind in de lijst
 *         	en markeer je de tegel.
 * 			terug
 * als de lijst leeg is, is het algoritme op zen einde. Nu komt het algoritme naar het korste pad
 * naar de barcode met waarde "finish"
 * 
 * en ook nog checken of de barcode "finish" gevonden is. Stel dat die barcode nog niet gevonden is 
 * alles wissen. (marking op false en de lijst wissen met kinderen en de tiles in zen geheugen)
 *  
 *  bij resultaat een geluidje geven om te zeggen dat het aan het kortste pad algoritme begint.
 */
