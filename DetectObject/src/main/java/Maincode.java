import java.io.FileWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

import swiftbot.Button;
import swiftbot.SwiftBotAPI;
public class Maincode {

	public static void main (String []args) 
	{
		System.out.println("\r\n"
				+ "   ___      __          __    ____  __     _         __ \r\n"
				+ "  / _ \\___ / /____ ____/ /_  / __ \\/ /    (_)__ ____/ /_\r\n"
				+ " / // / -_) __/ -_) __/ __/ / /_/ / _ \\  / / -_) __/ __/\r\n"
				+ "/____/\\__/\\__/\\__/\\__/\\__/  \\____/_.__/_/ /\\__/\\__/\\__/ \r\n"
				+ "                                     |___/              \r\n"
				+ "");
	
		System.out.println("Hi there Welcome to Swiftbot Detect Object!");
		System.out.println("Please scan one of the following modes: Curious SwiftBot , Scaredy SwiftBot or Dubious SwiftBot");
		
		// Use try block to prevent code from crashing from unhandled exceptions
	try 
	{
		// Call the api 
		SwiftBotAPI api = new SwiftBotAPI();
		// Create object from Mode class to access methods within it 
		Modes mode = new Modes(api);
		
		// call the method that allows activation of the x button to quit the program at any time
		mode.TerminateX();
		
	
	// Create a string for the purpose of storing the result of reading the QR code 
		while (true)
		{	
		String chosenmode = mode.QRcode();
			
			// exit if no mode chosen
			if (chosenmode == null  || chosenmode.isEmpty()) 
			{
				
				System.out.println ("No mode chosen. Please select a mode");
				continue;
			}
			
			
			// create switch block to account for the different scenarios where each of the modes is chosen 
			switch (chosenmode.toLowerCase()) 
			{ // change the string to lowercase so the switch block can reliably call the right method  
			
				case "curious swiftbot":
					 mode.modeCuriousSwiftbot();
					
					break;
				case "scaredy swiftbot":
					mode.modeScaredySwiftbot();
					break;
				case "dubious swiftbot":
					mode.modeDubiousSwiftBot();
					break;
					
					// create a default case to notify the user the QR code is not recognisable   
				
					default:
						System.out.println("Unrecognisable QR code");
			}
			break;
		}
			viewExecutionLog(mode);
			
		 }
			catch (Exception e ) 
		{
		e.printStackTrace();
	} 
	
	}
	
	// method asking if user wants to view execution log or not 
	public static void viewExecutionLog( Modes mode) {
	
		// Call the api 
				SwiftBotAPI api = mode.getApi();
				// Create object from Mode class to access methods within it 
				// disable buttons first 
				api.disableButton(Button.X);
				api.disableButton(Button.Y);
		
			System.out.println("Would you like to view the execution log? Press Y for 'Yes' and X for 'No'. You have 20 seconds to decide before program termination occurs");
			
			// use AtomicBoolean class to create a boolean flag that we can change the value of  within lambda expressions to determine what to do when the User presses either  the X and Y buttons   
			
			final AtomicBoolean Yes = new AtomicBoolean(false);
			final AtomicBoolean No = new AtomicBoolean(false);
			
	
			
			// Enable the  Y button for User 
						api.enableButton(Button.Y, () -> {  //Lambda expression
							Yes.set(true); // if the user presses Y the bollean flag becomes true 
							System.out.println("You have selected 'Yes'. Retrieving Log..."); // Console I/O flow 
						});
						
						// Enable the X button for User 
						api.enableButton(Button.X, () -> {
							No.set(true);
							System.out.println("You have selected 'No'. Saving Log and displaying file path only... "); // Console I/O flow 
							api.disableButton(Button.X);
							
						});
						while (true) {
							// if statement to handle scenario where User presses Y 
							if (Yes.get()) {
								// call a method that will display the log File  and store it in a string and then print the contents
								String pressedY = mode.logInfo();
								System.out.println(pressedY);
								 // shutdown program with status code 0 indicating a regular shutdown
								break;
							
							}
							// if statement to handle scenario if User presses X 
						if (No.get()) {
							System.out.println("Skipping the log file displaying filepath where logfile will be stored...");
							System.out.println("data/home/pi/logs/executionLog.txt");
							break;
							
							
						}
						try {
							Thread.sleep(100);
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						}
				String logInfo = mode.logInfo();
				String filePath =  "data/home/pi/logs/executionLog.txt";
				makeLogFile(logInfo, filePath);
				System.exit(0);
				
				
						
	}
	private static void makeLogFile(String logInfo, String filePath) {
		// make timestamp to act as a identifier/"foreign key" for individual log files
		LocalDateTime time = LocalDateTime.now(); 
		
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
		String bookmark = time.format(format);

		String fileName = filePath.replace(".txt", "_" + bookmark + ".txt");
		try (FileWriter file = new FileWriter(fileName)) {
			file.write(logInfo);
			System.out.println("Log file saved to: " + fileName);
		} catch (IOException e) {
			System.err.println("Error creating Log File: " + e.getMessage());
		}
			
		}
	
						
			
	}
	

