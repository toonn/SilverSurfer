package simulator.viewport;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.JPanel;

import mapping.MapGraph;
import mapping.MapReader;
import mapping.Tile;
import simulator.pilot.AbstractPilot;
import simulator.pilot.PilotInterface;
import simulator.pilot.SimulationPilot;
import simulator.pilot.RobotPilot;
import simulator.pilot.DummyPilot;

@SuppressWarnings("serial")
public class SimulatorPanel extends JPanel {
    private GroupLayout simulatorLayout;

    private OverallViewPort overallViewPort;

    private static AbstractPilot principalPilot;
    private UnitViewPort principalViewPort;

    private static List<AbstractPilot> simulatorPilots;
    private static List<DummyPilot> dummyPilots;
    private List<DummyViewPort> otherViewPorts;

    private Color[] teamColors;
    private int speed;
    private String mapName = "/";
    private MapGraph mapGraphLoaded;

    public SimulatorPanel(Color[] teamColors) {
        initialization(teamColors);
        createSims(false, 1, 0); // (Robot or not, amount of players, amount of
                                 // dummies) -> only (_, 1, 0), (_, 2, 1) or (_,
                                 // 4, 0) is in use and checked!
    }

    private void initialization(Color[] teamColors) {
        this.teamColors = teamColors;
        simulatorLayout = new GroupLayout(this);
        setLayout(simulatorLayout);
        simulatorLayout.setAutoCreateGaps(true);
        simulatorLayout.setAutoCreateContainerGaps(true);
    }

    private void createSims(boolean robot, int amount, int amountOfDummies) {
        if (!robot)
            principalPilot = new SimulationPilot(0, mapGraphLoaded);
        Set<AbstractPilot> principalPilotSet = new HashSet<AbstractPilot>();
        principalPilotSet.add(principalPilot);
        principalViewPort = new UnitViewPort(principalPilotSet, teamColors);

        simulatorPilots = new ArrayList<AbstractPilot>();
        dummyPilots = new ArrayList<DummyPilot>();
        otherViewPorts = new ArrayList<DummyViewPort>();

        // Creates dummies
        for (int i = 0; i < amountOfDummies; i++)
            dummyPilots.add(new DummyPilot(i + 1));
        for (DummyPilot pilot : dummyPilots) {
            Set<DummyPilot> dummyPilotSet = new HashSet<DummyPilot>();
            dummyPilotSet.add(pilot);
            otherViewPorts.add(new DummyViewPort(dummyPilotSet, teamColors));
        }

        // Creates non-dummies
        for (int i = 0; i < amount - 1 - amountOfDummies; i++)
            simulatorPilots.add(new SimulationPilot(amountOfDummies + i + 1,
                    mapGraphLoaded));
        for (AbstractPilot pilot : simulatorPilots) {
            Set<AbstractPilot> simulatorPilotSet = new HashSet<AbstractPilot>();
            simulatorPilotSet.add(pilot);
            otherViewPorts.add(new UnitViewPort(simulatorPilotSet, teamColors));
        }

        showSims();

        resetRobots();
    }

