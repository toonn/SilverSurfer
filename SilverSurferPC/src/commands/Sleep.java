package commands;

public class Sleep {

	public void sleepFor(int duration) {
        try {
            Thread.sleep(duration);
        } catch (Exception e) {
        	System.out.println("Exception while sleeping!");
        }
	}
}