package pages;

import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class BookingPage extends BasePage {

    private static final Function<String, By> LOCATION_CARD =
                name -> By.xpath("//a[@href='/rooms/" + name + "']");

    private static final By ROOM_LIST_TITLE =
            By.xpath("//h1[normalize-space()='Chỗ ở tại khu vực bản đồ đã chọn']");
    private static final By ROOM_CARD =
            By.xpath("//a[contains(@href, '/room-detail/')]");

    private static final By ROOM_TITLE =
            By.xpath("//h2[contains(@class,'font-bold') and contains(@class,'text-3xl')]");
    private static final By CHECK_IN_TEXT =
            By.xpath("//div[contains(@class,'cursor-pointer') and contains(@class,'rounded-tl-lg')]");
    private static final By CHECK_OUT_TEXT =
            By.xpath("//div[contains(@class,'cursor-pointer') and contains(@class,'rounded-tr-lg')]");
    private static final By GUEST_COUNT_TEXT = By.xpath(
            "//div[normalize-space()='Khách']/following-sibling::div/div[2]");
    private static final By GUEST_MINUS_BUTTON =
            By.xpath("//div[normalize-space()='Khách']/following-sibling::div/div[1]//button");
    private static final By GUEST_PLUS_BUTTON =
            By.xpath("//div[normalize-space()='Khách']/following-sibling::div/div[3]//button");
    private final By BOOK_BUTTON =
            By.xpath("//button[normalize-space()='Đặt phòng']");

    private static final By CONFIRM_MODAL =
            By.xpath("//div[@role='dialog']//div[contains(normalize-space(),'Bạn có chắc muốn đặt phòng')]");
    private static final By CONFIRM_BUTTON =
            By.xpath("//div[@role='dialog']//button[normalize-space()='Xác nhận']");
    private static final By BOOKING_SUCCESS_TOAST =
            By.xpath("//div[contains(@class,'ant-notification-notice-success') and contains(.,'Thêm mới thành công!')]");
    private static final By BOOKING_ERROR_TOAST   =
            By.xpath("//div[contains(@class,'ant-notification-notice-error') and contains(.,'Thêm mới không thành công!')]");

    private static final By BOOKED_ROOM_TITLE =
            By.xpath("//a[contains(@href,'/room-detail/')]//p[contains(@class,'truncate') and contains(@class,'text-xl')]");
    private static final By ANY_TOAST =
            By.xpath("//div[contains(@class,'ant-notification-notice')]");
    private static final By DASHBOARD_LINK =
            By.xpath("//div[@id='user-dropdown']//a[normalize-space()='Dashboard']");


    public enum Result {SUCCESS, FAILED}

    public BookingPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void selectLocation(String location) {
        Allure.step("Chọn địa điểm: " + location, () -> {
            By card = LOCATION_CARD.apply(location);
            for (int attempt = 1; attempt <= 3; attempt++) {
                WebElement el = wait.until(d -> d.findElements(card).stream()
                        .filter(WebElement::isDisplayed).findFirst().orElse(null));
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", el);
                try {
                    el.click();
                } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].click();", driver.findElement(card));
                }
                try {
                    new WebDriverWait(driver, Duration.ofSeconds(6))
                            .until(ExpectedConditions.urlContains("/rooms/"));
                    return;
                } catch (TimeoutException ignored) {
                }
            }
            throw new IllegalStateException("Bấm " + location
                    + " Không chuyển trang " + driver.getCurrentUrl());
        });
    }

    public void selectRoom(int index) {
        Allure.step("Chọn phòng thứ " + index + " trong danh sách", () -> {
            waitForVisible(ROOM_LIST_TITLE);
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(ROOM_CARD, index - 1));
            WebElement card = driver.findElements(ROOM_CARD).get(index - 1);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});", card);
            wait.until(ExpectedConditions.elementToBeClickable(card)).click();
            wait.until(ExpectedConditions.urlContains("/room-detail/"));
            waitForVisible(ROOM_TITLE);
        });
    }

    public String getRoomTitle() {
        return waitForVisible(ROOM_TITLE).getText().trim();
    }

    public String getSelectedDates() {
        String[] checkInLines = waitForVisible(CHECK_IN_TEXT)
                .getText().trim().split("\\R");
        String[] checkOutLines = waitForVisible(CHECK_OUT_TEXT)
                .getText().trim().split("\\R");

        String checkIn = checkInLines[checkInLines.length - 1];
        String checkOut = checkOutLines[checkOutLines.length - 1];

        return checkIn + " -> " + checkOut;
    }

    public void setGuests(int target) {
        Allure.step("Đặt số khách = " + target, () -> {
            for (int i = 0; i < 20; i++) {
                int current = getGuestCount();
                if (current == target) return;
                safeClick(current < target ? GUEST_PLUS_BUTTON : GUEST_MINUS_BUTTON);
                int before = current;
                wait.until(d -> getGuestCount() != before);
            }
            throw new IllegalStateException("Không đặt được số khách = " + target
                    + ", hiện là " + getGuestCount());
        });
    }

    public int getGuestCount() {
        String digits = waitForVisible(GUEST_COUNT_TEXT).getText().replaceAll("[^0-9]", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }

    public void clickBookRoom() {
        Allure.step("Bấm nút Đặt phòng", () -> {
            safeClick(BOOK_BUTTON);
            waitForVisible(CONFIRM_MODAL);
        });
    }

    public void confirmBooking() {
        Allure.step("Bấm Xác nhận đặt phòng", () -> safeClick(CONFIRM_BUTTON));
    }

    public Result waitForResult() {
        ExpectedCondition<Result> outcome = d -> {
            if (isAnyDisplayed(BOOKING_SUCCESS_TOAST)) return Result.SUCCESS;
            if (isAnyDisplayed(BOOKING_ERROR_TOAST)) return Result.FAILED;
            return null;
        };
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(8))
                    .pollingEvery(Duration.ofMillis(200))
                    .until(outcome);
        } catch (TimeoutException e) {
            return Result.FAILED;
        }
    }

    public boolean isRoomInBookedList(String roomTitle) {
        Allure.step("Kiểm tra phòng đã thuê: " + roomTitle);
        openInfoUserPage();
        String expected = roomTitle.trim();
        try {
            return wait.until(d -> d.findElements(BOOKED_ROOM_TITLE).stream()
                    .anyMatch(e -> textOf(e).contains(expected)));
        } catch (TimeoutException e) {
            return false;
        }
    }

    private void openInfoUserPage() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.invisibilityOfElementLocated(ANY_TOAST));
        } catch (TimeoutException ignored) {
        }
        try {
            new HomePage(driver, wait).openAccountMenu();
            safeClick(DASHBOARD_LINK);
        } catch (RuntimeException e) {
            driver.get(java.net.URI.create(driver.getCurrentUrl()).resolve("/info-user").toString());
        }
        wait.until(ExpectedConditions.urlContains("/info-user"));
    }

    public Result bookRoom(int guests) {
        setGuests(guests);
        clickBookRoom();
        confirmBooking();
        return waitForResult();
    }

    private void safeClick(By by) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement el = wait.until(ExpectedConditions.elementToBeClickable(by));
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", el);
                el.click();
                return;
            } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", waitForVisible(by));
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

    private static String textOf(WebElement e) {
        try {
            String t = e.getAttribute("textContent");
            return t == null ? "" : t.trim();
        } catch (StaleElementReferenceException ex) {
            return "";
        }
    }
}
