package mazeAlgorithm;

import mapping.Tile;
import simulator.pilot.AbstractPilot;

public class ExploreThread extends Thread {

    Tile startTile;
    AbstractPilot pilot;
    MazeExplorer explorer;
    
    public MazeExplorer getExplorer() {
    	return explorer;
    }

    public ExploreThread(Tile startTile, AbstractPilot pilot) {
        this.startTile = startTile;
        this.pilot = pilot;
    }

    public void quit() {
        explorer.quit();
    }

    @Override
    public void run() {
        explorer = new MazeExplorer(startTile, pilot);
        explorer.startExploringMaze();
    }
}