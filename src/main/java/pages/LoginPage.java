package pages;

import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ScreenshotUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LoginPage extends BasePage {

    private static final By LOGIN_PAGE =
            By.xpath("//div[@role='dialog' and @aria-modal='true']");
    private static final By EMAIL_INPUT =
            By.xpath("//div[@role='dialog']//input[@placeholder='Vui lòng nhập tài khoản']");
    private static final By PASSWORD_INPUT =
            By.xpath("//div[@role='dialog']//input[@placeholder='Vui lòng nhập mật khẩu']");
    private static final By LOGIN_BUTTON =
            By.xpath("//div[@role='dialog']//button[normalize-space()='Đăng nhập']");

    private static final By EMAIL_VALIDATION =
            By.xpath("//p[normalize-space()='Vui lòng không bỏ trống'][1]");
    private static final By PASSWORD_VALIDATION =
            By.xpath("//p[normalize-space()='Vui lòng không bỏ trống'][2]");
    private static final By LOGIN_SUCCESS =
            By.xpath("//span[normalize-space()='Đăng nhập thành công']");
    private static final By LOGIN_CREDENTIALS =
            By.xpath("//span[normalize-space()='Email hoặc mật khẩu không đúng !']");
    private static final By EMAIL_FORMAT_ERROR =
            By.xpath("//p[normalize-space()='Vui lòng nhập đúng định dạng email']");

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
        Allure.step("Mở khung đăng nhập", () -> {
            waitForVisible(LOGIN_PAGE);
            ScreenshotUtil.takeScreenshot(driver, "login_page");
        });
    }

    public void enterEmail(String email) {
        Allure.step("Nhập email", () -> {
            WebElement emailInput = waitForVisible(EMAIL_INPUT);
            highlight(emailInput);
            type(EMAIL_INPUT, email);
            ScreenshotUtil.takeScreenshot(driver, "enter_email");
            unhighlight(emailInput);
        });
    }

    public void enterPassword(String password) {
        Allure.step("Nhập mật khẩu", () -> {
            WebElement passwordInput = waitForVisible(PASSWORD_INPUT);
            highlight(passwordInput);
            type(PASSWORD_INPUT, password);
            ScreenshotUtil.takeScreenshot(driver, "enter_password");
            unhighlight(passwordInput);
        });
    }

    public void clickLoginButton() {
        Allure.step("Click login button", () -> {
            WebElement loginButton = waitForClickable(LOGIN_BUTTON);
            highlight(loginButton);
            ScreenshotUtil.takeScreenshot(driver, "click_login_button");
            unhighlight(loginButton);
            click(LOGIN_BUTTON);
        });
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLoginButton();
    }

    public enum Result {SUCCESS, WRONG_CREDENTIALS, VALIDATION_ERROR, INVALID_EMAIL, FAILED}

    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();
        addVisibleError(errors, "email", EMAIL_VALIDATION);
        addVisibleError(errors, "password", PASSWORD_VALIDATION);
        return errors;
    }

    private void addVisibleError(List<String> errors, String field, By locator) {
        for (WebElement element : driver.findElements(locator)) {
            try {
                if (element.isDisplayed()) {
                    errors.add(field + ": " + element.getText().trim());
                }
            } catch (StaleElementReferenceException ignored) {
            }
        }
    }

    private boolean isAnyDisplayed(By locator) {
        for (WebElement element : driver.findElements(locator)) {
            try {
                if (element.isDisplayed()) return true;
            } catch (StaleElementReferenceException ignored) {
            }
        }
        return false;
    }

    public Result waitForResult() {
        ExpectedCondition<Result> outcome = driver -> {
            if (isAnyDisplayed(LOGIN_SUCCESS)) return LoginPage.Result.SUCCESS;
            if (isAnyDisplayed(LOGIN_CREDENTIALS)) return LoginPage.Result.WRONG_CREDENTIALS;
            if (isAnyDisplayed(PASSWORD_VALIDATION)) return Result.VALIDATION_ERROR;
            if (isAnyDisplayed(EMAIL_FORMAT_ERROR)) return Result.INVALID_EMAIL;
            return null;
        };
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(3))
                    .pollingEvery(Duration.ofMillis(200))
                    .until(outcome);
        } catch (TimeoutException e) {
            return Result.FAILED;
        }
    }
}