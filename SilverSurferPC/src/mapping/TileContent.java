package mapping;

public abstract class TileContent {

    protected Tile tile;

    protected TileContent(final Tile tile) {
        this.tile = tile;
    }

    /*
     * TODO: kunnen meegeven of de barcode een finishbarcode is.
     */

    public abstract int getColorValue(double x, double y);

}
