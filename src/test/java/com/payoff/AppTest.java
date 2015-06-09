package com.payoff;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Unit test for simple App.
 */
public class AppTest
{
  private WebDriver driver;
  private Map<String,String> baseURLs;

  public AppTest()
  {
    baseURLs = new HashMap<>();

    baseURLs.put("google", "http://www.google.com");
    baseURLs.put("payoff", "http://www.payoff.com");

  }

  @BeforeTest
  public void setUpDriverFromSelenium() throws Exception
  {
    driver = new FirefoxDriver();
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @AfterTest
  public void closeSeleniumDriver() throws Exception
  {
    this.driver.quit();
  }

  @Test(groups = {"TestSetup"})
  public void testSeleniumSetup() throws Exception {
    this.driver.get(baseURLs.get("google"));

    //Opposite to JUnit assertEquals - actual, expected, custom failure msg
    assertEquals(this.driver.getTitle(), "Google", "Cannot view a webpage and maybe network is down?!");
  }

  @Test(groups = {"TestSetup"})
  public void testPayOffSiteUp() throws Exception
  {
    String siteName = "Payoff";
    this.driver.get(baseURLs.get("payoff"));
    assertTrue(this.driver.getTitle().contains(siteName),
      "Cannot view the basepage of the application and maybe the application is down?!");
  }

  //one positive
  @Test
  public void testPayOffScreenWithValidDataSubmission() throws Exception
  {
    WebDriverWait poScreenWait = new WebDriverWait(this.driver, 3);
    this.driver.get(baseURLs.get("payoff"));
    poScreenWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("po-screen-widget")));

    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); //for the click and page directs

    WebElement loanAmountInputElement;
    WebElement creditScoreInputElement;
    Select creditSelectDropDown;
    WebElement whatWouldYouDoInputElement;
    WebElement applyNowButton;

    if (isElementPresent(By.id("po-screen-widget")) && isElementPresent(By.id("btn-submit-po-screen-widget")))
    {
      loanAmountInputElement = driver.findElement(By.id("loan_amount-po-screen-widget"));
      loanAmountInputElement.sendKeys("$5,000");
      //System.out.println(inputElement.getAttribute("value"));

      creditScoreInputElement = driver.findElement(By.id("credit_score_bracket-po-screen-widget"));
      creditSelectDropDown = new Select(creditScoreInputElement);
      creditSelectDropDown.selectByValue("fair");

      whatWouldYouDoInputElement = driver.findElement(By.id("introspection-po-screen-widget"));
      whatWouldYouDoInputElement.sendKeys("I will feel FREEDOM.");

      applyNowButton = driver.findElement(By.id("btn-submit-po-screen-widget"));

      assertEquals(loanAmountInputElement.getAttribute("value"), "$5,000");
      assertEquals(creditScoreInputElement.getAttribute("value"), "fair");
      assertEquals(whatWouldYouDoInputElement.getAttribute("value"), "I will feel FREEDOM.");

      applyNowButton.click();

      //Thread.sleep(5000);
      WebDriverWait waitForPrequalificationPage = new WebDriverWait(driver, 5L);
      waitForPrequalificationPage.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-submit")));

      //https://apply.payoff.com/b9d5c66d-3a1c-49d8-8b8c-b04cba6532c4/#/pre-qualification
      assertEquals(this.driver.getTitle(), "Payoff - Pre Qualification", "Direct to the wrong page.");
      assertTrue(this.isElementPresent(By.id("eca")), "UI widget- " + By.id("eca").toString() + " is missing.");
      assertTrue(this.isElementPresent(By.id("btn-submit")), "UI widget- " + By.id("btn-submit").toString() + " is missing.");
      assertTrue(driver.getCurrentUrl().contains("pre-qualification"), "Direct to the wrong page or URL is changed.");
    }
    else
      assertTrue(false, "Missing UI Widget at the basepage and unable to proceed the test");

  }

  //one negative: Submit
  @Test
  public void testPayOffScreenSubmissionWithInvalidData()
  {
    WebDriverWait poScreenWait = new WebDriverWait(this.driver, 3);
    this.driver.get(baseURLs.get("payoff"));
    poScreenWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("po-screen-widget")));

    WebElement loanAmountInputElement;
    WebElement creditScoreInputElement;
    Select creditSelectDropDown;
    WebElement whatWouldYouDoInputElement;
    WebElement applyNowButton;

    if (isElementPresent(By.id("po-screen-widget")) && isElementPresent(By.id("btn-submit-po-screen-widget")))
    {
      loanAmountInputElement = driver.findElement(By.id("loan_amount-po-screen-widget"));
      loanAmountInputElement.sendKeys("");

      creditScoreInputElement = driver.findElement(By.id("credit_score_bracket-po-screen-widget"));
      creditSelectDropDown = new Select(creditScoreInputElement);
      creditSelectDropDown.selectByValue("0");

      whatWouldYouDoInputElement = driver.findElement(By.id("introspection-po-screen-widget"));
      whatWouldYouDoInputElement.sendKeys("I will feel FREEDOM.");

      applyNowButton = driver.findElement(By.id("btn-submit-po-screen-widget"));

      applyNowButton.click();
      assertTrue(this.isElementPresent(By.cssSelector("div.text-danger")), "The UI widget is missing for Error msg.");
      //System.out.println(this.driver.getTitle());
      assertTrue(this.driver.getTitle().contains("Refinancing Credit Card Debt"),
        "Direct to the wrong page after clicking Apply Now.  Should stay the samge page");
    }
    else
      assertTrue(false, "Missing UI Widget at the basepage and unable to proceed the test.");

  }

  //convenient method
  public boolean isElementPresent(By by)
  {
    try
    {
      driver.findElements(by);
      return true;
    }
    catch (NoSuchElementException e)
    {
      return false;
    }
  }
}
