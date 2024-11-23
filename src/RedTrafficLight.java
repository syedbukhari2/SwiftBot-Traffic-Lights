import swiftbot.SwiftBotAPI;

public class RedTrafficLight extends TrafficLight {

	public RedTrafficLight(int[] RGBValue) {
		super(new int[]{255, 0, 0}); // RBG for red
	}
	
	@Override
    public void performTrafficLightAction(SwiftBotAPI API) {
		
		// Set underlights to red
		try {
			API.fillUnderlights(underlightColour);
		} catch (IllegalArgumentException e) {
			System.out.println("\nError changing the underlight colour:");
			e.printStackTrace();
		}
		
		// Come to a stop
		API.stopMove();
		
		// Wait half a second
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println("\nError waiting 0.5 seconds for red traffic light.");
			e.printStackTrace();
		}
    }
	
	public void reversedSteps(SwiftBotAPI API) {
		try {
			API.fillUnderlights(underlightColour);
			API.stopMove();
			Thread.sleep(500);
		} catch (InterruptedException | IllegalArgumentException e) {
			System.out.println("\nError reversing steps for red traffic light.");
		}
	}
}
