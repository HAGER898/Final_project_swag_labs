package Swag;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class MultiUserCheckoutTest {

    private WebDriver driver;
    private final String url = "https://www.saucedemo.com/";

    // Login locators
    private final By userNameLocator = By.id("user-name");
    private final By passwordLocator = By.name("password");
    private final By loginButtonLocator = By.id("login-button");

    // Cart and checkout locators
    private final By checkoutButton = By.id("checkout");
    private final By firstName = By.id("first-name");
    private final By lastName = By.id("last-name");
    private final By postalCode = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By finishButton = By.id("finish");
    private final By completeHeader = By.cssSelector(".complete-header");

    @DataProvider(name = "checkoutUsers")
    public Object[][] checkoutUsers() {
        // Build a list from LoginData.userName but exclude users known to not be able to complete checkout
        // such as "locked_out_user".
        List<String> allowed = new ArrayList<>();
        List<String> source = Arrays.asList(LoginData.userName);
        for (String u : source) {
            if (u == null) continue;
            String name = u.trim().toLowerCase();
            if (name.equals("locked_out_user")) continue; // cannot log in
            // Keep commonly testable accounts
            allowed.add(u);
        }
        Object[][] data = new Object[allowed.size()][1];
        for (int i = 0; i < allowed.size(); i++) {
            data[i][0] = allowed.get(i);
        }
        return data;
    }

    @BeforeMethod
    public void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        // Disable Chrome password save/change prompts
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        // Block site notifications
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);
        // Dismiss any unexpected JS alerts/prompts to avoid interrupting tests
        options.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void login(String username) {
        driver.get(url);
        driver.findElement(userNameLocator).sendKeys(username);
        driver.findElement(passwordLocator).sendKeys(LoginData.password);
        driver.findElement(loginButtonLocator).click();
        // Wait for Products page
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30)) // allow slow/performance_glitch_user
                .pollingEvery(Duration.ofMillis(100))
                .ignoring(NoSuchElementException.class);
        wait.until(d -> d.findElement(By.cssSelector("span.title")).isDisplayed());
    }

    @Test(dataProvider = "checkoutUsers", testName = "MU_CO_TC_01 - Single item checkout for multiple users")
    public void checkoutSingleItemForMultipleUsers(String username) {
        login(username);
        ProductsPage products = new ProductsPage(driver);

        // Precondition
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"), "Should be on inventory page");

        // Add one item and go to cart
        products.addFirstItemToCart();
        products.clickCartIcon();
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.urlContains("cart.html"));
        Assert.assertEquals(products.getTitleText(), "Your Cart");

        // Checkout step one
        driver.findElement(checkoutButton).click();
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.urlContains("checkout-step-one.html"));
        Assert.assertEquals(products.getTitleText(), "Checkout: Your Information");

        // Enter info
        driver.findElement(firstName).sendKeys("hager");
        driver.findElement(lastName).sendKeys("hager");
        driver.findElement(postalCode).sendKeys("10001");
        driver.findElement(continueButton).click();

        // Overview and finish
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.urlContains("checkout-step-two.html"));
        Assert.assertEquals(products.getTitleText(), "Checkout: Overview");
        driver.findElement(finishButton).click();

        // Complete page
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.urlContains("checkout-complete.html"));
        Assert.assertEquals(products.getTitleText(), "Checkout: Complete!");
        Assert.assertTrue(driver.findElement(completeHeader).isDisplayed(),
                "Completion header should be visible");
    }

    @Test(dataProvider = "checkoutUsers", testName = "MU_CO_TC_02 - Multiple items checkout for multiple users")
    public void checkoutMultipleItemsForMultipleUsers(String username) {
        login(username);
        ProductsPage products = new ProductsPage(driver);

        // Add multiple items
        products.addMultipleItems(2);
        Assert.assertTrue(products.getCartBadgeCount() >= 2, "Badge should reflect multiple items");

        // Proceed to cart and through checkout
        products.clickCartIcon();
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.urlContains("cart.html"));

        driver.findElement(checkoutButton).click();
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.urlContains("checkout-step-one.html"));

        driver.findElement(firstName).sendKeys("hager");
        driver.findElement(lastName).sendKeys("hager");
        driver.findElement(postalCode).sendKeys("10001");
        driver.findElement(continueButton).click();

        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.urlContains("checkout-step-two.html"));
        driver.findElement(finishButton).click();

        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.urlContains("checkout-complete.html"));
        Assert.assertTrue(driver.findElement(completeHeader).isDisplayed(), "Completion header should be visible");
    }
}
