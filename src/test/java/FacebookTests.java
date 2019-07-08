import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class FacebookTests {
    // please note, I do not have a Facebook!  So this exercise was not as exciting (or terrifying) as im sure it is for others
    // I did create a generic account for this exercise to get an access token and create an app to test with etc.
    // TODO: enter your personal access token here for tests to run
    private String accessToken = "EAAFlZBoiYCigBAKS9iO9v8HXvf2ZCkIRygoMxMZBq3YYIuKbNQwFgXT5jqBnzlVrXGfZAUzb3ZCVpSNOKdt7E8V9phEv95D6jAzoiZCRU3BfFKkn54ux0xGRqN20VU0ereddgVqzhljktZB6hNZAdhZCrZAHRq6FdTA2KN05iZAXapi8kpYwSZCzy6PgRVAMZB9IEZAhKqwZBQZB9UZC2RqvgrdbDMVAK";

    @Test
    public void getCurrentUsersIdandName() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.navigate().to("https://graph.facebook.com/v3.3/me?fields=id%2Cname&access_token=" + accessToken);
        driver.quit();
        driver.close();
    }


    @Test
    public void seeAccessibleData() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.navigate().to("https://graph.facebook.com/v3.3/me?fields=permissions&access_token=" + accessToken);
        driver.quit();
        driver.close();
    }

    @Test
    public void findTaggableFriendsNames() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.navigate().to("https://graph.facebook.com/v3.3/me?fields=taggable_friends%7Bname%7D&access_token=" + accessToken);
        driver.quit();
        driver.close();
    }
}
