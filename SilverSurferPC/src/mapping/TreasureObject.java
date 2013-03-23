package mapping;

import commands.BarcodeCommand;

public class TreasureObject extends TileContent {
	
	private int teamNumber;

    public TreasureObject(final Tile tile, final int value) {
        super(tile, value);
    }

	public int getColor() {
		int color = -1;
		for(int i = 0; i < 4; i++)
			if(value == BarcodeCommand.TREASURE_TEAM0[i] || value == BarcodeCommand.TREASURE_TEAM0_INVERSE[i] || value == BarcodeCommand.TREASURE_TEAM1[i] || value == BarcodeCommand.TREASURE_TEAM1_INVERSE[i])
				color = i; //Team color (0 to 3)
		if(color == -1)
			color = 6; //Gray color
		return color;
	}
	
	public int getTeamNumber() {
		return teamNumber;
	}
	
	public void setTeamNumber(int teamNumber) {
		this.teamNumber = teamNumber;
	}
    
    public void setValue(final int value) {
        this.value = value;
    }
}
