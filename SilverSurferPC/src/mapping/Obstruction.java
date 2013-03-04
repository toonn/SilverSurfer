package mapping;

public enum Obstruction {
    WALL {
        @Override
        public boolean isPasseble() {
            return false;
        }
    },
    WHITE_LINE {
        @Override
        public boolean isPasseble() {
            return true;
        }
    };

    public boolean isPasseble() {
        return true;
    }
}
