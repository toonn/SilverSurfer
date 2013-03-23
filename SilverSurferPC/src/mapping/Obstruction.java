package mapping;

public enum Obstruction {
    WHITE_LINE {
        @Override
        public boolean isPassable() {
            return true;
        }
    },
    SEESAW_UP {
    	@Override
        public boolean isPassable() {
            return false;
        }
    },
    SEESAW_FLIP {
    	@Override
        public boolean isPassable() {
            return true;
        }
    },
    SEESAW_DOWN {
    	@Override
        public boolean isPassable() {
            return true;
        }
    },
    WALL {
        @Override
        public boolean isPassable() {
            return false;
        }
    };

    public abstract boolean isPassable();
}
