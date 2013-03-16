package simulator.viewport;

import java.util.HashSet;
import java.util.Set;

import mapping.MapGraph;
import simulator.pilot.PilotInterface;

@SuppressWarnings("serial")
public class DummyViewPort extends AbstractViewPort {
	
    public DummyViewPort(Set<? extends PilotInterface> pilotSet) {
        super(pilotSet);
    }

    @Override
    protected Set<MapGraph> getAllMapGraphs() {
        Set<MapGraph> maps = new HashSet<MapGraph>();
        maps.add(pilots.iterator().next().getMapGraphConstructed());
        return maps;
    }
    
    public boolean containsDummy() {
    	return true;
    }
}