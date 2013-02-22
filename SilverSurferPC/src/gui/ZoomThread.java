package gui;

import simulator.viewport.ViewPort;

//Used for zooming in.
public class ZoomThread extends Thread {

    private final ViewPort simulationPanel;
    private boolean ZoomIn = false;
    private final static double scaleDifferencePerZoom = 1f / 4f;

    public ZoomThread(final String str, final ViewPort simulationpanel,
            final boolean ZoomIn) {
        super(str);
        simulationPanel = simulationpanel;
        this.ZoomIn = ZoomIn;
    }

    @Override
    public void run() {
        double scale = 0;
        if (simulationPanel.getScalingfactor() == scaleDifferencePerZoom
                && !ZoomIn) {
            return;
        }
        if (simulationPanel.getScalingfactor() == 3 && ZoomIn) {
            return;
        }
        if (ZoomIn) {
            scale = simulationPanel.getScalingfactor() + scaleDifferencePerZoom;
        } else {
            scale = simulationPanel.getScalingfactor() - scaleDifferencePerZoom;
        }
        simulationPanel.setScalingfactor(scale);
    }
}