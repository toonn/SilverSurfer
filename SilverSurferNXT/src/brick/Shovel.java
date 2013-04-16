package brick;

import lejos.nxt.Motor;

public class Shovel {

	public enum State {
		UP, DOWN, MOVING, LOADED
	}

	private final int UP_TO_DOWN = 170;
	private final int LOADED_TO_DOWN = 160;

	private State currentShovelState;

	public State getCurrentShovelState() {
		return currentShovelState;
	}

	public void setCurrentShovelState(State currentShovelState) {
		this.currentShovelState = currentShovelState;
	}

	public void lower() { //From UP to DOWN.
		if (currentShovelState == State.UP) {
			setCurrentShovelState(State.MOVING);
			Motor.C.rotate(UP_TO_DOWN);
			setCurrentShovelState(State.DOWN);
		}
	}

	public void raise() { //From DOWN to UP.
		if (currentShovelState == State.DOWN) {
			setCurrentShovelState(State.MOVING);
			Motor.C.rotate(-UP_TO_DOWN);
			setCurrentShovelState(State.UP);
		}
	}

	public void unload() { //From LOADED to DOWN.
		if (currentShovelState == State.LOADED) {
			setCurrentShovelState(State.MOVING);
			Motor.C.rotate(LOADED_TO_DOWN);
			setCurrentShovelState(State.DOWN);
		}
	}

	public void load() { //From DOWN to LOADED.
		if (currentShovelState == State.DOWN) {
			setCurrentShovelState(State.MOVING);
			Motor.C.rotate(-LOADED_TO_DOWN);
			setCurrentShovelState(State.LOADED);
		}
	}
}