    private void showSims() {
        removeAll();
        invalidate();

        if (mapGraphLoaded == null) {
            if (otherViewPorts.size() == 1) { // Eigen sim en teamsim zonder map
                                              // (nodig voor demo 6)
                simulatorLayout.setHorizontalGroup(simulatorLayout
                        .createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(principalViewPort)
                        .addComponent(otherViewPorts.get(0)));
                simulatorLayout.setVerticalGroup(simulatorLayout
                        .createSequentialGroup()
                        .addComponent(principalViewPort)
                        .addComponent(otherViewPorts.get(0)));
                System.out.println("[VIEW] 1 robot and 1 dummy robot, no map");
            } else { // Enkel eigen sim zonder map (default start)
                simulatorLayout.setHorizontalGroup(simulatorLayout
                        .createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(principalViewPort));
                simulatorLayout.setVerticalGroup(simulatorLayout
                        .createSequentialGroup()
                        .addComponent(principalViewPort));
                System.out.println("[VIEW] 1 robot, no map");
            }
        } else {
            Set<PilotInterface> allPilots = new HashSet<PilotInterface>(
                    simulatorPilots);
            allPilots.addAll(dummyPilots);
            allPilots.add(principalPilot);
            overallViewPort = new OverallViewPort(allPilots, mapGraphLoaded,
                    teamColors);

            if (otherViewPorts.size() == 1) { // Eigen sim en teamsim met map
                                              // (nodig voor demo 5)
                simulatorLayout.setHorizontalGroup(simulatorLayout
                        .createSequentialGroup()
                        .addComponent(overallViewPort)
                        .addGroup(
                                simulatorLayout
                                        .createParallelGroup(
                                                GroupLayout.Alignment.CENTER)
                                        .addComponent(principalViewPort)
                                        .addComponent(otherViewPorts.get(0))));
                simulatorLayout.setVerticalGroup(simulatorLayout
                        .createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(overallViewPort)
                        .addGroup(
                                simulatorLayout.createSequentialGroup()
                                        .addComponent(principalViewPort)
                                        .addComponent(otherViewPorts.get(0))));
                System.out
                        .println("[VIEW] 1 robot and 1 dummy robot, with map");
            } else if (otherViewPorts.size() == 3) { // Eigen sim en 3 andere
                                                     // sims met map
                simulatorLayout
                        .setHorizontalGroup(simulatorLayout
                                .createSequentialGroup()
                                .addComponent(overallViewPort)
                                .addGroup(
                                        simulatorLayout
                                                .createParallelGroup(
                                                        GroupLayout.Alignment.CENTER)
                                                .addGroup(
                                                        simulatorLayout
                                                                .createSequentialGroup()
                                                                .addComponent(
                                                                        principalViewPort)
                                                                .addComponent(
                                                                        otherViewPorts
                                                                                .get(0)))
                                                .addGroup(
                                                        simulatorLayout
                                                                .createSequentialGroup()
                                                                .addComponent(
                                                                        otherViewPorts
                                                                                .get(1))
                                                                .addComponent(
                                                                        otherViewPorts
                                                                                .get(2)))));
                simulatorLayout
                        .setVerticalGroup(simulatorLayout
                                .createParallelGroup(
                                        GroupLayout.Alignment.CENTER)
                                .addComponent(overallViewPort)
                                .addGroup(
                                        simulatorLayout
                                                .createSequentialGroup()
                                                .addGroup(
                                                        simulatorLayout
                                                                .createParallelGroup(
                                                                        GroupLayout.Alignment.CENTER)
                                                                .addComponent(
                                                                        principalViewPort)
                                                                .addComponent(
                                                                        otherViewPorts
                                                                                .get(0)))
                                                .addGroup(
                                                        simulatorLayout
                                                                .createParallelGroup(
                                                                        GroupLayout.Alignment.CENTER)
                                                                .addComponent(
                                                                        otherViewPorts
                                                                                .get(1))
                                                                .addComponent(
                                                                        otherViewPorts
                                                                                .get(2)))));
                System.out
                        .println("[VIEW] 1 robot and 3 simulated robots, with map");
            } else { // Enkel eigen sim met map
                simulatorLayout.setHorizontalGroup(simulatorLayout
                        .createSequentialGroup().addComponent(overallViewPort)
                        .addComponent(principalViewPort));
                simulatorLayout.setVerticalGroup(simulatorLayout
                        .createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(overallViewPort)
                        .addComponent(principalViewPort));
                System.out.println("[VIEW] 1 robot, with map");
            }
        }

        validate();
    }

    public void connect() {
        stopSimulation();
        mapName = "/";
        mapGraphLoaded = null;
        overallViewPort = null;
        principalPilot = new RobotPilot(0);
        createSims(true, 1, 0);
    }

