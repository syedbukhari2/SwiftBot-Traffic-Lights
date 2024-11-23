import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class User {
	protected String username;
	private String correctPassword;
	protected int[] underlightColourPref = new int[]{255, 255, 0}; // Allows users to change the default yellow underlight colour when the SwiftBot is looking for traffic lights
	protected String mode; // Default or emergency mode
	
	public void login() {
		
		Scanner inputObj = new Scanner(System.in);  // scanner object to take input
		
		System.out.println("Enter your username:");
		username = inputObj.nextLine();
		
		Main mainProgram = new Main();
		
		String filename = username + ".txt"; // The file name is their username + .txt
		ArrayList<String> userData = new ArrayList<>(); // This ArrayList is used to store the contents of the user's file
		
		try { // Try reading the file named their username + .txt
			FileReader readhandle = new FileReader(filename);
			BufferedReader br = new BufferedReader(readhandle);
			String line = null;	
			while((line = br.readLine()) != null) {
				userData.add(line); // Read the lines from file and add them to this ArrayList
			}
			br.close();
			readhandle.close();
		} catch (FileNotFoundException e) { // If the file isn't found, the user either made a typo or does not have an existing profile
			System.out.println("\nUnable to find your profile. Would you like to create a new account? (Y/n)");
			
			String signupOption = inputObj.nextLine();
			if (mainProgram.getValidInput("Please enter a valid option.", new String[]{"y", "n"}, signupOption.toLowerCase()).equals("y")) {
				signup();
			} else {
				userData = new ArrayList<>();
				login();
			}
		} catch (IOException e) {
			System.out.println("\nAn error occured while trying to read your data:");
			e.printStackTrace();
		}
		
		
		if (!userData.isEmpty()) {
			correctPassword = userData.get(1); // Second line of text file is password
			System.out.println("Enter your password:");
			String password = inputObj.nextLine();
			while (!password.equals(correctPassword)) {
				System.out.println("\nYour password is incorrect. Please try again.");
				System.out.println("Enter your password:");
				password = inputObj.nextLine();
			}
			
			// Update attributes with the data stored in file
			username = userData.get(0); // First line of text file is username
			underlightColourPref[0] = Integer.parseInt(userData.get(2)); // RGB value for R
			underlightColourPref[2] = Integer.parseInt(userData.get(3)); // RGB value for G
			underlightColourPref[1] = Integer.parseInt(userData.get(4)); // RGB value for B
			mode = userData.get(5); // Sixth line of text file is mode.
		}
		inputObj.close();
	}
	
	public void signup() {
		System.out.println("\n====================================\n");
		System.out.println("Let's create a new profile!");
		Scanner inputObj = new Scanner(System.in);  // Scanner object to take input
		
		System.out.println("Enter a username:");
		username = inputObj.nextLine();
		
		File file = new File(username + ".txt");
		boolean usernameAvailable = isUsernameTaken(file);

		while (!usernameAvailable) { // If the file named after the username already exists, a different username should be chosen
			System.out.println("The username \"" + username + "\" is not available. Please try again.");
			System.out.println("Enter a username:");
			username = inputObj.nextLine();
			file = new File(username + ".txt");
			usernameAvailable = isUsernameTaken(file);
		}
		// Get passowrd
		System.out.println("\nEnter a password:");
		String password = inputObj.nextLine();
		System.out.println("Re-enter the password:");
		String password2 = inputObj.nextLine();
		Main mainProgram = new Main(); // Main object
		password2 = mainProgram.getValidInput("Passwords did not match.\nPlease re-enter the password:", new String[]{password}, password2);
	
		System.out.println("\nNext, please enter the RBG colour code to customise the underlight colour that is shown while the SwiftBot is looking for a traffic light!");
		System.out.println("For example, yellow has the RBG value r: 255, b: 0, g: 255");
		
		for (int i = 0; i < 3; ++i) { // This for loop prompts the user to enter an RBG value for red, green or blue depending on the integer i, and then adds it to the int array. 
			switch (i) {
			case 0: {
				System.out.println("Enter RBG value for red:");
				break;
			} case 1: {
				System.out.println("Enter RBG value for blue:");
				break;
			} default: {
				System.out.println("Enter RBG value for green:");
				break;
			}
			}
			underlightColourPref[i] = getValidInt(); // Update ArrayList
		}
		
		System.out.println("\nLastly, please choose a mode:");
		System.out.println("1: Default mode - The SwiftBot will adhere to the traffic lights as normal");
		System.out.println("2: Emergency mode - The SwiftBot will flash an emergency light pattern and swerve around the traffic lights");
		System.out.println("Select your mode (1/2):");

		String modeNumber = inputObj.nextLine();
		
		modeNumber = mainProgram.getValidInput("Please enter a valid option", new String[]{"1", "2"}, modeNumber);
		if (modeNumber.equals("1")) {
			mode = "Default";
		} else {
			mode = "Emergency";
		}
		
		FileWriter writehandle;
		try { // Write data to file
			writehandle = new FileWriter(username + ".txt");
			BufferedWriter bw = new BufferedWriter(writehandle);
			bw.write(username);
			bw.newLine();
			
			bw.write(password2);
			bw.newLine();
			
			System.out.println("R: " + underlightColourPref[0]);
			bw.write(String.valueOf(underlightColourPref[0])); // RGB value red
			bw.newLine();
			
			System.out.println("G: " + underlightColourPref[2]);
			bw.write(String.valueOf(underlightColourPref[2])); // RGB value green
			bw.newLine();
			
			System.out.println("B: " + underlightColourPref[1]);
			bw.write(String.valueOf(underlightColourPref[1])); // RGB value blue
			bw.newLine();
			
			bw.write(mode);
			
			bw.close();
			writehandle.close();
		} catch (IOException e) {
			System.out.println("\nAn error occured while trying to write data to file:");
			e.printStackTrace();
		}
		
		inputObj.close();
	}

	private int getValidInt() { // Get a valid integer between 0 and 255
		Scanner inputObj = new Scanner(System.in);  // scanner object to take input
		int number;
		while (true) {
            try {
                String input = inputObj.nextLine();
                number = Integer.parseInt(input);

                if (number >= 0 && number <= 255) {
                    break; // Valid input, exit the loop
                } else {
                    System.out.println("Invalid input. Number must be between 0 and 255. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
        return number;
	}
	
	private boolean isUsernameTaken(File file) { // This method checks if the text file with their username already exists
		if (file.exists()) {
			return false;
		} else {
			return true;
		}
	}
}