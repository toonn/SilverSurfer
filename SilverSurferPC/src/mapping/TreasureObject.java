package mapping;

import commands.BarcodeCommand;

public class TreasureObject extends TileContent {
	
	private int teamNo;

	/**
     * Creates an object with as value 'value'.
     */
    public TreasureObject(final Tile tile, final int value) {
        super(tile, value);
    }
    
	@Override
	public int getColorValue(double x, double y) {
		//Only for barcodes
		return 0;
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
	
	/**
	 * Get the destined team (0 or 1).
	 */
	public int getTeamNo() {
		return teamNo;
	}
	
	public void setTeamNo(int teamNo) {
		this.teamNo = teamNo;
	}
}
