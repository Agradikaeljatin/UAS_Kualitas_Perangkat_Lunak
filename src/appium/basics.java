package appium;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.MobileCapabilityType;

public class basics {
    public static void main(String[] args) {
        // Define basic test requirements
        DesiredCapabilities cap = new DesiredCapabilities();
        
        // APK setup
        File appDir = new File("src/appium");
        File app = new File(appDir, "ApiDemos-debug.apk");
        
        System.out.println("APK path: " + app.getAbsolutePath());
        System.out.println("APK exists: " + app.exists());
        
        // APPIUM 2.0+ COMPATIBLE CAPABILITIES WITH PROPER VENDOR PREFIXES
        cap.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Device");
        cap.setCapability("appium:udid", "5cbacd90");
        cap.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());
        cap.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
        cap.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        
        // CRITICAL FIXES WITH PROPER VENDOR PREFIXES (appium:):
        
        // 1. Skip problematic server operations
        cap.setCapability("appium:skipServerInstallation", true);
        cap.setCapability("appium:skipDeviceInitialization", true);
        cap.setCapability("appium:skipLogcatCapture", true);
        
        // 2. Handle hidden API policy errors (Android 9+)
        cap.setCapability("appium:ignoreHiddenApiPolicyError", true);
        cap.setCapability("appium:allowTestPackages", true);
        
        // 3. Real device specific settings
        cap.setCapability("appium:systemPort", 8201); // Unique port for this device
        cap.setCapability("appium:chromeDriverPort", 9516); // Unique Chrome driver port
        
        // 4. Permission and installation settings
        cap.setCapability("appium:autoGrantPermissions", true);
        cap.setCapability("appium:noReset", true); // Keep app state for real device testing
        cap.setCapability("appium:fullReset", false);
        cap.setCapability("appium:ensureWebviewsHavePages", true);
        
        // 5. Real device stability settings
        cap.setCapability("appium:newCommandTimeout", 300);
        cap.setCapability("appium:androidInstallTimeout", 120000); // Longer for real devices
        cap.setCapability("appium:adbExecTimeout", 40000);
        cap.setCapability("appium:androidDeviceReadyTimeout", 60);
        
        // 6. Skip screen unlock attempts (safer for real devices)
        cap.setCapability("appium:skipUnlock", true);
        
        // 7. Additional real device optimizations
        cap.setCapability("appium:disableIdLocatorAutocompletion", true);
        cap.setCapability("appium:shouldTerminateApp", true);
        cap.setCapability("appium:forceAppLaunch", true);
        
