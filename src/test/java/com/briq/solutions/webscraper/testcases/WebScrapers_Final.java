package com.briq.solutions.webscraper.testcases;

import com.briq.solutions.utilities.ExcelUtilities;
import com.briq.solutions.utilities.ReadPropertiesFile;
import com.briq.solutions.webscraper.pageobject.BizJournalsPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This is used to Fetch the details from the given Target.
 * And writes all the details in an excel file with the excel headers.
 */

public class WebScrapers_Final {
    private final String currDir = System.getProperty("user.dir");
    protected String propFilePath = currDir + "\\configurations\\Briq_Test.properties";
    protected final Properties prop = ReadPropertiesFile.loadPropertiesFile(propFilePath);
    public WebDriver driver = null;
    protected WebDriverWait exWait = null;

    /**
     * This is a setup method, which is executed before any test starts executing.
     * In this, we are instantiating the Chrome webdriver and maximizing the window.
     */
    @BeforeTest
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        exWait = new WebDriverWait(driver, 10);

    }

    /**
     * This Test is navigating on to the given target in properties file and storing information of various
     * news cards availalbe on the target page.
     * All the information for latest cards(available on target page) is beng fetched.
     * - clicking on each card and fetching the value.
     * For each cards following valuesa re being captured-
     *      headling
     *      release time
     *      headline description
     *      author
     *
     * All the stored information for all the cards is being written on an excel file with same headers given above.
     *
     * If there is any exception, handled or unhandled in catch block, the Excel file will get ready till the execution is successful.
     */
    @Test
    void storeInformationOfCards() {
        BizJournalsPage bz = new BizJournalsPage(driver); //Page Factory class object
        ExcelUtilities eu = new ExcelUtilities(); //ExcelUtilities class object to perform excel operations
        Actions ac = new Actions(driver); //Selenium Actions class object
        Set<String> windows = null; //For storing value of   different browser windows

        List<WebElement> newsCards = null; // locator value of all the cards
        WebElement time = null; // locator value of time
        WebElement newsDetail = null; // locator value of news details
        WebElement author_Ele = null; // locator value of author
        ArrayList<String> dataToWrite = null; // ArrayList of data to be written in Excel
        String auth_Xpath = bz.author_Element_Xpath; // Xpath of author locator
        ArrayList<String> excelHeaders = null;

        String url = prop.getProperty("target"); // URL value from properties file
        String outputFile = currDir + "\\results\\Briq_Result_WebScraper.xlsx"; //Output Excel FIle Path
        String parentWindow = driver.getWindowHandle(); // Parent window value
        int row = 1; //for Rows in Excel
        String headLine = "";
        String releaseTime = "";
        String headLine_Detail = "";
        String author = "";

        newsCards = bz.headlines_Element;
        newsDetail = bz.headlineDetail_Element;
        ac = new Actions(driver);
        excelHeaders = new ArrayList<String>();
        excelHeaders.add("HeadLine");
        excelHeaders.add("Release Time");
        excelHeaders.add("Headline Details");
        excelHeaders.add("Author");

        eu.writeExcelData(0, excelHeaders);

        driver.get(url);
        System.out.println(newsCards.size());

        try {
            for (WebElement cardValue : newsCards) {
                dataToWrite = new ArrayList<String>();
                System.out.println("Fetching values for card: " + row);
                try {
                    time = driver.findElement(By.xpath("(//time)[" + row + "]"));
                    headLine = storeTextOfElement(cardValue);
                    System.out.println(headLine);
                    dataToWrite.add(headLine);

                    releaseTime = storeTextOfElement(time).trim();
                    System.out.println(releaseTime);
                    dataToWrite.add(releaseTime);

                    ac.keyDown(Keys.CONTROL).click().build().perform();
                    cardValue.click();
                    Thread.sleep(2000);
                    windows = driver.getWindowHandles();

                    for (String win : windows) {
                        if (!win.equals(parentWindow)) {
                            driver.switchTo().window(win);
                            Thread.sleep(1000);
                            headLine_Detail = storeTextOfElement(newsDetail);
                            System.out.println(headLine_Detail);
                            dataToWrite.add(headLine_Detail);
                            try {
                                author_Ele = driver.findElement(By.xpath(auth_Xpath));
                                author = storeTextOfElement(author_Ele);
                            } catch (NoSuchElementException e) {
                                System.out.println("No author found with xpath: " + auth_Xpath);
                                author_Ele = driver.findElement(By.xpath(auth_Xpath.replace("/a", "")));
                                author = storeTextOfElement(author_Ele);
                            }
                            dataToWrite.add(author);
                            System.out.println(author);
                            driver.close(); // to close newly opened window.
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                eu.writeExcelData(row++, dataToWrite);
                driver.switchTo().window(parentWindow);
                windows = null;
                dataToWrite = null;
            }
            eu.flushExcel(outputFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Please check if the file to be written is opened.");
            System.out.println("Your Output Excel File is ready");
            eu.flushExcel(outputFile);
        }
    }

    String storeTextOfElement(WebElement element) {
        String text = "";
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            if (element.isDisplayed()) {
                text = element.getText();
            } else {
                closePopup();
                System.out.println("***** Not Visible *****");
                text = element.getText();
            }
        } catch (StaleElementReferenceException e) {
            closePopup();
        }
        if (text.equals("")) {
            closePopup();
            text = element.getText();
        }
        return text;
    }

    void closePopup() {
        BizJournalsPage bz = new BizJournalsPage(driver);
        new Actions(driver).moveToElement(bz.popup_CloseButton).click().build().perform();
        System.out.println("Popup is closed.");
    }

    @AfterTest
    void tearDown() {
        driver.quit();
        System.out.println("Execution Completed...");
    }
}
