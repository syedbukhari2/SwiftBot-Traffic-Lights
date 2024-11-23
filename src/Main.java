import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

import swiftbot.Button;
import swiftbot.ImageSize;
import swiftbot.SwiftBotAPI;
import swiftbot.Underlight;

public class Main {

	static private SwiftBotAPI API = new SwiftBotAPI();
	static private boolean flashButtonA = true; // When true, it will flash the Button A light.
	
	// Traffic lights
	static private TrafficLight redLight = new RedTrafficLight(new int[]{255, 0, 0});
	static private TrafficLight greenLight = new GreenTrafficLight(new int[]{0, 0, 255});
	static private TrafficLight blueLight = new BlueTrafficLight(new int[]{0, 255, 0});
	
	static private Stack<String> trafficLightStack = new Stack<String>(); // Stack used to hold the traffic light colours and the time taken to detect that colour
	
	static private int startTime = 0;
	static private int endTime = 0;
	static private boolean programEnded = false;
	static private int totalTrafficLights = 0; // Total number of traffic lights detected in the program's execution.
	
	static private Scanner inputObj = new Scanner(System.in);  // Create a Scanner object
	
	public static void main(String[] args) {
		System.out.println("====================================");
		System.out.println("   CS1810 Software implementation");
		System.out.println("       Task 2 - Traffic Light");
		System.out.println("====================================\n");
		
		System.out.println("Please select an option:\n1 - Log in\n2 - Create new profile");
		System.out.println("\nSelect your option (1/2): ");
		
		
	    String loginOption = inputObj.nextLine(); // Scanner object to read user input
	    loginOption = getValidInput("Please enter a valid option", new String[]{"1", "2"}, loginOption);
	    
	    User user = new User(); // Create a user object
	    
	    if (loginOption.equals("1")) {
	    	user.login();
	    } else {
	    	user.signup();
	    }
		
	    System.out.println("\n====================================\n");
	    System.out.println(getGreeting() + ", " + user.username + "!\n"); // A personalised greeting!
	    System.out.println("Your RGB colour prefrence is: " + user.underlightColourPref[0] + ", " + user.underlightColourPref[2] + ", " + user.underlightColourPref[1]);
	    System.out.println("The program will run in the " + user.mode + " mode.");	    
		System.out.println("\nTo start the program, please press Button A.");
		
		API.enableButton(Button.A, () -> {
			API.disableButton(Button.A); // Disable Button A
			startTime = (int)System.currentTimeMillis(); // Start time of the program in milliseconds.
			System.out.println("\nProgram started! To terminate at any point, please press Button X.");
			API.enableButton(Button.X, () -> {
				programEnded = true;
				endTime = (int)System.currentTimeMillis(); // End time of the program in milliseconds.
				System.out.println("\n====================================\n");
				exitProgram(user.mode);
			});
			flashButtonA = false;
			
			startProgram(user.underlightColourPref, user.mode);
			
		});
		
		while (flashButtonA) { // Flash Button A light to give user an indication and improve their experience
			try {
				API.setButtonLight(Button.A, true);
				Thread.sleep(600);
				API.setButtonLight(Button.A, false);
				Thread.sleep(600);
			} catch (InterruptedException e) {
				System.out.println("\nAn exception occured while flashing Button A light");
				e.printStackTrace();
			}
		}
	}

