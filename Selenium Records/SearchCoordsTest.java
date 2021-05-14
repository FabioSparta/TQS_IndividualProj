// Generated by Selenium IDE
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;
public class SearchCoordsTest {
  private WebDriver driver;
  private Map<String, Object> vars;
  JavascriptExecutor js;
  @Before
  public void setUp() {
    driver = new ChromeDriver();
    js = (JavascriptExecutor) driver;
    vars = new HashMap<String, Object>();
  }
  @After
  public void tearDown() {
    driver.quit();
  }
  @Test
  public void searchCoords() {
    // Test name: searchCoords
    // Step # | name | target | value
    // 1 | open | / | 
    driver.get("http://localhost:8081/");
    // 2 | setWindowSize | 1552x840 | 
    driver.manage().window().setSize(new Dimension(1552, 840));
    // 3 | click | id=latitude | 
    driver.findElement(By.id("latitude")).click();
    // 4 | type | id=latitude | 20
    driver.findElement(By.id("latitude")).sendKeys("20");
    // 5 | click | id=longitude | 
    driver.findElement(By.id("longitude")).click();
    // 6 | type | id=longitude | 30
    driver.findElement(By.id("longitude")).sendKeys("30");
    // 7 | click | id=coordsSearch | 
    driver.findElement(By.id("coordsSearch")).click();
    // 8 | click | id=latitude | 
    driver.findElement(By.id("latitude")).click();
    // 9 | click | id=latitude | 
    driver.findElement(By.id("latitude")).click();
    // 10 | type | id=latitude | gb
    driver.findElement(By.id("latitude")).sendKeys("gb");
    // 11 | click | id=longitude | 
    driver.findElement(By.id("longitude")).click();
    // 12 | type | id=longitude | gbg
    driver.findElement(By.id("longitude")).sendKeys("gbg");
    // 13 | click | id=coordsSearch | 
    driver.findElement(By.id("coordsSearch")).click();
    // 14 | click | css=.w3-third:nth-child(2) > .w3-card | 
    driver.findElement(By.cssSelector(".w3-third:nth-child(2) > .w3-card")).click();
    // 15 | assertText | id=coords_err | Coords must be numbers.
    assertThat(driver.findElement(By.id("coords_err")).getText(), is("Coords must be numbers."));
  }
}