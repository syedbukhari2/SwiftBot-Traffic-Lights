import swiftbot.SwiftBotAPI;

public class GreenTrafficLight extends TrafficLight {

	public GreenTrafficLight(int[] RGBValue) {
		super(new int[]{0, 0, 255}); // RBG for green
	}

	@Override
    public void performTrafficLightAction(SwiftBotAPI API) {
		
		// Set underlights to green
		try {
			API.fillUnderlights(underlightColour);
		} catch (IllegalArgumentException e) {
			System.out.println("\nError changing the underlight colour:");
			e.printStackTrace();
		}
		
		boolean passedGreenLight = false;
		double distanceToTrafficLight = 0;
		distanceToTrafficLight = API.useUltrasound();
		
		while (distanceToTrafficLight < 20) { // This loop keeps the boolean passedGreenLight to false to keep the SwiftBot moving at the previous speed until it has 'passed' the green light.
			distanceToTrafficLight = API.useUltrasound();
			passedGreenLight = false;
		}
		passedGreenLight = true;
		
		API.stopMove();
		
		try {
			Thread.sleep(500); // wait for half a second
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
	
	public void reversedSteps(SwiftBotAPI API) {
		try {
			API.stopMove();
			API.fillUnderlights(underlightColour);
			Thread.sleep(500);
			API.startMove(-30,-30); // Start moving back
			Thread.sleep(2000);
		} catch (InterruptedException | IllegalArgumentException e) {
			System.out.println("\nError reversing steps for green traffic light.");
		}
	}
}
