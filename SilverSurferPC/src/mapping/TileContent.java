package mapping;

import java.awt.Point;

public abstract class TileContent {

    protected Tile tile;
    protected int value;

    protected TileContent(final Tile tile, final int value) {
        this.tile = tile;
        this.value = value;
    }

    public abstract int getColorValue(double x, double y);

    public Point getPosition() {
        return tile.getPosition();
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(final int value) {
        this.value = value;
    }
}
