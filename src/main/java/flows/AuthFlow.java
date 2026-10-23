package flows;

import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.HomePage;
import pages.LoginPage;
import pages.RegisterPage;

public class AuthFlow {

    public record Account(String name, String email, String password,
                          String phone, String birthday, String gender) {
        @Override
        public String toString() {
            return "Account[name=" + name + ", email=" + email + "]";
        }
    }

    private static final String DEFAULT_PASSWORD = "Test@12345";
    private final WebDriver driver;
    private final WebDriverWait wait;

    public AuthFlow(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public static Account newAccount() {
        long t = System.currentTimeMillis();
        String phone = "09" + (10000000 + (int) (Math.random() * 89999999));
        return new Account("Auto " + t, "auto" + t + "@gmail.com",
                DEFAULT_PASSWORD, phone, "01/01/1998", "Nam");
    }

    public void register(Account a) {
        Allure.step("Đăng ký tài khoản " + a.email(), () -> {
            HomePage home = new HomePage(driver, wait);
            home.open();
            RegisterPage page = home.openRegisterPage();
            page.enterName(a.name());
            page.enterEmail(a.email());
            page.enterPassword(a.password());
            page.enterPhone(a.phone());
            page.enterBirthday(a.birthday());
            page.selectGender(a.gender());
            page.clickRegister();

            RegisterPage.Result result = page.waitForResult();
            if (result != RegisterPage.Result.SUCCESS) {
                throw new IllegalStateException("Đăng ký thất bại: " + a.email());
            }
        });
    }

    public void login(String email, String password) {
        Allure.step("Đăng nhập " + email, () -> {
            HomePage home = new HomePage(driver, wait);
            home.open();
            LoginPage page = home.openLoginPage();
            page.login(email, password);

            LoginPage.Result result = page.waitForResult();
            if (result != LoginPage.Result.SUCCESS) {
                throw new IllegalStateException("Đăng nhập thất bại: " + result + email);
            }
        });
    }

    public Account registerAndLogin() {
        Account account = newAccount();
        register(account);
        login(account.email(), account.password());
        return account;
    }
}
