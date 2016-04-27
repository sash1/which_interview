package uk.which;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


public class StepDefinitions {

    WebDriver driver;
    int lastpage;
    List<String> selectedFilters = new ArrayList<String>();

    @Before
    public void bfScenario() {
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    }

    @Given("^I have navigated to http://www\\.which\\.co\\.uk/reviews/televisions$")
    public void i_have_navigated_to_http_www_which_co_uk_reviews_televisions()
            throws Throwable {
        driver.navigate().to("http://www.which.co.uk/reviews/televisions");
        driver.findElement(
                By.cssSelector("#dfp__listing__main button[type='button']"))
                .click();
    }

    @Then("^I should see more then one search results$")
    public void i_should_see_more_then_one_search_results() throws Throwable {
        int results_count = driver.findElements(
                By.cssSelector("ul.products > li")).size();

        assertTrue("No results found!", results_count > 1);
    }

    @Then("^Results count should be visible$")
    public void results_count_should_be_visible() throws Throwable {

        WebElement page_entries_info = driver.findElement(By
                .cssSelector("p.page-entries-info"));
        String page_as_string = page_entries_info.getText();
        List<WebElement> numbers = page_entries_info.findElements(By
                .cssSelector("strong"));

        String first = numbers.get(0).getText();
        String second = numbers.get(1).getText();
        String third = numbers.get(2).getText();
        String expected_text = first + " – " + second + " of " + third
                + " Results";
        lastpage = (int) Math.ceil(Double.parseDouble(third)
                / Double.parseDouble(second));
        System.out.println("============>    " + lastpage);

        assertTrue("Page entries info: First number is not correct",
                Integer.parseInt(first) > 0);
        assertTrue("Page entries info: Second number is not correct",
                Integer.parseInt(second) > 0);
        assertTrue("Page entries info: Third number is not correct",
                Integer.parseInt(third) > 0);

        assertEquals("Page entries info is invalid", page_as_string,
                expected_text);
    }

    @Then("^Pagination should be visible and correct$")
    public void pagination_should_be_visible_and_correct() throws Throwable {

        List<WebElement> plist1 = driver.findElements(By
                .cssSelector(".is-numbered a"));

        for (int i = 0; i < plist1.size(); i++) {
            String value1 = plist1.get(i).getText();
            assertEquals("Pagination Error", Integer.toString(i + 1), value1);

        }

        driver.findElement(By.cssSelector(".pagination__link.is-last-page"))
                .click();

        WebElement plist2 = driver.findElement(By
                .cssSelector(".is-numbered > a.is-current"));

        assertEquals("Pagination Error", Integer.toString(lastpage),
                plist2.getText());
    }

    @Then("^Sort dropdown should be set to '(.*)'$")
    public void sort_dropdown_should_be_set_to_Price_low_to_high(
            String selectedOption) throws Throwable {
        WebElement sortDropdown = new Select(driver.findElement(By
                .cssSelector("select.sort-selector"))).getFirstSelectedOption();

        assertEquals("Sort dropdown is incorrect", sortDropdown.getText(),
                selectedOption);
    }

    @When("^I select (\\d+) random filters$")
    public void i_select_random_filters(int arg1) throws Throwable {
        List<WebElement> show_more_buttons = driver.findElements(By
                .xpath("//button[text()='Show more…']"));
        show_more_buttons.get(0).click();
        show_more_buttons.get(1).click();

        for (int i = 0; i < arg1; i++) {
            WebElement checkbox = getRandomFilter();
            System.out.println("CheckBox ===> Checked  " + checkbox.getText());
            List<WebElement> inputs = checkbox.findElements(By
                    .cssSelector("input"));
            if (inputs.size() > 0) {
                selectedFilters.add(checkbox.getAttribute("for"));

                checkbox.click();

                waitUntilElementNotPresent(By.className("loading-indicator"),
                        10);

            }
        }
    }

    @When("^I click clear filters$")
    public void i_click_clear_filters() throws Throwable {
        driver.findElement(By.xpath("//button[text()='Clear all']")).click();
    }

    @Then("^Filters should be empty$")
    public void filters_should_be_empty() throws Throwable {
        for (int i = 0; i < selectedFilters.size(); i++) {
            String id = selectedFilters.get(i);
            WebElement checkbox = driver.findElement(By.id(id));

            assertFalse("Filter is not empty: '" + id + "'",
                    checkbox.isSelected());
        }
    }

