import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SearchCitySteps {
    private WebDriver driver;
    private WebDriverWait wait ;

    @Given("Open Browser")
    public void OpenBrowser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

    }

    @When("I navigate to {string}")
    public void iNavigateToHttpLocalhost(String url) {
        driver.get(url);

    }

    @And("I write {string} in the searchbox")
    public void iWriteParisInTheSearchbox(String cityName) {
        driver.findElement(By.id("city")).click();
        driver.findElement(By.id("city")).sendKeys(cityName);
    }

    @And("clicks on GO 1")
    public void clicksOnGO1() {
        driver.findElement(By.id("getCityInfo")).click();
    }

    @Then("a div containing the city airquality info with city name {string} should appear")
    public void aDivContainingTheCityAirqualityInfoWithCityNameParisShouldAppear(String cityName) {
        wait = new WebDriverWait(driver, 5000);
        WebElement name = wait.until(ExpectedConditions.presenceOfElementLocated((By.cssSelector(".card-header"))));
        assertThat(name.getText(), is(cityName));
    }

    @And("the city's Latitude should be {double}")
    public void theCitySLatitudeShouldBe(double latitude) {
        assertThat(driver.findElement(By.cssSelector(".card-title:nth-child(3)")).getText(), is("Latitude: "+latitude));
    }

    @And("the city's Longitude should be {double}")
    public void theCitySLongitudeShouldBe(double longitude) {
        assertThat(driver.findElement(By.cssSelector(".card-title:nth-child(4)")).getText(), is("Longitude: "+longitude));
    }

    @And("the Date should be {string}")
    public void theDateShouldBeToday(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (date.equals("Today"))
            date= LocalDate.now().format(formatter);
        else
            date= LocalDate.now().plusDays(1).format(formatter);

        assertThat(driver.findElement(By.cssSelector(".card-title:nth-child(1)")).getText(), is("Date: "+date));
    }

    @And("selects {string} as Date")
    public void selectsTomorrowAsDate(String date) {
        driver.findElement(By.cssSelector(".btn:nth-child(4)")).click();
    }

    @Then("an error message saying {string} should appear 1")
    public void anErrorMessageSayingCityNotFoundShouldAppear1(String error) throws InterruptedException {
        Thread.sleep(3000);
        assertThat(driver.findElement(By.id("searchbox_err")).getText(), is(error));
    }
    @Then("an error message saying {string} should appear 2")
    public void anErrorMessageSayingCityNotFoundShouldAppear2(String error) throws InterruptedException {
        Thread.sleep(3000);
        assertThat(driver.findElement(By.id("select_err")).getText(), is(error));
    }

    @And("I clicks on {string} in the select bar")
    public void iClicksOnParisInTheSelectBar(String cityName) {
        driver.findElement(By.id("city2")).click();
        {
            WebElement dropdown = driver.findElement(By.id("city2"));
            dropdown.findElement(By.xpath("//option[. = '"+ cityName +"']")).click();
        }
    }

    @And("I leave the default option {string}")
    public void iLeavesTheDefaultOptionCities(String _default) {
    }

    @And("clicks on GO 2")
    public void clicksOnGO2() {
        driver.findElement(By.id("getCityInfo2")).click();
    }



    @And("the user writes {string} as Latitude")
    public void theUserWritesAsLatitude(String latitude) {
        driver.findElement(By.id("latitude")).click();
        driver.findElement(By.id("latitude")).sendKeys(latitude);
    }

    @And("the user writes {string} as Longitude")
    public void theUserWritesAsLongitude(String longitude) {
        driver.findElement(By.id("longitude")).click();
        driver.findElement(By.id("longitude")).sendKeys(longitude);
    }

    @And("clicks on GO 3")
    public void clicksOnGO() {
        driver.findElement(By.id("coordsSearch")).click();
    }


    @Then("an error message saying {string} should appear 3")
    public void anErrorMessageSayingCoordsMustBeNumbersShouldAppear(String error) {
        assertThat(driver.findElement(By.id("coords_err")).getText(), is(error));
    }







    // Dynamism Steps
    @Then("div should have {int} result cards")
    public void divShouldHaveResultCards(int qty) {
        WebElement element = driver.findElement(By.id("results"));
        int numberOfChilds = Integer.parseInt(element.getAttribute("childElementCount"));
        assertThat(numberOfChilds,is(qty));
    }

    @And("I delete one of them")
    public void iDeleteOneOfThem() {
        driver.findElement(By.cssSelector(".card:nth-child(1) .fas")).click();
    }

    @And("I delete all")
    public void iDeleteAll()  {
        driver.findElement(By.id("clearResults")).click();
    }

    @And("I do another search on another existent city")
    public void iDoAnotherSearchOnAnotherExistentCity() throws InterruptedException {
        driver.findElement(By.id("getCityInfo")).click();
        Thread.sleep(3000);
    }

    @And("I do a search on an existent city")
    public void iDoASearchOnAnExistentCity() throws InterruptedException {
        driver.findElement(By.id("city")).click();
        driver.findElement(By.id("city")).sendKeys("Paris");
        driver.findElement(By.id("getCityInfo")).click();
        Thread.sleep(3000);
    }

}
