package mazeAlgorithm;

@SuppressWarnings("serial")
public class CollisionAvoidedException extends Exception {
	
	public CollisionAvoidedException() {
		
	}

	public CollisionAvoidedException(String message) {
		super(message);
    }

	public CollisionAvoidedException(Throwable cause) {
		super(cause);
    }

	public CollisionAvoidedException(String message, Throwable cause) {
		super(message, cause);
    }
}