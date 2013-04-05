package mapping;

public class Seesaw extends TileContent {

    private Orientation orientation;
    private boolean closed = false;
    private Seesaw otherSeesaw;

    public Seesaw(final Tile tile, final Orientation orientation) {
        super(tile, 0);
        this.orientation = orientation;
    }

    /**
     * Flips the Seesaw. If the current Seesaw-edge is up, it is set down and
     * vice versa.
     */
    public void flipSeesaw() {
        for (Edge edge : tile.getEdges())
            if (edge.getObstruction() == Obstruction.SEESAW_DOWN)
                edge.setObstruction(Obstruction.SEESAW_UP);
            else if (edge.getObstruction() == Obstruction.SEESAW_UP)
                edge.setObstruction(Obstruction.SEESAW_DOWN);
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Seesaw getOtherSeesaw() {
        return otherSeesaw;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean isUp() {
        for (Edge edge : tile.getEdges())
            if (edge.getObstruction() == Obstruction.SEESAW_UP)
                return true;
        return false;
    }

    public void setOtherSeesaw(Seesaw otherSeesaw) {
        this.otherSeesaw = otherSeesaw;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    public void switchClosed() {
        closed = !closed;
    }
}