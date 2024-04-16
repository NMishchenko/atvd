package nms.atvd.lab2;

import org.testng.Assert;
import org.testng.annotations.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;

public class DeepStatesMapTests {
    private WebDriver foxDriver;
    private final String url = "https://deepstatemap.live";

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--start-fullscreen");
        options.setImplicitWaitTimeout(Duration.ofSeconds(15));
        foxDriver = new FirefoxDriver();
    }

    @BeforeMethod
    public void precondition() {
        foxDriver.get(url);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        foxDriver.quit();
    }

    @Test
    public void testMapLoads() {
        WebElement mapElement = foxDriver.findElement(By.id("map"));
        Assert.assertNotNull(mapElement);
    }

    @Test
    public void testLegendAbsenceWithoutInfoClick() {
        List<WebElement> legendElements = foxDriver.findElements(By.xpath("//div[contains(@class, 'deep-dialog')]"));
        Assert.assertEquals(legendElements.size(), 0);
    }


    @Test
    public void testLegendPresenceOnInfoClick() {
        WebElement legendButton = foxDriver.findElement(By.className("control-info"));
        legendButton.click();
        WebElement legendElement = foxDriver.findElement(By.xpath("//div[contains(@class, 'deep-dialog')]"));
        Assert.assertNotNull(legendElement);
    }

    @Test
    public void testOneOfTheCapitalsDestroy() throws InterruptedException {
        Thread.sleep(2000);
        List<WebElement> popupDialogs = foxDriver.findElements(By.className("cl-dialog-close-icon"));

        if (!popupDialogs.isEmpty()) {
            popupDialogs.get(0).click();
        }

        WebElement allImagesBlock = foxDriver.findElement(By.xpath("//div[contains(@class, 'leaflet-marker-pane')]"));
        WebElement kyivImage = allImagesBlock.findElement(By.xpath("//img[contains(@src, 'images/custom_nato/ukrainecapital.png')]"));
        kyivImage.click();
        Thread.sleep(1000);

        List<WebElement> enemyCapitalImages = allImagesBlock.findElements(By.xpath("//img[contains(@src, '/images/custom_nato/russiacapital.png')]"));
        JavascriptExecutor executor = (JavascriptExecutor) foxDriver;
        executor.executeScript("arguments[0].click();", enemyCapitalImages.get(0));

        Thread.sleep(5000);
        enemyCapitalImages = allImagesBlock.findElements(By.xpath("//img[contains(@src, '/images/custom_nato/russiacapital.png')]"));
        Assert.assertEquals(enemyCapitalImages.size(), 1);
    }
}

