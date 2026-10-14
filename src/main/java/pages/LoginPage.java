package pages;

import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ScreenshotUtil;

public class LoginPage extends BasePage {

    private static final By LOGIN_PAGE =
            By.xpath("//div[@role='dialog' and @aria-modal='true']");
    private static final By EMAIL_INPUT =
            By.xpath("//div[@role='dialog']//input[@placeholder='Vui lòng nhập tài khoản']");
    private static final By PASSWORD_INPUT =
            By.xpath("//div[@role='dialog']//input[@placeholder='Vui lòng nhập mật khẩu']");
    private static final By LOGIN_BUTTON =
            By.xpath("//div[@role='dialog']//button[normalize-space()='Đăng nhập']");

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

    public boolean isLoginModalVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_PAGE)).isDisplayed();
    }
}