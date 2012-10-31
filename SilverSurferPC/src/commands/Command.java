package commands;

public class Command {
	
	public static final int FORWARD_PRESSED = 0;
	public static final int FORWARD_RELEASED = 1;
	public static final int BACKWARD_PRESSED = 2;
	public static final int BACKWARD_RELEASED = 3;
	public static final int LEFT_PRESSED = 4;
	public static final int LEFT_RELEASED = 5;
	public static final int RIGHT_PRESSED = 6;
	public static final int RIGHT_RELEASED = 7;
	public static final int AUTOMATIC_MOVE_FORWARD = 8;
	public static final int AUTOMATIC_TURN_ANGLE = 9;
	
	public static String toConsoleString(int c) {
		if (c == 0)
			return "FORWARD_PRESSED";
		else if (c == 1)
			return "FORWARD_RELEASED";
		else if (c == 2)
			return "BACKWARD_PRESSED";
		else if (c == 3)
			return "BACKWARD_RELEASED";
		else if (c == 4)
			return "LEFT_PRESSED";
		else if (c == 5)
			return "LEFT_RELEASED";
		else if (c == 6)
			return "RIGHT_PRESSED";
		else if (c == 7)
			return "RIGHT_RELEASED";
		return " ";
	}
}