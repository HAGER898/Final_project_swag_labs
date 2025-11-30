package Swag;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class ProductsPageTest {

    private WebDriver driver;
    private final String url = "https://www.saucedemo.com/";

    // Login locators
    private final By userNameLocator = By.id("user-name");
    private final By passwordLocator = By.name("password");
    private final By loginButtonLocator = By.id("login-button");

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(url);
        // Login with a valid user
        driver.findElement(userNameLocator).sendKeys(LoginData.userName[0]);
        driver.findElement(passwordLocator).sendKeys(LoginData.password);
        driver.findElement(loginButtonLocator).click();
        // Fluent wait after login (up to 20 seconds) for Products page to be ready
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);
        wait.until(d -> d.findElement(By.cssSelector("span.title")).isDisplayed());
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(testName = "PP_TC_01 - Verify Products title is displayed")
    public void verifyProductsTitle() {
        ProductsPage products = new ProductsPage(driver);
        Assert.assertEquals(products.getTitleText(), "Products", "Products title should be visible");
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"), "Should be on inventory page");
    }

    @Test(testName = "PP_TC_02 - Verify cart badge increments after adding an item")
    public void verifyAddToCartBadgeIncrements() {
        ProductsPage products = new ProductsPage(driver);
        int before = products.getCartBadgeCount();
        products.addFirstItemToCart();
        int after = products.getCartBadgeCount();
        Assert.assertEquals(after, before + 1, "Cart badge should increment by 1 after adding an item");
    }

    @Test(testName = "PP_TC_05 - Add to Cart changes to Remove and badge updates")
    public void addToCartChangesButtonAndBadge() {
        ProductsPage products = new ProductsPage(driver);
        // Initially the first item's button should be Add to cart
        String beforeText = products.getFirstItemButtonText();
        Assert.assertTrue(beforeText.equalsIgnoreCase("Add to cart"), "Initial button should be 'Add to cart'");
        int beforeBadge = products.getCartBadgeCount();

        products.addFirstItemToCart();

        String afterText = products.getFirstItemButtonText();
        Assert.assertTrue(afterText.equalsIgnoreCase("Remove"), "Button should change to 'Remove' after adding");
        int afterBadge = products.getCartBadgeCount();
        Assert.assertEquals(afterBadge, beforeBadge + 1, "Badge should update by +1 after adding first item");
    }

    @Test(testName = "PP_TC_06 - Add multiple items updates badge count accordingly")
    public void addMultipleItemsIncrementsBadge() {
        ProductsPage products = new ProductsPage(driver);
        int before = products.getCartBadgeCount();
        int toAdd = 3;
        products.addMultipleItems(toAdd);
        int after = products.getCartBadgeCount();
        Assert.assertEquals(after, before + toAdd, "Badge should equal previous count + number of adds");
    }

    @Test(testName = "PP_TC_07 - Removing an item reverts its button back to Add to cart")
    public void removeRevertsButtonToAdd() {
        ProductsPage products = new ProductsPage(driver);
        // Ensure item is added first
        products.addFirstItemToCart();
        Assert.assertTrue(products.getFirstItemButtonText().equalsIgnoreCase("Remove"), "Should be 'Remove' after adding");

        products.removeFirstItemFromCart();

        String buttonText = products.getFirstItemButtonText();
        Assert.assertTrue(buttonText.equalsIgnoreCase("Add to cart"), "After removing, button should be 'Add to cart'");
    }

    @Test(testName = "PP_TC_08 - Removing last item hides the cart badge")
    public void removingLastItemHidesBadge() {
        ProductsPage products = new ProductsPage(driver);
        // Clean state: remove all items if any
        products.removeAllItemsFromCart();
        Assert.assertFalse(products.isCartBadgeVisible(), "Badge should not be visible when cart is empty at start");

        // Add then remove single item
        products.addFirstItemToCart();
        Assert.assertTrue(products.isCartBadgeVisible(), "Badge should be visible after adding an item");
        products.removeFirstItemFromCart();

        Assert.assertFalse(products.isCartBadgeVisible(), "Badge should disappear after removing last item");
    }

    @Test(testName = "PP_TC_03 - Verify sorting Name (Z to A) changes first item")
    public void verifySortingZToAChangesOrder() {
        ProductsPage products = new ProductsPage(driver);
        String before = products.getFirstItemName();
        products.sortByNameZToA();
        String after = products.getFirstItemName();
        Assert.assertNotEquals(after, before, "First item should change after sorting Z to A");
    }

    @Test(testName = "PP_TC_04 - Verify logout from products page returns to login")
    public void verifyLogout() {
        ProductsPage products = new ProductsPage(driver);
        // Wait after login to ensure products page is fully loaded before attempting logout
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.title")));
        products.logout();
        Assert.assertTrue(driver.getCurrentUrl().startsWith("https://www.saucedemo.com"), "Should be back on login page");
        Assert.assertTrue(driver.findElement(By.id("login-button")).isDisplayed(), "Login button should be visible after logout");
    }

    @Test(testName = "PP_TC_09 - State persists on refresh: Verify cart state after refresh")
    public void statePersistsOnRefresh() {
        ProductsPage products = new ProductsPage(driver);
        // Clean state
        products.removeAllItemsFromCart();
        int initial = products.getCartBadgeCount();
        Assert.assertEquals(initial, 0, "Precondition: badge should be 0");

        // Add one item and verify state
        products.addFirstItemToCart();
        int beforeRefreshBadge = products.getCartBadgeCount();
        Assert.assertEquals(beforeRefreshBadge, 1, "Badge should be 1 after adding first item");
        Assert.assertTrue(products.getFirstItemButtonText().equalsIgnoreCase("Remove"), "Button should be Remove before refresh");

        // Refresh and re-assert
        driver.navigate().refresh();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.title")));
        Assert.assertEquals(products.getCartBadgeCount(), beforeRefreshBadge, "Badge count should persist after refresh");
        Assert.assertTrue(products.getFirstItemButtonText().equalsIgnoreCase("Remove"), "Button should remain Remove after refresh");
    }

    @Test(testName = "PP_TC_10 - Cart navigation: Cart icon redirects to Cart page")
    public void cartIconNavigatesToCartPage() {
        ProductsPage products = new ProductsPage(driver);
        products.clickCartIcon();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("cart.html"));
        Assert.assertTrue(driver.getCurrentUrl().contains("cart.html"), "URL should be cart page");
        Assert.assertEquals(products.getTitleText(), "Your Cart", "Cart page title should be 'Your Cart'");
    }

    @Test(testName = "PP_TC_11 - Cart content matches badge: Ensure cart matches badge count")
    public void cartContentMatchesBadge() {
        ProductsPage products = new ProductsPage(driver);
        // Ensure clean state
        products.removeAllItemsFromCart();
        Assert.assertEquals(products.getCartBadgeCount(), 0, "Badge should be 0 at start");

        int toAdd = 3;
        products.addMultipleItems(toAdd);
        int badge = products.getCartBadgeCount();
        Assert.assertEquals(badge, toAdd, "Badge should equal number of items added");

        products.clickCartIcon();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("cart.html"));
        int cartCount = products.getCartItemsCount();
        Assert.assertEquals(cartCount, badge, "Number of items listed in cart should match badge count");
    }

    @Test(testName = "PP_TC_12 - Add same product multiple times: Ensure product not duplicated")
    public void addingSameProductIsNotDuplicated() {
        ProductsPage products = new ProductsPage(driver);
        // Ensure clean slate
        products.removeAllItemsFromCart();

        String firstName = products.getFirstItemName();
        products.addFirstItemToCart();
        // Attempting to add again isn't possible (button is Remove). Navigate to cart and verify single occurrence
        products.clickCartIcon();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("cart.html"));
        List<String> names = products.getCartItemNames();
        long occurrences = 0;
        for (String n : names) {
            if (n.equalsIgnoreCase(firstName)) occurrences++;
        }
        Assert.assertEquals(occurrences, 1, "Product should appear only once in the cart");

    }
}
