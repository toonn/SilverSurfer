package mazeAlgorithm;

public class ExploreThread extends Thread {

    private final MazeExplorer explorer;

    public ExploreThread(final MazeExplorer explorer) {
        this.explorer = explorer;
    }

    @Override
    public void run() {
        super.run();
        explorer.startExploringMaze();
    }
}