	public static void startProgram(int[] underlightColour, String mode) {
		
		API.fillUnderlights(underlightColour); // Set underlights to the colour chosen by user
		API.startMove(31,30); // Start moving
		
		int startMoveTime = (int)System.currentTimeMillis(); // This variable is used to calculate the time taken to detect a traffic light for the stack 

		boolean trafficLightDetected = false;
		double distanceToTrafficLight = 0;
		distanceToTrafficLight = API.useUltrasound();

		while (distanceToTrafficLight > 20) { // This loop keeps the boolean trafficLightDetected to false to keep the SwiftBot moving at the previous speed until it has detected a traffic light.
			distanceToTrafficLight = API.useUltrasound();
			trafficLightDetected = false;
		}
		// When program exits the loop, a traffic light (or an object) has been detected. 
		trafficLightDetected = true;

		if (!programEnded) {
			int trafficLightClearedTime = (int)System.currentTimeMillis(); // This variable is used to calculate the time taken to detect a traffic light for the stack
			int detectionTime = trafficLightClearedTime - startMoveTime;
			
			++totalTrafficLights;
			System.out.println("\nTraffic light " + totalTrafficLights + " detected.");
			
			if (mode.equals("Emergency")) {
				emergencyMode();
			} else {
				BufferedImage trafficLightImage = API.takeStill(ImageSize.SQUARE_144x144); // Take an image
				System.out.println("Image captured successfully!");
				
				String trafficLightColour = null;
				trafficLightColour = getTrafficLightColour(trafficLightImage); // Analyse image
				System.out.println("Analysing traffic light colours...");
				
				if (trafficLightColour.equals("Red")) { // This IF statement records the traffic light and performs the appropriate action
					System.out.println("\nRed traffic light detected!");
					redLight.recordTrafficLight();
					System.out.println("Executing steps for a red light...");
					redLight.performTrafficLightAction(API);
					System.out.println("Traffic light cleared.");
				} else if (trafficLightColour.equals("Green")) {
					System.out.println("\nGreen traffic light detected!");
					greenLight.recordTrafficLight();
					System.out.println("Executing steps for a green light...");
					greenLight.performTrafficLightAction(API);
					System.out.println("Traffic light cleared.");
				} else if (trafficLightColour.equals("Blue")) {
					System.out.println("\nBlue traffic light detected!");
					blueLight.recordTrafficLight();
					System.out.println("Executing steps for a blue light...");
					blueLight.performTrafficLightAction(API);
					System.out.println("Traffic light cleared.");
				} else { // If a valid traffic light is not detected, check again.
					System.out.println("\nFailed to determine traffic light colour...");
					API.stopMove();
					API.fillUnderlights(new int[]{255, 255, 255}); // RBG value for white
					trafficLightImage = API.takeStill(ImageSize.SQUARE_144x144);
					System.out.println("Retrying...");
					trafficLightColour = getTrafficLightColour(trafficLightImage);
					
					// Check if it is "Red", "Green" or "Blue" again
					if (trafficLightColour.equals("Red")) {
						System.out.println("\nRed traffic light detected!");
						redLight.recordTrafficLight();
						System.out.println("Executing steps for a red light...");
						redLight.performTrafficLightAction(API);
						System.out.println("Traffic light cleared.");
					} else if (trafficLightColour.equals("Green")) {
						System.out.println("\nGreen traffic light detected!");
						greenLight.recordTrafficLight();
						System.out.println("Executing steps for a green light...");
						greenLight.performTrafficLightAction(API);
						System.out.println("Traffic light cleared.");
					} else if (trafficLightColour.equals("Blue")) {
						System.out.println("\nBlue traffic light detected!");
						blueLight.recordTrafficLight();
						System.out.println("Executing steps for a blue light...");
						blueLight.performTrafficLightAction(API);
						System.out.println("Traffic light cleared.");
					} else {
						System.out.println("\nFailed to determine a valid traffic light colour.");
						System.out.println("Continuing with program...");
					}
				}
				trafficLightStack.add(String.valueOf(detectionTime)); // Push the time taken to detect the previous traffic light to the stack
				trafficLightStack.add(String.valueOf(trafficLightColour)); // Push the colour of the previous traffic light to the stack
			}
			startProgram(underlightColour, mode); // Call this method again
		}
	}
		
