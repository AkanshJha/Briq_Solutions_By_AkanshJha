package com.briq.solutions.webscraper.testcases;

import com.briq.solutions.utillities.ExcelUtilities;
import com.briq.solutions.utillities.ReadPropertiesFile;
import com.briq.solutions.webscraper.pageobject.BizJournalsPage;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class WebScraper {
    protected final String currentDir = System.getProperty("user.dir");
    protected String propFilePath = currentDir + "\\configurations\\Briq_Test.properties";
    protected final Properties prop = ReadPropertiesFile.loadPropertiesFile(propFilePath);
    public WebDriver driver = null;
    protected WebDriverWait exWait = null;

    @BeforeTest
    void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        System.out.println("Chrome Browser initiated..");
        exWait = new WebDriverWait(driver, 10);

    }

    @Test
    void captureInformationFromAllCards() {
        BizJournalsPage bz = new BizJournalsPage(driver);
        String url = prop.getProperty("target");
        ExcelUtilities eu = new ExcelUtilities();
        String outputFile = currentDir + "\\results\\Briq_Result.xlsx";
        Actions ac = new Actions(driver);
        String parentWindow = driver.getWindowHandle();
        //System.out.println("Parent Window:"+parentWindow);
        Set<String> windows = null;
        int row = 1;
        driver.get(url);
        List<WebElement> headlines = bz.headlines_Element;
        WebElement time = null;
        System.out.println(headlines.size());
        ListIterator<WebElement> itr = headlines.listIterator();
        WebElement heading = null;
        String bulletin = "";
        String releaseTime = "";
        String details = "";
        String author = "";
        ArrayList<String> dataToWrite = null;
        try {
            while (itr.hasNext()) {
                System.out.println("Reading card number: " + row);
                dataToWrite = new ArrayList<String>();
                heading = itr.next();
                //System.out.println(heading.toString());
                try {
                    time = driver.findElement(By.xpath("(//time)[" + row + "]"));
                    bulletin = heading.getText();

                    System.out.println(bulletin);
                    dataToWrite.add(bulletin);

                    releaseTime = storeTextOfElement(time).trim();

                    System.out.println(releaseTime);
                    dataToWrite.add(releaseTime);
                    //ac.moveToElement(e).click().build().perform();
                    ac.keyDown(Keys.CONTROL).build().perform();
                    heading.click();
                    //Thread.sleep(2000);
                    windows = driver.getWindowHandles();
                    for (String win : windows) {
                        //System.out.println("Windows:"+win);
                        if (!win.equals(parentWindow)) {
                            driver.switchTo().window(win);
                            System.out.println("Child window is selected..");
                            details = storeTextOfElement(bz.headlineDetail_Element);
                            System.out.println("Details Stored");
                            Thread.sleep(3000);
                            dataToWrite.add(details);
                            System.out.println("Details have been added to arrayList");
                            System.out.println(details);
                            try {
                                System.out.println("Waiting for author to get visible..");
                                bz.author_Element = exWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(bz.author_Element_Xpath)));
                                System.out.println("author is displayed..");
                                author = storeTextOfElement(bz.author_Element);
                                System.out.println("author value is stored in variable.");
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                                String auth_xpath = bz.author_Element_Xpath.replace("/a", "");
                                bz.author_Element = driver.findElement(By.xpath(auth_xpath));
                                author = storeTextOfElement(bz.author_Element);
                            }
                            dataToWrite.add(author);
                            System.out.println(author);
                            driver.close();
                        }
                    }
                } catch (ElementClickInterceptedException e) {
                    closePopup();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                eu.writeExcelData(row++, dataToWrite);
                driver.switchTo().window(parentWindow);
                windows = null;
                dataToWrite = null;
                System.out.println("Parent Window selected.");
                /*if(row==2){
                    break;
                }*/
            }
            eu.flushExcel(outputFile);
        } catch (ElementClickInterceptedException e) {
            closePopup();
        } catch
        (Exception e) {
            e.printStackTrace();
            SoftAssert soft = new SoftAssert();
            soft.assertTrue(false);
            //e.printStackTrace();
            System.out.println("Please check if the file to be written is opened.");
            System.out.println("Your Output Excel File is ready");
            eu.flushExcel(outputFile);
        }
    }

    String storeTextOfElement(WebElement element) {
        String text = "";
        try {
            System.out.println("Scrolling to the element..");
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            System.out.println("Scrolled to element successfully.\nChecking if element is displayed..?");
            if (element.isDisplayed()) {
                text = element.getText();
                System.out.println("Text is stored...");
            } else {
                closePopup();
                System.out.println("***** Not Visible *****");
                text = element.getText();
                //return "***** Not Visible *****";
            }
//            text = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].text;", element);
//            System.out.println("Text '" + text + "' is stored using JavaScriptExecutor.");
        } catch (StaleElementReferenceException e) {
            closePopup();
        }
        if (text.equals("")) {
            closePopup();
            //text = element.getText();
            //text = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].text;", element);
            //System.out.println("Text '"+text+"' is stored using JavaScriptExecutor on seconds attempt...");
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
