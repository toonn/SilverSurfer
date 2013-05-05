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
import mapping.StartBase;
import mapping.Tile;
import mazeAlgorithm.CollisionAvoidedException;
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
    private UnitViewPort principalViewPort;
    private DummyViewPort principalDummyViewPort;
    private static List<SimulationPilot> simulatorPilots;
    private List<UnitViewPort> simulatorViewPorts;
    private List<DummyViewPort> simulatorDummyViewPorts;
    private Color[] teamColors;
    private int speed;
    private String mapName = "/";
    private String view = "/";
    private MapGraph mapGraphLoaded;
    private Point mapGraphLoadedSize;
    private boolean realRobot = false;
    private boolean showingDummies = true;
    private boolean showingMap = true;

    public SimulatorPanel(Color[] teamColors) {
        initialization(teamColors);
        createSims(1); 
    }

    private void initialization(Color[] teamColors) {
        this.teamColors = teamColors;
        simulatorLayout = new GroupLayout(this);
        setLayout(simulatorLayout);
        simulatorLayout.setAutoCreateGaps(true);
        simulatorLayout.setAutoCreateContainerGaps(true);
    }

    private void createSims(int amount) {
        if (!realRobot)
            principalPilot = new SimulationPilot(0, mapGraphLoaded, mapGraphLoadedSize);
        Set<AbstractPilot> principalPilotSet = new HashSet<AbstractPilot>();
        principalPilotSet.add(principalPilot);
        principalViewPort = new UnitViewPort(principalPilotSet, teamColors, teamColors[principalPilot.getPlayerNumber()]);
        Set<DummyPilot> principalDummyPilotSet = new HashSet<DummyPilot>();
        principalDummyPilotSet.add(principalPilot.getTeamPilot());
        principalDummyViewPort = new DummyViewPort(principalDummyPilotSet, teamColors, teamColors[principalPilot.getPlayerNumber()]);

        simulatorPilots = new ArrayList<SimulationPilot>();
        simulatorViewPorts = new ArrayList<UnitViewPort>();
        simulatorDummyViewPorts = new ArrayList<DummyViewPort>();

        for (int i = 1; i < amount; i++)
            simulatorPilots.add(new SimulationPilot(i, mapGraphLoaded, mapGraphLoadedSize));
        for (SimulationPilot pilot : simulatorPilots) {
            Set<AbstractPilot> simulatorPilotSet = new HashSet<AbstractPilot>();
            simulatorPilotSet.add(pilot);
            simulatorViewPorts.add(new UnitViewPort(simulatorPilotSet, teamColors, teamColors[pilot.getPlayerNumber()]));
            Set<DummyPilot> simulatorDummyPilotSet = new HashSet<DummyPilot>();
            simulatorDummyPilotSet.add(pilot.getTeamPilot());
            simulatorDummyViewPorts.add(new DummyViewPort(simulatorDummyPilotSet, teamColors, teamColors[pilot.getPlayerNumber()]));
        }

        showSims(showingDummies, showingMap);

        resetRobots();
    }
    
    public void toggleAll(boolean show) {
    	if(show)
    		showSims(true, true);
    	else
    		showSims(false, false);    		
    }
    
    public void toggleTeamPilots() {
    	showSims(!showingDummies, showingMap);
    }
    
    public void toggleOverallMap() {
    	showSims(showingDummies, !showingMap);
    }

    private void showSims(boolean showDummies, boolean showMap) {
        removeAll();
        invalidate();

        if (mapGraphLoaded == null) { // 1 robot
            showOnePlayer(showDummies, false);
            view = "1 robot";
        } else {
            Set<PilotInterface> allPilots = new HashSet<PilotInterface>(
                    simulatorPilots);
            allPilots.add(principalPilot);
            overallViewPort = new OverallViewPort(allPilots, mapGraphLoaded,
                    teamColors);

            if (simulatorViewPorts.size() == 3) { // 4 robots with map
                showFourPlayers(showDummies, showMap);
                view = "4 robots + map";
            } else if (simulatorViewPorts.size() == 2) { // 3 robots with map
                showThreePlayers(showDummies, showMap);
                view = "3 robots + map";
            } else if (simulatorViewPorts.size() == 1) { // 2 robots with map
                showTwoPlayers(showDummies, showMap);
                view = "2 robots + map";
            } else { // 1 robot with map
                showOnePlayer(showDummies, showMap);
                view = "1 robot + map";
            }
        }

        validate();

        showingDummies = showDummies;
        showingMap = showMap;
    }
    
    private void showOnePlayer(boolean showDummies, boolean showMap) {
    	if(showMap) {
        	if(showDummies) {
        		simulatorLayout.setHorizontalGroup(simulatorLayout.createSequentialGroup()
                        .addComponent(overallViewPort)
                        .addComponent(principalViewPort)
                        .addComponent(principalDummyViewPort));
        		simulatorLayout.setVerticalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(overallViewPort)
                        .addComponent(principalViewPort)
                        .addComponent(principalDummyViewPort));
        	} else {
        		simulatorLayout.setHorizontalGroup(simulatorLayout.createSequentialGroup()
                        .addComponent(overallViewPort)
        				.addComponent(principalViewPort));
        		simulatorLayout.setVerticalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(overallViewPort)
        				.addComponent(principalViewPort));
        	}
    	} else {
        	if(showDummies) {
        		simulatorLayout.setHorizontalGroup(simulatorLayout.createSequentialGroup()
        				.addComponent(principalViewPort)
        				.addComponent(principalDummyViewPort));
        		simulatorLayout.setVerticalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
        				.addComponent(principalViewPort)
        				.addComponent(principalDummyViewPort));
        	} else {
        		simulatorLayout.setHorizontalGroup(simulatorLayout
        				.createParallelGroup(GroupLayout.Alignment.CENTER)
        				.addComponent(principalViewPort));
        		simulatorLayout.setVerticalGroup(simulatorLayout
        				.createSequentialGroup()
        				.addComponent(principalViewPort));
        	}
    	}
    }
    
    private void showTwoPlayers(boolean showDummies, boolean showMap) {
    	if(showMap) {
        	if(showDummies) {
            	simulatorLayout.setHorizontalGroup(simulatorLayout.createSequentialGroup()
                        .addComponent(overallViewPort)
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(principalViewPort)
                        		.addComponent(simulatorViewPorts.get(0)))
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(principalDummyViewPort)
                        		.addComponent(simulatorDummyViewPorts.get(0))));
            	simulatorLayout.setVerticalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(overallViewPort)
                        .addGroup(simulatorLayout.createSequentialGroup()
                       			.addComponent(principalViewPort)
                       			.addComponent(simulatorViewPorts.get(0)))
                   		.addGroup(simulatorLayout.createSequentialGroup()
                   				.addComponent(principalDummyViewPort)
                   				.addComponent(simulatorDummyViewPorts.get(0))));
        	} else {
            	simulatorLayout.setHorizontalGroup(simulatorLayout.createSequentialGroup()
                        .addComponent(overallViewPort)
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(principalViewPort)
                        		.addComponent(simulatorViewPorts.get(0))));
            	simulatorLayout.setVerticalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(overallViewPort)
                        .addGroup(simulatorLayout.createSequentialGroup()
                        		.addComponent(principalViewPort)
                        		.addComponent(simulatorViewPorts.get(0))));
        	}
    	} else {
        	if(showDummies) {
            	simulatorLayout.setHorizontalGroup(simulatorLayout.createSequentialGroup()
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(principalViewPort)
                        		.addComponent(simulatorViewPorts.get(0)))
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(principalDummyViewPort)
                        		.addComponent(simulatorDummyViewPorts.get(0))));
            	simulatorLayout.setVerticalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(simulatorLayout.createSequentialGroup()
                       			.addComponent(principalViewPort)
                       			.addComponent(simulatorViewPorts.get(0)))
                   		.addGroup(simulatorLayout.createSequentialGroup()
                   				.addComponent(principalDummyViewPort)
                   				.addComponent(simulatorDummyViewPorts.get(0))));
        	} else {
            	simulatorLayout.setHorizontalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
            			.addComponent(principalViewPort)
            			.addComponent(simulatorViewPorts.get(0)));
            	simulatorLayout.setVerticalGroup(simulatorLayout.createSequentialGroup()
            			.addComponent(principalViewPort)
            			.addComponent(simulatorViewPorts.get(0)));
        	}
    	}
    }
    
    private void showThreePlayers(boolean showDummies, boolean showMap) {
    	if(showMap) {
        	if(showDummies) {
            	simulatorLayout.setHorizontalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(simulatorLayout.createSequentialGroup()
                        		.addComponent(overallViewPort)
                        		.addComponent(principalViewPort)
                        		.addComponent(principalDummyViewPort))
                        .addGroup(simulatorLayout.createSequentialGroup()
                        		.addComponent(simulatorViewPorts.get(0))
                        		.addComponent(simulatorDummyViewPorts.get(0))
                        		.addComponent(simulatorViewPorts.get(1))
                        		.addComponent(simulatorDummyViewPorts.get(1))));
            	simulatorLayout.setVerticalGroup(simulatorLayout.createSequentialGroup()
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(overallViewPort)
                        		.addComponent(principalViewPort)
                        		.addComponent(principalDummyViewPort))
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(simulatorViewPorts.get(0))
                        		.addComponent(simulatorDummyViewPorts.get(0))
                        		.addComponent(simulatorViewPorts.get(1))
                        		.addComponent(simulatorDummyViewPorts.get(1))));
        	} else {
            	simulatorLayout.setHorizontalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(simulatorLayout.createSequentialGroup()
                        		.addComponent(overallViewPort)
                        		.addComponent(principalViewPort))
                        .addGroup(simulatorLayout.createSequentialGroup()
                        		.addComponent(simulatorViewPorts.get(0))
                        		.addComponent(simulatorViewPorts.get(1))));
            	simulatorLayout.setVerticalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
            			.addGroup(simulatorLayout.createSequentialGroup()
            					.addComponent(overallViewPort)
                        		.addComponent(simulatorViewPorts.get(0)))
                        .addGroup(simulatorLayout.createSequentialGroup()
            					.addComponent(principalViewPort)
                        		.addComponent(simulatorViewPorts.get(1))));
        	}
    	} else {
        	if(showDummies) {
            	simulatorLayout.setHorizontalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(simulatorLayout.createSequentialGroup()
                        		.addComponent(principalViewPort)
                        		.addComponent(principalDummyViewPort))
                        .addGroup(simulatorLayout.createSequentialGroup()
                        		.addComponent(simulatorViewPorts.get(0))
                        		.addComponent(simulatorDummyViewPorts.get(0))
                        		.addComponent(simulatorViewPorts.get(1))
                        		.addComponent(simulatorDummyViewPorts.get(1))));
            	simulatorLayout.setVerticalGroup(simulatorLayout.createSequentialGroup()
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(principalViewPort)
                        		.addComponent(principalDummyViewPort))
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(simulatorViewPorts.get(0))
                        		.addComponent(simulatorDummyViewPorts.get(0))
                        		.addComponent(simulatorViewPorts.get(1))
                        		.addComponent(simulatorDummyViewPorts.get(1))));
        	} else {
            	simulatorLayout.setHorizontalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
            			.addComponent(principalViewPort)
                        .addGroup(simulatorLayout.createSequentialGroup()
                        		.addComponent(simulatorViewPorts.get(0))
                        		.addComponent(simulatorViewPorts.get(1))));
            	simulatorLayout.setVerticalGroup(simulatorLayout.createSequentialGroup()
            			.addComponent(principalViewPort)
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(simulatorViewPorts.get(0))
                        		.addComponent(simulatorViewPorts.get(1))));
        	}
    	}
    }
    
    private void showFourPlayers(boolean showDummies, boolean showMap) {
    	if(showMap) {
        	if(showDummies) {
            	simulatorLayout.setHorizontalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(simulatorLayout.createSequentialGroup()
                                .addComponent(overallViewPort)
                        		.addComponent(principalViewPort)
                        		.addComponent(principalDummyViewPort)
                        		.addComponent(simulatorViewPorts.get(0))
                        		.addComponent(simulatorDummyViewPorts.get(0)))
                        .addGroup(simulatorLayout.createSequentialGroup()
                        		.addComponent(simulatorViewPorts.get(1))
                        		.addComponent(simulatorDummyViewPorts.get(1))
                        		.addComponent(simulatorViewPorts.get(2))
                        		.addComponent(simulatorDummyViewPorts.get(2))));
            	simulatorLayout.setVerticalGroup(simulatorLayout.createSequentialGroup()
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(overallViewPort)
                        		.addComponent(principalViewPort)
                        		.addComponent(principalDummyViewPort)
                        		.addComponent(simulatorViewPorts.get(0))
                        		.addComponent(simulatorDummyViewPorts.get(0)))
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(simulatorViewPorts.get(1))
                        		.addComponent(simulatorDummyViewPorts.get(1))
                        		.addComponent(simulatorViewPorts.get(2))
                        		.addComponent(simulatorDummyViewPorts.get(2))));
        	} else {
            	simulatorLayout.setHorizontalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(simulatorLayout.createSequentialGroup()
                                .addComponent(overallViewPort)
                        		.addComponent(principalViewPort)
                        		.addComponent(simulatorViewPorts.get(0)))
                        .addGroup(simulatorLayout.createSequentialGroup()
                        		.addComponent(simulatorViewPorts.get(1))
                        		.addComponent(simulatorViewPorts.get(2))));
            	simulatorLayout.setVerticalGroup(simulatorLayout.createSequentialGroup()
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(overallViewPort)
                        		.addComponent(principalViewPort)
                        		.addComponent(simulatorViewPorts.get(0)))
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(simulatorViewPorts.get(1))
                        		.addComponent(simulatorViewPorts.get(2))));
        	}
    	} else {
        	if(showDummies) {
            	simulatorLayout.setHorizontalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(simulatorLayout.createSequentialGroup()
                        		.addComponent(principalViewPort)
                        		.addComponent(principalDummyViewPort)
                        		.addComponent(simulatorViewPorts.get(0))
                        		.addComponent(simulatorDummyViewPorts.get(0)))
                        .addGroup(simulatorLayout.createSequentialGroup()
                        		.addComponent(simulatorViewPorts.get(1))
                        		.addComponent(simulatorDummyViewPorts.get(1))
                        		.addComponent(simulatorViewPorts.get(2))
                        		.addComponent(simulatorDummyViewPorts.get(2))));
            	simulatorLayout.setVerticalGroup(simulatorLayout.createSequentialGroup()
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(principalViewPort)
                        		.addComponent(principalDummyViewPort)
                        		.addComponent(simulatorViewPorts.get(0))
                        		.addComponent(simulatorDummyViewPorts.get(0)))
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(simulatorViewPorts.get(1))
                        		.addComponent(simulatorDummyViewPorts.get(1))
                        		.addComponent(simulatorViewPorts.get(2))
                        		.addComponent(simulatorDummyViewPorts.get(2))));
        	} else {
            	simulatorLayout.setHorizontalGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(simulatorLayout.createSequentialGroup()
                        		.addComponent(principalViewPort)
                        		.addComponent(simulatorViewPorts.get(0)))
                        .addGroup(simulatorLayout.createSequentialGroup()
                        		.addComponent(simulatorViewPorts.get(1))
                        		.addComponent(simulatorViewPorts.get(2))));
            	simulatorLayout.setVerticalGroup(simulatorLayout.createSequentialGroup()
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(principalViewPort)
                        		.addComponent(simulatorViewPorts.get(0)))
                        .addGroup(simulatorLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        		.addComponent(simulatorViewPorts.get(1))
                        		.addComponent(simulatorViewPorts.get(2))));
        	}
    	}
    }

    public void resetRobots() {
        stopSimulation();

        principalPilot.reset();
        principalPilot.setPosition(20, 20);
        if (mapGraphLoaded != null)
            setOnStartTile(principalPilot);
        principalPilot.reset();
        principalViewPort.resetPath();

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

    public void setMapFile(File mapFile, int amount) {
        stopSimulation();
        mapName = mapFile.getName();
        mapGraphLoaded = MapReader.createMapFromFile(mapFile);
        mapGraphLoadedSize = mapGraphLoaded.getMapSize();
        createSims(amount);
    }

    public void removeMapFile() {
        stopSimulation();
        mapName = "/";
        mapGraphLoaded = null;
        mapGraphLoadedSize = null;
        overallViewPort = null;
        createSims(1);
    }

    public void connect() {
        stopSimulation();
        mapName = "/";
        mapGraphLoaded = null;
        mapGraphLoadedSize = null;
        overallViewPort = null;
        principalPilot = new RobotPilot(0, mapGraphLoadedSize);
        realRobot = true;
        createSims(1);
    }

    public void disconnect() {
        if (principalPilot instanceof RobotPilot)
            ((RobotPilot) principalPilot).endConnection();
        principalPilot = new SimulationPilot(0, mapGraphLoaded, mapGraphLoadedSize);
        realRobot = false;
        stopSimulation();
        mapName = "/";
        mapGraphLoaded = null;
        mapGraphLoadedSize = null;
        overallViewPort = null;
        createSims(1);
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
        setColors();
    }

    private void setOnStartTile(AbstractPilot pilot) {
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
    
    private void setColors() {
    	principalViewPort.setMainColor(teamColors[principalPilot.getPlayerNumber()]);
    	principalDummyViewPort.setMainColor(teamColors[principalPilot.getPlayerNumber()]);
    	for(int i = 0; i < simulatorViewPorts.size(); i++) {
    		simulatorViewPorts.get(i).setMainColor(teamColors[simulatorPilots.get(i).getPlayerNumber()]);
    		simulatorDummyViewPorts.get(i).setMainColor(teamColors[simulatorPilots.get(i).getPlayerNumber()]);
    	}
    }
    
    public void resetAllPaths() {
        principalViewPort.resetPath();
        for (UnitViewPort viewPort : simulatorViewPorts)
        	viewPort.resetPath();
    }

    public void playGame() {
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
    	try {
    		principalPilot.travel(distance, true);
    	} catch(CollisionAvoidedException e) {
    		//Nothing to see here
    	}
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
    
    public static Set<Point2D> getAllRobotPositions() {
        // TODO All robot positions via htttp?
        Set<Point2D> allRobotPositions = new HashSet<Point2D>();
        for (SimulationPilot pilot : simulatorPilots) {
            allRobotPositions.add(pilot.getMatrixPosition());
        }
        allRobotPositions.add(principalPilot.getMatrixPosition());

        return allRobotPositions;
    }  
}