    @Then("^Result should be ordered by price low to high$")
    public void result_should_be_ordered_by_price_low_to_high()
            throws Throwable {

        double lastPrice = 0.0;

        List<WebElement> prices = driver.findElements(By
                .cssSelector("ul.products div[data-test='price-amount']"));

        for (int i = 0; i < prices.size(); i++) {
            String priceString = prices.get(i).getText();
            double currentPrice = Double.valueOf(priceString.replaceAll(
                    "[^\\d.-]", ""));

            assertTrue("failure - Price is not bigger or equal ",
                    currentPrice >= lastPrice);

            lastPrice = currentPrice;
        }
    }

    @When("^I order results by \"(.*?)\"$")
    public void i_order_results_by(String arg1) throws Throwable {

        new Select(driver.findElement(By.cssSelector("select.sort-selector")))
                .selectByVisibleText(arg1);

        waitForDateToChange();

    }

    @When("^I select screen size to be (.+)$")
    public void i_select_screen_size_to_be(String arg1) throws Throwable {
        WebElement filter = driver.findElement(By
                .xpath("//aside/section/ul/li/label/div/span/span[text()='"
                        + arg1 + "']"));
        filter.click();

        waitForDateToChange();
    }

    @Then("^I should see search results for (\\d+)-(\\d+)\" listings$")
    public void i_should_see_search_results_for_listings(int minSize,
            int maxSize) throws Throwable {

        List<WebElement> screenSizes = driver.findElements(By
                .cssSelector("ul.products > li .product-listing__key-fact"));

        for (int i = 0; i < screenSizes.size(); i++) {
            String screenSize = screenSizes.get(i).getText();
            int currentSize = Integer.parseInt(screenSize.replace("\"", ""));

            assertTrue("Screen size is not correct", currentSize >= minSize
                    && currentSize <= maxSize);
        }

    }

    @When("^I select prices from ([\\d,]+) to ([\\d,]+)$")
    public void i_select_prices_from_to(String lower, String upper)
            throws Throwable {
        WebElement lowerPrice = driver.findElement(By
                .cssSelector("select[name='search[range][55][price][lower]']"));
        new Select(lowerPrice).selectByVisibleText("£" + lower);
        waitForDateToChange();

        WebElement upperPrice = driver.findElement(By
                .cssSelector("select[name='search[range][55][price][upper]']"));
        new Select(upperPrice).selectByVisibleText("£" + upper);
        waitForDateToChange();
    }

    @Then("^I should see only results in price range (\\d+)-(\\d+)$")
    public void i_should_see_only_results_in_price_range(int minPrice,
            int maxPrice) throws Throwable {

        List<WebElement> prices = driver.findElements(By
                .cssSelector("ul.products div[data-test='price-amount']"));

        for (int i = 0; i < prices.size(); i++) {
            String priceString = prices.get(i).getText();
            double currentPrice = Double.valueOf(priceString.replaceAll(
                    "[^\\d.]", ""));

            assertTrue("Listing in not in correct prize range",
                    currentPrice >= minPrice && currentPrice <= maxPrice);
        }
    }

    @When("^I select Samsung$")
    public void i_select_Samsung() throws Throwable {

    }

    @Then("^I should see results only for Samsung$")
    public void i_should_see_results_only_for_Samsung() throws Throwable {

    }

    @When("^I select LED$")
    public void i_select_LED() throws Throwable {

    }

    @Then("^I should see results for LED Screen type$")
    public void i_should_see_results_for_LED_Screen_type() throws Throwable {

    }

    @After
    public void afterScenario() throws InterruptedException {
        Thread.sleep(2000);
        driver.close();
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private void waitUntilElementNotPresent(By by, int seconds)
            throws Exception {
        for (int second = 0;; second++) {
            if (second >= seconds) {
                throw new Exception("Timeout");
            }

            Thread.sleep(300);

            if (isElementPresent(by) == false) {
                break;
            }

        }
    }

    private void waitForDateToChange() throws Exception {
        waitUntilElementNotPresent(By.className("loading-indicator"), 10);
    }

    private WebElement getRandomFilter() {
        List<WebElement> checkboxes = driver.findElements(By
                .cssSelector("aside ul > li > label"));
        Collections.shuffle(checkboxes);

        return checkboxes.get(0);
    }

}
