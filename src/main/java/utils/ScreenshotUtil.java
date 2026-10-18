package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

public class ScreenshotUtil {
    private static final ThreadLocal<String> testFolder = new ThreadLocal<>();

    private static final ThreadLocal<Integer> stepCount = new ThreadLocal<>();

    public static void setTestFolder(String folderPath) {
        testFolder.set(folderPath);

        stepCount.set(0);
    }

    public static void clear() {
        testFolder.remove();
        stepCount.remove();
    }

    public static void takeScreenshot(WebDriver driver, String stepName) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(d -> "complete".equals(
                            ((JavascriptExecutor) d)
                                    .executeScript("return document.readyState")
                    ));
        } catch (TimeoutException ignored){}
        try {
            String testFolderName = testFolder.get();

            String screenshotFolderName = testFolderName + File.separator + "screenshots";
            Files.createDirectories(Paths.get(screenshotFolderName));

            Integer stepNumber = stepCount.get();
            stepNumber = (stepNumber == null) ? 1 : stepNumber + 1;
            stepCount.set(stepNumber);

            String fileName = String.format("step_%02d_%s.png", stepNumber, stepName);

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            File destFile = new File(screenshotFolderName, fileName);
            Files.copy(srcFile.toPath(), destFile.toPath());
        } catch (IOException e) {
            System.out.println("Error taking screenshot: " + e.getMessage());
        }
    }
}
