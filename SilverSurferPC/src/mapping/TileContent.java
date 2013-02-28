package mapping;

import java.awt.Point;

public abstract class TileContent {

    protected Tile tile;

    protected TileContent(final Tile tile) {
        this.tile = tile;
    }

    public abstract int getColorValue(double x, double y);

    public Point getPosition() {
        return tile.getPosition();
    }
}
