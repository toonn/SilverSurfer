package commands;

//All commands needed to control the robot.
public class Command {

	public static final int AUTOMATIC_MOVE_FORWARD = 0;
	public static final int AUTOMATIC_TURN_ANGLE = 1;
	public static final int CLOSE_CONNECTION = 2;
	public static final int SLOW_SPEED = 3;
	public static final int NORMAL_SPEED = 4;
	public static final int FAST_SPEED = 5;
	public static final int VERY_FAST_SPEED = 6;
	public static final int ALIGN_WHITE_LINE = 7;
	public static final int ALIGN_WALL = 8;
	public static final int CHECK_FOR_OBSTRUCTION = 9;
	public static final int START_READING_BARCODES = 10; //Starts checking for barcodes and reading them when found
	public static final int STOP_READING_BARCODES = 11; //Stops checking for barcodes and reading them when found
	public static final int PERMA_STOP_READING_BARCODES = 12; //Hard stop for reading barcodes
}