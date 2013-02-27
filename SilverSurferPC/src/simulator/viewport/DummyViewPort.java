package simulator.viewport;

import simulator.pilot.AbstractPilot;

public class DummyViewPort extends AbstractViewPort {
    public DummyViewPort(final AbstractPilot pilot) {
        super();
        pilots.add(pilot);
    }
}
