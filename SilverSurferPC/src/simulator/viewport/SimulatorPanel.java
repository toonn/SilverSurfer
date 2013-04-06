package simulator.viewport;

import java.awt.Color;
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
import mapping.StartBase;
import mapping.Tile;
import simulator.pilot.AbstractPilot;
import simulator.pilot.DummyPilot;
import simulator.pilot.PilotInterface;
import simulator.pilot.RobotPilot;
import simulator.pilot.SimulationPilot;

@SuppressWarnings("serial")
public class SimulatorPanel extends JPanel {

    private GroupLayout simulatorLayout;
    private OverallViewPort overallViewPort;
    private static AbstractPilot principalPilot;
    private UnitViewPort principalUnitViewPort;
    private DummyViewPort principalDummyViewPort;
    private static List<SimulationPilot> simulatorPilots;
    private List<UnitViewPort> simulatorViewPorts;
    private List<DummyViewPort> simulatorDummyViewPorts;
    private Color[] teamColors;
    private int speed;
    private String mapName = "/";
    private String view = "/";
    private MapGraph mapGraphLoaded;

    public SimulatorPanel(Color[] teamColors) {
        initialization(teamColors);
        //(Robot or not, amount of players, show dummies)
        //Only (_, 1, true), (_, 2, true) or (_, 4, false) is in use and checked!
        createSims(false, 1, true); 
    }

    private void initialization(Color[] teamColors) {
        this.teamColors = teamColors;
        simulatorLayout = new GroupLayout(this);
        setLayout(simulatorLayout);
        simulatorLayout.setAutoCreateGaps(true);
        simulatorLayout.setAutoCreateContainerGaps(true);
    }

    private void createSims(boolean robot, int amount, boolean showDummies) {
        if (!robot)
            principalPilot = new SimulationPilot(0, mapGraphLoaded);
        Set<AbstractPilot> principalPilotSet = new HashSet<AbstractPilot>();
        principalPilotSet.add(principalPilot);
        principalUnitViewPort = new UnitViewPort(principalPilotSet, teamColors);
        Set<DummyPilot> principalDummyPilotSet = new HashSet<DummyPilot>();
        principalDummyPilotSet.add(principalPilot.getTeamPilot());
        principalDummyViewPort = new DummyViewPort(principalDummyPilotSet, teamColors);

        simulatorPilots = new ArrayList<SimulationPilot>();
        simulatorViewPorts = new ArrayList<UnitViewPort>();
        simulatorDummyViewPorts = new ArrayList<DummyViewPort>();

        for (int i = 1; i < amount; i++)
            simulatorPilots.add(new SimulationPilot(i, mapGraphLoaded));
        for (SimulationPilot pilot : simulatorPilots) {
            Set<AbstractPilot> simulatorPilotSet = new HashSet<AbstractPilot>();
            simulatorPilotSet.add(pilot);
            simulatorViewPorts.add(new UnitViewPort(simulatorPilotSet, teamColors));
            Set<DummyPilot> simulatorDummyPilotSet = new HashSet<DummyPilot>();
            simulatorDummyPilotSet.add(pilot.getTeamPilot());
            simulatorDummyViewPorts.add(new DummyViewPort(simulatorDummyPilotSet, teamColors));
        }

        showSims(showDummies);

        resetRobots();
    }

    private void showSims(boolean showDummies) {
        removeAll();
        invalidate();

        if (mapGraphLoaded == null) { //1 robot
        	showOnePlayer(showDummies, false);
        	if(showDummies)
        		view = "1 robot + dummy";
        	else
        		view = "1 robot";
        } else {
            Set<AbstractPilot> allPilots = new HashSet<AbstractPilot>(simulatorPilots);
            allPilots.add(principalPilot);
            overallViewPort = new OverallViewPort(allPilots, mapGraphLoaded, teamColors);

            if(simulatorViewPorts.size() == 3) { //4 robots without dummies
                showFourPlayers(showDummies);
                view = "4 robots + map";
            } else if(simulatorViewPorts.size() == 1) { //2 robots with dummies
                showTwoPlayers(showDummies);
                view = "2 robots + dummies + map";
            } else { //1 robot
            	showOnePlayer(showDummies, true);
            	if(showDummies)
            		view = "1 robot + dummy + map";
            	else
            		view = "1 robot + map";
            }
        }

        validate();
    }
    
