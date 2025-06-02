package appium;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.touch.offset.PointOption;
import io.appium.java_client.TouchAction;
import org.openqa.selenium.remote.DesiredCapabilities;

public class EcozymAllFitur {
    private static AndroidDriver<AndroidElement> driver;
    private static WebDriverWait wait;
    
    // Test data constants
    private static final String TEST_EMAIL = "user@gmail.com";
    private static final String TEST_PASSWORD = "12345678";
    
    public static void main(String[] args) {
        try {
            setupDriver();
            runEcozymFullFlowTest();
            System.out.println("✅ Test completed successfully!");
        } catch (Exception e) {
            System.err.println("❌ Test failed with error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanupDriver();
        }
    }
    
    private static void setupDriver() throws MalformedURLException, URISyntaxException {
        System.out.println("🚀 Setting up Appium driver for Ecozym app...");
        
        DesiredCapabilities cap = new DesiredCapabilities();
        
        // APK setup
        File appDir = new File("src/appium");
        File app = new File(appDir, "ecozym.apk");
        
        if (!app.exists()) {
            throw new RuntimeException("❌ APK file not found at: " + app.getAbsolutePath());
        }
        
        System.out.println("📱 APK path: " + app.getAbsolutePath());
        
        // Core capabilities for Appium 2.0+
        cap.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Device");
        cap.setCapability("appium:udid", "5cbacd90");
        cap.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());
        cap.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
        cap.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        cap.setCapability("appium:appPackage", "com.ecozym.wastemanagement");
        
        // FIX: Remove appActivity to let Appium auto-detect the main activity
        // OR use the correct activity name without the dot prefix
        // cap.setCapability("appium:appActivity", "MainActivity"); // Try this if auto-detection fails
        
        cap.setCapability("appium:noReset", true);
        cap.setCapability("appium:fullReset", false);
        
        // Performance optimizations
        cap.setCapability("appium:skipServerInstallation", true);
        cap.setCapability("appium:skipDeviceInitialization", true);
        cap.setCapability("appium:skipLogcatCapture", true);
        cap.setCapability("appium:ignoreHiddenApiPolicyError", true);
        cap.setCapability("appium:allowTestPackages", true);
        
        // Real device settings
        cap.setCapability("appium:systemPort", 8201);
        cap.setCapability("appium:chromeDriverPort", 9516);
        
        // App management
        cap.setCapability("appium:autoGrantPermissions", true);
        
        // Timeout configurations
        cap.setCapability("appium:newCommandTimeout", 300);
        cap.setCapability("appium:androidInstallTimeout", 120000);
        cap.setCapability("appium:adbExecTimeout", 40000);
        cap.setCapability("appium:androidDeviceReadyTimeout", 60);
        
        // Additional optimizations
        cap.setCapability("appium:skipUnlock", true);
        cap.setCapability("appium:disableIdLocatorAutocompletion", true);
        
        URL serverUrl = new URI("http://127.0.0.1:4723").toURL();
        driver = new AndroidDriver<>(serverUrl, cap);
        wait = new WebDriverWait(driver, 20);
        
        // Wait for app to load
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.err.println("❌ Interrupted while waiting for app to load: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        System.out.println("✅ Driver setup completed successfully!");
    }
    
    private static void runEcozymFullFlowTest() throws InterruptedException {
        System.out.println("📋 Starting Ecozym full flow test...");
        
        // Wait for main element to ensure app is loaded
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.ecozym.wastemanagement:id/btn_signIn")));
        
        try {
            // Step 1: Click Sign In button
            clickSignInButton();
            
            // Step 2: Fill login credentials
            fillLoginCredentials();
            
            // Step 3: Click Log In button
            clickLogInButton();
            
            // Step 4: Navigate to Waste Pickup Tracking
            navigateToWastePickupTracking();
            
            // Step 5: Navigate to Register New Waste
            navigateToRegisterNewWaste();
            
            // Step 6: Navigate to Report
            navigateToReport();
            
            // Step 7: Navigate to Waste Pricing Guide
            navigateToWastePricingGuide();
            
            // Step 8: Navigate to Profile
            navigateToProfile();
            
            // Step 9: Click Logout button and confirm
            clickLogoutButton();
            
            System.out.println("🎉 Full test flow completed!");
            
        } catch (Exception e) {
            System.err.println("❌ Test execution failed: " + e.getMessage());
            debugCurrentScreen();
            throw e;
        }
    }
    
    private static void clickSignInButton() throws InterruptedException {
        System.out.println("🔍 Looking for Sign In button...");
        try {
            WebElement signInButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.id("com.ecozym.wastemanagement:id/btn_signIn")
                )
            );
            System.out.println("✅ Found Sign In button, clicking...");
            signInButton.click();
            Thread.sleep(2000);
        } catch (Exception e) {
            System.err.println("❌ Failed to click Sign In button: " + e.getMessage());
            debugCurrentScreen();
            throw new RuntimeException("Cannot locate Sign In button", e);
        }
    }
    
    private static void fillLoginCredentials() throws InterruptedException {
        System.out.println("📝 Filling login credentials...");
        
        try {
            // Try to find email field by different methods
            WebElement emailField = null;
            try {
                // Method 1: Try by resource-id
                emailField = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                        By.id("com.ecozym.wastemanagement:id/etEmail")
                    )
                );
            } catch (Exception e1) {
                try {
                    // Method 2: Try by hint text
                    emailField = wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//android.widget.EditText[@text='Email' or contains(@hint, 'Email') or contains(@hint, 'email')]")
                        )
                    );
                } catch (Exception e2) {
                    // Method 3: Try first EditText
                    System.out.println("⚠️ Email field not found by ID or hint, trying first EditText...");
                    List<AndroidElement> editTexts = driver.findElementsByClassName("android.widget.EditText");
                    if (!editTexts.isEmpty()) {
                        emailField = editTexts.get(0);
                    }
                }
            }
            
            if (emailField == null) {
                throw new RuntimeException("❌ Email field not found by any method");
            }
            
            emailField.clear();
            emailField.sendKeys(TEST_EMAIL);
            System.out.println("✅ Email entered successfully");
            Thread.sleep(1000);
            
            // Fill password field
            WebElement passwordField = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.id("com.ecozym.wastemanagement:id/etPassword")
                )
            );
            passwordField.clear();
            passwordField.sendKeys(TEST_PASSWORD);
            System.out.println("✅ Password entered successfully");
            Thread.sleep(1000);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to fill login credentials: " + e.getMessage());
            debugCurrentScreen();
            throw new RuntimeException("Cannot fill login credentials", e);
        }
    }
    
    private static void clickLogInButton() throws InterruptedException {
        System.out.println("🔍 Looking for Log In button...");
        try {
            // Try multiple methods to find the login button
            WebElement logInButton = null;
            
            try {
                // Method 1: Try by resource-id
                logInButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.id("com.ecozym.wastemanagement:id/btnLogin")
                    )
                );
            } catch (Exception e1) {
                try {
                    // Method 2: Try by text
                    logInButton = wait.until(
                        ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.widget.Button[@text='Log In' or @text='LOGIN' or @text='Sign In']")
                        )
                    );
                } catch (Exception e2) {
                    // Method 3: Use coordinates as fallback
                    System.out.println("⚠️ Using coordinate-based click for Log In button");
                    int x = (int) (22.5118 + 244.414 / 2);
                    int y = (int) (413.252 + 38.5917 / 2);
                    new TouchAction<>(driver).tap(PointOption.point(x, y)).perform();
                    System.out.println("✅ Clicked Log In button using coordinates");
                    Thread.sleep(3000);
                    return;
                }
            }
            
            if (logInButton != null) {
                logInButton.click();
                System.out.println("✅ Clicked Log In button");
                Thread.sleep(3000);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Failed to click Log In button: " + e.getMessage());
            debugCurrentScreen();
            throw new RuntimeException("Cannot locate Log In button", e);
        }
    }
    
    private static void navigateToWastePickupTracking() throws InterruptedException {
        System.out.println("🚚 Navigating to Waste Pickup Tracking...");
        try {
            WebElement navTruck = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.id("com.ecozym.wastemanagement:id/navTruck")
                )
            );
            System.out.println("✅ Found Waste Pickup Tracking navigation, clicking...");
            navTruck.click();
            Thread.sleep(2000);
        } catch (Exception e) {
            System.err.println("❌ Failed to navigate to Waste Pickup Tracking: " + e.getMessage());
            debugCurrentScreen();
            throw new RuntimeException("Cannot locate Waste Pickup Tracking navigation", e);
        }
    }
    
    private static void navigateToRegisterNewWaste() throws InterruptedException {
        System.out.println("🗑️ Navigating to Register New Waste...");
        try {
            WebElement navTrash = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.id("com.ecozym.wastemanagement:id/navTrash")
                )
            );
            System.out.println("✅ Found Register New Waste navigation, clicking...");
            navTrash.click();
            Thread.sleep(2000);
        } catch (Exception e) {
            System.err.println("❌ Failed to navigate to Register New Waste: " + e.getMessage());
            debugCurrentScreen();
            throw new RuntimeException("Cannot locate Register New Waste navigation", e);
        }
    }
    
    private static void navigateToReport() throws InterruptedException {
        System.out.println("📊 Navigating to Report...");
        try {
            // Note: This seems to use the same ID as Waste Pickup Tracking - verify this is correct
            WebElement navReport = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.id("com.ecozym.wastemanagement:id/navTruck")
                )
            );
            System.out.println("✅ Found Report navigation, clicking...");
            navReport.click();
            Thread.sleep(2000);
        } catch (Exception e) {
            System.err.println("❌ Failed to navigate to Report: " + e.getMessage());
            debugCurrentScreen();
            throw new RuntimeException("Cannot locate Report navigation", e);
        }
    }
    
    private static void navigateToWastePricingGuide() throws InterruptedException {
        System.out.println("📖 Navigating to Waste Pricing Guide...");
        try {
            WebElement navArticle = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.id("com.ecozym.wastemanagement:id/navArticle")
                )
            );
            System.out.println("✅ Found Waste Pricing Guide navigation, clicking...");
            navArticle.click();
            Thread.sleep(2000);
        } catch (Exception e) {
            System.err.println("❌ Failed to navigate to Waste Pricing Guide: " + e.getMessage());
            debugCurrentScreen();
            throw new RuntimeException("Cannot locate Waste Pricing Guide navigation", e);
        }
    }
    
    private static void navigateToProfile() throws InterruptedException {
        System.out.println("👤 Navigating to Profile...");
        try {
            WebElement btnProfile = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.id("com.ecozym.wastemanagement:id/btnProfile")
                )
            );
            System.out.println("✅ Found Profile navigation, clicking...");
            btnProfile.click();
            Thread.sleep(2000);
        } catch (Exception e) {
            System.err.println("❌ Failed to navigate to Profile: " + e.getMessage());
            debugCurrentScreen();
            throw new RuntimeException("Cannot locate Profile navigation", e);
        }
    }
    
    private static void clickLogoutButton() throws InterruptedException {
        System.out.println("🔍 Looking for Logout button...");
        try {
            WebElement logoutButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.id("com.ecozym.wastemanagement:id/btnLogout")
                )
            );
            System.out.println("✅ Found Logout button, clicking...");
            logoutButton.click();
            Thread.sleep(2000);
            
            // Confirm logout by clicking YES button
            System.out.println("🔍 Looking for YES button to confirm logout...");
            WebElement yesButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.id("android:id/button1")
                )
            );
            System.out.println("✅ Found YES button, clicking...");
            yesButton.click();
            Thread.sleep(2000);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to click Logout button or YES button: " + e.getMessage());
            debugCurrentScreen();
            throw new RuntimeException("Cannot locate Logout button or YES button", e);
        }
    }
    
    private static void debugCurrentScreen() {
        try {
            System.out.println("\n=== 🔍 DEBUG: Current Screen Elements ===");
            
            String pageSource = driver.getPageSource();
            System.out.println("Current screen contains " + pageSource.length() + " characters");
            
            // List visible TextViews
            List<AndroidElement> textViews = driver.findElementsByClassName("android.widget.TextView");
            System.out.println("Found " + textViews.size() + " TextViews:");
            
            for (int i = 0; i < Math.min(textViews.size(), 10); i++) {
                try {
                    AndroidElement element = textViews.get(i);
                    String text = element.getText();
                    String resourceId = element.getAttribute("resource-id");
                    boolean displayed = element.isDisplayed();
                    
                    if (text != null && !text.trim().isEmpty()) {
                        System.out.println("  TextView " + i + ": '" + text + "' [ID: " + resourceId + ", Visible: " + displayed + "]");
                    }
                } catch (Exception e) {
                    // Skip problematic elements
                }
            }
            
            // List EditTexts
            List<AndroidElement> editTexts = driver.findElementsByClassName("android.widget.EditText");
            System.out.println("\nFound " + editTexts.size() + " EditTexts:");
            
            for (int i = 0; i < editTexts.size(); i++) {
                try {
                    AndroidElement element = editTexts.get(i);
                    String hint = element.getAttribute("hint");
                    String text = element.getAttribute("text");
                    String resourceId = element.getAttribute("resource-id");
                    
                    System.out.println("  EditText " + i + ": hint='" + hint + "', text='" + text + "' [ID: " + resourceId + "]");
                } catch (Exception e) {
                    // Skip problematic elements
                }
            }
            
            // List Buttons
            List<AndroidElement> buttons = driver.findElementsByClassName("android.widget.Button");
            System.out.println("\nFound " + buttons.size() + " Buttons:");
            
            for (int i = 0; i < buttons.size(); i++) {
                try {
                    AndroidElement element = buttons.get(i);
                    String text = element.getText();
                    String resourceId = element.getAttribute("resource-id");
                    boolean enabled = element.isEnabled();
                    boolean displayed = element.isDisplayed();
                    
                    System.out.println("  Button " + i + ": '" + text + "' [ID: " + resourceId + ", Enabled: " + enabled + ", Visible: " + displayed + "]");
                } catch (Exception e) {
                    // Skip problematic elements
                }
            }
            
            System.out.println("=== END DEBUG ===\n");
            
        } catch (Exception e) {
            System.err.println("❌ Debug failed: " + e.getMessage());
        }
    }
    
    private static void cleanupDriver() {
        if (driver != null) {
            try {
                System.out.println("🧹 Cleaning up driver...");
                driver.quit();
                System.out.println("✅ Driver closed successfully");
            } catch (Exception e) {
                System.err.println("❌ Error closing driver: " + e.getMessage());
            }
        }
    }
}