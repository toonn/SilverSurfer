package mapping;

public class Barcode extends TileContent {

    private Orientation direction;

    /**
     * Creates a barcode with as value 'value'.
     */
    public Barcode(final Tile tile, final int value, final Orientation direction) {
        super(tile, value);
        setDirection(direction);
    }

    /**
     * Creates a default, empty barcode, facing NORTH. (value == 0).
     */
    public Barcode(final Tile tile) {
        this(tile, 0, Orientation.NORTH);
    }

    /**
     * false = black, true = white
     */
    public boolean[] getBoolRep() {
        final boolean[] boolRep = new boolean[8];
        for (int i = 0; i < 8; i++) {
            if (toString().charAt(i) == '1') {
                boolRep[i] = true;
            }
        }

        return boolRep;
    }

    /**
     * Gives the color of the barcode on the given x-y-coordinate
     * 
     * @param x
     *            relative to the tile! (0,0 is upper-left corner)
     * @param y
     *            relative to the tile!
     * @param direction
     *            the orientation the barcode is being crossed over (North =
     *            from south to North, East = from West to East. We simplify the
     *            'mirroring' by stating North = South and West = East
     * @return 0, when standing on a black part of the barcode 1, when standing
     *         on a white part of the barcode something else, when standing next
     *         to the barcode but on the same tile (brown underground)
     */
    public int getColorValue(final double x, final double y) {
        if (getDirection() == Orientation.NORTH
                || getDirection() == Orientation.SOUTH) {

            if (y < 12) {
                return 2; // TODO COLORVALUE ONDERGROND
            } else if (12 <= y && y < 14) {
                return Character.getNumericValue(toString().charAt(0));
            } else if (14 <= y && y < 16) {
                return Character.getNumericValue(toString().charAt(1));
            } else if (16 <= y && y < 18) {
                return Character.getNumericValue(toString().charAt(2));
            } else if (18 <= y && y < 20) {
                return Character.getNumericValue(toString().charAt(3));
            } else if (20 <= y && y < 22) {
                return Character.getNumericValue(toString().charAt(4));
            } else if (22 <= y && y < 24) {
                return Character.getNumericValue(toString().charAt(5));
            } else if (24 <= y && y < 26) {
                return Character.getNumericValue(toString().charAt(6));
            } else if (26 <= y && y < 28) {
                return Character.getNumericValue(toString().charAt(7));
            } else {
                return 2; // y > 28
            }
        } else {
            if (x < 12) {
                return 2; // TODO COLORVALUE ONDERGROND
            } else if (12 <= x && x < 14) {
                return Character.getNumericValue(toString().charAt(0));
            } else if (14 <= x && x < 16) {
                return Character.getNumericValue(toString().charAt(1));
            } else if (16 <= x && x < 18) {
                return Character.getNumericValue(toString().charAt(2));
            } else if (18 <= x && x < 20) {
                return Character.getNumericValue(toString().charAt(3));
            } else if (20 <= x && x < 22) {
                return Character.getNumericValue(toString().charAt(4));
            } else if (22 <= x && x < 24) {
                return Character.getNumericValue(toString().charAt(5));
            } else if (24 <= x && x < 26) {
                return Character.getNumericValue(toString().charAt(6));
            } else if (26 <= x && x < 28) {
                return Character.getNumericValue(toString().charAt(7));
            } else {
                return 2;
            }
        }
    }

    /**
     * Returns the direction this barcode is in. (N to S or E to W)
     * 
     * @return
     */
    public Orientation getDirection() {
        return direction;
    }

    public void setDirection(final Orientation direction) {
        this.direction = direction;
    }

    /**
     * Given a decimal barcode-value, get the team-number associated with it.     
     * only useful for treasure-barcodes
     */
    public static int getTeamNumberFrom(int value) {
    	//generate binary-string
    	String binaryRep = Integer.toBinaryString(value);
    	String binaryExp = "000000000000".concat(binaryRep);
    	int length = binaryExp.length();
    	//see barcode specification doc.
    	int teamNo = Integer.parseInt(binaryExp.substring(length-3,length-2),2);
    	return teamNo;
	}
    
    /**
     * Given a decimal barcode-value, get the player-number associated with it.
     * only useful for treasure-barcodes.
     */
    public static int getPlayerNumberFrom(int value) {
    	//generate binary-string
    	String binaryRep = Integer.toBinaryString(value);
    	String binaryExp = "000000000000".concat(binaryRep);
    	int length = binaryExp.length();
    	//see barcode specification doc.
    	int playerNo = Integer.parseInt(binaryExp.substring(length-2,length),2);
    	return playerNo;
	}
    /**
     * 0 = black, 1 = white
     */
    @Override
    public String toString() {
        String body = Integer.toBinaryString(getValue());
        while (body.length() < 6) {
            body = "0" + body;
        }
        return 0 + body + 0;
    }
    
}