    public void disconnect() {
        if (principalPilot instanceof RobotPilot)
            ((RobotPilot) principalPilot).endConnection();
        principalPilot = new SimulationPilot(0, mapGraphLoaded);
        stopSimulation();
        mapName = "/";
        mapGraphLoaded = null;
        overallViewPort = null;
        createSims(false, 1, 0);
    }

    public void addDummy() {
        stopSimulation();
        mapName = "/";
        mapGraphLoaded = null;
        overallViewPort = null;
        createSims(principalPilot instanceof RobotPilot, 2, 1);
    }

    public void setMapFile(File mapFile, int amount, int amountOfDummies) {
        stopSimulation();
        mapName = mapFile.getName();
        mapGraphLoaded = MapReader.createMapFromFile(mapFile);
        createSims(principalPilot instanceof RobotPilot, amount,
                amountOfDummies);
    }

    private void setOnStartTile(PilotInterface pilot) {
    	
    	for (Tile t : mapGraphLoaded.getStartTiles())
			if (t.getContent().getValue() == pilot.getPlayerNumber()){
				Point tPos = t.getPosition();
				pilot.setPosition(tPos.x*40+20,tPos.y*40+20);
			}	
	}

	public void removeMapFile() {
        stopSimulation();
        mapName = "/";
        mapGraphLoaded = null;
        overallViewPort = null;
        createSims(principalPilot instanceof RobotPilot, 1, 0);
    }

    public void resetRobots() {
        stopSimulation();

        principalPilot.setPosition(20, 20);
        if (mapGraphLoaded != null)
        	setOnStartTile(principalPilot);
        principalPilot.reset();
        principalViewPort.resetPath();

        if (mapGraphLoaded != null) {
            if (simulatorPilots.size() == 3) {
            	setOnStartTile(simulatorPilots.get(0));
            	setOnStartTile(simulatorPilots.get(1));
            	setOnStartTile(simulatorPilots.get(2));

            }
            for (AbstractPilot pilot : simulatorPilots){
            	setOnStartTile(pilot);
                pilot.reset();
            }
            for (DummyPilot pilot : dummyPilots){
            	setOnStartTile(pilot);
                pilot.reset();
            }
            for (DummyViewPort viewPort : otherViewPorts)
                if (viewPort instanceof UnitViewPort)
                    ((UnitViewPort) viewPort).resetPath();
        }

        changeSpeed(2);
    }

    public void startSimulation() {
        principalPilot.startExploring();
        for (AbstractPilot pilot : simulatorPilots)
            pilot.startExploring();
    }

    private void stopSimulation() {
        principalPilot.stopExploring();
        for (AbstractPilot pilot : simulatorPilots)
            pilot.stopExploring();
    }

    public void changeSpeedMainRobot(final int value) {
        speed = value;
        principalPilot.setSpeed(value);
    }

    public void changeSpeed(final int value) {
        speed = value;
        principalPilot.setSpeed(value);
        for (AbstractPilot pilot : simulatorPilots)
            pilot.setSpeed(value);
    }

    public AbstractPilot getPrincipalPilot() {
        return principalPilot;
    }

    public int getSpeed() {
        return speed;
    }

    public String getMapName() {
        return mapName;
    }

    public MapGraph getMapGraphLoaded() {
        return mapGraphLoaded;
    }

    public void turnLeftPrincipalPilot(double alpha) {
        principalPilot.rotate(-alpha);
    }

    public void turnRightPrincipalPilot(double alpha) {
        principalPilot.rotate(alpha);
    }

    public void travelPrincipalPilot(double distance) {
        principalPilot.travel(distance);
    }

    // Enkel bruikbaar door simulators (mapgraphLoaded en robotposities nodig)
    public static boolean robotOn(final Point2D.Double point) {
        for (PilotInterface pilot : simulatorPilots)
            if (point.equals(pilot.getPosition()))
                return true;
        for (PilotInterface pilot : dummyPilots)
            if (point.equals(pilot.getPosition()))
                return true;

        return false;
    }
}