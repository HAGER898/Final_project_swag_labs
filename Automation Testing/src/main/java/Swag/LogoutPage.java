package Swag;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Simple page object to handle logout flow from any page where the burger menu is available.
 */
public class LogoutPage {

    private final WebDriver driver;

    // Common Sauce Demo locators
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");
    private final By loginButton = By.id("login-button");

    public LogoutPage(WebDriver driver) {
        this.driver = driver;
    }


     // Performs logout using the sidebar menu and waits until redirected to the login page.

    public void logout() {
        // Open the burger menu
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(menuButton))
                .click();

        // Click Logout in the sidebar
        WebElement logout = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(logoutLink));
        logout.click();

        // Wait until login page is visible again
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(loginButton));
    }


     // Returns true if the driver is currently on the Sauce Demo login page.
    public boolean isOnLoginPage() {
        try {
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(loginButton));
            return btn.isDisplayed() && driver.getCurrentUrl().contains("saucedemo.com");
        } catch (Exception e) {
            return false;
        }
    }
}
