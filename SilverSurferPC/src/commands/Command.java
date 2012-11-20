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
	public static final int ALIGN_PERPENDICULAR = 14;
	public static final int ALIGN_WALL = 15;
	public static final int LOOK_AROUND = 17;
	public static final int PLAY_SONG = 18;
	
	public static String toConsoleString(int c) {
		if (c == FORWARD_PRESSED)
			return "FORWARD_PRESSED";
		else if (c == FORWARD_RELEASED)
			return "FORWARD_RELEASED";
		else if (c == BACKWARD_PRESSED)
			return "BACKWARD_PRESSED";
		else if (c == BACKWARD_RELEASED)
			return "BACKWARD_RELEASED";
		else if (c == LEFT_PRESSED)
			return "LEFT_PRESSED";
		else if (c == LEFT_RELEASED)
			return "LEFT_RELEASED";
		else if (c == RIGHT_PRESSED)
			return "RIGHT_PRESSED";
		else if (c == RIGHT_RELEASED)
			return "RIGHT_RELEASED";
		else if (c == ALIGN_PERPENDICULAR)
		    return "Aligning perpendicularly to a white line";
		else if (c == ALIGN_WALL)
		    return "Aligning on walls";
		else if (c == LOOK_AROUND)
		    return "Looking around";
		else if (c == PLAY_SONG)
			return "Playing a song";
		return " ";
	}
}