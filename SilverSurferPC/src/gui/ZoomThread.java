package gui;

import simulator.viewport.AbstractViewPort;

//Used for zooming in.
@SuppressWarnings("unused")
public class ZoomThread extends Thread {

    private final AbstractViewPort simulationPanel;
    private boolean ZoomIn = false;
    private final static double scaleDifferencePerZoom = 1f / 4f;

    public ZoomThread(final String str, final AbstractViewPort simulationpanel,
            final boolean ZoomIn) {
        super(str);
        simulationPanel = simulationpanel;
        this.ZoomIn = ZoomIn;
    }

    @Override
    public void run() {
        // double scale = 0;
        // if (simulationPanel.getScalingfactor() == scaleDifferencePerZoom
        // && !ZoomIn) {
        // return;
        // }
        // if (simulationPanel.getScalingfactor() == 3 && ZoomIn) {
        // return;
        // }
        // if (ZoomIn) {
        // scale = simulationPanel.getScalingfactor() + scaleDifferencePerZoom;
        // } else {
        // scale = simulationPanel.getScalingfactor() - scaleDifferencePerZoom;
        // }
        // simulationPanel.setScalingfactor(scale);
    }
}