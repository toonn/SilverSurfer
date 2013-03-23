package mapping;

import java.awt.Point;

public abstract class TileContent {

    protected Tile tile;
    protected int value;

    public TileContent(final Tile tile, final int value) {
        this.tile = tile;
        this.value = value;
    }

    public Point getPosition() {
        return tile.getPosition();
    }
    
    public int getValue() {
        return value;
    }
}