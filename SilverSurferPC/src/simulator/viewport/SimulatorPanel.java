package simulator.viewport;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.JPanel;

import mapping.MapGraph;
import mapping.MapReader;
import simulator.pilot.AbstractPilot;
import simulator.pilot.SimulationPilot;

@SuppressWarnings("serial")
public class SimulatorPanel extends JPanel {
	
    private GroupLayout simulatorLayout;
    private AbstractPilot principalPilot;
    private UnitViewPort principalViewPort;
    private int speed;
    private String mapName = "/";
    private MapGraph mapGraphLoaded;

    public SimulatorPanel() {
        principalPilot = new SimulationPilot();
        Set<AbstractPilot> principalPilotSet = new HashSet<AbstractPilot>();
        principalPilotSet.add(principalPilot);
        principalViewPort = new UnitViewPort(principalPilotSet);
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
        System.out.println(principalPilot.getConsoleTag() + " Current Speed Level: " + value + ".");
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
    }

    public void turnLeftPrincipalPilot(double alpha) {
        principalPilot.rotate(-1 * alpha);
    }

    public void turnRightPrincipalPilot(double alpha) {
        principalPilot.rotate(alpha);
    }

    public void travelPrincipalPilot(double distance) {
        principalPilot.travel(distance);
    }
}