package simulator.viewport;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

import simulator.pilot.AbstractPilot;
import simulator.pilot.PilotInterface;
import mapping.MapGraph;
import mapping.Seesaw;
import mapping.Tile;

@SuppressWarnings("serial")
public class OverallViewPort extends AbstractViewPort {

    private MapGraph mapGraphLoaded;

    public OverallViewPort(Set<PilotInterface> pilotSet,
            MapGraph mapGraphLoaded, Color[] teamColors) {
        super(pilotSet, teamColors);
        this.mapGraphLoaded = mapGraphLoaded;
    }

    @Override
    protected Set<MapGraph> getAllMapGraphs() {
        Set<MapGraph> maps = new HashSet<MapGraph>();
        maps.add(mapGraphLoaded);
        return maps;
    }
    
    @Override
    protected void paintComponent(final Graphics graph) {
    	robotOnSeesaw();
        super.paintComponent(graph);
    }
	
	private void robotOnSeesaw() {
		for(Tile tile : mapGraphLoaded.getTiles())
			if(tile.getContent() instanceof Seesaw)
				for(PilotInterface pilot : pilots) 
					if(pilot.getMatrixPosition().equals(tile.getPosition()) && ((Seesaw)tile.getContent()).isUp()) {
							flipSeesaw(tile.getContent().getValue(), mapGraphLoaded);
							flipSeesaw(tile.getContent().getValue(), pilot.getMapGraphConstructed());
					}
	}
    
    /**
     * Flips all the seesaws with the given value to the other side.
     */
    private void flipSeesaw(int value, MapGraph map) {
    	for (Tile tile: map.getTiles())
    		if(tile.getContent() instanceof Seesaw && tile.getContent().getValue() == value)
    			((Seesaw) tile.getContent()).flipSeesaw();
    }
    
    private void updateTeamsNoDummies() {
    	int teamPilot0 = -1;
    	int teamPilot1 = -1;
    	int teamPilot2 = -1;
    	int teamPilot3 = -1;    	
		for(PilotInterface pilot : pilots) {
			if(pilot instanceof AbstractPilot) {
				if(pilot.getPlayerNumber() == 0 && pilot.getTeamNumber() != -1)
					teamPilot0 = pilot.getTeamNumber();
				if(pilot.getPlayerNumber() == 1 && pilot.getTeamNumber() != -1)
					teamPilot1 = pilot.getTeamNumber();
				if(pilot.getPlayerNumber() == 2 && pilot.getTeamNumber() != -1)
					teamPilot2 = pilot.getTeamNumber();
				if(pilot.getPlayerNumber() == 3 && pilot.getTeamNumber() != -1)
					teamPilot3 = pilot.getTeamNumber();
			}
		}
		if(teamPilot0 != -1)
			for(PilotInterface pilot : pilots)
				if(pilot instanceof AbstractPilot && pilot.getPlayerNumber() != 0 && pilot.getTeamNumber() == teamPilot0)
					((AbstractPilot)pilot).setTeamMemberFound(0);
		if(teamPilot1 != -1)
			for(PilotInterface pilot : pilots)
				if(pilot instanceof AbstractPilot && pilot.getPlayerNumber() != 1 && pilot.getTeamNumber() == teamPilot1)
					((AbstractPilot)pilot).setTeamMemberFound(1);
		if(teamPilot2 != -1)
			for(PilotInterface pilot : pilots)
				if(pilot instanceof AbstractPilot && pilot.getPlayerNumber() != 2 && pilot.getTeamNumber() == teamPilot2)
					((AbstractPilot)pilot).setTeamMemberFound(2);
		if(teamPilot3 != -1)
			for(PilotInterface pilot : pilots)
				if(pilot instanceof AbstractPilot && pilot.getPlayerNumber() != 3 && pilot.getTeamNumber() == teamPilot3)
					((AbstractPilot)pilot).setTeamMemberFound(3);
    }
}