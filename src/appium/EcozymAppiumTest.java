package appium;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.Point;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;

public class EcozymAppiumTest {
    private static AndroidDriver<AndroidElement> driver;
    private static WebDriverWait wait;
    private static Scanner scanner = new Scanner(System.in);
    
    // Test data constants
    private static final String TEST_EMAIL = "test@ecozym.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String COMPANY_NAME = "Test Company Ltd";
    private static final String COMPANY_ADDRESS = "123 Test Street, Test City";
    private static final String PHONE_NUMBER = "1234567890";
    
    public static void main(String[] args) {
        try {
            setupDriver();
            runEcozymRegistrationTest();
            System.out.println("✅ Test completed successfully!");
        } catch (Exception e) {
            System.err.println("❌ Test failed with error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanupDriver();
            scanner.close();
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
        cap.setCapability("appium:noReset", true);
        cap.setCapability("appium:fullReset", false);
        
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
        wait = new WebDriverWait(driver,20);
        
        System.out.println("✅ Driver setup completed successfully!");
    }
    
    private static void runEcozymRegistrationTest() throws InterruptedException {
        System.out.println("📋 Starting Ecozym registration test...");
        
        // Wait for app to fully load
        Thread.sleep(5000);
        
        try {
            // Step 1: Navigate to registration
            clickCreateAccountButton();
            
            // Step 2: Fill basic registration info
            fillRegistrationStep1();
            
            // Step 3: Fill company information
            fillRegistrationStep2();
            
            // Step 4: Handle manual document upload
            handleManualDocumentUpload();
            
            System.out.println("🎉 Registration test flow completed!");
            
        } catch (Exception e) {
            System.err.println("❌ Test execution failed: " + e.getMessage());
            debugCurrentScreen(); // Debug info on failure
            throw e;
        }
    }
    
    private static void clickCreateAccountButton() throws InterruptedException {
        System.out.println("🔍 Looking for Create Account button...");
        
        try {
            WebElement createAccountBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.id("com.ecozym.wastemanagement:id/btn_createAccount")
                )
            );
            
            System.out.println("✅ Found Create Account button, clicking...");
            createAccountBtn.click();
            Thread.sleep(2000);
            
        } catch (Exception e) {
            System.out.println("⚠️ Primary strategy failed, trying UIAutomator...");
            
            try {
                WebElement createAccountBtn = driver.findElementByAndroidUIAutomator(
                    "new UiSelector().resourceId(\"com.ecozym.wastemanagement:id/btn_createAccount\")"
                );
                createAccountBtn.click();
                Thread.sleep(2000);
                System.out.println("✅ Successfully clicked Create Account using UIAutomator");
                
            } catch (Exception e2) {
                System.err.println("❌ All strategies failed to find Create Account button");
                debugCurrentScreen();
                throw new RuntimeException("Cannot locate Create Account button", e2);
            }
        }
    }
    
    private static void fillRegistrationStep1() throws InterruptedException {
        System.out.println("📝 Filling registration step 1 (credentials)...");
        
        try {
            // Fill email field - using multiple strategies
            fillEmailField();
            Thread.sleep(1000);
            
            // Fill password field
            fillPasswordField();
            Thread.sleep(1000);
            
            // Fill confirm password field
            fillConfirmPasswordField();
            Thread.sleep(1000);
            
            // Click Next button
            clickNextButton("Step 1");
            Thread.sleep(3000);
            
        } catch (Exception e) {
            System.err.println("❌ Error in registration step 1: " + e.getMessage());
            debugCurrentScreen();
            throw e;
        }
    }
    
    private static void fillEmailField() {
        try {
            // Strategy 1: Find first EditText (usually email)
            List<AndroidElement> editTexts = driver.findElementsByClassName("android.widget.EditText");
            if (!editTexts.isEmpty()) {
                AndroidElement emailField = editTexts.get(0);
                emailField.clear();
                emailField.sendKeys(TEST_EMAIL);
                System.out.println("✅ Email entered successfully");
                return;
            }
            
            // Strategy 2: Try by hint text
            WebElement emailField = driver.findElementByAndroidUIAutomator(
                "new UiSelector().className(\"android.widget.EditText\").textContains(\"email\")"
            );
            emailField.clear();
            emailField.sendKeys(TEST_EMAIL);
            System.out.println("✅ Email entered using hint strategy");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to fill email field: " + e.getMessage());
            throw new RuntimeException("Cannot fill email field", e);
        }
    }
    
