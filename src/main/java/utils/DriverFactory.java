package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashMap;
import java.util.Map;

public class DriverFactory {
    public static WebDriver createDriver(String browser, String deviceName) {
        if (browser == null || browser.isEmpty()) {
            browser = "chrome";
        }

        boolean isheadless = ConfigReader.getBoolean("headless");

        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();

                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--start-maximized");

                return new FirefoxDriver(firefoxOptions);
            case "edge":
                WebDriverManager.edgedriver().setup();

                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--start-maximized");

                return new EdgeDriver(edgeOptions);
            default:
                WebDriverManager.chromedriver().setup();

                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setExperimentalOption("detach", false);

                if (isheadless) {
                    chromeOptions.addArguments(
                            "--headless=new",
                            "--disable-gpu",
                            "--no-sandbox",
                            "--window-size=1920,1080",
                            "--disable-dev-shm-usage"
                    );
                } else {
                    chromeOptions.addArguments("--start-maximized");
                }

                if (deviceName != null && !deviceName.isEmpty()) {
                    Map<String, String> mobileEmulation = new HashMap<>();
                    mobileEmulation.put("deviceName", deviceName.trim());

                    chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
                }
                return new ChromeDriver(chromeOptions);
        }
    }
}

