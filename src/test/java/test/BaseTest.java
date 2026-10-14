package test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import utils.DriverFactory;
import utils.ScreenshotUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseTest {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> waitThreadLocal = new ThreadLocal<>();

    protected WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    protected WebDriverWait getWait() {
        return waitThreadLocal.get();
    }

    @Parameters({"browser", "device"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser,
                      @Optional("") String device,
                      Method method) throws IOException {
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String folderName = String.format("%s_%s_%s_%s_%s", className, methodName, timestamp, browser, device);
        String testFolderPath = "target/test-output/" + folderName;

        Files.createDirectories(Paths.get(testFolderPath));
        ScreenshotUtil.setTestFolder(testFolderPath);

        WebDriver driver = DriverFactory.createDriver(browser, device);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driverThreadLocal.set(driver);
        waitThreadLocal.set(wait);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        WebDriver driver = getDriver();

        try {
            if (driver != null && result.getStatus() == ITestResult.FAILURE) {
                try {
                    ScreenshotUtil.takeScreenshot(driver, "FAILED_" + result.getName());
                } catch (RuntimeException screenshotError) {
                    System.err.println("Không chụp được screenshot khi test lỗi: " + screenshotError.getMessage());
                }
            }
        } finally {
            try {
                if (driver != null) {
                    driver.quit();
                }
            } catch (RuntimeException quitError) {
                System.err.println("Không đóng được WebDriver: " + quitError.getMessage());
            } finally {
                ScreenshotUtil.clear();
                driverThreadLocal.remove();
                waitThreadLocal.remove();
            }
        }
    }
}
