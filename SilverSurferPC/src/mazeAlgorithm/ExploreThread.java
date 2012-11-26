package mazeAlgorithm;

public class ExploreThread extends Thread{

	private MazeExplorer explorer;
	
	public ExploreThread(MazeExplorer explorer){
		this.explorer = explorer;
	}
	
	@Override
	public void run() {
		super.run();
		explorer.startExploringMaze();
	}
}
