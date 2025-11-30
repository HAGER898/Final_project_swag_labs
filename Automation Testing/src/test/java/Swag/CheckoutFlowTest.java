package Swag;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class CheckoutFlowTest {

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

    @BeforeMethod
    public void setUp() {
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
        driver.get(url);
        // Login with a valid user
        driver.findElement(userNameLocator).sendKeys(LoginData.userName[0]);
        driver.findElement(passwordLocator).sendKeys(LoginData.password);
        driver.findElement(loginButtonLocator).click();
        // Wait for Products page
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofMillis(50))
                .ignoring(NoSuchElementException.class);
        wait.until(d -> d.findElement(By.cssSelector("span.title")).isDisplayed());
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(testName = "CO_TC_01 - Complete checkout with one item")
    public void completeCheckoutWithOneItem() {
        ProductsPage products = new ProductsPage(driver);

        // Ensure we are on Products page
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"), "Should be on inventory page");

        // Add a single item and go to cart
        products.addFirstItemToCart();
        products.clickCartIcon();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("cart.html"));
        Assert.assertEquals(products.getTitleText(), "Your Cart");

        // Proceed to checkout information
        driver.findElement(checkoutButton).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("checkout-step-one.html"));
        Assert.assertEquals(products.getTitleText(), "Checkout: Your Information");

        // Fill user info and continue
        driver.findElement(firstName).sendKeys("hager");
        driver.findElement(lastName).sendKeys("hager");
        driver.findElement(postalCode).sendKeys("12345");
        driver.findElement(continueButton).click();

        // Overview page
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("checkout-step-two.html"));
        Assert.assertEquals(products.getTitleText(), "Checkout: Overview");

        // Finish
        driver.findElement(finishButton).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("checkout-complete.html"));
        Assert.assertEquals(products.getTitleText(), "Checkout: Complete!");
        Assert.assertTrue(driver.findElement(completeHeader).getText().toLowerCase().contains("thank you"),
                "Complete header should contain 'Thank you'");
    }

    @Test(testName = "CO_TC_02 - Complete checkout with multiple items")
    public void completeCheckoutWithMultipleItems() {
        ProductsPage products = new ProductsPage(driver);

        // Add multiple items
        products.addMultipleItems(2);
        Assert.assertTrue(products.getCartBadgeCount() >= 2, "Badge should reflect multiple items");

        // Go to cart
        products.clickCartIcon();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("cart.html"));

        // Proceed to checkout information
        driver.findElement(checkoutButton).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("checkout-step-one.html"));

        driver.findElement(firstName).sendKeys("hager");
        driver.findElement(lastName).sendKeys("hager");
        driver.findElement(postalCode).sendKeys("90210");
        driver.findElement(continueButton).click();

        // Overview page then finish
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("checkout-step-two.html"));
        driver.findElement(finishButton).click();

        // Complete page assertions
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("checkout-complete.html"));
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-complete.html"));
        Assert.assertTrue(driver.findElement(completeHeader).isDisplayed(), "Completion header should be visible");
    }

    @Test(testName = "CO_TC_03 - Complete checkout with three items")
    public void completeCheckoutWithThreeItems() {
        ProductsPage products = new ProductsPage(driver);

        // Add three items
        products.addMultipleItems(3);
        Assert.assertTrue(products.getCartBadgeCount() >= 3, "Badge should reflect three items");

        // Go to cart
        products.clickCartIcon();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("cart.html"));

        // Proceed to checkout information
        driver.findElement(checkoutButton).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("checkout-step-one.html"));

        driver.findElement(firstName).sendKeys("hager");
        driver.findElement(lastName).sendKeys("hager");
        driver.findElement(postalCode).sendKeys("33333");
        driver.findElement(continueButton).click();

        // Overview page then finish
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("checkout-step-two.html"));
        driver.findElement(finishButton).click();

        // Complete page assertions
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("checkout-complete.html"));
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-complete.html"));
        Assert.assertTrue(driver.findElement(completeHeader).isDisplayed(), "Completion header should be visible");
    }

    @Test(testName = "CO_TC_04 - Complete checkout with four items")
    public void completeCheckoutWithFourItems() {
        ProductsPage products = new ProductsPage(driver);

        // Add four items
        products.addMultipleItems(4);
        Assert.assertTrue(products.getCartBadgeCount() >= 4, "Badge should reflect four items");

        // Go to cart
        products.clickCartIcon();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("cart.html"));

        // Proceed to checkout information
        driver.findElement(checkoutButton).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("checkout-step-one.html"));

        driver.findElement(firstName).sendKeys("hager");
        driver.findElement(lastName).sendKeys("hager");
        driver.findElement(postalCode).sendKeys("44444");
        driver.findElement(continueButton).click();

        // Overview page then finish
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("checkout-step-two.html"));
        driver.findElement(finishButton).click();

        // Complete page assertions
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("checkout-complete.html"));
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-complete.html"));
        Assert.assertTrue(driver.findElement(completeHeader).isDisplayed(), "Completion header should be visible");
    }

    @Test(testName = "CO_TC_05 - Complete checkout with all items")
    public void completeCheckoutWithAllItems() {
        ProductsPage products = new ProductsPage(driver);

        // Add all available items
        products.addAllItems();
        Assert.assertTrue(products.getCartBadgeCount() >= 1, "Badge should reflect items added");

        // Go to cart
        products.clickCartIcon();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("cart.html"));

        // Proceed to checkout information
        driver.findElement(checkoutButton).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("checkout-step-one.html"));

        driver.findElement(firstName).sendKeys("hager");
        driver.findElement(lastName).sendKeys("hager");
        driver.findElement(postalCode).sendKeys("99999");
        driver.findElement(continueButton).click();

        // Overview page then finish
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("checkout-step-two.html"));
        driver.findElement(finishButton).click();

        // Complete page assertions
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("checkout-complete.html"));
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-complete.html"));
        Assert.assertTrue(driver.findElement(completeHeader).isDisplayed(), "Completion header should be visible");
    }
}
