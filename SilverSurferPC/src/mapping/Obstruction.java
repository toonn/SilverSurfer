package mapping;

public enum Obstruction {
	
    WHITE_LINE {
    	
        @Override
        public boolean isPassable() {
            return true;
        }

		@Override
		public boolean isCertain() {
			return true;
		}
    },
    SEESAW_UP {
        @Override
        public boolean isPassable() {
			return true;
        }

		@Override
		public boolean isCertain() {
			return true;
		}
    },
    SEESAW_FLIP {
        @Override
        public boolean isPassable() {
            return true;
        }

		@Override
		public boolean isCertain() {
			return true;
		}
    },
    SEESAW_DOWN {
        @Override
        public boolean isPassable() {
            return true;
        }

		@Override
		public boolean isCertain() {
			return true;
		}
    },
    WALL {
        @Override
        public boolean isPassable() {
            return false;
        }

		@Override
		public boolean isCertain() {
			return viewCount > VIEW_CNT_NCSSRY;
		}
    };
    
    
	int viewCount = 0;
	final int VIEW_CNT_NCSSRY = 1;
    public abstract boolean isPassable();
    public abstract boolean isCertain();

}