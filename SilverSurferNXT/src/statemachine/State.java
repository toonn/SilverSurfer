package statemachine;
public class State {

	protected int NORMAL_SPEED = 180;
	protected int SLOW_SPEED = NORMAL_SPEED / 2;
	protected int HIGH_SPEED = NORMAL_SPEED * 2;
	
	public State ForwardPressed() {
		return this;
	}
	
	public State ForwardReleased() {
		return this;
	}
	
	public State BackwardPressed() {
		return this;
	}
	
	public State BackwardReleased() {
		return this;
	}
		
	public State LeftPressed() {
		return this;
	}
	
	public State LeftReleased() {
		return this;
	}
	
	public State RightPressed() {
		return this;
	}
	
	public State RightReleased() {
		return this;
	}
}