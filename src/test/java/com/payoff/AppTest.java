package com.payoff;

import static org.testng.Assert.assertEquals;
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
  public void setUp() throws Exception
  {
    driver = new FirefoxDriver();
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @AfterTest
  public void tearDown() throws Exception
  {
    this.driver.quit();
  }

  @Test
  public void testSeleniumSetup() throws Exception {
    this.driver.get(baseURLs.get("google"));
    assertEquals("Google", this.driver.getTitle());
  }

  @Test
  public void testPayOffSiteUp() throws Exception
  {
    String siteName = "Payoff";
    this.driver.get(baseURLs.get("payoff"));
    assert(this.driver.getTitle().contains(siteName));
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

      assertEquals("$5,000", loanAmountInputElement.getAttribute("value"));
      assertEquals("fair", creditScoreInputElement.getAttribute("value"));
      assertEquals("I will feel FREEDOM.", whatWouldYouDoInputElement.getAttribute("value"));

      applyNowButton.click();

      //Thread.sleep(5000);
      WebDriverWait waitForPrequalificationPage = new WebDriverWait(driver, 5L);
      waitForPrequalificationPage.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-submit")));

      //https://apply.payoff.com/b9d5c66d-3a1c-49d8-8b8c-b04cba6532c4/#/pre-qualification
      assertEquals(this.driver.getTitle(), "Payoff - Pre Qualification");
      assert(this.isElementPresent(By.id("eca")));
      assert(this.isElementPresent(By.id("btn-submit")));
      assert(driver.getCurrentUrl().contains("pre-qualification"));
    }
    else
      assert(false);

  }

  //one positive: Input with invalid data
  @Test
  public void testPayOffScreenWithInvalidData()
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
      creditSelectDropDown.selectByValue("not_sure");

      whatWouldYouDoInputElement = driver.findElement(By.id("introspection-po-screen-widget"));
      whatWouldYouDoInputElement.sendKeys("I will feel FREEDOM.");

      applyNowButton = driver.findElement(By.id("btn-submit-po-screen-widget"));

      assertEquals("not_sure", creditScoreInputElement.getAttribute("value"));
      assertEquals("I will feel FREEDOM.", whatWouldYouDoInputElement.getAttribute("value"));

      //System.out.println(loanAmountInputElement.getAttribute("value"));
      if (loanAmountInputElement.getAttribute("value").equals(""))
      {
        if (this.isElementPresent(By.cssSelector("div.text-danger")))
        {
            String actualErrorMsg = driver.findElement(By.cssSelector("div.text-danger")).getText();
            assertEquals("Please enter a value between $5,000 to $25,000", actualErrorMsg);
            assert(!applyNowButton.isEnabled());
        }
        else
            assert(false);
      }
      else
        assert(false);
    }
    else
      assert(false);

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
      assert(this.isElementPresent(By.cssSelector("div.text-danger")));
      System.out.println(this.driver.getTitle());
      assert(this.driver.getTitle().contains("Refinancing Credit Card Debt"));
    }
    else
      assert(false);

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
