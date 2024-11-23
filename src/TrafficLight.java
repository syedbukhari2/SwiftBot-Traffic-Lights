import java.io.IOException;

import swiftbot.SwiftBotAPI;

public class TrafficLight {

	protected int timesEncountered = 0; // Number of times a traffic light has been encountered.
	int[] underlightColour;
	
	public TrafficLight(int[] RGBValue) {
		underlightColour = RGBValue;
	}
	
	public void recordTrafficLight() { // Updates the number of times a particular traffic light has been encountered 
		++timesEncountered;
	}
	
	public int getTotalEncounters() {
		return timesEncountered;
	}

	public void performTrafficLightAction(SwiftBotAPI API) {
		try {
			API.fillUnderlights(underlightColour);
		} catch (IllegalArgumentException e) {
			System.out.println("Error changing the underlight colour:");
			e.printStackTrace();
		}
	}

	public void reversedSteps(SwiftBotAPI API) {
		System.out.println("Execute the steps for traffic light in the reversed order");
	}
}