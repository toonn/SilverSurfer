package mazeAlgorithm;

@SuppressWarnings("serial")
public class CollisionRollbackException extends Exception {
	
	public CollisionRollbackException() {
		
	}

	public CollisionRollbackException(String message) {
		super(message);
    }

	public CollisionRollbackException(Throwable cause) {
		super(cause);
    }

	public CollisionRollbackException(String message, Throwable cause) {
		super(message, cause);
    }
}