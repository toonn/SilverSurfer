package mazeAlgorithm;

import java.awt.Point;

import mapping.MapGraph;
import simulator.pilot.AbstractPilot;

public class ExploreThread extends Thread {
	
	MapGraph graph;
	Point p;
	AbstractPilot pilot;
	MazeExplorer explorer;

	public ExploreThread(MapGraph graph, Point p, AbstractPilot pilot) {
		this.graph = graph;
		this.p = p;
		this.pilot = pilot;
	}
	
	@Override
	public void run() {
		explorer = new MazeExplorer(graph.getTile(p), pilot, true);
		explorer.startExploringMaze();
	}
	
	public void quit() {
		explorer.setQuit(true);
	}
}