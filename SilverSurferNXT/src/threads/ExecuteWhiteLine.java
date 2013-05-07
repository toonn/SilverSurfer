package threads;

import brick.CommandUnit;

public class ExecuteWhiteLine extends Thread {

	private CommandUnit CU;
	
	public ExecuteWhiteLine(CommandUnit CU) {
		this.CU = CU;
	}
	
	@Override
	public void run() {
        int resultAlign = CU.alignOnWhiteLine();
		if(resultAlign != -1)
			CU.sendStringToUnit("[BC] " + resultAlign);
        CU.stopRobot();
	}
}