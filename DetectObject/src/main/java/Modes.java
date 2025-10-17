import swiftbot.*;

import java.awt.image.BufferedImage;
import java.util.Random;



public class Modes {
		// call the api as a class field 
		private SwiftBotAPI api;
		
		// set a boolean flag that exits out of loops in the mode methods when the user presses x and use volatile to ensure loops in different threads see an updated value  
		private volatile boolean logLoop = true;
		
		public boolean islogLoop() {
			return logLoop; 
		}
		public void logLoop(boolean value) {
			logLoop = value;
			
		
		}
		
		//Declare class fields to be used later by log file 
		
		private String userMode;
		 private int objectEncounters;
		 private long durationExecution;
	     private String imageFilePath;
	     private String logFilePath;
	// create the constructor and the paramater so that we can interact with API 
		public Modes(SwiftBotAPI api) {
			this.api = api;
			this.userMode = "";
			this.objectEncounters = 0;
			this.durationExecution = 0;
			this.imageFilePath = "";
			this.logFilePath = "";
			
			}
		
		public SwiftBotAPI getApi() {
			return this.api;
		}
		// method to read QR code 
				public String QRcode() {
					//Declare and Initialise  a local variable that will store QR code 
					String QRMode = "";
					try {
						Thread.sleep(10000);
						System.out.println("Scanning QR Code..."); // I/O flow 
						BufferedImage QRImage = api.getQRImage(); // create an object from Bufferedimage class and then call method from APi that retrieves the QR image and then store it in the object 
						System.out.println("get qrImage() method called successfully");
						if (QRImage!=null) {
							QRMode = api.decodeQRImage(QRImage); // Call the api method to decode QR image and then store the result in the local variable
						}
						System.out.println("QR code: " +QRMode); // User interface 
					} catch (Exception e) { // Error handling 
						System.err.println("Error while reading QR code: " + e.getMessage()); // Error handling 
					}
					return QRMode.trim();
				}
				private double objectDetection() {
					//create local variable and make  fall back distance incase reading fails 
					double distance = 9999;
					try {
						// call the ultrasound method 
						distance = api.useUltrasound();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					// return the value given by the ultrasound method
					return distance;
				}
				
		public void wandering() 
		{
			HelperMethods helperMethods = new HelperMethods(api);
			// Fulfil requirement of having the underlights set to blue when the robot is 'wandering'
			helperMethods.allUnderLights(0, 0, 255); // call the underlights method 
			
			boolean wandering = true;
			while  (wandering) 
			{
			
				double objectdetection = objectDetection();
			
				if (objectdetection < 100) 
				{
					System.out.println("Breaking from wander mode!");
					wandering = false;
					break;
				}
			else
	
				{
				api.move(40, 40, 2000);
				}
			
			}
			
		}	
		public void modeCuriousSwiftbot() {
		
			System.out.println("Welcome to  Curious SwiftBot mode!"); // User interface 
			// update class field for mode being used 
		 
			// create start time for mode 
			long start = System.currentTimeMillis();
			
			System.out.println("The robot will begin wandering in 5 seconds please move your phone out the way");
			try {
				Thread.sleep(5000);
				wandering();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			
			
			// delcare the variables for the Log file 
			 userMode = "Curious SwiftBot";
			 objectEncounters = 0;
			 logFilePath = "date.apiaddress/curious_log.txt";
			 imageFilePath = "";
			
			// Use try block to prevent code from crashing from unhandled exceptions
			try {
			
				HelperMethods helperMethods = new HelperMethods(api);
				// Fulfil requirement of having the underlights set to blue when the robot is 'wandering'
				helperMethods.allUnderLights(0, 0, 255); // call Underlight method and set the blue to maximum 
			
			
		// create a loop that keeps going so that we can look for objects  until the user terminates the program 
		while (true) 
		{
			
		
				// First create a local variable to hold result of objectDetection 
			double objectDistance = objectDetection();
			System.out.println("Distance from object is:  " + objectDistance + "cm"); //User Interface 
			
			// IF the object is within a range of 200 cm count this as an object detected
			
			if (objectDistance < 200)
			{
				objectEncounters++;
			}
			//
			// update classfield for  objectEncounter if an object has been detected  
			
			// While loop to hold bufferzone scenarios 
			boolean bufferZone = true;
				while (bufferZone) 
				{
				objectDistance = objectDetection();
				System.out.println("Distance from object is:  " + objectDistance + "cm");	
			    // Scenario if Swiftbot is roughly at the bufferzone of 30 cm  give or take 2 cm
			   
				if (Math.abs(objectDistance - 30) < 10)
			    {
				api.stopMove();
				helperMethods.blinkGreenUnderlights();
				BufferedImage image = api.takeStill(ImageSize.SQUARE_480x480);
				helperMethods.saveImage(image, "CuriousSwiftbotimage.jpg");
				bufferZone = false;
				break;
				}
			
			// Scenario if SwiftBot is within bufferzone but is greater than 5cm from the object 
			    else if (objectDistance < 30 && objectDistance > 5)
					{
					helperMethods.allUnderLights(0, 255, 0);
					api.move(-40, -40, 2000);
					api.stopMove();
					
					objectDistance = objectDetection();
					continue;
			
					}
			// Scenario if SwiftBot is outside bufferzone 
			    else if (objectDistance > 30 && objectDistance < 200) 
			    {
				api.move(40, 40, 2000);
				api.stopMove();
		
				objectDistance = objectDetection();
				continue;
			    }
				
			}
			
			
			
				
			// pause for 5 seconds to see if object is moved 
			Thread.sleep(5000);
			
			// check if object has moved 
			double newDistance = objectDetection();
			System.out.println("Distance from object is:  " + 	objectDistance + "cm");
		
			// if object has "not moved" from the bufferzone then change direction and wander until new object is found  
			if (Math.abs(newDistance - objectDistance) <= 3) 
			{
				System.out.println("Object has not moved. Changing direction and beginning wandering");
				helperMethods.rightTurn();
				helperMethods.moveForward();
				wandering();
				continue;
				
			
			} 
			// scenario if object is no longer at bufferzone then reform bufferzone by going back to beginning of While(true) loop
			else 
			{
		
			continue; 
			} 
			
			}
			// scenario where no object is detected within 200 cm
			
			
		} //error handling 
		catch (Exception e) 
			{
			e.printStackTrace();
			} 
			finally 
			{
			// track the total time for the program running 
			
			
			
			
			
			
			// Turn the lights off 
			api.disableUnderlights();
			long end = System.currentTimeMillis();
			 durationExecution = end - start;
			}
		
	
		}
			
	
			
	

			
			
					
				
			
		
		public void modeScaredySwiftbot() 
		{
			
			System.out.println("Welcome to  Scaredy SwiftBot mode!");
			// create start time for mode 
			long start = System.currentTimeMillis();
			// delcare the variables for the Log file 
			userMode = "Scaredy SwiftBot";
			objectEncounters = 0;
			logFilePath = "date.apiaddress/scaredy_log.txt";
			imageFilePath = "";			
			// Fulfil requirement of  underlights set to blue when the robot is 'wandering'
			
			// Use try block to prevent code from crashing from unhandled exceptions
			try 
			{ 
				
				//Create object from HelperMethod class to access its methods 
				HelperMethods helperMethods = new HelperMethods(api);
				
				
				// use a while (true) loop so that object detection can continue going until the user wants to terminate the program with the x button
				
				while (true) 
				{ 
					// Fulfil requirement of  underlights set to blue when the robot is 'wandering' by calling wandering method 
					wandering();
					//Call the object detection method to get the object distance 
					double objectDistance = objectDetection();
					System.out.println("Distance from object is:  " + objectDistance + "cm");

	
					// if the object is within 200 cm count this as an object encounter and turn red 
					if (objectDistance < 200)
					{
					objectEncounters++;
					helperMethods.allUnderLights(255,0,0);
					}
			
					// while loop to deal with scenario where swiftbot is more then 50 cm from the object 
			
					boolean scaredyObject = true;
					while (scaredyObject)
					{
			
						// if statement to fulfill requirement when Swiftbot is more than 50 cm from the object  
			
					if (objectDistance > 50 && objectDistance < 200)
						{
							System.out.println("Object is " + objectDistance + "cm");
							api.move(40, 40, 2000);
							api.stopMove();
							continue;
						
							
							
				
						}
						
						
						// if statement for scenario where swifbot is within 50cm of object
					else if (objectDistance < 50 && objectDistance > 5) 
						{
				
				
							// first take an image of the object 
							BufferedImage image = api.takeStill(ImageSize.SQUARE_240x240);
							helperMethods.saveImage(image, "CuriousSwiftbotimage.jpg");
							// then blink the under lights red
							helperMethods.blinkRedUnderlights();
				
							// finally reverse away from the object 
							api.move(-40, -40, 2000);
				
							// spin in the opposite direction 
							api.move(40, -40, 1000);
				
							// finally move away for 3 seconds 
							api.move(40, 40, 3000);
							// continue scanning for objects 
							scaredyObject = false;
							break;
				
						} 
					}
					
					// scenario if no object has been detected within 5 seconds 
					long noDetection = System.currentTimeMillis();
					// create a flag to come out of a while loop if an object is found within 5 seconds  
					
					while ((System.currentTimeMillis() - noDetection <5000)) 
					{
					objectDistance = objectDetection();
					if (objectDistance < 200)
						{
						System.out.println ("Object found");
						break;
					
						}
				// short sleep to avoid hammering 
						Thread.sleep(500);
				// if statement to handle scenario if no object is detected within 5 seconds
					
					 if ((System.currentTimeMillis() - noDetection <5000)) 
					{
						System.out.println("No object detected. Reorientating to a new direction!");
						helperMethods.rightTurn();
						helperMethods.moveForward();
						break;
					}
					}
					continue;
				
				
				
				
			}
			}
			// short break before continuing loop 
			
			
			catch (Exception e) {
				e.printStackTrace();
			
		}
			api.disableUnderlights();
			long end = System.currentTimeMillis();
			durationExecution = end - start;
	}
		
	
		

	public void modeDubiousSwiftBot() {
		System.out.println("Welcome to Dubious SwiftBot!");
		Random chanceMode = new Random();
		Boolean curious = chanceMode.nextBoolean();
	
		
		if (curious) {
			System.out.println("Curious mode has been selected! Loading program..."); // UI and I/O flow 
			modeCuriousSwiftbot();
			return;
			
		}
		else {
			System.out.println("Scaredy mode has been selected! Loading program...");
			modeScaredySwiftbot();
			return;
			
		}
	
	
	
		
	}
	
	
	public void TerminateX() 
	{
		api.enableButton(Button.X, () -> { // Lambda expression which tells the program what to do when the button X is pressed 
			System.out.println("Terminating program..."); // console I/O flow 
			logLoop(false); 
			Maincode.viewExecutionLog(this);
		api.disableButton(Button.X);
		
		});
	
	}
	// multiline string at end of program to retrieve  all the logfile info 
	public String logInfo() {
	
		StringBuilder log = new StringBuilder();
		log.append("Mode used: ").append(userMode).append("\n");
		log.append("Objects encountered: ").append(objectEncounters).append("\n");
		log.append("Length of execution: ").append(durationExecution).append("\n");
		log.append("log file path: ").append( logFilePath).append("\n");
		log.append("Image file path: ").append( imageFilePath).append("\n");
		return log.toString();	}

}