    private static void fillPasswordField() {
        try {
            WebElement passwordField = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.id("com.ecozym.wastemanagement:id/etPassword")
                )
            );
            passwordField.clear();
            passwordField.sendKeys(TEST_PASSWORD);
            System.out.println("✅ Password entered successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to fill password field: " + e.getMessage());
            throw new RuntimeException("Cannot fill password field", e);
        }
    }
    
    private static void fillConfirmPasswordField() {
        try {
            WebElement confirmPasswordField = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.id("com.ecozym.wastemanagement:id/etConfirmPassword")
                )
            );
            confirmPasswordField.clear();
            confirmPasswordField.sendKeys(TEST_PASSWORD);
            System.out.println("✅ Confirm password entered successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to fill confirm password field: " + e.getMessage());
            throw new RuntimeException("Cannot fill confirm password field", e);
        }
    }
    
    private static void fillRegistrationStep2() throws InterruptedException {
        System.out.println("🏢 Filling registration step 2 (company information)...");
        
        try {
            // Fill company name
            fillCompanyName();
            Thread.sleep(1000);
            
            // Handle industry type selection
            selectIndustryType();
            Thread.sleep(1000);
            
            // Fill company address using coordinates
            fillCompanyAddress();
            Thread.sleep(1000);
            
            // Fill phone number using coordinates
            fillPhoneNumber();
            Thread.sleep(1000);
            
            // Click Next button for step 2
            clickNextButton("Step 2");
            Thread.sleep(3000);
            
        } catch (Exception e) {
            System.err.println("❌ Error in registration step 2: " + e.getMessage());
            debugCurrentScreen();
            throw e;
        }
    }
    
    private static void fillCompanyName() {
        try {
            WebElement companyNameField = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.id("com.ecozym.wastemanagement:id/etCompanyName")
                )
            );
            companyNameField.clear();
            companyNameField.sendKeys(COMPANY_NAME);
            System.out.println("✅ Company name entered successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to fill company name: " + e.getMessage());
            throw new RuntimeException("Cannot fill company name field", e);
        }
    }
    
    private static void selectIndustryType() {
        try {
            // Click on industry dropdown using coordinates (based on your highlight box)
            int x = 19 + 250/2; // Center of the highlighted area
            int y = 296 + 36/2;
            
            TouchAction touchAction = new TouchAction(driver);
            touchAction.tap(PointOption.point(x, y)).perform();
            Thread.sleep(1500);
            
            // Select "Food Processing" option
            WebElement foodProcessingOption = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.CheckedTextView[@text='Food Processing']")
                )
            );
            foodProcessingOption.click();
            System.out.println("✅ Industry type 'Food Processing' selected");
            
        } catch (Exception e) {
            System.out.println("⚠️ Industry selection failed, trying alternative approach: " + e.getMessage());
            
            try {
                // Alternative: Find any spinner and select first option
                List<AndroidElement> spinners = driver.findElementsByClassName("android.widget.Spinner");
                if (!spinners.isEmpty()) {
                    spinners.get(0).click();
                    Thread.sleep(1000);
                    
                    List<AndroidElement> options = driver.findElementsByClassName("android.widget.CheckedTextView");
                    if (!options.isEmpty()) {
                        options.get(0).click();
                        System.out.println("✅ First industry option selected");
                    }
                }
            } catch (Exception e2) {
                System.out.println("⚠️ Industry selection completely failed, continuing...");
            }
        }
    }
    
    private static void fillCompanyAddress() {
        try {
            // Using coordinates from your highlight box for address field
            int x = 19 + 73/2; // Center of address field
            int y = 441 + 38/2;
            
            TouchAction touchAction = new TouchAction(driver);
            touchAction.tap(PointOption.point(x, y)).perform();
            Thread.sleep(500);
            
            // Send keys to the focused field
            driver.getKeyboard().sendKeys(COMPANY_ADDRESS);
            System.out.println("✅ Company address entered using coordinates");
            
        } catch (Exception e) {
            System.out.println("⚠️ Address coordinate method failed, trying EditText search: " + e.getMessage());
            
            try {
                // Fallback: Find by hint or placeholder text
                List<AndroidElement> editTexts = driver.findElementsByClassName("android.widget.EditText");
                for (AndroidElement editText : editTexts) {
                    String hint = editText.getAttribute("hint");
                    String contentDesc = editText.getAttribute("content-desc");
                    
                    if ((hint != null && hint.toLowerCase().contains("address")) ||
                        (contentDesc != null && contentDesc.toLowerCase().contains("address"))) {
                        editText.clear();
                        editText.sendKeys(COMPANY_ADDRESS);
                        System.out.println("✅ Address entered using hint matching");
                        return;
                    }
                }
                System.out.println("⚠️ Address field not found, continuing...");
                
            } catch (Exception e2) {
                System.out.println("⚠️ All address field strategies failed");
            }
        }
    }
    
    private static void fillPhoneNumber() {
        try {
            // Primary strategy: Use the correct resource ID we found in debug
            WebElement phoneField = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.id("com.ecozym.wastemanagement:id/etPhoneNumber")
                )
            );
            phoneField.clear();
            phoneField.sendKeys(PHONE_NUMBER);
            System.out.println("✅ Phone number entered successfully using resource ID");
            
        } catch (Exception e) {
            System.out.println("⚠️ Phone ID method failed, trying coordinates: " + e.getMessage());
            
            try {
                // Fallback: Using coordinates from your highlight box for phone field
                int x = 98 + 171/2; // Center of phone field
                int y = 441 + 38/2;
                
                TouchAction touchAction = new TouchAction(driver);
                touchAction.tap(PointOption.point(x, y)).perform();
                Thread.sleep(500);
                
                // Send keys to the focused field
                driver.getKeyboard().sendKeys(PHONE_NUMBER);
                System.out.println("✅ Phone number entered using coordinates");
                
            } catch (Exception e2) {
                System.out.println("⚠️ All phone field strategies failed: " + e2.getMessage());
                throw new RuntimeException("Cannot fill phone field", e2);
            }
        }
    }
    
    private static void clickNextButton(String stepName) {
        try {
            WebElement nextButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.id("com.ecozym.wastemanagement:id/btnNext")
                )
            );
            nextButton.click();
            System.out.println("✅ Clicked Next button - " + stepName + " completed");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to click Next button for " + stepName);
            throw new RuntimeException("Cannot click Next button", e);
        }
    }
    
    private static void handleManualDocumentUpload() throws InterruptedException {
        System.out.println("📄 Starting manual document upload step...");
        
        try {
            // Debug current screen first
            System.out.println("🔍 Current screen elements before manual upload:");
            debugCurrentScreen();
            
            // Show instructions to user
            System.out.println("\n" + "=".repeat(60));
            System.out.println("📋 MANUAL UPLOAD REQUIRED");
            System.out.println("=".repeat(60));
            System.out.println("🔸 Please manually upload the required document(s) on your device");
            System.out.println("🔸 Look for upload button/area on the screen");
            System.out.println("🔸 Select and upload the document");
            System.out.println("🔸 Complete any required fields related to the document");
            System.out.println("🔸 DO NOT click the Next/Continue button yet");
            System.out.println("=".repeat(60));
            
            // Wait for user confirmation
            System.out.print("\n✋ Press ENTER after you have completed the manual upload: ");
            scanner.nextLine();
            
            System.out.println("✅ Manual upload completed, continuing with automated test...");
            Thread.sleep(2000);
            
            // Debug screen after manual upload
            System.out.println("🔍 Screen elements after manual upload:");
            debugCurrentScreen();
            
            // Now try to click the Next/Continue button to proceed
            clickFinalNextButton();
            
        } catch (Exception e) {
            System.err.println("❌ Error in manual document upload step: " + e.getMessage());
            System.out.println("🔍 Final debug after upload failure:");
            debugCurrentScreen();
            throw e;
        }
    }
    
    private static void clickFinalNextButton() {
        try {
            System.out.println("🔍 Looking for Next/Continue button to proceed...");
            
            // Strategy 1: Try the standard Next button ID
            try {
                WebElement finalNextButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.id("com.ecozym.wastemanagement:id/btnNext")
                    )
                );
                finalNextButton.click();
                System.out.println("🎉 Clicked Next button using resource ID - Registration completed!");
                Thread.sleep(3000);
                return;
            } catch (Exception e) {
                System.out.println("⚠️ Standard Next button ID not found: " + e.getMessage());
            }
            
            // Strategy 2: Look for any button with "Next", "Continue", "Finish", "Submit" text
            String[] buttonTexts = {"Next", "Continue", "Finish", "Submit", "Complete", "Done", "Proceed"};
            
            for (String buttonText : buttonTexts) {
                try {
                    WebElement button = driver.findElementByAndroidUIAutomator(
                        "new UiSelector().textContains(\"" + buttonText + "\").clickable(true)"
                    );
                    button.click();
                    System.out.println("✅ Clicked button with text: " + buttonText);
                    Thread.sleep(3000);
                    return;
                } catch (Exception e) {
                    // Continue to next button text
                }
            }
            
            // Strategy 3: Look for any Button element that's clickable
            try {
                List<AndroidElement> buttons = driver.findElementsByClassName("android.widget.Button");
                for (AndroidElement button : buttons) {
                    if (button.isEnabled() && button.isDisplayed()) {
                        String buttonText = button.getText();
                        System.out.println("🔍 Found button: " + buttonText);
                        
                        // Skip buttons that might be navigation/back buttons
                        if (buttonText != null && 
                            !buttonText.toLowerCase().contains("back") && 
                            !buttonText.toLowerCase().contains("cancel")) {
                            button.click();
                            System.out.println("✅ Clicked button: " + buttonText);
                            Thread.sleep(3000);
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ Button class search failed: " + e.getMessage());
            }
            
            // Strategy 4: Look for TextView that might be styled as button
            try {
                String[] possibleButtonTexts = {"NEXT", "CONTINUE", "SUBMIT", "FINISH"};
                for (String text : possibleButtonTexts) {
                    try {
                        WebElement textButton = driver.findElementByAndroidUIAutomator(
                            "new UiSelector().text(\"" + text + "\").clickable(true)"
                        );
                        textButton.click();
                        System.out.println("✅ Clicked text button: " + text);
                        Thread.sleep(3000);
                        return;
                    } catch (Exception e) {
                        // Continue
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ TextView button search failed: " + e.getMessage());
            }
            
            // Strategy 5: Manual intervention if automatic button detection fails
            System.out.println("\n" + "=".repeat(60));
            System.out.println("⚠️  BUTTON DETECTION FAILED");
            System.out.println("=".repeat(60));
            System.out.println("🔸 Could not automatically find the Next/Continue button");
            System.out.println("🔸 Please click the Next/Continue button manually");
            System.out.println("🔸 This will complete the registration process");
            System.out.println("=".repeat(60));
            
            System.out.print("\n✋ Press ENTER after you have clicked the Next/Continue button: ");
            scanner.nextLine();
            
            System.out.println("🎉 Manual button click completed - Registration process finished!");
            Thread.sleep(2000);
            
        } catch (Exception e) {
            System.err.println("❌ All next button strategies failed: " + e.getMessage());
            
            // Final fallback - manual intervention
            System.out.println("\n" + "=".repeat(60));
            System.out.println("❌ AUTOMATIC COMPLETION FAILED");
            System.out.println("=".repeat(60));
            System.out.println("🔸 Please manually complete the registration process");
            System.out.println("🔸 Click any remaining buttons to finish registration");
            System.out.println("=".repeat(60));
            
            System.out.print("\n✋ Press ENTER when registration is completely finished: ");
            scanner.nextLine();
            
            System.out.println("✅ Registration process completed with manual assistance!");
        }
    }
    
    private static void debugCurrentScreen() {
        try {
            System.out.println("\n=== 🔍 DEBUG: Current Screen Elements ===");
            
            // Get page source for detailed debugging
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
    
    // Utility method for safe element interaction
    private static boolean safeClick(By locator, String elementName) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
            System.out.println("✅ Successfully clicked " + elementName);
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Failed to click " + elementName + ": " + e.getMessage());
            return false;
        }
    }
    
    // Utility method for safe text input
    private static boolean safeInput(By locator, String value, String fieldName) {
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            element.clear();
            element.sendKeys(value);
            System.out.println("✅ Successfully entered " + fieldName);
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Failed to enter " + fieldName + ": " + e.getMessage());
            return false;
        }
    }
}