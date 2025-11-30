package Swag;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class ProductsPage {
    private final WebDriver driver;

    // Locators
    private final By title = By.cssSelector("span.title");
    private final By inventoryItemNames = By.cssSelector(".inventory_item_name");
    private final By firstAddToCartBtn = By.cssSelector("button[id^='add-to-cart']");
    private final By cartBadge = By.cssSelector(".shopping_cart_badge");
    private final By sortSelect = By.cssSelector("select[class='product_sort_container']");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");
    private final By firstItemActionButton = By.cssSelector(".inventory_item:first-of-type button.btn_inventory");
    private final By anyAddButton = By.cssSelector("button[id^='add-to-cart']");
    private final By anyRemoveButton = By.cssSelector("button[id^='remove-']");
    private final By cartIcon = By.cssSelector(".shopping_cart_link");
    private final By cartItems = By.cssSelector(".cart_item");
    private final By cartItemNames = By.cssSelector(".cart_item .inventory_item_name");

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getTitleText() {
        return driver.findElement(title).getText();
    }

    public void addFirstItemToCart() {
        driver.findElement(firstAddToCartBtn).click();
    }
    // Method

    public int getCartBadgeCount() {
        try {
            String text = driver.findElement(cartBadge).getText();
            return Integer.parseInt(text.trim());
        } catch (NoSuchElementException e) {
            return 0; // no items in cart
        }
    }

    public void sortByNameZToA() {
        // Wait for the sort dropdown to be visible
        WebElement sort = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(sortSelect));
        Select select = new Select(sort);
        // Visible text on Sauce Demo for Z to A
        select.selectByVisibleText("Name (Z to A)");
    }

    public String getFirstItemName() {
        List<WebElement> items = driver.findElements(inventoryItemNames);
        if (items.isEmpty()) return "";
        return items.get(0).getText();
    }

    // Returns current text of the first item's action button ("Add to cart" or "Remove")
    public String getFirstItemButtonText() {
        return driver.findElement(firstItemActionButton).getText();
    }

    // Add N items to cart by repeatedly clicking the first available Add button
    public void addMultipleItems(int count) {
        for (int i = 0; i < count; i++) {
            WebElement addBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(anyAddButton));
            addBtn.click();
        }
    }

    // Add all available items to the cart
    public void addAllItems() {
        // Keep clicking the first available Add button until none remain
        List<WebElement> addButtons = driver.findElements(anyAddButton);
        while (!addButtons.isEmpty()) {
            // Ensure the button is clickable before clicking
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(addButtons.get(0)))
                    .click();
            // Refresh the list since the DOM changes (button id/text toggles)
            addButtons = driver.findElements(anyAddButton);
        }
    }

    // Remove the first available item from the cart (first visible Remove button)
    public void removeFirstItemFromCart() {
        WebElement removeBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(anyRemoveButton));
        removeBtn.click();
    }

    // Remove all items currently in the cart (click all Remove buttons)
    public void removeAllItemsFromCart() {
        List<WebElement> removeButtons = driver.findElements(anyRemoveButton);
        while (!removeButtons.isEmpty()) {
            // Click the first then refresh the list (ids change back to add-to-cart)
            removeButtons.get(0).click();
            removeButtons = driver.findElements(anyRemoveButton);
        }
    }

    // Whether the cart badge is present (visible) on the page
    public boolean isCartBadgeVisible() {
        try {
            return driver.findElement(cartBadge).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void logout() {
        // Ensure the menu button is clickable, then open the sidebar
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(menuButton))
                .click();

        // Wait for the logout link to be visible and clickable (sidebar animation)
        WebElement logout = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(logoutLink));
        logout.click();

        // Wait until redirected back to login page (login button visible)
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
    }

    // Click on the cart icon to navigate to the cart page
    public void clickCartIcon() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(cartIcon))
                .click();
    }

    // Returns number of items listed in the cart page
    public int getCartItemsCount() {
        // Wait until either items are visible or the cart is empty (badge might be absent)
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.getCurrentUrl().contains("cart.html"));
        return driver.findElements(cartItems).size();
    }

    // Returns the list of item names currently shown in the cart
    public List<String> getCartItemNames() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.getCurrentUrl().contains("cart.html"));
        return driver.findElements(cartItemNames).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }
}