	private static String getTrafficLightColour(BufferedImage image) {
		ArrayList<String> detectedColoursArrayList = new ArrayList<String>();

		for(int x = 0; x < image.getWidth(); ++x) {
			for(int y = 0; y < image.getHeight(); ++y) {
				int pixel = image.getRGB(x,y); // A single pixel

				int r = (pixel >> 16) & 0xFF; // Right-shift the 32-bit RGB value by 16 positions
				int g = (pixel >> 8) & 0xFF; // Right-shift the 32-bit RGB value by 8 positions
				int b = pixel & 0xFF; // Bitwise AND with Hexadecimal value FF to the original RGB value to get the last 8 bits

				// Calculate the Euclidean distance for each colour in that pixel
				double redEuclideanDistance = Math.sqrt(Math.pow((255 - r),2) + Math.pow((0 - g),2) + Math.pow((0 - b),2));
				double greenEuclideanDistance = Math.sqrt(Math.pow((0 - r),2) + Math.pow((255 - g),2) + Math.pow((0 - b),2));
				double blueEuclideanDistance = Math.sqrt(Math.pow((0 - r),2) + Math.pow((0 - g),2) + Math.pow((255 - b),2));

				// Get the highest Euclidean distance value, this will be the colour on that pixel 
				if ((redEuclideanDistance < 250) && (redEuclideanDistance < greenEuclideanDistance) && (redEuclideanDistance < blueEuclideanDistance)) { // Implies red is the most accurate
					detectedColoursArrayList.add("Red");
				} else if  ((greenEuclideanDistance < 250) && (greenEuclideanDistance < redEuclideanDistance) && (greenEuclideanDistance < blueEuclideanDistance)) { // Implies green is the most accurate
					detectedColoursArrayList.add("Green");
				} else if  ((blueEuclideanDistance < 250) && (blueEuclideanDistance < redEuclideanDistance) && (blueEuclideanDistance < greenEuclideanDistance)) { // Implies green is the most accurate
					detectedColoursArrayList.add("Blue");
				}
			}
		}

		int totalPixels = image.getWidth() * image.getHeight(); // Total number of pixels in the image
		int totalRedPixels = 0;   // Total number of pixels that were considered red
		int totalGreenPixels = 0; // Total number of pixels that were considered green
		int totalBluePixels = 0;  // Total number of pixels that were considered blue
		
		// This for loop goes through each pixel and updates the variables above to calculate what percent of each colour appears in that image 
		for (int i = 0; i < detectedColoursArrayList.size(); ++i) {
			if (detectedColoursArrayList.get(i).equals("Red")) {
				++totalRedPixels;
			} else if (detectedColoursArrayList.get(i).equals("Green")) {
				++totalGreenPixels;
			} else {
				++totalBluePixels;
			}
		}

		// Calculate percentages
		double redPercentage = ((double)totalRedPixels/totalPixels)*100;
		double greenPercentage = ((double)totalGreenPixels/totalPixels)*100;
		double bluePercentage = ((double)totalBluePixels/totalPixels)*100;

		// If 75% of the image is either Red, Green or Blue...
		if (redPercentage >= 75) {
			return "Red";
		} else if (greenPercentage >= 75) {
			return "Green";
		} else if (bluePercentage >= 75) {
			return "Blue";
		} else {
			return "Invalid";
		}
	}
	
	public static String getGreeting() { // This method displays a greeting based on the time of the day
        LocalTime currentTime = LocalTime.now();

        // Compare the current time to decide the greeting
        if (currentTime.isBefore(LocalTime.NOON)) { // If time is before 12 pm
            return "Good morning";
        } else if (currentTime.isBefore(LocalTime.of(17, 0))) { // else if time is before 5 pm
            return "Good afternoon";
        } else {
            return "Good evening";
        }
    }
	
	public static String getValidInput(String prompt, String validInputs[], String input) { // This method checks if the user input matches the valid options. If not, the method displays the prompt and asks the user to enter a correct option. 
		boolean validInputFound = false;
		for (int i = 0; i < validInputs.length; ++i) { // Check in input exists in the String array validInputs.
			if (input.equals(validInputs[i])) {
				validInputFound = true;
				break;
			}
		}
		if (validInputFound) {
			return input;
		} else {
			System.out.println(prompt);
		    input = inputObj.nextLine(); // Read user input
		    return getValidInput(prompt, validInputs, input); // Method gets called again with the same prompt and String array, but the new input.
		}
	}
	