    private void showOnePlayer(boolean showDummies, boolean showMap) {
    	if(showMap) {
        	if(showDummies) {
        		simulatorLayout.setHorizontalGroup(simulatorLayout.createSequentialGroup()
                        .addComponent(overallViewPort)
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(principalUnitViewPort)
                        		.addComponent(principalDummyViewPort)));
        		simulatorLayout.setVerticalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(overallViewPort)
                        .addGroup(simulatorLayout.createSequentialGroup()
                        		.addComponent(principalUnitViewPort)
                        		.addComponent(principalDummyViewPort)));
        	} else {
        		simulatorLayout.setHorizontalGroup(simulatorLayout
        				.createParallelGroup(GroupLayout.Alignment.CENTER)
        				.addComponent(principalUnitViewPort));
        		simulatorLayout.setVerticalGroup(simulatorLayout
        				.createSequentialGroup()
        				.addComponent(principalUnitViewPort));
        	}
    	} else {
        	if(showDummies) {
        		simulatorLayout.setHorizontalGroup(simulatorLayout
        				.createParallelGroup(GroupLayout.Alignment.CENTER)
        				.addComponent(principalUnitViewPort)
        				.addComponent(principalDummyViewPort));
        		simulatorLayout.setVerticalGroup(simulatorLayout
        				.createSequentialGroup()
        				.addComponent(principalUnitViewPort)
        				.addComponent(principalDummyViewPort));
        	} else {
        		simulatorLayout.setHorizontalGroup(simulatorLayout
        				.createParallelGroup(GroupLayout.Alignment.CENTER)
        				.addComponent(principalUnitViewPort));
        		simulatorLayout.setVerticalGroup(simulatorLayout
        				.createSequentialGroup()
        				.addComponent(principalUnitViewPort));
        	}
    	}
    }
    
    private void showTwoPlayers(boolean showDummies) {
    	simulatorLayout.setHorizontalGroup(simulatorLayout.createSequentialGroup()
                .addComponent(overallViewPort)
                .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                		.addGroup(simulatorLayout.createSequentialGroup()
                				.addComponent(principalUnitViewPort)
                				.addComponent(principalDummyViewPort))
                		.addGroup(simulatorLayout.createSequentialGroup()
                				.addComponent(simulatorViewPorts.get(0))
                				.addComponent(simulatorDummyViewPorts.get(0)))));
    	simulatorLayout.setVerticalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(overallViewPort)
                .addGroup(simulatorLayout.createSequentialGroup()
                		.addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                				.addComponent(principalUnitViewPort)
                				.addComponent(principalDummyViewPort))
                		.addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                				.addComponent(simulatorViewPorts.get(0))
                				.addComponent(simulatorDummyViewPorts.get(0)))));
    }
    
    private void showFourPlayers(boolean showDummies) {
    	simulatorLayout.setHorizontalGroup(simulatorLayout.createSequentialGroup()
                .addComponent(overallViewPort)
                .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                		.addGroup(simulatorLayout.createSequentialGroup()
                				.addComponent(principalUnitViewPort)
                				.addComponent(simulatorViewPorts.get(0)))
                		.addGroup(simulatorLayout.createSequentialGroup()
                				.addComponent(simulatorViewPorts.get(1))
                				.addComponent(simulatorViewPorts.get(2)))));
    	simulatorLayout.setVerticalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(overallViewPort)
                .addGroup(simulatorLayout.createSequentialGroup()
                		.addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                				.addComponent(principalUnitViewPort)
                				.addComponent(simulatorViewPorts.get(0)))
                		.addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                				.addComponent(simulatorViewPorts.get(1))
                				.addComponent(simulatorViewPorts.get(2)))));
    }

    public void resetRobots() {
        stopSimulation();

        principalPilot.reset();
        principalPilot.setPosition(20, 20);
        if (mapGraphLoaded != null)
            setOnStartTile(principalPilot);
        principalPilot.reset();
        principalUnitViewPort.resetPath();

        if (mapGraphLoaded != null) {
            for (SimulationPilot pilot : simulatorPilots) {
                pilot.reset();
                setOnStartTile(pilot);
                pilot.reset();
            }
            for (UnitViewPort viewPort : simulatorViewPorts)
            	viewPort.resetPath();
        }

        if (mapGraphLoaded != null) {
            ; // TODO: reset wip
        }

        changeSpeed(2);
    }

    public void setMapFile(File mapFile, int amount, boolean showDummies) {
        stopSimulation();
        mapName = mapFile.getName();
        mapGraphLoaded = MapReader.createMapFromFile(mapFile);
        createSims(principalPilot instanceof RobotPilot, amount, showDummies);
    }

    public void removeMapFile() {
        stopSimulation();
        mapName = "/";
        mapGraphLoaded = null;
        overallViewPort = null;
        createSims(principalPilot instanceof RobotPilot, 1, true);
    }

    public void connect() {
        stopSimulation();
        mapName = "/";
        mapGraphLoaded = null;
        overallViewPort = null;
        principalPilot = new RobotPilot(0);
        createSims(true, 1, true);
    }

    public void disconnect() {
        if (principalPilot instanceof RobotPilot)
            ((RobotPilot) principalPilot).endConnection();
        principalPilot = new SimulationPilot(0, mapGraphLoaded);
        stopSimulation();
        mapName = "/";
        mapGraphLoaded = null;
        overallViewPort = null;
        createSims(false, 1, true);
    }

    public void changeSpeed(final int value) {
        speed = value;
        principalPilot.setSpeed(value);
        for (SimulationPilot pilot : simulatorPilots) {
            pilot.setSpeed(value);
        }
    }

    public void changeSpeedMainRobot(final int value) {
        speed = value;
        principalPilot.setSpeed(value);
    }

    public void makeReadyToPlay(AbstractPilot pilot) {
        setOnStartTile(pilot);
        pilot.makeReadyToPlay();
        pilot.getTeamPilot().makeReadyToPlay();
    }

    public void setOnStartTile(AbstractPilot pilot) {
    	if(mapGraphLoaded != null)
            for (Tile tile : mapGraphLoaded.getStartTiles()) {
                if (tile.getContent().getValue() == pilot.getPlayerNumber()) {
                    pilot.setPosition(tile.getPosition().x * 40 + 20,
                            tile.getPosition().y * 40 + 20);
                    pilot.setAngle(((StartBase) tile.getContent()).getOrientation()
                            .getAngle());
                }
            }
    }
    
    public void resetAllPaths() {
        principalUnitViewPort.resetPath();
        for (UnitViewPort viewPort : simulatorViewPorts)
        	viewPort.resetPath();
    }

    public void playGame() {
        // Set game modus on x set up for game
        for (SimulationPilot pilot : simulatorPilots) {
            pilot.setGameModus(true);
            pilot.setupForGame(this);
        }
        principalPilot.setGameModus(true);
        principalPilot.setupForGame(this);
    }

    public AbstractPilot getPrincipalPilot() {
        return principalPilot;
    }

    public MapGraph getMapGraphLoaded() {
        return mapGraphLoaded;
    }

    public String getMapName() {
        return mapName;
    }
    
    public String getView() {
    	return view;
    }

    public int getSpeed() {
        return speed;
    }

    public void startSimulation() {
        principalPilot.startExploring();
        for (AbstractPilot pilot : simulatorPilots) {
            pilot.startExploring();
        }
    }

    private void stopSimulation() {
        principalPilot.stopExploring();
        for (AbstractPilot pilot : simulatorPilots) {
            pilot.stopExploring();
        }
    }

    public void travelPrincipalPilot(double distance) {
        principalPilot.travel(distance);
    }

    public void turnLeftPrincipalPilot(double alpha) {
        principalPilot.rotate(-alpha);
    }

    public void turnRightPrincipalPilot(double alpha) {
        principalPilot.rotate(alpha);
    }
    
    // Enkel bruikbaar door simulators (mapgraphLoaded en robotposities nodig)
    public static boolean robotOn(final Point2D.Double point) {
        for (PilotInterface pilot : simulatorPilots)
            if (point.equals(pilot.getPosition()))
                return true;
        return false;
    }
}