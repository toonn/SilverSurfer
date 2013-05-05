package commands;

public class Sleep {

	public void sleepFor(int duration) {
		if(duration > 0)
			try {
				Thread.sleep(duration);
			} catch (Exception e) {
				System.out.println("Exception while sleeping!");
			}
	}
}