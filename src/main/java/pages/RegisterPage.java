package pages;

import io.qameta.allure.Allure;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class RegisterPage extends BasePage {

    private final By NAME_INPUT = By.xpath("//input[@id='name']");
    private final By EMAIL_INPUT = By.xpath("//input[@id='email']");
    private final By PASSWORD_INPUT = By.xpath("//input[@id='password']");
    private final By PHONE_INPUT = By.xpath("//input[@id='phone']");
    private final By BIRTHDAY_PICKER = By.xpath("(//div[contains(@class,'ant-picker')])[1]");
    private final By BIRTHDAY_INPUT = By.xpath("//input[@id='birthday']");
    private final By GENDER_DROPDOWN = By.xpath("//div[@name='gender']//div[@class='ant-select-selector']");
    private final By MALE_OPTION = By.xpath("//div[contains(@class,'ant-select-item-option-content') and normalize-space()='Nam']");
    private final By FEMALE_OPTION = By.xpath("//div[contains(@class,'ant-select-item-option-content') and normalize-space()='Nữ']");
    private final By REGISTER_BUTTON = By.xpath("//button[@type='submit' and normalize-space()='Đăng ký']");
    private static final By EMAIL_DUPLICATE = By.xpath(
            "//span[normalize-space()='Email đã tồn tại !']"
    );
    private static final By REGISTER_SUCCESS = By.xpath(
            "//span[normalize-space()='Đăng ký thành công']"
    );
    private static final By VALIDATION_ERROR = By.xpath(
            "//div[normalize-space()='Vui lòng không bỏ trống']");
    private static final By NAME_VALIDATION =
            By.xpath("//div[normalize-space()='Vui lòng không bỏ trống'][1]");
    private static final By EMAIL_VALIDATION =
            By.xpath("//div[normalize-space()='Vui lòng không bỏ trống'][5]");
    private static final By PASSWORD_VALIDATION =
            By.xpath("//div[normalize-space()='Vui lòng không bỏ trống'][9]");
    private static final By PHONE_VALIDATION =
            By.xpath("//div[normalize-space()='Vui lòng không bỏ trống'][13]");
    private static final By BIRTHDAY_VALIDATION =
            By.xpath("//*[@id='birthday_help']");
    private static final By EMAIL_FORMAT_ERROR =
            By.xpath("//div[normalize-space()='Vui lòng nhập đúng định dạng email']");

    public RegisterPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void enterName(String name) {
        Allure.step("Nhập họ tên đăng ký");
        type(NAME_INPUT, name);
    }

    public void enterEmail(String email) {
        Allure.step("Nhập email đăng ký");
        type(EMAIL_INPUT, email);
    }

    public void enterPassword(String password) {
        Allure.step("Nhập mật khẩu đăng ký");
        type(PASSWORD_INPUT, password);
    }

    public void enterPhone(String phone) {
        Allure.step("Nhập số điện thoại đăng ký");
        type(PHONE_INPUT, phone);
    }

    public void enterBirthday(String birthday) {
        Allure.step("Nhập ngày sinh");
        click(BIRTHDAY_PICKER);
        type(BIRTHDAY_INPUT, birthday);
        waitForVisible(BIRTHDAY_INPUT).sendKeys(Keys.ENTER);
    }

    public void selectGender(String gender) {
        Allure.step("Chọn giới tính: " + gender);
        click(GENDER_DROPDOWN);

        if ("Nam".equalsIgnoreCase(gender)) {
            click(MALE_OPTION);
        } else if ("Nữ".equalsIgnoreCase(gender)) {
            click(FEMALE_OPTION);
        } else ;
    }

    public void clickRegister() {
        Allure.step("Nhấn nút Đăng ký");
        click(REGISTER_BUTTON);
    }

    public enum Result {SUCCESS, DUPLICATE_EMAIL, VALIDATION_ERROR, INVALID_EMAIL, FAILED}

    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        addVisibleError(errors, "name", NAME_VALIDATION);
        addVisibleError(errors, "email", EMAIL_VALIDATION);
        addVisibleError(errors, "password", PASSWORD_VALIDATION);
        addVisibleError(errors, "phone", PHONE_VALIDATION);
        addVisibleError(errors, "birthday", BIRTHDAY_VALIDATION);

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
        for (WebElement e : driver.findElements(locator)) {
            try {
                if (e.isDisplayed()) return true;
            } catch (StaleElementReferenceException ignored) {
            }
        }
        return false;
    }

    public Result waitForResult() {
        ExpectedCondition<Result> outcome = d -> {
            if (isAnyDisplayed(REGISTER_SUCCESS)) return Result.SUCCESS;
            if (isAnyDisplayed(EMAIL_DUPLICATE)) return Result.DUPLICATE_EMAIL;
            if (isAnyDisplayed(VALIDATION_ERROR)) return Result.VALIDATION_ERROR;
            if (isAnyDisplayed(EMAIL_FORMAT_ERROR)) return Result.INVALID_EMAIL;
            if (isAnyDisplayed(VALIDATION_ERROR)
                    || isAnyDisplayed(BIRTHDAY_VALIDATION)) {
                return Result.VALIDATION_ERROR;
            }
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
