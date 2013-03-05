package simulator.viewport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.JPanel;

import mapping.MapGraph;
import mapping.MapReader;
import simulator.pilot.AbstractPilot;
import simulator.pilot.PilotInterface;
import simulator.pilot.SimulationPilot;

@SuppressWarnings("serial")
public class SimulatorPanel extends JPanel {

    private GroupLayout simulatorLayout;

    private OverallViewPort overallViewPort;

    private AbstractPilot principalPilot;
    private UnitViewPort principalViewPort;

    private List<AbstractPilot> simulatorPilots;
    private List<UnitViewPort> simulatorViewPorts;

    private int speed;
    private String mapName = "/";
    private MapGraph mapGraphLoaded;

    public SimulatorPanel() {
        principalPilot = new SimulationPilot();
        principalPilot.setSimulatorPanel(this);
        Set<AbstractPilot> principalPilotSet = new HashSet<AbstractPilot>();
        principalPilotSet.add(principalPilot);
        principalViewPort = new UnitViewPort(principalPilotSet);

        simulatorPilots = new ArrayList<AbstractPilot>();
        simulatorViewPorts = new ArrayList<UnitViewPort>();

        for (int i = 0; i < 3; i++)
            simulatorPilots.add(new SimulationPilot());
        for (AbstractPilot pilot : simulatorPilots) {
            pilot.setSimulatorPanel(this);
            Set<AbstractPilot> simulatorPilotSet = new HashSet<AbstractPilot>();
            simulatorPilotSet.add(pilot);
            simulatorViewPorts.add(new UnitViewPort(simulatorPilotSet));
        }

        changeSpeed(2);

        simulatorLayout = new GroupLayout(this);
        setLayout(simulatorLayout);
        simulatorLayout.setAutoCreateGaps(true);
        simulatorLayout.setAutoCreateContainerGaps(true);
        simulatorLayout.setHorizontalGroup(simulatorLayout.createParallelGroup(
                GroupLayout.Alignment.CENTER).addComponent(principalViewPort));
        simulatorLayout.setVerticalGroup(simulatorLayout
                .createSequentialGroup().addComponent(principalViewPort));
    }

    public int getSpeed() {
        return speed;
    }

    public void changeSpeed(final int value) {
        speed = value;
        principalPilot.setSpeed(value);
        System.out.println(principalPilot.getConsoleTag()
                + " Current Speed Level: " + value + ".");
    }

    public String getMapName() {
        return mapName;
    }

    public MapGraph getMapGraphLoaded() {
        return mapGraphLoaded;
    }

    public void setMapFile(File mapFile) {
        mapName = mapFile.getName();
        mapGraphLoaded = MapReader.createMapFromFile(mapFile);

        simulatorPilots.get(0).setPosition(
                mapGraphLoaded.getMapSize().x * 40 + 20, 20);
        simulatorPilots.get(1).setPosition(20,
                mapGraphLoaded.getMapSize().y * 40 + 20);
        simulatorPilots.get(2).setPosition(
                mapGraphLoaded.getMapSize().x * 40 + 20,
                mapGraphLoaded.getMapSize().y * 40 + 20);
        for (AbstractPilot pilot : simulatorPilots)
            pilot.reset();
        for (UnitViewPort viewPort : simulatorViewPorts)
            viewPort.resetPath();

        Set<PilotInterface> allPilots = new HashSet<PilotInterface>(simulatorPilots);
        allPilots.add(principalPilot);
        overallViewPort = new OverallViewPort(allPilots, mapGraphLoaded);

        simulatorLayout = new GroupLayout(this);
        setLayout(simulatorLayout);
        simulatorLayout.setAutoCreateGaps(true);
        simulatorLayout.setAutoCreateContainerGaps(true);
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
                                                                simulatorViewPorts
                                                                        .get(0)))
                                        .addGroup(
                                                simulatorLayout
                                                        .createSequentialGroup()
                                                        .addComponent(
                                                                simulatorViewPorts
                                                                        .get(1))
                                                        .addComponent(
                                                                simulatorViewPorts
                                                                        .get(2)))));
        simulatorLayout
                .setVerticalGroup(simulatorLayout
                        .createParallelGroup(GroupLayout.Alignment.CENTER)
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
                                                                simulatorViewPorts
                                                                        .get(0)))
                                        .addGroup(
                                                simulatorLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.CENTER)
                                                        .addComponent(
                                                                simulatorViewPorts
                                                                        .get(1))
                                                        .addComponent(
                                                                simulatorViewPorts
                                                                        .get(2)))));
        repaint();
    }

    public void turnLeftPrincipalPilot(double alpha) {
        // MoveTurnThread MTT = new MoveTurnThread("MTT", principalPilot, 0,
        // (int)(-1*alpha));
        // MTT.start();
        principalPilot.rotate(-alpha);
    }

    public void turnRightPrincipalPilot(double alpha) {
        // MoveTurnThread MTT = new MoveTurnThread("MTT", principalPilot, 0,
        // (int)alpha);
        // MTT.start();
        principalPilot.rotate(alpha);

    }

    public void travelPrincipalPilot(double distance) {
        // MoveTurnThread MTT = new MoveTurnThread("MTT", principalPilot,
        // (int)distance, 0);
        // MTT.start();
        principalPilot.travel(distance);
    }

    public void startSimulation() {
        principalPilot.startExploring();
        for (AbstractPilot pilot : simulatorPilots)
            pilot.startExploring();
    }
}