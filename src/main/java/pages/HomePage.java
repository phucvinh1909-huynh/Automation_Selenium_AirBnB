package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;

public class HomePage extends BasePage {
    private static final By AVATAR_BUTTON = By.xpath("//button[.//img[contains(@src,'6596121.png')]]");
    private static final By LOGIN_REGISTER_MENU = By.xpath("//div[@id='user-dropdown']");
    private static final By LOGIN_BUTTON = By.xpath("//div[@id='user-dropdown']//button[normalize-space()='Đăng nhập']");
    private static final By LOGIN_MODAL = By.xpath("//div[@role='dialog' and @aria-modal='true']");
    private static final By LOGIN_EMAIL_INPUT = By.xpath(
            "//div[@role='dialog']//input[@placeholder='Vui lòng nhập tài khoản']");
    private static final By DASHBOARD_MENU_ITEM =
            By.xpath("//div[@id='user-dropdown']//a[normalize-space()='Dashboard']");
    private static final By REGISTER_BUTTON = By.xpath("//div[@id='user-dropdown']//button[normalize-space()='Đăng ký']");
    private static final By REGISTER_MODAL = By.xpath(
            "//div[@role='dialog' and @aria-modal='true'][.//*[normalize-space()='Đăng ký tài khoản']]"
    );


    public HomePage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void open() {
        driver.get(ConfigReader.get("base.url"));
        wait.until(ExpectedConditions.elementToBeClickable(AVATAR_BUTTON));
    }

    public LoginPage openLoginPage() {
        click(AVATAR_BUTTON);
        wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_REGISTER_MENU));
        click(LOGIN_BUTTON);
        wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_MODAL));
        return new LoginPage(driver, wait);
    }

    public void openAccountMenu() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOGIN_EMAIL_INPUT));
        if (driver.findElements(DASHBOARD_MENU_ITEM).stream().noneMatch(WebElement::isDisplayed)) {
            click(AVATAR_BUTTON);
        }
        waitForVisible(DASHBOARD_MENU_ITEM);
    }

    public RegisterPage openRegisterPage() {
        click(AVATAR_BUTTON);
        wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_REGISTER_MENU));

        click(REGISTER_BUTTON);
        wait.until(ExpectedConditions.visibilityOfElementLocated(REGISTER_MODAL));

        return new RegisterPage(driver, wait);
    }

    public boolean loginSuccess() {
        return waitForVisible(DASHBOARD_MENU_ITEM).isDisplayed();
    }
}