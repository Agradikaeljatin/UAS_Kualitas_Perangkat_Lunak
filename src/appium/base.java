package appium;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.openqa.selenium.remote.DesiredCapabilities;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.MobileCapabilityType;

public class base {
    public static void main(String[] args) {
        // define basic test requirements such as device name, app/apk location
        DesiredCapabilities cap = new DesiredCapabilities();

        // Using the correct path - the APK is in src/appium folder
        File appDir = new File("src/appium");
        File app = new File(appDir, "ApiDemos-debug.apk");

        // Print path to verify
        System.out.println("APK path: " + app.getAbsolutePath());
        System.out.println("APK exists: " + app.exists());

        // Capabilities setup
        cap.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Device");
        cap.setCapability("udid", "5cbacd90"); // Using the UDID from your logs
        cap.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());
        cap.setCapability(MobileCapabilityType.AUTOMATION_NAME, "uiautomator2");
        cap.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        cap.setCapability("noReset", true);
        cap.setCapability("autoGrantPermissions", true);

        try {
            // For Appium 2.0+, the correct endpoint is /
            URL url = new URI("http://127.0.0.1:4723").toURL();
            AndroidDriver<AndroidElement> driver = new AndroidDriver<>(url, cap);
            System.out.println("Session created successfully!");
            
            // Add a sleep to keep the session open for a while
            Thread.sleep(5000);
            
            // Close the driver
            driver.quit();
            
        } catch (MalformedURLException | URISyntaxException e) {
            System.out.println("Error creating URL: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Thread sleep interrupted: " + e.getMessage());
            e.printStackTrace();
        }
    }
}