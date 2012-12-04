package mazeAlgorithm;

import gui.SilverSurferGUI;

import java.util.ArrayList;
import java.util.Vector;

import commands.Command;
import communication.Communicator;
import mapping.ExtMath;
import mapping.Orientation;
import mapping.Tile;

public class MazeExplorer {
	
	/**
	 * Hierin komt elke tile die bezocht is, op het einde dus alle tiles van het doolhof
	 */
	private Vector<Tile> allTiles = new Vector<Tile>();
	/**
	 * hierin worden de tiles in de wachtrij gezet
	 */
	private Vector<Tile> queu = new Vector<Tile>();
	private Tile startTile = null;
	private SilverSurferGUI gui;
	
	private Tile checkTile = null;
	private Tile endTile = null;
	
	public MazeExplorer(SilverSurferGUI gui){
		this.gui = gui;
		startTile = gui.getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates((int)gui.getInformationBuffer().getXCoordinateRelative(), (int)gui.getInformationBuffer().getYCoordinateRelative());
	}
	
	/**
	 * Deze methode wordt opgeroepen als het object het algoritme moet uitvoeren
	 */
	public void startExploringMaze(){
		gui.getCommunicator().setTilesBeforeAllign(1);
		gui.getCommunicator().mustAllign(true);
		allTiles.add(startTile);
		algorithm(startTile);
		
		//TODO test shortestPath op't einde.
		if(getCheckTile() != null && getEndTile() != null){
			
			//Drive to checkpoint.
			gui.getSimulationPanel().clearPath();
			ShortestPath almostFinalPath = new ShortestPath(gui, gui.getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates((int)gui.getInformationBuffer().getXCoordinateRelative(), (int)gui.getInformationBuffer().getYCoordinateRelative()),getCheckTile(), allTiles);
			almostFinalPath.goShortestPath();
								
			//Drive to endpoint.
			gui.getSimulationPanel().clearPath();
			ShortestPath finalPath = new ShortestPath(gui, getCheckTile(), getEndTile(), allTiles);
			finalPath.goShortestPath();
		}
		for(Object tile: allTiles){
			((Tile) tile).setMarkingExploreMaze(false);
		}
		gui.getCommunicator().mustAllign(false);
		
	}
	
	/**
	 * Set the endtile that's the ending of the shortest-path algorithm.
	 */
	public void setEndTile(Tile endTile) {
		this.endTile = endTile;
	}
	
	/**
	 * Get the endtile that's the ending of the shortest-path algorithm.
	 */
	public Tile getEndTile() {
		return endTile;
	}
	
	/**
	 * Set the checktile that's the beginning of the shortest-path algorithm.
	 */
	public void setCheckTile(Tile checkTile) {
		this.checkTile = checkTile;
	}
	
	/**
	 * Get the checktile that's the beginning of the shortest-path algorithm.
	 */
	public Tile getCheckTile() {
		return checkTile;
	}
	
