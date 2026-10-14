package test;

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
import java.util.List;

@Epic("CyberBnB")
@Feature("Authentication")
public class LoginTest extends BaseTest {

    @DataProvider(name = "loginData")
    public Object[][] loginDataProvider() throws Exception {
        URL resource = getClass().getClassLoader().getResource("emailData.csv");
        if (resource == null) {
            throw new IllegalStateException("Không tìm thấy emailData.csv trong src/test/resources");
        }

        String filePath = Path.of(resource.toURI()).toString();
        List<String[]> rows = CsvReader.readCsvFile(filePath);
        return CsvReader.toDataProviderArray(rows);
    }

    @Story("Đăng nhập")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Đăng nhập với dữ liệu email trong emailData.csv")
    @Test(description = "Đăng nhập với dữ liệu CSV", dataProvider = "loginData")
    public void testLoginWithCsv(String email, String password, String expectedResult) {
        HomePage homePage = new HomePage(getDriver(), getWait());
        homePage.open();

        LoginPage loginPage = homePage.openLoginPage();
        loginPage.login(email, password);

        if ("success".equalsIgnoreCase(expectedResult)) {
            homePage.openAccountMenu();

        } else if ("fail".equalsIgnoreCase(expectedResult)) {
            Assert.assertTrue(
                    loginPage.isLoginModalVisible(),
                    "Đăng nhập thất bại nhưng khung đăng nhập không còn hiển thị"
            );
        } else {
            Assert.fail("expectedResult chỉ nhận success hoặc fail, nhưng nhận: " + expectedResult);
        }
    }
}
