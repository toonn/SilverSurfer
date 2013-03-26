package mapping;

public class StartBase extends TileContent {

    private Orientation orientation;

    public StartBase(Tile tile, int value, Orientation orientation) {
        super(tile, value);
        this.orientation = orientation;
    }

    public Orientation getOrientation() {
        return orientation;
    }
}