import swiftbot.SwiftBotAPI;

public class BlueTrafficLight extends TrafficLight {

	public BlueTrafficLight(int[] RGBValue) {
		super(new int[]{0, 255, 0}); // RBG for blue
	}
	
	@Override
    public void performTrafficLightAction(SwiftBotAPI API) {
		
		try {
			// Stop for half a second
			API.stopMove();
			Thread.sleep(500);
			
			// Blink underlight in blue
			API.fillUnderlights(underlightColour);
			Thread.sleep(600);
			API.disableUnderlights();
			
			//Rotate 90° to the left (anti-clockwise)
			API.startMove(0, 60);
			Thread.sleep(1021);
			API.stopMove();
			
			// Move in this direction for 1 second.
			API.startMove(93, 100);
			Thread.sleep(1000);
			API.stopMove();
			
			// Wait 0.5 seconds then retrace steps
			Thread.sleep(500);
			API.startMove(-93, -100);
			Thread.sleep(1000);
			API.stopMove();
			API.startMove(0, -60);
			Thread.sleep(900);
			API.stopMove();
			
			// Blink underlight in blue
			API.fillUnderlights(underlightColour);
			Thread.sleep(600);
			API.disableUnderlights();
		} catch (InterruptedException | IllegalArgumentException e) {
			System.out.println("\nError performing steps for blue traffic light.");
		}
    }
	
	public void reversedSteps(SwiftBotAPI API) {
		
		try {
			API.stopMove();
			
			// Blink underlight in blue
			API.fillUnderlights(underlightColour);
			Thread.sleep(600);
			API.disableUnderlights();
			
			//Rotate 90° to the left (anti-clockwise)
			API.startMove(0, 60);
			Thread.sleep(1021);
			API.stopMove();
			
			// Move in this direction for 1 second.
			API.startMove(93, 100);
			Thread.sleep(1000);
			API.stopMove();
			
			// Wait 0.5 seconds then retrace steps
			Thread.sleep(500);
			API.startMove(-93, -100);
			Thread.sleep(1000);
			API.stopMove();
			API.startMove(0, -60);
			Thread.sleep(900);
			API.stopMove();
			
			// Blink underlight in blue
			API.fillUnderlights(underlightColour);
			Thread.sleep(600);
			API.disableUnderlights();
			
			Thread.sleep(500);
			
		} catch (InterruptedException | IllegalArgumentException e) {
			System.out.println("\nError performing steps for blue traffic light.");
		}
		
	}
}
