package simulator.viewport;

import gui.MoveTurnThread;

import java.io.File;
import java.util.HashSet;
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
    private AbstractPilot principalPilot;
    private UnitViewPort principalViewPort;
    private OverallViewPort overallViewPort;
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

    public SimulatorPanel(File mapFile) {
        mapName = mapFile.getName();
        mapGraphLoaded = MapReader.createMapFromFile(mapFile);
        
        principalPilot = new SimulationPilot();
        Set<AbstractPilot> principalPilotSet = new HashSet<AbstractPilot>();
        principalPilotSet.add(principalPilot);
        principalViewPort = new UnitViewPort(principalPilotSet);
        overallViewPort = new OverallViewPort(new HashSet<PilotInterface>(), mapGraphLoaded);
        changeSpeed(2);
        
        simulatorLayout = new GroupLayout(this);
        setLayout(simulatorLayout);
        simulatorLayout.setAutoCreateGaps(true);
        simulatorLayout.setAutoCreateContainerGaps(true);
        simulatorLayout.setHorizontalGroup(simulatorLayout.createParallelGroup(
                GroupLayout.Alignment.CENTER).addComponent(principalViewPort).addComponent(overallViewPort));
        simulatorLayout.setVerticalGroup(simulatorLayout
                .createSequentialGroup().addComponent(principalViewPort).addComponent(overallViewPort));
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
        
        overallViewPort = new OverallViewPort(new HashSet<PilotInterface>(), mapGraphLoaded);
        
        simulatorLayout = new GroupLayout(this);
        setLayout(simulatorLayout);
        simulatorLayout.setAutoCreateGaps(true);
        simulatorLayout.setAutoCreateContainerGaps(true);
        simulatorLayout.setHorizontalGroup(simulatorLayout.createParallelGroup(
                GroupLayout.Alignment.CENTER).addComponent(principalViewPort).addComponent(overallViewPort));
        simulatorLayout.setVerticalGroup(simulatorLayout
                .createSequentialGroup().addComponent(principalViewPort).addComponent(overallViewPort));
    }

    public void turnLeftPrincipalPilot(double alpha) {
    	//MoveTurnThread MTT = new MoveTurnThread("MTT", principalPilot, 0, (int)(-1*alpha));
    	//MTT.start();
    	for(int i = 0; i < alpha; i++)
    		principalPilot.rotate(-1);
    }

    public void turnRightPrincipalPilot(double alpha) {
    	//MoveTurnThread MTT = new MoveTurnThread("MTT", principalPilot, 0, (int)alpha);
    	//MTT.start();
    	for(int i = 0; i < alpha; i++)
    		principalPilot.rotate(1);
    	
    }

    public void travelPrincipalPilot(double distance) {
    	//MoveTurnThread MTT = new MoveTurnThread("MTT", principalPilot, (int)distance, 0);
    	//MTT.start();
    	for(int i = 0; i < distance; i++)
    		principalPilot.travel(1);
    }
}