package simulator.viewport;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

import simulator.pilot.PilotInterface;
import mapping.MapGraph;
import mapping.Seesaw;
import mapping.Tile;

@SuppressWarnings("serial")
public class OverallViewPort extends AbstractViewPort {

    private MapGraph mapGraphLoaded;

    public OverallViewPort(Set<PilotInterface> pilotSet, MapGraph mapGraphLoaded, Color[] teamColors) {
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
    	robotOnWip();
        super.paintComponent(graph);
    }
	
	private void robotOnWip() {
		for(Tile tile : mapGraphLoaded.getTiles())
			if(tile.getContent() instanceof Seesaw)
				for(PilotInterface pilot : pilots) 
					if(pilot.getMatrixPosition().equals(tile.getPosition()) && ((Seesaw)tile.getContent()).isUp()) {
							mapGraphLoaded.flipSeesaw(tile.getContent().getValue());
							pilot.getMapGraphConstructed().flipSeesaw(tile.getContent().getValue());
					}
	}
}