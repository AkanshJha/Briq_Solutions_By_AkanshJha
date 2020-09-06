package com.briq.solutions.webscraper.pageobject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class BizJournalsPage {
    WebDriver driver;
    public final String author_Element_Xpath = "(//div[contains(@class,'item__title')]/a)[1]";

    public BizJournalsPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "(//div[@class='item__body']/following::time)/following::h3[@class='item__title']")
   public List<WebElement> headlines_Element;

    /*@FindBy(xpath="//time")
    public List<WebElement> releaseTime;*/

    @FindBy(xpath = "//p[contains(@class,'content__segment')]")
    public WebElement headlineDetail_Element;

    @FindBy(xpath = "(//div[contains(@class,'item__title')]/a)[1]")
    public WebElement author_Element;

    @FindBy(xpath = "//a[contains(@id,'bx-close-inside')]/*[@class='bx-close-xsvg']")
    public WebElement popup_CloseButton;

    /*@FindBy(xpath = "(//div[contains(@class,'item__title')])[1]")
    public WebElement author_Element_1;*/
}
