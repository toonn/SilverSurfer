package mapping;

public enum Obstruction {
    WALL {
        @Override
        public boolean isPassable() {
            return false;
        }
    },
    WHITE_LINE {
        @Override
        public boolean isPassable() {
            return true;
        }
    };

    public boolean isPassable() {
        return true;
    }
}
