package mapping;

import java.awt.Point;

public enum Orientation {
    NORTH {
        @Override
        public final Point getNext(Point point) {
            return new Point(point.x, point.y - 1);
        }

        @Override
        public final Orientation getOppositeOrientation() {
            return SOUTH;
        }

        @Override
        public final Orientation getCounterClockwiseOrientation() {
            return WEST;
        }

        @Override
        public final int getAngle() {
            return 270;
        }
    },
    EAST {
        @Override
        public final Point getNext(Point point) {
            return new Point(point.x + 1, point.y);
        }

        @Override
        public final Orientation getOppositeOrientation() {
            return WEST;
        }

        @Override
        public final Orientation getCounterClockwiseOrientation() {
            return NORTH;
        }

        @Override
        public final int getAngle() {
            return 0;
        }
    },
    SOUTH {
        @Override
        public final Point getNext(Point point) {
            return new Point(point.x, point.y + 1);
        }

        @Override
        public final Orientation getOppositeOrientation() {
            return NORTH;
        }

        @Override
        public final Orientation getCounterClockwiseOrientation() {
            return EAST;
        }

        @Override
        public final int getAngle() {
            return 90;
        }
    },
    WEST {
        @Override
        public final Point getNext(Point point) {
            return new Point(point.x - 1, point.y);
        }

        @Override
        public final Orientation getOppositeOrientation() {
            return EAST;
        }

        @Override
        public final Orientation getCounterClockwiseOrientation() {
            return SOUTH;
        }

        @Override
        public final int getAngle() {
            return 180;
        }
    };

    public static Orientation calculateOrientation(final double angle) {
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

    public abstract Point getNext(Point point);

    public abstract Orientation getOppositeOrientation();
    
    public abstract Orientation getCounterClockwiseOrientation();

    public abstract int getAngle();
}