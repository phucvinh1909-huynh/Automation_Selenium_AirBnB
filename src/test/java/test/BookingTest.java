package test;

import flows.AuthFlow;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BookingPage;

@Epic("CyberBnB")
@Feature("Booking")
public class BookingTest extends BaseTest {

    @Story("Đặt phòng sau khi đăng ký và đăng nhập")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Đăng ký tài khoản mới -> đăng nhập -> chọn Hồ Chí Minh -> phòng đầu tiên -> 2 khách -> đặt phòng -> kiểm tra Dashboard")
    @Test(description = "Đặt phòng với tài khoản vừa đăng ký")
    public void testBookRoomAfterRegisterAndLogin() {
        AuthFlow.Account account = new AuthFlow(getDriver(), getWait()).registerAndLogin();
        Allure.step("Đã đăng nhập bằng " + account.email());

        BookingPage booking = new BookingPage(getDriver(), getWait());
        booking.selectLocation("ho-chi-minh");
        booking.selectRoom(1);

        String roomTitle = booking.getRoomTitle();
        Allure.step("Phòng: " + roomTitle + " | Ngày: " + booking.getSelectedDates());

        BookingPage.Result result = booking.bookRoom(2);
        Assert.assertEquals(result, BookingPage.Result.SUCCESS,
                "Đặt phòng không thành công: không thấy thông báo 'Thêm mới thành công!' | phòng=" + roomTitle);

        Assert.assertTrue(booking.isRoomInBookedList(roomTitle),
                "Không thấy phòng '" + roomTitle + "' trong mục Phòng đã thuê");
    }
}
