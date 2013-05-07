package simulator.viewport;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.HashSet;
import java.util.Set;

import mapping.MapGraph;
import mapping.Seesaw;
import mapping.Tile;
import simulator.pilot.AbstractPilot;
import simulator.pilot.DummyPilot;
import simulator.pilot.PilotInterface;

@SuppressWarnings("serial")
public class OverallViewPort extends AbstractViewPort {

    private MapGraph mapGraphLoaded;

    public OverallViewPort(Set<PilotInterface> pilotSet,
            MapGraph mapGraphLoaded, Color[] teamColors) {
        super(pilotSet, teamColors, Color.GRAY);
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

    @Override
    protected void paintRobotColor(final Graphics graph) {
        int diam = 25;
        final Graphics2D g2 = (Graphics2D) graph;

        for (PilotInterface pilot : pilots) {
            if (!(pilot instanceof DummyPilot) || ((DummyPilot) pilot).isActive()) {
                AffineTransform oldTransform = g2.getTransform();
                if (!(pilot instanceof DummyPilot) && pilot.getTeamNumber() < 0)
                	g2.setColor(teamColors[((AbstractPilot)pilot).getPlayerNumber()]);
                else
                    g2.setColor(teamColors[4 + pilot.getTeamNumber()]);

                g2.fill(new Ellipse2D.Double(
                        (pilot.getPosition().getX() - (diam / 2)), (pilot
                                .getPosition().getY() - (diam / 2)), diam, diam));
                g2.setTransform(oldTransform);
            }
        }
        Set<int[]> positions = SimulatorPanel.getAllRobotPositions();
        Set<int[]> positionsNotToDraw = new HashSet<int[]>();
        for(PilotInterface pilot : pilots)
            for(int[] position : positions)
            	if(!(pilot instanceof DummyPilot) && position[0] == ((AbstractPilot)pilot).getPlayerNumber())
            		positionsNotToDraw.add(position);
        for (int[] position : positions) {
            if (!positionInArray(position, positionsNotToDraw)) {
                AffineTransform oldTransform = g2.getTransform();
                g2.setColor(teamColors[6]);
                g2.fill(new Ellipse2D.Double(
                        (position[1]*40 + 20 - (diam / 2)), (position[2]*40 + 20 - (diam / 2)), diam, diam));
                g2.setTransform(oldTransform);
            }
        }
    }

    @Override
    protected void paintRobots(final Graphics graph) {
        Graphics2D g2 = (Graphics2D) graph;
        for (PilotInterface pilot : pilots) {
            if (!(pilot instanceof DummyPilot)) {
                AffineTransform oldTransform = g2.getTransform();
                g2.rotate(Math.toRadians(pilot.getAngle()), pilot.getPosition().getX(), pilot.getPosition().getY());
                g2.drawImage(robotSprite.getImage(), (int) ((pilot.getPosition().getX() - robotSprite.getIconWidth() / 2)), (int) ((pilot.getPosition().getY() - robotSprite.getIconHeight() / 2)), null);
                g2.setTransform(oldTransform);
            }
        }
        Set<int[]> positions = SimulatorPanel.getAllRobotPositions();
        Set<int[]> positionsNotToDraw = new HashSet<int[]>();
        for(PilotInterface pilot : pilots)
            for(int[] position : positions)
            	if(!(pilot instanceof DummyPilot) && position[0] == ((AbstractPilot)pilot).getPlayerNumber())
            		positionsNotToDraw.add(position);
        for (int[] position : positions) {
            if (!positionInArray(position, positionsNotToDraw)) {
                AffineTransform oldTransform = g2.getTransform();
                g2.rotate(Math.toRadians(position[3]), position[1]*40 + 20, position[2]*40 + 20);
                g2.drawImage(robotSprite.getImage(), (int) ((position[1]*40 + 20 - robotSprite.getIconWidth() / 2)), (int) ((position[2]*40 + 20 - robotSprite.getIconHeight() / 2)), null);
                g2.setTransform(oldTransform);
            }
        }
    }
    
    private boolean positionInArray(int[] position, Set<int[]> positionArray) {
        for (int[] positionOfArray : positionArray) {
        	if(position[0] == positionOfArray[0])
        		return true;
        }
        return false;
    }

    private void robotOnSeesaw() {
        for (Tile tile : mapGraphLoaded.getTiles())
            if (tile.getContent() instanceof Seesaw
                    && ((Seesaw) tile.getContent()).isClosed())
                for (PilotInterface pilot : pilots)
                    if (pilot.getMatrixPosition().equals(tile.getPosition()))
                        for (Tile mapTile : mapGraphLoaded.getTiles())
                            if (mapTile.getContent() instanceof Seesaw
                                    && mapTile.getContent().getValue() == tile
                                            .getContent().getValue())
                                ((Seesaw) mapTile.getContent()).flipSeesaw();
    }
}