package utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heather.reid on 17/05/16.
 * For Xpath searching in dynamically
 * changing tables.
 * Ref: Richard Bradshaw aka Friendly
 * Tester https://github.com/FriendlyTester/Table-ControlObject
 */
public class Table {
    private WebElement TABLE_BODY;
    // List of all the TABLE_HEADERS used to get the column indices.
    private List<WebElement> TABLE_HEADERS;
    // List of all the rows to loop through to look for matches.
    private List<WebElement> TABLE_ROWS;

    public Table(WebElement table) {
        // Look for and assign the tbody element.
        TABLE_BODY = table.findElement(By.tagName("tbody"));

        // Look for all the table headers.
        TABLE_HEADERS = table.findElements(By.tagName("th"));

        // Look for and assign the tbody element, exclude table headers.
        TABLE_ROWS = TABLE_BODY.findElements(By.tagName("tr"));
    }

    /**
     * Find the first instance in the table headers where
     * the string matches the columnName we are looking for.
     * We have to add one as list is zero indexed, however
     * we would say column 1 not 0, also XPath isn't 0 based.
     * @param columnName The header/title of the column we need the index for.
     * @return the Index of the column that the header is for.
     * @throws Exception No header matching the column name was found.
     */
    public int findColumnIndex(final String columnName) throws Exception {

        WebElement requiredColumn = TABLE_HEADERS.stream()
                .filter(e -> e.getText().equals(columnName))
                .findFirst()
                .get();
        return TABLE_HEADERS.indexOf(requiredColumn) + 1;
    }

    /**
     * Find the first row that contains the specified
     * value in the specified column.
     * @param columnName The column to look for the value in.
     * @param knownValue The value to look for.
     * @return The matching row element.
     */
    public WebElement findRowMatchingColumnData(String columnName, String knownValue) throws Exception {
        int columnIndex = findColumnIndex(columnName);

        return TABLE_ROWS.stream()
                .filter(e -> e.findElement(By.xpath(String.format("td[%d]", columnIndex))).getText().trim().equals(knownValue))
                .findFirst()
                .get();
    }

    /**
     * Find a row that contains the given value in any column.
     * @param knownValue The value to look for.
     * @return The matching row element.
     */
    public WebElement findFirstRowByKnownValue(String knownValue) throws Exception {
        int i = 1;

        while (i <= TABLE_HEADERS.size()) {
            for (WebElement row : TABLE_ROWS) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                for (WebElement cell : cells) {
                    if (cell.getText().equals(knownValue)){
                        return row;
                    }
                }
            }
            i++;
        }

        throw new Exception(String.format("Unable to find a row containing: %s in a column", knownValue));
    }

    /**
     * Check to see if a value is within a specified column.
     * @param columnName The column name to check in.
     * @param knownValue The value to look for.
     * @return True if the value is found.
     */
    public boolean isValuePresentWithinColumn(String columnName, String knownValue) throws Exception {
        for (WebElement row : TABLE_ROWS) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            for (WebElement cell : cells) {
                if (cell.getText().equals(knownValue)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Find a cell by the column name and known value.
     * @param columnName Column to look for the known value in.
     * @param knownValue The known value to look for.
     * @return Matching cell element.
     */
    public WebElement findCellByColumnAndKnownValue(String columnName, String knownValue) throws Exception {
        for (WebElement row : TABLE_ROWS) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            for (WebElement cell : cells) {
                if (cell.getText().equals(knownValue)) {
                    return cell;
                }
            }
        }

        throw new Exception(String.format("Unable to find a cell in column: %s containing %s", columnName, knownValue));
    }

    /**
     * Find a cell by using the row element and the column name.
     * @param row The row element to read column value from.
     * @param columnName The column name to read value from.
     * @return Matching cell element.
     */
    public WebElement findCellByRowAndColumnName(WebElement row, String columnName) throws Exception {
        WebElement cell;

        try {
            cell = row.findElement(By.xpath(String.format("td[%d]", findColumnIndex(columnName))));
        }
        catch (Exception e) {
            throw new Exception("Unable to find a cell using given row and columnName");
        }

        return cell;
    }

    /**
     * Returns a list of all the column headers.
     * @return A list of all the column names as strings.
     */
    public List<String> readAllColumnHeaders() {
        List<String> columnNames = new ArrayList<String>();

        for (WebElement element: TABLE_HEADERS) {
            columnNames.add(element.getText());
        }

        return columnNames;
    }

    /**
     * Return the number of columns from the table.
     * @return Number of columns as an int.
     */
    public int columnCount() {
        return TABLE_HEADERS.size();
    }

    /**
     * Returns the number of rows in the table.
     * Added a plus 1 to also include the header
     * row which is excluded in the TABLE_ROWS
     * declaration.
     * @return Number of rows as an int.
     */
    public int rowCount() {
        return TABLE_ROWS.size()+1;
    }

    /**
     * Find a cell by column name and the row number.
     * @param columnName The name of the column to read from.
     * @param row The number of rows.
     * @return Matching cell element.
     */
    public WebElement findCellByColumnAndRowNumber(String columnName, int row) throws Exception {
        WebElement matchingCell = TABLE_BODY.findElement(By.xpath(String.format("tr[%d]/td[%d]", row, findColumnIndex(columnName))));

        return matchingCell;
    }

    /**
     * Read all the data from a specific column.
     * @param columnName The name of the column to read from.
     * @return A list of all column data as strings.
     */
    public List<String> readAllDataFromAColumn(String columnName) throws Exception {
        // Need to be able to add items to columnData so declare as ArrayList.
        List<String> columnData = new ArrayList<String>();
        int columnIndex = findColumnIndex(columnName);

        for (WebElement row : TABLE_ROWS) {
            columnData.add(row.findElement(By.xpath(String.format("td[%d]", columnIndex))).getText());
        }

        return columnData;
    }

    /**
     * Read the value of a cell for the row that contains a known value.
     * @param columnName The column name to read value from.
     * @param knownValue The value to find a matching row.
     * @return The value of cell as a string.
     */
    public String readColumnValueForRowContaining(String columnName, String knownValue) throws Exception {
        WebElement requiredRow = findFirstRowByKnownValue(knownValue);
        WebElement requiredCell = findCellByRowAndColumnName(requiredRow, columnName);

        return requiredCell.getText();
    }

    /**
     * Read a cells value by finding the value using a known value from a specific column.
     * @param columnToRead Column to read data from.
     * @param knownValue Known value to match.
     * @param knownValueColumn Column which should contain the known value.
     * @return Value of the matching cell as a string.
     */
    public String readAColumnForRowContainingValueInColumn(String columnToRead, String knownValue, String knownValueColumn) throws Exception {
        WebElement requiredRow = findRowMatchingColumnData(knownValueColumn, knownValue);
        WebElement requiredCell = findCellByRowAndColumnName(requiredRow, columnToRead);

        return requiredCell.getText();
    }
}
