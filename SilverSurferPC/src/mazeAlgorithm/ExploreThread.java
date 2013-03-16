package mazeAlgorithm;

import mapping.Tile;
import simulator.pilot.AbstractPilot;

public class ExploreThread extends Thread {
	
	Tile startTile;
	AbstractPilot pilot;
	MazeExplorer explorer;

	public ExploreThread(Tile startTile, AbstractPilot pilot) {
		this.startTile = startTile;
		this.pilot = pilot;
	}
	
	@Override
	public void run() {
		explorer = new MazeExplorer(startTile, pilot, true);
		explorer.startExploringMaze();
	}
	
	public void quit() {
		explorer.quit();
	}
}