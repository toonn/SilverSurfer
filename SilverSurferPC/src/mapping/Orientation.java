package mapping;

import java.awt.Point;

public enum Orientation {
    NORTH {
        @Override
        public final Point getNext(Point point) {
            return new Point(point.x, point.y - 1);
        }

        @Override
        public final int getNumberArray() {
            return 0;
        }

        @Override
        public final int getNumberOrientation() {
            return 0;
        }

        @Override
        public final Orientation getOppositeOrientation() {
            return SOUTH;
        }

        @Override
        public final Orientation getOtherOrientationCorner() {
            return WEST;
        }

        @Override
        public final int getRightAngle() {
            return 270;
        }
    },
    EAST {
        @Override
        public final Point getNext(Point point) {
            return new Point(point.x + 1, point.y);
        }

        @Override
        public final int getNumberArray() {
            return 1;
        }

        @Override
        public final int getNumberOrientation() {
            return 4;
        }

        @Override
        public final Orientation getOppositeOrientation() {
            return WEST;
        }

        @Override
        public final Orientation getOtherOrientationCorner() {
            return NORTH;
        }

        @Override
        public final int getRightAngle() {
            return 0;
        }
    },
    SOUTH {
        @Override
        public final Point getNext(Point point) {
            return new Point(point.x, point.y + 1);
        }

        @Override
        public final int getNumberArray() {
            return 2;
        }

        @Override
        public final int getNumberOrientation() {
            return 3;
        }

        @Override
        public final Orientation getOppositeOrientation() {
            return NORTH;
        }

        @Override
        public final Orientation getOtherOrientationCorner() {
            return EAST;
        }

        @Override
        public final int getRightAngle() {
            return 90;
        }
    },
    WEST {
        @Override
        public final Point getNext(Point point) {
            return new Point(point.x - 1, point.y);
        }

        @Override
        public final int getNumberArray() {
            return 3;
        }

        @Override
        public final int getNumberOrientation() {
            return 1;
        }

        @Override
        public final Orientation getOppositeOrientation() {
            return EAST;
        }

        @Override
        public final Orientation getOtherOrientationCorner() {
            return SOUTH;
        }

        @Override
        public final int getRightAngle() {
            return 180;
        }
    };

    // private static double angleDelta(final Orientation orientation, final
    // double angle) {
    // return Math.abs(orientation.getRightAngle() - angle);
    // }

    /**
     * Calculates the orientation of the edge you will cross first while moving
     * in the direction alpha, starting from x,y.
     */
    public static Orientation calculateOrientation(final double angle) {
    	/*
        Orientation orientation = EAST;
        if (angleDelta(WEST, angle) < angleDelta(orientation, angle)) {
            orientation = WEST;
        }
        if (angleDelta(NORTH, angle) < angleDelta(orientation, angle)) {
            orientation = NORTH;
        }
        if (angleDelta(SOUTH, angle) < angleDelta(orientation, angle)) {
            orientation = SOUTH;
        }*/
    	Orientation orientation;
    	if(angle >= 315 || angle < 45)
    		orientation = EAST;
    	else if(angle >= 45 && angle < 135)
    		orientation = SOUTH;
    	else if(angle >= 135 && angle < 225)
    		orientation = WEST;
    	else
    		orientation = NORTH;

        return orientation;
    }

    /**
     * @param xTemp
     * @param yTemp
     * @param xTempPrev
     * @param yTempPrev
     * @return
     */
    public static Orientation defineBorderCrossed(final double xTemp,
            final double yTemp, final double xTempPrev, final double yTempPrev,
            final double sizeTile) {
        Orientation oriTemp = null;

        // TODO waarom 20 en 5 ?

        // you have crossed a horizontal border
        if (Math.abs(yTempPrev % sizeTile - yTemp % sizeTile) > 5) {
            if (yTempPrev % sizeTile < sizeTile / 2) {
                oriTemp = Orientation.NORTH;
            }
            // if(xTempPrev%40 < 20)
            else {
                oriTemp = Orientation.SOUTH;
            }
        }
        // you have crossed a vertical border
        else if (Math.abs(xTempPrev % sizeTile - xTemp % sizeTile) > 5) {
            if (xTempPrev % sizeTile > sizeTile / 2) {
                oriTemp = Orientation.EAST;
            }
            // if(xTempPrev%40 < 20)
            else {
                oriTemp = Orientation.WEST;
            }
        }
        return oriTemp;
    }

    // /**
    // * hulpmethode die verder niet gebruikt wordt.. is in de setTileMethode
    // van
    // * nut
    // */
    // static Orientation getOrientationOfArray(final int[] array) {
    // if (array[0] == 1) {
    // return EAST;
    // } else if (array[0] == -1) {
    // return WEST;
    // } else if (array[1] == -1) {
    // return NORTH;
    // } else {
    // return SOUTH;
    // }
    // }

    /**
     * enkel gebruikt in mapreader , doet verder niet ter zake
     */
    public static Orientation switchStringToOrientation(final String string) {
        if (string.equals("N")) {
            return NORTH;
        } else if (string.equals("S")) {
            return SOUTH;
        } else if (string.equals("E")) {
            return EAST;
        } else {
            return WEST;
        }
    }

    public abstract Point getNext(Point point);

    public int getNumberArray() {

        // implementation is orientation dependent
        return -1;

    }

    /**
     * dit wordt enkel gebruikt in tile en edges doet voor de rest ook niet echt
     * ter zake
     * 
     * 
     * Return the number of the given direction.
     * 
     * Each pair of opposite directions has received an even and an odd number.
     * Like that, it separates the directions in two groups: The even numbers
     * are North, East and Ceiling. The corresponding odd numbers are the
     * opposites, respectively South, West and Floor.
     * 
     * Each pair has the same residual when you divide the number according to
     * the direction by 3. Like that, it separates the directions in three
     * groups, each group contains one pair :
     * 
     * The directions with residual 0 are North and South, the directions with
     * residual 1 are West and East, the directions with residual 2 are Floor
     * and Ceiling.
     * 
     * 
     * @param direction
     *            the direction to get the number of.
     * @return the number of the given direction.
     * @throws IllegalArgumentException
     *             The given direction is not effective. | direction == null
     */
    public int getNumberOrientation() {
        // implementation is orientation dependent
        return -1;
    }

    public Orientation getOppositeOrientation() {
        // implementation is orientation dependent
        return null;
    }

    /**
     * enkel gebruikt in mapreader , doet verder niet ter zake
     */
    public Orientation getOtherOrientationCorner() {
        // implementation is orientation dependent
        return null;
    }

    /**
     * enkel gebruikt in whiteAllignAlgoritme , doet verder niet ter zake
     */
    public int getRightAngle() {
        // implementation is orientation dependent
        // This line will never be reached, each valid direction has a return
        // statement.
        return -1;
    }
}