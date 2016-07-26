package tablecontrolobjectdemo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import utilities.Table;

import java.util.*;

/**
 * Created by heather.reid on 16/06/16.
 * Tests to verify the Table Control object
 * against the W3C site.
 */
public class TableTests {
    protected WebDriver WebDriver;

    @Before
    public void Setup()
    {
        String ChromeDriverLocation = "/home/dev/Documents/kerus-test/chromedriver";
        System.setProperty("webdriver.chrome.driver", ChromeDriverLocation);
        WebDriver = new ChromeDriver();
        WebDriver.navigate().to("http://www.thefriendlytester.co.uk/2012/12/table-controlobject.html");
        WebDriver.manage().window().maximize();
    }

    @After
    public void TearDown()
    {
        WebDriver.quit();
    }

    @Test
    public void VerifyColumnHeaders() throws Exception
    {
        List<String> ExpectedColumnHeaders = new ArrayList<String>();
        ExpectedColumnHeaders.add("Company");
        ExpectedColumnHeaders.add("Contact");
        ExpectedColumnHeaders.add("Country");

        Table w3cTable = new Table(WebDriver.findElement(By.id("customers")));
    }
}
