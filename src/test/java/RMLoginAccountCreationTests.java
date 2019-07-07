import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class RMLoginAccountCreationTests {
    private WebDriver driver;

    @Before
    public void setup(){
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        driver = new ChromeDriver();
        driver.navigate().to("http://www.rocketmiles.com/login");
    }

    @After
    public void closedown() {
        // action is performed after every test
        driver.close();
        driver.quit();
    }

    @Test
    // assert a new account cannot be created without email
    public void createAccountWithoutEmail() {
        // this wait function tells the test to wait 30 seconds for an element to appear before erroring
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        // when sing in button is clicked, modal defaults to sign in.  this test tests creating an account so we need to click 'sign up'
        WebElement signUpButton = driver.findElement(By.xpath("//*[@id=\"login-page\"]/div/div[1]/ul/li[2]/a"));
        signUpButton.click();
        WebElement firstNameField = driver.findElement(By.id("firstName"));
        firstNameField.sendKeys("Austin");
        WebElement lastNameField = driver.findElement(By.id("lastName"));
        lastNameField.sendKeys("Powers");
        WebElement password = driver.findElement(By.id("password"));
        password.sendKeys("austinpowers67");
        WebElement confirmPassword = driver.findElement(By.id("confirmPassword"));
        confirmPassword.sendKeys("austinpowers67");
        WebElement signUpSubmit = driver.findElement(By.xpath("//*[@id=\"main-sign-up-form\"]/div[5]/input"));
        signUpSubmit.click();

        // assert there is no redirect
        Assert.assertEquals("https://www.rocketmiles.com/login/auth", driver.getCurrentUrl());
        WebElement errorRow = driver.findElement(By.xpath("//*[@id=\"main-sign-up-form\"]/div[1]/div/ul"));
        Assert.assertEquals("Email is required", errorRow.getText());
    }

    @Test
    // assert new user can log in
    public void login() {
        // this wait function tells the test to wait 30 seconds for an element to appear before erroring
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        WebElement emailField = driver.findElement(By.id("j_username"));
        emailField.sendKeys("austin.powers@dangerismymiddlename.com");
        WebElement password = driver.findElement(By.id("j_password"));
        password.sendKeys("austinpowers67");
        WebElement signInSubmit = driver.findElement(By.className("login-button"));
        signInSubmit.click();

        Assert.assertEquals("https://www.rocketmiles.com/", driver.getCurrentUrl());
    }
}

