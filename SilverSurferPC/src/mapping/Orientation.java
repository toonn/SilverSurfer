package mapping;

import java.awt.Point;

public enum Orientation {
    NORTH {
        @Override
        public final int getAngle() {
            return 270;
        }

        @Override
        public final Orientation getCounterClockwiseOrientation() {
            return WEST;
        }

        @Override
        public final Orientation getClockwiseOrientation() {
            return EAST;
        }

        @Override
        public final Point getNext(Point point) {
            return new Point(point.x, point.y - 1);
        }

        @Override
        public final Orientation getOppositeOrientation() {
            return SOUTH;
        }

        @Override
		public Orientation orientationRotatedOver(Orientation ori) {
			return ori;
		}

    },
    EAST {
        @Override
        public final int getAngle() {
            return 0;
        }

        @Override
        public final Orientation getCounterClockwiseOrientation() {
            return NORTH;
        }

        @Override
        public final Orientation getClockwiseOrientation() {
            return SOUTH;
        }

        @Override
        public final Point getNext(Point point) {
            return new Point(point.x + 1, point.y);
        }

        @Override
        public final Orientation getOppositeOrientation() {
            return WEST;
        }

        @Override
		public Orientation orientationRotatedOver(Orientation ori) {
			if (ori == NORTH) {
				return EAST;
			}
			if (ori == SOUTH) {
				return WEST;
			}
			if (ori == WEST) {
				return NORTH;
			}
			if (ori == EAST) {
				return SOUTH;
			}
			return null;
		}
    },
    SOUTH {
        @Override
        public final int getAngle() {
            return 90;
        }

        @Override
        public final Orientation getCounterClockwiseOrientation() {
            return EAST;
        }

        @Override
        public final Orientation getClockwiseOrientation() {
            return WEST;
        }

        @Override
        public final Point getNext(Point point) {
            return new Point(point.x, point.y + 1);
        }

        @Override
        public final Orientation getOppositeOrientation() {
            return NORTH;
        }

        @Override
		public Orientation orientationRotatedOver(Orientation ori) {
			if (ori == NORTH) {
				return SOUTH;
			}
			if (ori == SOUTH) {
				return NORTH;
			}
			if (ori == WEST) {
				return EAST;
			}
			if (ori == EAST) {
				return WEST;
			}
			return null;
		}
    },
    WEST {
        @Override
        public final int getAngle() {
            return 180;
        }

        @Override
        public final Orientation getCounterClockwiseOrientation() {
            return SOUTH;
        }

        @Override
        public final Orientation getClockwiseOrientation() {
            return NORTH;
        }

        @Override
        public final Point getNext(Point point) {
            return new Point(point.x - 1, point.y);
        }

        @Override
        public final Orientation getOppositeOrientation() {
            return EAST;
        }

		@Override
		public Orientation orientationRotatedOver(Orientation ori) {
			if (ori == NORTH) {
				return WEST;
			}
			if (ori == SOUTH) {
				return EAST;
			}
			if (ori == WEST) {
				return SOUTH;
			}
			if (ori == EAST) {
				return NORTH;
			}
			return null;
		}
    };

    public static Orientation calculateOrientation(final double angle) {
        Orientation orientation;
        if (angle >= 315 || angle < 45)
            orientation = EAST;
        else if (angle >= 45 && angle < 135)
            orientation = SOUTH;
        else if (angle >= 135 && angle < 225)
            orientation = WEST;
        else
            orientation = NORTH;
        return orientation;
    }

    public static String toToken(Orientation o) {
        if (o.equals(NORTH))
            return "N";
        else if (o.equals(EAST))
            return "E";
        else if (o.equals(SOUTH))
            return "S";
        else
            return "W";
    }

    public abstract int getAngle();

    public abstract Orientation getCounterClockwiseOrientation();
    
    public abstract Orientation getClockwiseOrientation();

    public abstract Point getNext(Point point);

    public abstract Orientation getOppositeOrientation();

    public abstract Orientation orientationRotatedOver(Orientation ori);
}