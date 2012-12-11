package mazeAlgorithm;

import gui.SilverSurferGUI;

import java.util.ArrayList;
import java.util.Vector;

import commands.Command;
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
	private Vector<Tile> queue = new Vector<Tile>();
	private Tile startTile = null;
	private SilverSurferGUI gui;
	
	private Tile checkTile = null;
	private Tile endTile = null;
	private boolean checkTileFound = false;
	private boolean endTileFound = false;
	
	public MazeExplorer(SilverSurferGUI gui){
		this.gui = gui;
		startTile = gui.getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates((int)SilverSurferGUI.getInformationBuffer().getXCoordinateRelative(), (int)SilverSurferGUI.getInformationBuffer().getYCoordinateRelative());
	}
	
	/**
	 * Deze methode wordt opgeroepen als het object het algoritme moet uitvoeren
	 */
	public void startExploringMaze(){
		gui.getCommunicator().setTilesBeforeAllign(3);
		gui.getCommunicator().mustAllign(true);
		allTiles.add(startTile);
		algorithm(startTile);
		//TODO test shortestPath op't einde.
		if(getCheckTile() != null && getEndTile() != null) {
			gui.getCommunicator().sendCommand(Command.PERMA_STOP_READING_BARCODES);
			SilverSurferGUI.changeSpeed(3);
			//Drive to checkpoint.
			gui.getSimulationPanel().clearPath();
			ShortestPath almostFinalPath = new ShortestPath(gui, gui.getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates((int)SilverSurferGUI.getInformationBuffer().getXCoordinateRelative(), (int)SilverSurferGUI.getInformationBuffer().getYCoordinateRelative()) ,getCheckTile(), allTiles);
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
	
	public void setCheckTileFound(boolean check) {
		checkTileFound = check;
	}
	
	public void setEndTileFound(boolean check) {
		endTileFound = check;
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
			ArrayList<Tile> array = currentTile.getAllNeighbours();
			if(array.get(numberVariable) != null && (((Tile) array.get(numberVariable)).isMarkedExploreMaze()))
				whichTilesAllreadyBeen[numberVariable] = 0;
			else
				whichTilesAllreadyBeen[numberVariable] = 1;
			numberVariable = numberVariable + 1;
			if(numberVariable == 4)
				numberVariable = 0;
		}
		
		numberVariable = number;
		
		//update robot tilecoordinates
		if(gui.getCommunicator().getRobotConnected())
			gui.getCommunicator().getSimulationPilot().setCurrentTileCoordinatesRobot(gui.getCommunicator().getSimulationPilot().getCurrentPositionAbsoluteX(), gui.getCommunicator().getSimulationPilot().getCurrentPositionAbsoluteY());
		
		for(int i = 0; i < 4 ; i++){
			currentOrientation = gui.getCommunicator().getSimulationPilot().getCurrentOrientation();
			if(whichTilesAllreadyBeen[numberVariable] != 0) {
				double angle = (((numberVariable - currentOrientation.getNumberArray()) * 90)+360)%360;
				angle = ExtMath.getSmallestAngle(angle);
				gui.getCommunicator().sendCommand((int) (angle*10)*10 + Command.AUTOMATIC_TURN_ANGLE);
				gui.getCommunicator().sendCommand(Command.CHECK_OBSTRUCTIONS_AND_SET_TILE);
			}
			numberVariable = numberVariable + 1;
			if(numberVariable == 4)
				numberVariable = 0;
		}
		if(checkTileFound) {
			setCheckTile(gui.getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates(gui.getCommunicator().getSimulationPilot().getCurrentPositionRelativeX(), gui.getCommunicator().getSimulationPilot().getCurrentPositionRelativeY()));
			setCheckTileFound(false);
		}
		if(endTileFound) {
			setEndTile(gui.getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates(gui.getCommunicator().getSimulationPilot().getCurrentPositionRelativeX(), gui.getCommunicator().getSimulationPilot().getCurrentPositionRelativeY()));
			setEndTileFound(false);
		}
		
		//zet het mark-veld van de currentTile op true zodat deze niet meer opnieuw in de queu
		//terecht kan komen
		currentTile.setMarkingExploreMaze(true);
				
	
		//voegt buurtiles van de currentTile toe aan de queu, enkel als deze nog niet begaan
		//zijn (niet gemarkeerd) 
		for(Object neighbourTile: currentTile.getReachableNeighbours()) {
			if(neighbourTile != null && !(((Tile) neighbourTile).isMarkedExploreMaze())) {
				queue.add((Tile) neighbourTile);
				/*int i = 0;
				for(Object neighbour: ((Tile) neighbourTile).getAllNeighbours()) {
					if(neighbour != null && (((Tile) neighbour).isMarkedExploreMaze()))
						i++;
				}
				if(i == 4 && ((Tile)neighbourTile).getAmountOfWalls() > 2) {
					((Tile) neighbourTile).setMarkingExploreMaze(true);
					allTiles.add((Tile) neighbourTile);
					while(queue.contains(neighbourTile))
						queue.remove(neighbourTile);
				}*/
			}
		}
		
		//returnt als er geen tiles meer in de wachtrij zitten (algoritme is afgelopen)
		if(queue.isEmpty()){
			return;
		}
		Tile nextTile;
		currentOrientation = gui.getCommunicator().getSimulationPilot().getCurrentOrientation();
		
		if(currentTile.getEdge(currentOrientation).isPassable() 
				&& currentTile.getEdge(currentOrientation).getNeighbour(currentTile) != null 
				&& queue.contains(currentTile.getEdge(currentOrientation).getNeighbour(currentTile)))
			nextTile = currentTile.getEdge(currentOrientation).getNeighbour(currentTile);
		else if(currentTile.getEdge(currentOrientation.getOtherOrientationCorner()).isPassable() 
				&& currentTile.getEdge(currentOrientation.getOtherOrientationCorner()).getNeighbour(currentTile) != null 
				&& queue.contains(currentTile.getEdge(currentOrientation.getOtherOrientationCorner()).getNeighbour(currentTile)))
			nextTile = currentTile.getEdge(currentOrientation.getOtherOrientationCorner()).getNeighbour(currentTile);
		else if(currentTile.getEdge(currentOrientation.getOtherOrientationCorner().getOppositeOrientation()).isPassable() 
				&& currentTile.getEdge(currentOrientation.getOtherOrientationCorner().getOppositeOrientation()).getNeighbour(currentTile) != null 
				&& queue.contains(currentTile.getEdge(currentOrientation.getOtherOrientationCorner().getOppositeOrientation()).getNeighbour(currentTile)))
			nextTile = currentTile.getEdge(currentOrientation.getOtherOrientationCorner().getOppositeOrientation()).getNeighbour(currentTile);
		else if(currentTile.getEdge(currentOrientation.getOppositeOrientation()).isPassable() 
				&& currentTile.getEdge(currentOrientation.getOppositeOrientation()).getNeighbour(currentTile) != null 
				&& queue.contains(currentTile.getEdge(currentOrientation.getOppositeOrientation()).getNeighbour(currentTile)))
			nextTile = currentTile.getEdge(currentOrientation.getOppositeOrientation()).getNeighbour(currentTile);
		else
			nextTile = queue.lastElement();
		
		while(!isGoodNextTile(nextTile)) {
			nextTile.setMarkingExploreMaze(true);
			allTiles.add(nextTile);
			while(queue.contains(nextTile))
				queue.remove(nextTile);
			if(queue.isEmpty())
				return;
			nextTile = queue.lastElement();
		}
		allTiles.add(nextTile);
		//verwijdert alle nextTiles uit de queu. De reden waarom deze meermaals in de queu 
		//voorkomen is omdat het voordeliger is om de laatste versie te pakken omdat deze
		//het dichts bij de currentTile ligt, zodat de robot niet voor elke nextTile massa's
		//omweg moet doen
		while(queue.contains(nextTile))
			queue.remove(nextTile);
		//voert een shortestPath uit om van currentTile naar startTile te gaan.
		ShortestPath shortestPath = new ShortestPath(gui, currentTile, nextTile, allTiles);
		shortestPath.goShortestPath();
		//voert methode opnieuw uit met nextTile
		algorithm(nextTile);
	}
	
	public boolean isGoodNextTile(Tile nextTile) {
		 int j = 0;
		 if(!nextTile.isStraightTile()){
			 for(Object neighbourTile : nextTile.getAllNeighbours()){
				 if(neighbourTile != null &&((Tile) neighbourTile).isMarkedExploreMaze())
					 j++;
			 }
		 }
		 if(j == 4)
			 return false;
		 return true;
	}
}