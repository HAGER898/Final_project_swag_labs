package Swag;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.*;

public class MultiUserLogoutTest {

    private WebDriver driver;
    private final String url = "https://www.saucedemo.com/";

    // Login locators
    private final By userNameLocator = By.id("user-name");
    private final By passwordLocator = By.name("password");
    private final By loginButtonLocator = By.id("login-button");

    @DataProvider(name = "allUsersForLogout")
    public Object[][] allUsersForLogout() {
        List<String> allowed = new ArrayList<>();
        for (String u : LoginData.userName) {
            if (u == null) continue;
            String name = u.trim().toLowerCase();
            if (name.equals("locked_out_user")) continue; // cannot log in to perform logout
            allowed.add(u);
        }
        Object[][] data = new Object[allowed.size()][1];
        for (int i = 0; i < allowed.size(); i++) {
            data[i][0] = allowed.get(i);
        }
        return data;
    }

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);
        options.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void login(String username) {
        driver.get(url);
        driver.findElement(userNameLocator).sendKeys(username);
        driver.findElement(passwordLocator).sendKeys(LoginData.password);
        driver.findElement(loginButtonLocator).click();
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(100))
                .ignoring(NoSuchElementException.class);
        wait.until(d -> d.findElement(By.cssSelector("span.title")).isDisplayed());
    }

    @Test(dataProvider = "allUsersForLogout", testName = "MU_LO_TC_01 - Logout works for all users")
    public void logoutWorksForAllUsers(String username) {
        // Login as given user
        login(username);

        // Ensure we are on products page
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("inventory.html"));

        // Perform logout using dedicated page object
        LogoutPage logoutPage = new LogoutPage(driver);
        logoutPage.logout();

        // Verify back on login page
        Assert.assertTrue(logoutPage.isOnLoginPage(), "User should be navigated back to login page after logout");
    }
}