	private static void emergencyMode() {
		Random rand = new Random();
		int randomInt = rand.nextInt(2); // Generate a random number between 0-1 (2 different possible values)
		try {
			if (randomInt == 1) {
				API.disableUnderlights(); // Turn underlights off
				
				API.startMove(-40, 40); // Turn left
				Thread.sleep(560);
				API.startMove(93, 100); // Go straight
				emergencyFlash();
				API.startMove(40, -40); // Turn right
				Thread.sleep(560);
				API.startMove(93, 100); // Go straight
				emergencyFlash();
				API.startMove(40, -40); // Turn right
				Thread.sleep(560);
				API.startMove(93, 100); // Go straight
				emergencyFlash();
				API.startMove(-40, 40); // Turn left
				Thread.sleep(560);
			} else {
				API.startMove(40, -40); // Turn right
				Thread.sleep(560);
				API.startMove(93, 100); // Go straight
				emergencyFlash();
				API.startMove(-40, 40); // Turn left
				Thread.sleep(560);
				API.startMove(93, 100); // Go straight
				emergencyFlash();
				API.startMove(-40, 40); // Turn left
				Thread.sleep(560);
				API.startMove(93, 100); // Go straight
				emergencyFlash();
				API.startMove(40, -40); // Turn right
				Thread.sleep(560);
			}
		} catch (InterruptedException e) {
			System.out.println("\nAn exception occured during emergency mode.");
			e.printStackTrace();
		}
	}
	