        try {
            URL url = new URI("http://127.0.0.1:4723").toURL();
            AndroidDriver<AndroidElement> driver = new AndroidDriver<>(url, cap);
            System.out.println("Session created successfully!");
            
            WebDriverWait wait = new WebDriverWait(driver, 15); // Increased timeout
            
            // Wait for app to fully load
            Thread.sleep(3000); // Increased wait time
            
            // Print app context for debugging
            System.out.println("Current context: " + driver.getContext());
            System.out.println("Available contexts: " + driver.getContextHandles());
            
            // List all visible elements for debugging
            System.out.println("Attempting to find and list all text elements:");
            try {
                java.util.List<AndroidElement> allElements = driver.findElementsByClassName("android.widget.TextView");
                System.out.println("Found " + allElements.size() + " TextView elements");
                for (int i = 0; i < Math.min(allElements.size(), 10); i++) { // Limit to first 10
                    AndroidElement element = allElements.get(i);
                    try {
                        String text = element.getText();
                        boolean displayed = element.isDisplayed();
                        System.out.println("Element " + i + ": Text='" + text + "', Displayed=" + displayed);
                    } catch (Exception e) {
                        System.out.println("Element " + i + ": Could not get details - " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception while listing elements: " + e.getMessage());
            }
            
            // Improved element finding with better error handling
            System.out.println("Attempting to click on App using improved strategies");
            
            boolean appClicked = false;
            
            // Strategy 1: UIAutomator with text
            try {
                WebElement appOption = driver.findElementByAndroidUIAutomator(
                    "new UiSelector().text(\"App\").className(\"android.widget.TextView\")");
                System.out.println("Found App element using UIAutomator, clicking...");
                appOption.click();
                appClicked = true;
            } catch (Exception e1) {
                System.out.println("UIAutomator text approach failed: " + e1.getMessage());
                
                // Strategy 2: UIAutomator with partial text
                try {
                    WebElement appOption = driver.findElementByAndroidUIAutomator(
                        "new UiSelector().textContains(\"App\")");
                    System.out.println("Found App element using UIAutomator partial text, clicking...");
                    appOption.click();
                    appClicked = true;
                } catch (Exception e2) {
                    System.out.println("UIAutomator partial text failed: " + e2.getMessage());
                    
                    // Strategy 3: XPath with wait
                    try {
                        WebElement appOption = wait.until(
                            ExpectedConditions.elementToBeClickable(
                                By.xpath("//android.widget.TextView[@text='App']")
                            )
                        );
                        System.out.println("Found App element using XPath with wait, clicking...");
                        appOption.click();
                        appClicked = true;
                    } catch (Exception e3) {
                        System.out.println("XPath with wait failed: " + e3.getMessage());
                        
                        // Strategy 4: Try to find by index if we know the structure
                        try {
                            java.util.List<AndroidElement> textViews = driver.findElementsByClassName("android.widget.TextView");
                            for (int i = 0; i < textViews.size(); i++) {
                                AndroidElement tv = textViews.get(i);
                                if ("App".equals(tv.getText())) {
                                    System.out.println("Found App element by iterating, clicking...");
                                    tv.click();
                                    appClicked = true;
                                    break;
                                }
                            }
                        } catch (Exception e4) {
                            System.out.println("Iteration approach failed: " + e4.getMessage());
                        }
                    }
                }
            }
            
            if (!appClicked) {
                System.out.println("Failed to click App element with all strategies");
                throw new RuntimeException("Unable to find and click App element");
            }
            
            // Wait for navigation
            Thread.sleep(2000);
            
            // Continue with Activity navigation
            try {
                WebElement activityOption = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//android.widget.TextView[@text='Activity']")
                    )
                );
                System.out.println("Found Activity element, clicking...");
                activityOption.click();
                
                Thread.sleep(1500);
                
                // Click on Custom Title
                WebElement customTitleOption = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//android.widget.TextView[@text='Custom Title']")
                    )
                );
                System.out.println("Found Custom Title element, clicking...");
                customTitleOption.click();
                
                Thread.sleep(1500);
                
                // Interact with input fields
                try {
                    WebElement leftTextField = wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                            By.id("io.appium.android.apis:id/left_text_edit")
                        )
                    );
                    leftTextField.clear();
                    leftTextField.sendKeys("Hello Appium Fixed!");
                    System.out.println("Entered text in left field");
                    
                    WebElement changeLeftButton = wait.until(
                        ExpectedConditions.elementToBeClickable(
                            By.id("io.appium.android.apis:id/left_text_button")
                        )
                    );
                    changeLeftButton.click();
                    System.out.println("Clicked the Change Left button");
                    
                    Thread.sleep(2000);
                    
                } catch (Exception e) {
                    System.out.println("Failed to interact with input fields: " + e.getMessage());
                }
                
            } catch (Exception e) {
                System.out.println("Failed to navigate to Activity > Custom Title: " + e.getMessage());
            }
            
            // Navigate back to main menu
            System.out.println("Navigating back to main menu...");
            for (int i = 0; i < 3; i++) {
                try {
                    driver.navigate().back();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("Back navigation attempt " + (i+1) + " failed: " + e.getMessage());
                }
            }
            
            // Test scrolling functionality
            System.out.println("Testing scroll functionality...");
            try {
                // Try to scroll to Views first
                WebElement viewsElement = driver.findElementByAndroidUIAutomator(
                    "new UiSelector().text(\"Views\")");
                viewsElement.click();
                System.out.println("Clicked on Views");
                
                Thread.sleep(2000);
                
                // Try scrolling to find WebView
                try {
                    WebElement webViewElement = driver.findElementByAndroidUIAutomator(
                        "new UiScrollable(new UiSelector().scrollable(true))" +
                        ".scrollIntoView(new UiSelector().textContains(\"WebView\"))");
                    
                    if (webViewElement != null) {
                        System.out.println("Successfully scrolled to WebView");
                        webViewElement.click();
                        Thread.sleep(2000);
                    }
                } catch (Exception e) {
                    System.out.println("Scrolling to WebView failed: " + e.getMessage());
                }
                
            } catch (Exception e) {
                System.out.println("Views navigation failed: " + e.getMessage());
            }
            
            System.out.println("Test completed successfully. Closing driver...");
            driver.quit();
            
        } catch (MalformedURLException | URISyntaxException e) {
            System.out.println("Error creating URL: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Thread sleep interrupted: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}