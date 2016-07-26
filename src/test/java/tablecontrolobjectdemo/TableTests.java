package tablecontrolobjectdemo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import utilities.Table;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by heather.reid on 16/06/16.
 * Tests to verify the Table Control object
 * against the W3C site.
 */
public class TableTests {
    protected WebDriver WebDriver;

    @Before
    public void setup() {
        String ChromeDriverLocation = "/home/dev/Documents/kerus-test/chromedriver";
        System.setProperty("webdriver.chrome.driver", ChromeDriverLocation);
        WebDriver = new ChromeDriver();
        WebDriver.navigate().to("http://www.thefriendlytester.co.uk/2012/12/table-controlobject.html");
        WebDriver.manage().window().maximize();
    }

    @After
    public void tearDown() {
        WebDriver.quit();
    }

    @Test
    public void verifyColumnHeaders() throws Exception {
        List<String> ExpectedColumnHeaders = new ArrayList<String>();
        ExpectedColumnHeaders.add("Company");
        ExpectedColumnHeaders.add("Contact");
        ExpectedColumnHeaders.add("Country");

        // Pass in the ID of the whole table.
        Table w3cTable = new Table(WebDriver.findElement(By.id("customers")));

        assertEquals(ExpectedColumnHeaders, w3cTable.readAllColumnHeaders());
    }

    @Test
    public void verifyColumnCount() throws Exception {
        Table w3cTable = new Table(WebDriver.findElement(By.id("customers")));

        assertEquals(3, w3cTable.columnCount());
    }

    @Test
    public void verifyRowCount() throws Exception{
        Table w3cTable = new Table(WebDriver.findElement(By.id("customers")));
        // There are 12 rows but there is also a header so 13 rows.
        assertEquals(13, w3cTable.rowCount());
    }

    @Test
    public void verifyRowContentsUsingKnownColumnValue() throws Exception {
        Table w3cTable = new Table(WebDriver.findElement(By.id("customers")));

        assertEquals("Alfreds Futterkiste Maria Anders Germany", w3cTable.findRowMatchingColumnData("Company", "Alfreds Futterkiste").getText());
    }

    @Test
    public void verifyRowContentContainingKnownValue() throws Exception{
        Table w3cTable = new Table(WebDriver.findElement(By.id("customers")));

        assertEquals("Laughing Bacchus Winecellars Yoshi Tannamuri Canada", w3cTable.findFirstRowByKnownValue("Canada").getText());
    }

    @Test
    public void doesColumnContain() throws Exception {
        Table w3cTable = new Table(WebDriver.findElement(By.id("customers")));

        assertTrue(w3cTable.isValuePresentWithinColumn("Country", "Denmark"));
        assertFalse(w3cTable.isValuePresentWithinColumn("Country", "Greece"));
    }

    @Test
    public void findCellByKnownColumnValue() throws Exception {
        Table w3cTable = new Table(WebDriver.findElement(By.id("customers")));

        WebElement matchedCell = w3cTable.findCellByColumnAndKnownValue("Company", "North/South");

        // Redundant but shows that it found the element.
        assertEquals("North/South", matchedCell.getText());
    }

    @Test
    public void findCellByRowAndColumnName() throws Exception {
        Table w3cTable = new Table(WebDriver.findElement(By.id("customers")));

        // Demonstration of another table method to get a row.
        WebElement requiredRow = w3cTable.findRowMatchingColumnData("Company", "The Big Cheese");

        assertEquals("Liz Nixon", w3cTable.findCellByRowAndColumnName(requiredRow, "Contact").getText());
    }

    @Test
    public void findCellByColumnAndRowNumber() throws Exception {
        Table w3cTable = new Table(WebDriver.findElement(By.id("customers")));

        assertEquals("Giovanni Rovelli", w3cTable.findCellByColumnAndRowNumber("Contact", 8).getText());
    }

    @Test
    public void verifyAllColumnData() throws Exception {
        List<String> ExpectedColumnData = new ArrayList<String>();
        ExpectedColumnData.add("Germany");
        ExpectedColumnData.add("Sweden");
        ExpectedColumnData.add("Mexico");
        ExpectedColumnData.add("Austria");
        ExpectedColumnData.add("UK");
        ExpectedColumnData.add("Germany");
        ExpectedColumnData.add("Canada");
        ExpectedColumnData.add("Italy");
        ExpectedColumnData.add("UK");
        ExpectedColumnData.add("France");
        ExpectedColumnData.add("USA");
        ExpectedColumnData.add("Denmark");

        Table w3cTable = new Table(WebDriver.findElement(By.id("customers")));

        assertEquals(ExpectedColumnData, w3cTable.readAllDataFromAColumn("Country"));
    }

    @Test
    public void verifyFranciscoChangCompany() throws Exception {
        Table w3cTable = new Table(WebDriver.findElement(By.id("customers")));

        assertEquals("Centro comercial Moctezuma", w3cTable.readAColumnForRowContainingValueInColumn("Company", "Francisco Chang", "Contact"));
    }
}
