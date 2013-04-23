package simulator.viewport;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

import mapping.MapGraph;
import mapping.Seesaw;
import mapping.Tile;
import simulator.pilot.AbstractPilot;
import simulator.pilot.PilotInterface;

@SuppressWarnings("serial")
public class OverallViewPort extends AbstractViewPort {

    private MapGraph mapGraphLoaded;

    public OverallViewPort(Set<AbstractPilot> pilotSet,
            MapGraph mapGraphLoaded, Color[] teamColors) {
        super(pilotSet, teamColors, Color.BLACK);
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
        for (Tile tile : mapGraphLoaded.getTiles())
            if (tile.getContent() instanceof Seesaw && ((Seesaw) tile.getContent()).isClosed())
                for (PilotInterface pilot : pilots)
                    if (pilot.getMatrixPosition().equals(tile.getPosition()))
                        for (Tile mapTile : mapGraphLoaded.getTiles())
                            if (mapTile.getContent() instanceof Seesaw && mapTile.getContent().getValue() == tile.getContent().getValue())
                                ((Seesaw) mapTile.getContent()).flipSeesaw();
    }
}