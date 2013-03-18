package simulator.viewport;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import simulator.pilot.PilotInterface;
import mapping.MapGraph;

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
}