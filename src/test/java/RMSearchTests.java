import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class RMSearchTests {
    private WebDriver driver;
    @Before
    public void setup(){
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        driver = new ChromeDriver();
        driver.navigate().to("http://www.rocketmiles.com");
        driver.manage().window().maximize();

        // rocketmiles.com consistently has an email signup popup,
        // for the purposes of this test I will always exit out of this
        WebElement popupWindowCloseButton = driver.findElement(By.xpath("//*[@id=\"new-sign-up-modal\"]/div/div[1]/button"));
        popupWindowCloseButton.click();
    }

    @After
    public void closedown() {
        driver.close();
        driver.quit();
    }

    public void search(String location) {
        // this wait function tells the test to wait 30 seconds for an element to appear before erroring
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

        // type everything and select from dropdown, click search button
        WebElement locationInput = driver.findElement(By.xpath("//*[@id=\"rm3-home-page\"]/div[2]/div[3]/div[3]/div/form/div[1]/div/span/input[2]"));
        locationInput.sendKeys(location);
        // search needs to be selected from list so select Paris France from the dropdown
        locationInput.sendKeys(Keys.ARROW_DOWN);
        locationInput.sendKeys(Keys.RETURN);

        WebElement rewardsInput = driver.findElement(By.xpath("//*[@id=\"rm3-home-page\"]/div[2]/div[3]/div[3]/div/form/div[2]/gofr-program-autosuggest/div/input"));
        rewardsInput.click();
        rewardsInput.sendKeys(Keys.ARROW_DOWN);
        rewardsInput.sendKeys(Keys.ARROW_DOWN);
        rewardsInput.sendKeys(Keys.RETURN);

        // set check in and check out dates
        driver.findElement(By.className("checkin")).click();
        driver.findElement(By.className("ui-datepicker-next")).click();
        driver.findElement(By.xpath("//*[@id=\"ui-datepicker-div\"]/table/tbody/tr[1]/td[6]/a")).click();
        driver.findElement(By.xpath("//*[@id=\"ui-datepicker-div\"]/table/tbody/tr[2]/td[1]/a")).click();

        // set 1 guest
        driver.findElement(By.xpath("//*[@id=\"rm3-home-page\"]/div[2]/div[3]/div[3]/div/form/div[6]/div/div/button")).click();
        driver.findElement(By.xpath("//*[@id=\"rm3-home-page\"]/div[2]/div[3]/div[3]/div/form/div[6]/div/div/ul/li[1]")).click();

        driver.findElement(By.className("search-submit-btn")).click();
    }

    @Test
    // utilize filters on results page to return only 10 options
    public void happyPathSearchResultPageInteraction() {
        this.search("paris, France");

        driver.manage().timeouts().implicitlyWait(90, TimeUnit.SECONDS);

        Assert.assertTrue(driver.getCurrentUrl().contains("https://www.rocketmiles.com/search?longitude="));
        Assert.assertTrue(driver.getCurrentUrl().contains("Paris"));
        driver.findElement(By.className("rating-dropdown")).click();
        driver.findElement(By.xpath("//*[@id=\"search-page\"]/div[2]/div[3]/div[2]/gofr-search-filter/div/div[3]/div[2]/div/ul/li[6]")).click();
        Assert.assertTrue(driver.getCurrentUrl().contains("rating=5"));

        WebElement slider = driver.findElement(By.xpath("//*[@id=\"search-page\"]/div[2]/div[3]/div[2]/gofr-search-filter/div/div[4]/div[2]/div/div/div[1]/div[2]"));
        slider.click();

        // when clicking the right arrow, the slider moves $10
        for (int i = 0; i < 100; i++) {
            slider.sendKeys(Keys.ARROW_RIGHT);
        }

        Assert.assertTrue(driver.findElement(By.xpath("//*[@id=\"search-page\"]/div[2]/div[3]/div[4]/gofr-search-list/div[5]/gofr-search-summary/div/span[1]")).getText().contains("3"));
    }

    @Test
    // assert that popup appears if user searches nothing
    public void emptySearch() {
        this.search("");
        WebElement noLocationPopup = driver.findElement(By.className("popover-content"));
        Assert.assertEquals(noLocationPopup.getText(), "Unknown location. Please type the city name again slowly and wait for the drop down options, or double-check the spelling.");
    }

    @Test
    // assert that popup appears if user searches a non-location string
    public void nonLocationSearch() {
        this.search("To be or not to be that is the question, whether tis nobler in the mind to");
        WebElement noLocationPopup = driver.findElement(By.className("popover-content"));
        Assert.assertEquals(noLocationPopup.getText(), "Unknown location. Please type the city name again slowly and wait for the drop down options, or double-check the spelling.");
    }

    @Test
    // assert that if user selects max date range (10 months), upon setting a start month the page auto resets the end date
    // to be a month from the start date as opposed to the user-entered 10 months away date
    public void maxDateRange() {
        // this wait function tells the test to wait 30 seconds for an element to appear before erroring
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        // type everything and select from dropdown, click search button
        WebElement locationInput = driver.findElement(By.xpath("//*[@id=\"rm3-home-page\"]/div[2]/div[3]/div[3]/div/form/div[1]/div/span/input[2]"));
        locationInput.sendKeys("Vienna, Austria");
        // search needs to be selected from list so select Paris France from the dropdown
        locationInput.sendKeys(Keys.ARROW_DOWN);
        locationInput.sendKeys(Keys.RETURN);

        WebElement rewardsInput = driver.findElement(By.xpath("//*[@id=\"rm3-home-page\"]/div[2]/div[3]/div[3]/div/form/div[2]/gofr-program-autosuggest/div/input"));
        rewardsInput.click();
        rewardsInput.sendKeys(Keys.ARROW_DOWN);
        rewardsInput.sendKeys(Keys.ARROW_DOWN);
        rewardsInput.sendKeys(Keys.RETURN);

        driver.findElement(By.className("checkout")).click();

        for (int i = 0; i < 10; i++) {
            driver.findElement(By.className("ui-datepicker-next")).click();
        }
        driver.findElement(By.xpath("//*[@id=\"ui-datepicker-div\"]/table/tbody/tr[6]/td[1]/a")).click();
        driver.findElement(By.className("checkin")).click();
        driver.findElement(By.xpath("//*[@id=\"ui-datepicker-div\"]/table/tbody/tr[2]/td[7]/a")).click();

        Assert.assertEquals("12", driver.findElement(By.xpath("//*[@id=\"rm3-home-page\"]/div[2]/div[3]/div[3]/div/form/div[4]/div[3]/span")).getText());
    }

    @Test
    // RocketMiles offers 5 non english languages for their site.  All 5 should be able to be searched and produce results
    public void nonEnglishSearchesPortuguese() {
        // Espanha (portuguese for Spain)
        this.search("Espanha");
        Assert.assertTrue(driver.getCurrentUrl().contains("https://www.rocketmiles.com/search?longitude="));
        Assert.assertTrue(driver.getCurrentUrl().contains("Espanha"));
    }

    @Test
    // RocketMiles offers 5 non english languages for their site.  All 5 should be able to be searched and produce results
    public void nonEnglishSearchesSpanish() {
        // Estanbul (spanish for istanbul)
        this.search("Estanbul");
        Assert.assertTrue(driver.getCurrentUrl().contains("https://www.rocketmiles.com/search?longitude="));
        Assert.assertTrue(driver.getCurrentUrl().contains("Turkey"));
    }

    @Test
    // RocketMiles offers 5 non english languages for their site.  All 5 should be able to be searched and produce results
    public void nonEnglishSearchesTurkish() {
        // Japon/Japonya (turkish for japan)
        // why does tokyo, japan have question marks next to it?'
        this.search("Tokyo, Japon");
        Assert.assertTrue(driver.getCurrentUrl().contains("https://www.rocketmiles.com/search?longitude="));
        Assert.assertTrue(driver.getCurrentUrl().contains("Tokyo"));
    }

    @Test
    // RocketMiles offers 5 non english languages for their site.  All 5 should be able to be searched and produce results
    public void nonEnglishSearchesJapanese() {
        // テキサス (japanese for texas)
        this.search("テキサス");
        Assert.assertTrue(driver.getCurrentUrl().contains("https://www.rocketmiles.com/search?longitude="));
        Assert.assertTrue(driver.getCurrentUrl().contains("Texas"));

    }

    @Test
    // RocketMiles offers 5 non english languages for their site.  All 5 should be able to be searched and produce results
    public void nonEnglishSearchesNorwegian() {
        // Lisboa (norwegian for lisbon)
        this.search("Lisboa");
        Assert.assertTrue(driver.getCurrentUrl().contains("https://www.rocketmiles.com/search?longitude="));
        Assert.assertTrue(driver.getCurrentUrl().contains("Lisboa"));
    }

    @Test
    // assert that if user is in a search result, clicks the rocket miles logo, but then clicks the back button,
    // they are taken to their previous search results
    public void saveSession() {
        this.search("Houston, Texas");
        Assert.assertTrue(driver.getCurrentUrl().contains("https://www.rocketmiles.com/search?longitude="));
        Assert.assertTrue(driver.getCurrentUrl().contains("Houston"));
        driver.navigate().to("https://www.rocketmiles.com/");
        driver.navigate().back();
        Assert.assertTrue(driver.getCurrentUrl().contains("https://www.rocketmiles.com/search?longitude="));
        Assert.assertTrue(driver.getCurrentUrl().contains("Houston"));
    }
}