	/**
	 * Deze methode exploreert het doolhof
	 */
	private void algorithm(Tile currentTile){	
		
		//kijkt eerst of er muren zijn, deze methode zet ook al tiles waar er zowiezo liggen
		//(dus waar geen muur staat),
		//zodat deze al in de map zitten en de robot er naartoe kan gaan
		Orientation currentOrientation = gui.getCommunicator().getSimulationPilot().getCurrentOrientation();
		int number = currentOrientation.getNumberArray();
		int numberVariable = number;
		int[] whichTilesAllreadyBeen = new int[4];
		for(int i = 0; i < 4 ; i++){
			ArrayList array = currentTile.getAllNeighbours();
			if(array.get(numberVariable) != null && (((Tile) array.get(numberVariable)).isMarkedExploreMaze())){
				whichTilesAllreadyBeen[numberVariable] = 0;}
			else {whichTilesAllreadyBeen[numberVariable] = 1;}
			numberVariable = numberVariable + 1;
			if(numberVariable == 4){
				numberVariable = 0;
			}
		}
		
		numberVariable = number;
		
		for(int i = 0; i < 4 ; i++){
			currentOrientation = gui.getCommunicator().getSimulationPilot().getCurrentOrientation();
			if(whichTilesAllreadyBeen[numberVariable] == 0){}
			else{
				double angle = (numberVariable - currentOrientation.getNumberArray()) * 90;
				angle = ExtMath.getSmallestAngle(angle);
				gui.getCommunicator().sendCommand((int) (angle*10)*10 + Command.AUTOMATIC_TURN_ANGLE);
				gui.getCommunicator().sendCommand(Command.checkObstructionsAndSetTile);
			}
			numberVariable = numberVariable + 1;
			if(numberVariable == 4){
				numberVariable = 0;
			}
		}
		
		//zet het mark-veld van de currentTile op true zodat deze niet meer opnieuw in de queu
				//terecht kan komen
				currentTile.setMarkingExploreMaze(true);
				
	
		//voegt buurtiles van de currentTile toe aan de queu, enkel als deze nog niet begaan
		//zijn (niet gemarkeerd) 
		for(Object neighbourTile: currentTile.getReachableNeighbours()){
			if(neighbourTile != null && !(((Tile) neighbourTile).isMarkedExploreMaze())){
			queu.add((Tile) neighbourTile);
			int i = 0;
			for(Object neighbour: ((Tile) neighbourTile).getAllNeighbours()){
				if(neighbour != null && (((Tile) neighbour).isMarkedExploreMaze())){
					i++;}
			}
			if(i == 4){
				((Tile) neighbourTile).setMarkingExploreMaze(true);
				allTiles.add((Tile) neighbourTile);
				while(queu.contains(neighbourTile)){
					queu.remove(neighbourTile);
				}
			}
			
			}
			
		}
		
		//returnt als er geen tiles meer in de wachtrij zitten (algoritme is afgelopen)
		if(queu.isEmpty()){
			return;
		}
		Tile nextTile;
		currentOrientation = gui.getCommunicator().getSimulationPilot().getCurrentOrientation();
		
		if(currentTile.getEdge(currentOrientation).isPassable() && currentTile.getEdge(currentOrientation).getNeighbour(currentTile) != null && 
				 queu.contains(currentTile.getEdge(currentOrientation).getNeighbour(currentTile))){
			nextTile = currentTile.getEdge(currentOrientation).getNeighbour(currentTile);
		}
		else if(currentTile.getEdge(currentOrientation.getOtherOrientationCorner()).isPassable() && currentTile.getEdge(currentOrientation.getOtherOrientationCorner()).getNeighbour(currentTile) != null && queu.contains(currentTile.getEdge(currentOrientation.getOtherOrientationCorner()).getNeighbour(currentTile))){
			nextTile = currentTile.getEdge(currentOrientation.getOtherOrientationCorner()).getNeighbour(currentTile);}
		else if(currentTile.getEdge(currentOrientation.getOtherOrientationCorner().getOppositeOrientation()).isPassable() && currentTile.getEdge(currentOrientation.getOtherOrientationCorner().getOppositeOrientation()).getNeighbour(currentTile) != null
				 && queu.contains(currentTile.getEdge(currentOrientation.getOtherOrientationCorner().getOppositeOrientation()).getNeighbour(currentTile))){
			nextTile = currentTile.getEdge(currentOrientation.getOtherOrientationCorner().getOppositeOrientation()).getNeighbour(currentTile);
		}
		else if(currentTile.getEdge(currentOrientation.getOppositeOrientation()).isPassable() && currentTile.getEdge(currentOrientation.getOppositeOrientation()).getNeighbour(currentTile) != null && queu.contains(currentTile.getEdge(currentOrientation.getOppositeOrientation()).getNeighbour(currentTile))){
			nextTile = currentTile.getEdge(currentOrientation.getOppositeOrientation()).getNeighbour(currentTile);
		}
		else{
			nextTile = queu.lastElement();
		}
			allTiles.add(nextTile);
		//verwijdert alle nextTiles uit de queu. De reden waarom deze meermaals in de queu 
		//voorkomen is omdat het voordeliger is om de laatste versie te pakken omdat deze
		//het dichts bij de currentTile ligt, zodat de robot niet voor elke nextTile massa's
		//omweg moet doen
		while(queu.contains(nextTile)){
		queu.remove(nextTile);}
		//voert een shortestPath uit om van currentTile naar startTile te gaan.
		ShortestPath shortestPath = new ShortestPath(gui, currentTile, nextTile, allTiles);
		shortestPath.goShortestPath();
		//voert methode opnieuw uit met nextTile
		algorithm(nextTile);
		
	}
	
}

