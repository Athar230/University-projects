import swiftbot.SwiftBotAPI;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class HelperMethods {
	private SwiftBotAPI api;
	
	public HelperMethods(SwiftBotAPI api) {
		this.api = api;
	}
		
	public void allUnderLights(int r, int g, int b) {
			int [] rgb = {r,g,b};
			api.fillUnderlights(rgb);
		}
	
 // helper method for curious mode to blink underlights green for 3 seconds 
	public void blinkGreenUnderlights() throws InterruptedException {
		for (int i = 0; i <3 ; i++) {
			//turn the underlights green for half a second 
			allUnderLights(0,255,0);
			Thread.sleep(500);
			
			//turn the underlights off for half a second 
			api.disableUnderlights();
			Thread.sleep(500);
	}
	}
	// helper method for Scaredy mode to blink underlights red
		public void blinkRedUnderlights() throws InterruptedException {
			for (int i = 0; i <3 ; i++) {
				//turn the underlights green for half a second 
				allUnderLights(255,0,0);
				Thread.sleep(500);
				
				//turn the underlights off for half a second 
				api.disableUnderlights();
				Thread.sleep(500);
			}  
		}
		public void saveImage(BufferedImage image, String Filename) {
			try {
				File file = new File(Filename);
				ImageIO.write(image, "jpg", file);
				System.out.println("image saved to" + file.getAbsolutePath());
				
			} catch (IOException e) {
				System.err.println("error while saving image" + e.getMessage());
			}
		}
		public void rightTurn() throws InterruptedException {
			api.move(40, -40, 2000);
			api.stopMove();
		}
		
		public void moveForward() throws InterruptedException {
			api.move(40, 40, 5000);
			api.stopMove();
		}
}
