package appium;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;
import io.github.bonigarcia.wdm.WebDriverManager;

public class SeleniumRunner {
    public static void main(String[] args) {
        // Setup ChromeDriver
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        
        try {
            // Memperbesar window browser untuk menghindari mobile layout
            driver.manage().window().maximize();
            
            // Eksekusi skenario 1: Input data lengkap
            runScenario1(driver);
            
            // Reset browser untuk skenario 2
            driver.get("https://krs.usk.ac.id/");
            Thread.sleep(3000);
            
            // Eksekusi skenario 2: Input data kosong
            runScenario2(driver);
            
            // Reset browser untuk skenario 3
            driver.get("https://krs.usk.ac.id/");
            Thread.sleep(3000);
            
            // Eksekusi skenario 3: Input nomor ujian dan tanggal lahir asal-asalan
            runScenario3(driver);
            
        } catch (Exception e) {
            System.out.println("Terjadi error utama: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Biarkan browser tetap terbuka agar bisa dilihat hasilnya
            // Uncomment jika ingin browser ditutup otomatis
            // driver.quit();
            
            System.out.println("Semua skenario testing selesai dijalankan.");
        }
    }
    
    // Fungsi untuk menavigasi dari halaman utama ke halaman Cek NPM
    private static void navigasiKeHalamanCekNPM(WebDriver driver, WebDriverWait wait) throws Exception {
        // Akses halaman utama KRS USK
        driver.get("https://krs.usk.ac.id/");
        System.out.println("Mengakses halaman utama KRS USK");
        Thread.sleep(3000);
        
        // Cari dan klik menu Cek NPM
        try {
            WebElement cekNPMMenu = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Cek NPM') or contains(@href, 'cek-npm')]")
            ));
            cekNPMMenu.click();
            System.out.println("Berhasil klik menu Cek NPM dengan selector utama");
        } catch (Exception e) {
            System.out.println("Selector menu utama gagal, mencoba alternatif...");
            List<WebElement> links = driver.findElements(By.tagName("a"));
            boolean clicked = false;
            for (WebElement link : links) {
                if (link.getText().contains("Cek NPM") || 
                    (link.getAttribute("href") != null && link.getAttribute("href").contains("cek-npm"))) {
                    link.click();
                    clicked = true;
                    System.out.println("Berhasil klik menu Cek NPM dengan selector alternatif");
                    break;
                }
            }
            if (!clicked) {
                System.out.println("Tidak dapat menemukan menu Cek NPM, mengakses URL secara langsung");
                driver.get("https://krs.usk.ac.id/profile/cek-npm");
            }
        }
        