	private static void emergencyFlash() { // This method shows an underlight pattern that mimics an emergency vehicle
		try {
			API.setUnderlight(Underlight.FRONT_LEFT, new int[] {0, 255, 0});
			API.setUnderlight(Underlight.BACK_RIGHT, new int[] {255, 0, 0});
			Thread.sleep(500); // Wait 500 ms
			
			API.disableUnderlights(); // Turn underlights off
			API.setUnderlight(Underlight.FRONT_RIGHT, new int[] {255, 0, 0});
			API.setUnderlight(Underlight.BACK_LEFT, new int[] {0, 255, 0});
			Thread.sleep(500); //Wait 500 ms
			
			API.disableUnderlights(); // Turn underlights off
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void retraceSteps() { // This method goes through the stack and pops the traffic lights, executing them in reverse.
		System.out.println("\nTraffic light stack:\nBOTTOM " + trafficLightStack + " TOP");
		
		while (!trafficLightStack.isEmpty()) {
			String trafficLight = trafficLightStack.pop(); // Pop the traffic light colour
			String duration = trafficLightStack.pop(); // Pop the duration of time taken to detect the traffic light
			int durationInt = Integer.parseInt(duration); // Convert from String to int
			
			if (trafficLight.equals("Red")) {
				API.fillUnderlights(new int[]{255, 0, 0}); // RBG value for red
				redLight.reversedSteps(API);
			} else if (trafficLight.equals("Green")) {
				API.fillUnderlights(new int[]{0, 0, 255}); // RBG value for green
				greenLight.reversedSteps(API);
			} else if (trafficLight.equals("Blue")) {
				API.fillUnderlights(new int[]{0, 255, 0}); // RBG value for blue
				blueLight.reversedSteps(API);
			} else if (trafficLight.equals("Invalid")) {
				API.fillUnderlights(new int[]{255, 255, 255}); // RBG value for blue
				API.stopMove();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			API.startMove(-34,-30); // Start moving back for the amount of time taken to detect the current traffic light
			API.fillUnderlights(new int[] {255, 0, 255});
			
			try {
				Thread.sleep(durationInt);
			} catch (InterruptedException e) {
				System.out.println("An error occured while retracing steps.");
				e.printStackTrace();
			}
		}
		System.out.println("\nGoodbye!");
		System.exit(0);
	}
	
	private static void displayLog(String totalTrafficLights, String mostFrequentLight, String totalEncounters, String duration) {
		System.out.println("\n====================================");
		System.out.println("        Log of execution");
		System.out.println("====================================");
		System.out.println(totalTrafficLights);
		System.out.println(mostFrequentLight);
		System.out.println(totalEncounters);
		System.out.println(duration);
	}
	
	private static String mostFrequentLight(int redNum, int greenNum, int blueNum) { // This method returns the most frequently appearing traffic light colour
        if ((redNum > greenNum) && (redNum > blueNum)) {
            return "red";
        } else if ((greenNum > redNum) && (greenNum > blueNum)) {
        	return "green";
        } else if ((blueNum > redNum) && (blueNum > greenNum)) {
        	return "blue";
        } else {
        	return "unavailable";
        }
	}
	
	private static void exitProgram(String mode) {
		API.disableButton(Button.X); // Disable Button X
		API.stopMove();
		try {
			Thread.sleep(1600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (mode.equals("Emergency")) { // Next two options are not applicable for emergency mode.
			System.exit(0);
		}
		
		// Prepare data to be displayed or stored in the text file
		String totalLights = "Total number of traffic light encounters: " + String.valueOf(totalTrafficLights);
		String largestColour = mostFrequentLight(redLight.getTotalEncounters(), greenLight.getTotalEncounters(), blueLight.getTotalEncounters());
		String frequentColour = "Most frequent traffic light colour: " + largestColour;
		String totalEncounters;
		if (largestColour.equals("red")) { // Get the number of times the most frequent colour was encountered
			totalEncounters = String.valueOf(redLight.getTotalEncounters());
		} else if (largestColour.equals("green")) {
			totalEncounters = String.valueOf(greenLight.getTotalEncounters());
		} else if (largestColour.equals("blue")) {
			totalEncounters = String.valueOf(blueLight.getTotalEncounters());
		} else {
			totalEncounters = "0";
		}
		String timesEncountered = "It was encountered " + String.valueOf(totalEncounters) + " times.";
		String duration = "The program ran for " + String.valueOf((endTime - startTime)/1000) + " seconds.";
		
		System.out.println("Would you like to view a log of execution?\n Button Y - Yes\n Button X - No");
		
		API.enableButton(Button.Y, () -> {
			displayLog(totalLights, frequentColour, timesEncountered, duration);
			API.disableButton(Button.Y);
			API.disableButton(Button.X);
			
			System.out.println("\nWould you like to retrace your steps?\n Button Y - Yes\n Button X - No");
			
			API.enableButton(Button.Y, () -> {
				retraceSteps();
				API.disableButton(Button.Y);
				API.disableButton(Button.X);
			});
			API.enableButton(Button.X, () -> {
				API.disableButton(Button.Y);
				API.disableButton(Button.X);
				System.exit(0);
			});
		});
		API.enableButton(Button.X, () -> {
			try { // Write data to file
				FileWriter writehandle;
				writehandle = new FileWriter("log.txt");
				BufferedWriter bw = new BufferedWriter(writehandle);
				bw.write(totalLights);
				bw.newLine();
				bw.write(frequentColour);
				bw.newLine();
				bw.write(timesEncountered);
				bw.newLine();
				bw.write(duration);
				bw.close();
				writehandle.close();
			} catch (IOException e) {
				System.out.println("\nAn error occured while writing log of execution.");
				e.printStackTrace();
			}
			API.disableButton(Button.Y);
			API.disableButton(Button.X);
			
			System.out.println("\nWould you like to retrace your steps?\n Button Y - Yes\n Button X - No");
			
			API.enableButton(Button.Y, () -> {
				retraceSteps();
				API.disableButton(Button.Y);
				API.disableButton(Button.X);
				System.exit(0);
			});
			API.enableButton(Button.X, () -> {
				API.disableButton(Button.Y);
				API.disableButton(Button.X);
				System.exit(0);
			});
		});
	}
}
