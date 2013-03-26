package simulator.viewport;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import mapping.MapGraph;
import simulator.pilot.PilotInterface;

@SuppressWarnings("serial")
public class DummyViewPort extends AbstractViewPort {

    public DummyViewPort(Set<? extends PilotInterface> pilotSet,
            Color[] teamColors) {
        super(pilotSet, teamColors);
    }

    @Override
    protected Set<MapGraph> getAllMapGraphs() {
        Set<MapGraph> maps = new HashSet<MapGraph>();
        maps.add(pilots.iterator().next().getMapGraphConstructed());
        return maps;
    }
}