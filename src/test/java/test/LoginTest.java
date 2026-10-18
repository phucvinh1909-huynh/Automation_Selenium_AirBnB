package test;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LoginPage;
import utils.CsvReader;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Epic("CyberBnB")
@Feature("Authentication")
public class LoginTest extends BaseTest {

    @DataProvider(name = "loginData")
    public Object[][] loginDataProvider() throws Exception {
        URL resource = getClass().getClassLoader().getResource("emailData.csv");
        if (resource == null) {
            throw new IllegalStateException(
                    "Không tìm thấy emailData.csv trong src/test/resources"
            );
        }

        String filePath = Path.of(resource.toURI()).toString();
        List<String[]> rows = CsvReader.readCsvFile(filePath);
        Object[][] data = new Object[rows.size()][2];
        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length < 2) {
                throw new IllegalStateException(
                        "Dòng " + (i + 1) + " trong emailData.csv cần có email và password"
                );
            }
            data[i][0] = row[0];
            data[i][1] = row[1];
        }
        return data;
    }

    @Story("Đăng nhập")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Đăng nhập với email/password; ghi nhận kết quả trả về từ LoginPage")
    @Test(description = "Đăng nhập với dữ liệu CSV", dataProvider = "loginData")
    public void testLoginWithCsv(String email, String password) {
        HomePage homePage = new HomePage(getDriver(), getWait());
        homePage.open();

        LoginPage loginPage = homePage.openLoginPage();
        loginPage.login(email, password);

        LoginPage.Result result = loginPage.waitForResult();
        List<String> missingFields = getMissingFields(email, password);
        String summary = "Kết quả=" + result + " | email=" + email
                + " | thông báo=" + loginPage.getValidationErrors();
        Allure.step(summary);

        switch (result) {
            case SUCCESS -> {
                homePage.openAccountMenu();
                Assert.assertTrue(homePage.loginSuccess(),
                        "Đăng nhập thành công" + summary);
                System.out.println("SUCCESS | email=" + email);
            }
            case WRONG_CREDENTIALS ->
                    System.out.println("Đăng nhập thất bại: email hoặc mật khẩu không đúng | email=" + email);

            case VALIDATION_ERROR -> {
                Assert.assertFalse(missingFields.isEmpty(),
                        "Thiếu " + summary);
                System.out.println("Fail | Thiếu=" + missingFields);
            }
            case INVALID_EMAIL -> {
                System.out.println("Email sai định dạng: " + email);
                Allure.step("Email sai định dạng: " + email);
            }
            case FAILED -> Assert.fail("Đăng nhập không thành công " + summary);
        }
    }

    private List<String> getMissingFields(
            String email,
            String password
    ) {
        List<String> missing = new ArrayList<>();
        if (isBlank(email)) missing.add("email");
        if (isBlank(password)) missing.add("password");
        return missing;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
