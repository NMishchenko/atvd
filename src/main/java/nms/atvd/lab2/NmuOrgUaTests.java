package nms.atvd.lab2;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class NmuOrgUaTests {
    private WebDriver chromeDriver;
    private static final String baseUrl = "https://www.nmu.org.ua/ua/";

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-fullscreen");
        options.setImplicitWaitTimeout(Duration.ofSeconds(15));
        this.chromeDriver = new ChromeDriver(options);
    }

    @BeforeMethod
    public void precondition() {
        chromeDriver.get(baseUrl);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        chromeDriver.quit();
    }

    @Test
    public void testHeaderExists() {
        WebElement header = chromeDriver.findElement(By.id("header"));
        Assert.assertNotNull(header);
    }
    @Test
    public void testClickOnForStudent() {
        WebElement forStudentButton = chromeDriver.findElement(By.xpath("/html/body/center/div[4]/div/div[1]/ul/li[4]/a"));
        Assert.assertNotNull(forStudentButton);
        forStudentButton.click();
        Assert.assertEquals(chromeDriver.getCurrentUrl(), baseUrl);
    }
    @Test
    public void testSearchFieldOnForStudentPage() {
        String studentPageUrl = baseUrl + "content/students/";
        chromeDriver.get(studentPageUrl);

        WebElement searchField = chromeDriver.findElement(By.tagName("input"));
        Assert.assertNotNull(searchField);

        System.out.println(String.format("Name attribute: %s", searchField.getAttribute("name")) +
                String.format("\nID attribute: %s", searchField.getAttribute("id")) +
                String.format("\nType attribute: %s", searchField.getAttribute("type")) +
                String.format("\nValue attribute: %s", searchField.getAttribute("value")) +
                String.format("\nPosition: (%d; %d)", searchField.getLocation().x, searchField.getLocation().y) +
                String.format("\nSize: %dx%d", searchField.getSize().height, searchField.getSize().width));

        String inputValue = "I need info";
        searchField.sendKeys(inputValue);
        Assert.assertEquals(searchField.getText(), inputValue);

        searchField.sendKeys(Keys.ENTER);
        Assert.assertNotEquals(chromeDriver.getCurrentUrl(), studentPageUrl);
    }
    @Test
    public void testSlider() {
        WebElement nextButton = chromeDriver.findElement(By.className("next"));
        WebElement nextButtonByCSS = chromeDriver.findElement(By.cssSelector("a.next"));
        Assert.assertEquals(nextButton, nextButtonByCSS);

        WebElement previousButton = chromeDriver.findElement(By.className("prev"));

        for(int i = 0; i < 20; i++) {
            if (nextButton.getAttribute("class").contains("disabled")) {
                previousButton.click();
                Assert.assertTrue(previousButton.getAttribute("class").contains("disabled"));
                Assert.assertFalse(nextButton.getAttribute("class").contains("disabled"));
            } else {
                nextButton.click();
                Assert.assertTrue(nextButton.getAttribute("class").contains("disabled"));
                Assert.assertFalse(previousButton.getAttribute("class").contains("disabled"));
            }
        }
    }
}
