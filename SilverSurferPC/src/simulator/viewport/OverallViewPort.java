package simulator.viewport;

import java.util.HashSet;
import java.util.Set;

import simulator.pilot.PilotInterface;
import mapping.MapGraph;

@SuppressWarnings("serial")
public class OverallViewPort extends AbstractViewPort {
	
    private MapGraph mapGraphLoaded;

    public OverallViewPort(Set<PilotInterface> pilotSet, MapGraph mapGraphLoaded) {
        super(pilotSet);
        this.mapGraphLoaded = mapGraphLoaded;
    }

    @Override
    protected Set<MapGraph> getAllMapGraphs() {
        Set<MapGraph> maps = new HashSet<MapGraph>();
        maps.add(mapGraphLoaded);
        return maps;
    }
}