        // Tunggu halaman Cek NPM dimuat
        Thread.sleep(2000);
    }
    
    // Fungsi untuk menangkap hasil NPM atau pesan error
    private static void captureResult(WebDriver driver, WebDriverWait wait) {
        try {
            // Tunggu hingga elemen hasil muncul (maksimal 10 detik)
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".alert, .alert-success, .alert-danger, .swal2-content, [class*='result'], [id*='result'], p, span")
            ));
            
            // Cari semua elemen yang mungkin berisi hasil atau pesan error
            List<WebElement> resultElements = driver.findElements(
                By.cssSelector(".alert, .alert-success, .alert-danger, .swal2-content, [class*='result'], [id*='result'], p, span")
            );
            
            boolean resultFound = false;
            for (WebElement element : resultElements) {
                if (element.isDisplayed()) {
                    String resultText = element.getText().trim();
                    if (!resultText.isEmpty()) {
                        // Asumsi NPM mengandung angka panjang atau kata kunci seperti "NPM", "Nomor Pokok Mahasiswa"
                        if (resultText.matches(".*\\d{8,}.*") || 
                            resultText.toLowerCase().contains("npm") || 
                            resultText.toLowerCase().contains("nomor pokok mahasiswa")) {
                            System.out.println("Hasil NPM ditemukan: " + resultText);
                        } else {
                            System.out.println("Pesan hasil/error ditemukan: " + resultText);
                        }
                        resultFound = true;
                    }
                }
            }
            
            if (!resultFound) {
                System.out.println("Tidak ada hasil NPM atau pesan error yang terdeteksi dengan selector.");
            }
            
        } catch (Exception e) {
            System.out.println("Error saat mencoba menangkap hasil NPM atau pesan error: " + e.getMessage());
        }
    }
    
    // Skenario 1: Input data lengkap
    private static void runScenario1(WebDriver driver) {
        System.out.println("===== Menjalankan Skenario 1: Input Data Lengkap =====");
        WebDriverWait wait = new WebDriverWait(driver, 15);
        
        try {
            navigasiKeHalamanCekNPM(driver, wait);
            
            try {
                WebElement jenjangButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.btn[data-jenjang='1']")
                ));
                jenjangButton.click();
                System.out.println("Berhasil klik tombol jenjang S1/D3/D4 dengan selector utama");
            } catch (Exception e) {
                System.out.println("Selector utama gagal, mencoba alternatif...");
                List<WebElement> buttons = driver.findElements(By.tagName("button"));
                boolean clicked = false;
                for (WebElement button : buttons) {
                    if (button.getText().contains("S1/D3/D4")) {
                        button.click();
                        clicked = true;
                        System.out.println("Berhasil klik tombol jenjang S1/D3/D4 dengan selector alternatif");
                        break;
                    }
                }
                if (!clicked) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("document.querySelector('button[data-jenjang=\"1\"]').click();");
                    System.out.println("Mencoba klik tombol jenjang S1/D3/D4 dengan JavaScript");
                }
            }
            
            Thread.sleep(2000);
            
            try {
                WebElement nomorUjianInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("input[name='email'][placeholder*='Nomor Ujian']")
                ));
                nomorUjianInput.clear();
                nomorUjianInput.sendKeys("425172915");
                System.out.println("Berhasil mengisi Nomor Ujian");
            } catch (Exception e) {
                System.out.println("Selector input nomor ujian gagal, mencoba alternatif...");
                List<WebElement> inputs = driver.findElements(By.tagName("input"));
                for (WebElement input : inputs) {
                    String placeholder = input.getAttribute("placeholder");
                    if (placeholder != null && placeholder.contains("Nomor Ujian")) {
                        input.clear();
                        input.sendKeys("425172915");
                        System.out.println("Berhasil mengisi Nomor Ujian dengan selector alternatif");
                        break;
                    }
                }
            }
            
            try {
                WebElement tanggalLahirInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("input[name='password'][placeholder*='Tanggal Lahir']")
                ));
                tanggalLahirInput.clear();
                tanggalLahirInput.sendKeys("17-04-2007");
                System.out.println("Berhasil mengisi Tanggal Lahir");
            } catch (Exception e) {
                System.out.println("Selector input tanggal lahir gagal, mencoba alternatif...");
                List<WebElement> inputs = driver.findElements(By.tagName("input"));
                for (WebElement input : inputs) {
                    String placeholder = input.getAttribute("placeholder");
                    if (placeholder != null && placeholder.contains("Tanggal Lahir")) {
                        input.clear();
                        input.sendKeys("17-04-2007");
                        System.out.println("Berhasil mengisi Tanggal Lahir dengan selector alternatif");
                        break;
                    }
                }
            }
            
            try {
                WebElement cekButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit'].btn-primary")
                ));
                cekButton.click();
                System.out.println("Berhasil klik tombol CEK");
            } catch (Exception e) {
                System.out.println("Selector tombol cek gagal, mencoba alternatif...");
                List<WebElement> buttons = driver.findElements(By.tagName("button"));
                boolean clicked = false;
                for (WebElement button : buttons) {
                    if (button.getText().contains("CEK")) {
                        button.click();
                        clicked = true;
                        System.out.println("Berhasil klik tombol CEK dengan selector alternatif");
                        break;
                    }
                }
                if (!clicked) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("document.querySelector('button[type=\"submit\"].btn-primary').click();");
                    System.out.println("Mencoba klik tombol CEK dengan JavaScript");
                }
            }
            
            // Tunggu hasil proses dan tangkap hasil NPM
            System.out.println("Menunggu hasil pengecekan NPM untuk Skenario 1...");
            Thread.sleep(5000);
            captureResult(driver, wait);
            
        } catch (Exception e) {
            System.out.println("Error pada Skenario 1: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Skenario 2: Input data kosong
    private static void runScenario2(WebDriver driver) {
        System.out.println("===== Menjalankan Skenario 2: Input Data Kosong =====");
        WebDriverWait wait = new WebDriverWait(driver, 15);
        
        try {
            navigasiKeHalamanCekNPM(driver, wait);
            
            try {
                WebElement jenjangButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.btn[data-jenjang='1']")
                ));
                jenjangButton.click();
                System.out.println("Berhasil klik tombol jenjang S1/D3/D4 dengan selector utama");
            } catch (Exception e) {
                System.out.println("Selector utama gagal, mencoba alternatif...");
                List<WebElement> buttons = driver.findElements(By.tagName("button"));
                boolean clicked = false;
                for (WebElement button : buttons) {
                    if (button.getText().contains("S1/D3/D4")) {
                        button.click();
                        clicked = true;
                        System.out.println("Berhasil klik tombol jenjang S1/D3/D4 dengan selector alternatif");
                        break;
                    }
                }
                if (!clicked) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("document.querySelector('button[data-jenjang=\"1\"]').click();");
                    System.out.println("Mencoba klik tombol jenjang S1/D3/D4 dengan JavaScript");
                }
            }
            
            Thread.sleep(2000);
            
            try {
                WebElement cekButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit'].btn-primary")
                ));
                cekButton.click();
                System.out.println("Berhasil klik tombol CEK");
            } catch (Exception e) {
                System.out.println("Selector tombol cek gagal, mencoba alternatif...");
                List<WebElement> buttons = driver.findElements(By.tagName("button"));
                boolean clicked = false;
                for (WebElement button : buttons) {
                    if (button.getText().contains("CEK")) {
                        button.click();
                        clicked = true;
                        System.out.println("Berhasil klik tombol CEK dengan selector alternatif");
                        break;
                    }
                }
                if (!clicked) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("document.querySelector('button[type=\"submit\"].btn-primary').click();");
                    System.out.println("Mencoba klik tombol CEK dengan JavaScript");
                }
            }
            
            // Tunggu hasil proses dan tangkap pesan error
            System.out.println("Menunggu hasil validasi form untuk Skenario 2...");
            Thread.sleep(5000);
            captureResult(driver, wait);
            
        } catch (Exception e) {
            System.out.println("Error pada Skenario 2: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Skenario 3: Input nomor ujian dan tanggal lahir asal-asalan
    private static void runScenario3(WebDriver driver) {
        System.out.println("===== Menjalankan Skenario 3: Input Nomor Ujian dan Tanggal Lahir Asal-asalan =====");
        WebDriverWait wait = new WebDriverWait(driver, 15);
        
        try {
            navigasiKeHalamanCekNPM(driver, wait);
            
            try {
                WebElement jenjangButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.btn[data-jenjang='1']")
                ));
                jenjangButton.click();
                System.out.println("Berhasil klik tombol jenjang S1/D3/D4 dengan selector utama");
            } catch (Exception e) {
                System.out.println("Selector utama gagal, mencoba alternatif...");
                List<WebElement> buttons = driver.findElements(By.tagName("button"));
                boolean clicked = false;
                for (WebElement button : buttons) {
                    if (button.getText().contains("S1/D3/D4")) {
                        button.click();
                        clicked = true;
                        System.out.println("Berhasil klik tombol jenjang S1/D3/D4 dengan selector alternatif");
                        break;
                    }
                }
                if (!clicked) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("document.querySelector('button[data-jenjang=\"1\"]').click();");
                    System.out.println("Mencoba klik tombol jenjang S1/D3/D4 dengan JavaScript");
                }
            }
            
            Thread.sleep(2000);
            
            try {
                WebElement nomorUjianInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("input[name='email'][placeholder*='Nomor Ujian']")
                ));
                nomorUjianInput.clear();
                nomorUjianInput.sendKeys("123");
                System.out.println("Berhasil mengisi Nomor Ujian dengan data asal-asalan: 123");
            } catch (Exception e) {
                System.out.println("Selector input nomor ujian gagal, mencoba alternatif...");
                List<WebElement> inputs = driver.findElements(By.tagName("input"));
                for (WebElement input : inputs) {
                    String placeholder = input.getAttribute("placeholder");
                    if (placeholder != null && placeholder.contains("Nomor Ujian")) {
                        input.clear();
                        input.sendKeys("123");
                        System.out.println("Berhasil mengisi Nomor Ujian dengan selector alternatif: 123");
                        break;
                    }
                }
            }
            
            try {
                WebElement tanggalLahirInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("input[name='password'][placeholder*='Tanggal Lahir']")
                ));
                tanggalLahirInput.clear();
                tanggalLahirInput.sendKeys("01-01-2001");
                System.out.println("Berhasil mengisi Tanggal Lahir dengan data asal-asalan: 01-01-2001");
            } catch (Exception e) {
                System.out.println("Selector input tanggal lahir gagal, mencoba alternatif...");
                List<WebElement> inputs = driver.findElements(By.tagName("input"));
                for (WebElement input : inputs) {
                    String placeholder = input.getAttribute("placeholder");
                    if (placeholder != null && placeholder.contains("Tanggal Lahir")) {
                        input.clear();
                        input.sendKeys("01-01-2001");
                        System.out.println("Berhasil mengisi Tanggal Lahir dengan selector alternatif: 01-01-2001");
                        break;
                    }
                }
            }
            
            try {
                WebElement cekButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit'].btn-primary")
                ));
                cekButton.click();
                System.out.println("Berhasil klik tombol CEK");
            } catch (Exception e) {
                System.out.println("Selector tombol cek gagal, mencoba alternatif...");
                List<WebElement> buttons = driver.findElements(By.tagName("button"));
                boolean clicked = false;
                for (WebElement button : buttons) {
                    if (button.getText().contains("CEK")) {
                        button.click();
                        clicked = true;
                        System.out.println("Berhasil klik tombol CEK dengan selector alternatif");
                        break;
                    }
                }
                if (!clicked) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("document.querySelector('button[type=\"submit\"].btn-primary').click();");
                    System.out.println("Mencoba klik tombol CEK dengan JavaScript");
                }
            }
            
            // Tunggu hasil proses dan tangkap hasil NPM atau pesan error
            System.out.println("Menunggu hasil validasi form untuk Skenario 3...");
            Thread.sleep(5000);
            captureResult(driver, wait);
            
        } catch (Exception e) {
            System.out.println("Error pada Skenario 3: " + e.getMessage());
            e.printStackTrace();
        }
    }
}