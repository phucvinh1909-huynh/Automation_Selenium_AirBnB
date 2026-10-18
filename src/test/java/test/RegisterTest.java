package test;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.RegisterPage;
import utils.CsvReader;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Epic("CyberBnB")
@Feature("Authentication")
public class RegisterTest extends BaseTest {

    @DataProvider(name = "registerData")
    public Object[][] registerDataProvider() throws Exception {
        URL resource = getClass().getClassLoader().getResource("registerData.csv");
        if (resource == null) {
            throw new IllegalStateException(
                    "Không tìm thấy registerData.csv trong src/test/resources"
            );
        }

        String filePath = Path.of(resource.toURI()).toString();
        List<String[]> rows = CsvReader.readCsvFile(filePath);
        return CsvReader.toDataProviderArray(rows);
    }

    @Story("Đăng ký tài khoản")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra đăng ký với dữ liệu CSV; ghi nhận kết quả validation theo trường thiếu")
    @Test(description = "Kiểm tra đăng ký với dữ liệu CSV", dataProvider = "registerData")
    public void testRegister(
            String name,
            String email,
            String password,
            String phone,
            String birthday,
            String gender
    ) {

        HomePage homePage = new HomePage(getDriver(), getWait());
        homePage.open();
        RegisterPage registerPage = homePage.openRegisterPage();

        registerPage.enterName(name);
        registerPage.enterEmail(email);
        registerPage.enterPassword(password);
        registerPage.enterPhone(phone);
        registerPage.enterBirthday(birthday);
        if (!isBlank(gender)) {
            registerPage.selectGender(gender);
        }
        registerPage.clickRegister();

        RegisterPage.Result result = registerPage.waitForResult();
        List<String> missingFields = getMissingFields(name, email, password, phone, birthday, gender);
        String summary = "Kết quả=" + result + " | thiếu=" + missingFields
                + " | thông báo web=" + registerPage.getValidationErrors()
                + " | email=" + email;
        Allure.step(summary);

        switch (result) {
            case SUCCESS -> {
                Assert.assertTrue(missingFields.isEmpty(),
                        "Đăng ký thành công " + missingFields);
                System.out.println("SUCCESS | email=" + email);
            }
            case DUPLICATE_EMAIL -> {
                String message = "Email đã tồn tại: " + email;
                System.out.println(message);
                Allure.step(message);
            }
            case INVALID_EMAIL -> {
                String message = "Email sai định dạng: " + email;
                System.out.println(message);
                Allure.step(message);
            }
            case VALIDATION_ERROR -> {
                Assert.assertFalse(missingFields.isEmpty(),
                        "Thiếu: " + summary);
                System.out.println("Fail | Thiếu=" + missingFields);
            }
            case FAILED -> Assert.fail("Đăng ký không thành công " + summary);
        }
    }

    private List<String> getMissingFields(
            String name,
            String email,
            String password,
            String phone,
            String birthday,
            String gender
    ) {
        List<String> missing = new ArrayList<>();
        if (isBlank(name)) missing.add("name");
        if (isBlank(email)) missing.add("email");
        if (isBlank(password)) missing.add("password");
        if (isBlank(phone)) missing.add("phone");
        if (isBlank(birthday)) missing.add("birthday");
        return missing;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}