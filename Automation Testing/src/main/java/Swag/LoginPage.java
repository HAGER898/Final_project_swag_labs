package Swag;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
public class LoginPage{

    //Drivers
    //Variables
    //By Locators
    //Elements
    //Methods

    //Drivers
    WebDriver myBrowser;


    //Variables
    //String userName;
    //String Pass = "secret_sauce";
    String url = "https://www.saucedemo.com/";

    //By Locators
    By userNameLocator = By.id("user-name");
    By passwordLocator = By.name("password");
    By loginButtonLocator = By.id("login-button");
    By errorMessage = By.cssSelector("h3[data-test='error']");


   @BeforeMethod
   public void start(){
     //Create Driver
     myBrowser = new ChromeDriver();
     //Navigate To Url
      myBrowser.get(url);
     //Maximize the Window
     myBrowser.manage().window().maximize();
    }
    @Test(testName ="TC_01-Verify login works with correct credentials",priority = 1)
    public void Standerd_User_Test(){
       myBrowser.findElement(userNameLocator).sendKeys(LoginData.userName[0]);
       myBrowser.findElement(passwordLocator).sendKeys(LoginData.password);
       myBrowser.findElement(loginButtonLocator).click();
       Assert.assertEquals("https://www.saucedemo.com/inventory.html",myBrowser.getCurrentUrl());
       myBrowser.quit();
    }
    @Test(testName ="TC_02-Verify login works with correct credentials",priority = 2)
    public void locked_out_user_Test(){
        myBrowser.findElement(userNameLocator).sendKeys(LoginData.userName[1]);
        myBrowser.findElement(passwordLocator).sendKeys(LoginData.password);
        myBrowser.findElement(loginButtonLocator).click();
        Assert.assertEquals("Epic sadface: Sorry, this user has been locked out.",myBrowser.findElement(errorMessage).getText());
        myBrowser.quit();
    }
    @Test(testName ="TC_03-Verify login works with correct credentials",priority = 3)
    public void problem_user_Test(){
        myBrowser.findElement(userNameLocator).sendKeys(LoginData.userName[2]);
        myBrowser.findElement(passwordLocator).sendKeys(LoginData.password);
        myBrowser.findElement(loginButtonLocator).click();
        Assert.assertEquals("https://www.saucedemo.com/inventory.html",myBrowser.getCurrentUrl());
        myBrowser.quit();
    }
    @Test(testName ="TC_04-Verify login works with correct credentials",priority = 4)
    public void performance_glitch_user_Test(){
        myBrowser.findElement(userNameLocator).sendKeys(LoginData.userName[3]);
        myBrowser.findElement(passwordLocator).sendKeys(LoginData.password);
        myBrowser.findElement(loginButtonLocator).click();
        Assert.assertEquals("https://www.saucedemo.com/inventory.html",myBrowser.getCurrentUrl());
        myBrowser.quit();
    }
    @Test(testName ="TC_05-Verify login works with correct credentials",priority = 5)
    public void error_user_Test(){
        myBrowser.findElement(userNameLocator).sendKeys(LoginData.userName[4]);
        myBrowser.findElement(passwordLocator).sendKeys(LoginData.password);
        myBrowser.findElement(loginButtonLocator).click();
        Assert.assertEquals("https://www.saucedemo.com/inventory.html",myBrowser.getCurrentUrl());
        myBrowser.quit();
    }
    @Test(testName ="TC_06-Verify login works with correct credentials",priority = 6)
    public void visual_user_Test(){
        myBrowser.findElement(userNameLocator).sendKeys(LoginData.userName[5]);
        myBrowser.findElement(passwordLocator).sendKeys(LoginData.password);
        myBrowser.findElement(loginButtonLocator).click();
        Assert.assertEquals("https://www.saucedemo.com/inventory.html",myBrowser.getCurrentUrl());
        myBrowser.quit();
    }
}
