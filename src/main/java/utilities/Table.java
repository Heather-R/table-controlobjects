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
    private WebElement tableBody;
    // List of all the tableHeaders used to get the column indices.
    private List<WebElement> tableHeaders;
    // List of all the rows to loop through to look for matches.
    private List<WebElement> tableRows;

    public Table(WebElement table) {
        // Look for and assign the tbody element.
        tableBody = table.findElement(By.tagName("tbody"));

        // Look for all the table headers.
        tableHeaders = table.findElements(By.tagName("th"));

        // Look for and assign the tbody element, exclude table headers.
        tableRows = tableBody.findElements(By.tagName("tr"));
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
    public int findColumnIndex(final String columnName) {

        WebElement requiredColumn = tableHeaders.stream()
                .filter(e -> e.getText().equals(columnName))
                .findFirst()
                .get();
        return tableHeaders.indexOf(requiredColumn) + 1;
    }

    /**
     * Find the first row that contains the specified
     * value in the specified column.
     * @param columnName The column to look for the value in.
     * @param knownValue The value to look for.
     * @return The matching row element.
     */
    public WebElement findRowMatchingColumnData(String columnName, String knownValue) {
        int columnIndex = findColumnIndex(columnName);

        return tableRows.stream()
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

        for (int i = 1; i <= tableHeaders.size(); i++) {
            for (WebElement row : tableRows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                for (WebElement cell : cells) {
                    if (cell.getText().equals(knownValue)){
                        return row;
                    }
                }
            }
        }

        throw new Exception(String.format("Unable to find a row containing: %s in a column", knownValue));
    }

    /**
     * Check to see if a value is within a specified column.
     * @param columnName The column name to check in.
     * @param knownValue The value to look for.
     * @return True if the value is found.
     */
    public boolean isValuePresentWithinColumn(String columnName, String knownValue) {

        int columnIndex = findColumnIndex(columnName);
        return tableRows.stream()
                .filter(e -> e.findElement(By.xpath(String.format("td[%d]", columnIndex))).getText().trim().equals(knownValue))
                .findFirst()
                .isPresent();
    }

    /**
     * Find a cell by the column name and known value.
     * @param columnName Column to look for the known value in.
     * @param knownValue The known value to look for.
     * @return Matching cell element.
     */
    public WebElement findCellByColumnAndKnownValue(String columnName, String knownValue) {

        int columnIndex = findColumnIndex(columnName);
        WebElement matchedRow = tableRows.stream()
                .filter(e -> e.findElement(By.xpath(String.format("td[%d]", columnIndex))).getText().trim().equals(knownValue))
                .findFirst()
                .get();

        return matchedRow.findElement(By.xpath(String.format("td[%d]", columnIndex)));
    }

    /**
     * Find a cell by using the row element and the column name.
     * @param row The row element to read column value from.
     * @param columnName The column name to read value from.
     * @return Matching cell element.
     */
    public WebElement findCellByRowAndColumnName(WebElement row, String columnName) {

        return row.findElement(By.xpath(String.format("td[%d]", findColumnIndex(columnName))));
    }

    /**
     * Returns a list of all the column headers.
     * @return A list of all the column names as strings.
     */
    public List<String> readAllColumnHeaders() {
        List<String> columnNames = new ArrayList<String>();

        for (WebElement element: tableHeaders) {
            columnNames.add(element.getText());
        }

        return columnNames;
    }

    /**
     * Return the number of columns from the table.
     * @return Number of columns as an int.
     */
    public int columnCount() {
        return tableHeaders.size();
    }

    /**
     * Returns the number of rows in the table.
     * Added a plus 1 to also include the header
     * row which is excluded in the tableRows
     * declaration.
     * @return Number of rows as an int.
     */
    public int rowCount() {
        return tableRows.size()+1;
    }

    /**
     * Find a cell by column name and the row number.
     * @param columnName The name of the column to read from.
     * @param row The number of rows.
     * @return Matching cell element.
     */
    public WebElement findCellByColumnAndRowNumber(String columnName, int row) {

        return tableBody.findElement(By.xpath(String.format("tr[%d]/td[%d]", row, findColumnIndex(columnName))));
    }

    /**
     * Read all the data from a specific column.
     * @param columnName The name of the column to read from.
     * @return A list of all column data as strings.
     */
    public List<String> readAllDataFromAColumn(String columnName) {
        // Need to be able to add items to columnData so declare as ArrayList.
        List<String> columnData = new ArrayList<String>();
        int columnIndex = findColumnIndex(columnName);

        for (WebElement row : tableRows) {
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
    public String readAColumnForRowContainingValueInColumn(String columnToRead, String knownValue, String knownValueColumn) {
        WebElement requiredRow = findRowMatchingColumnData(knownValueColumn, knownValue);
        WebElement requiredCell = findCellByRowAndColumnName(requiredRow, columnToRead);

        return requiredCell.getText();
    }

    /**
     * Finds a button that has HTML tag a which is the tag given to buttons
     * with a navigation link attached to them. If a cell only has one link
     * button then there will be no number after the a tag so this function
     * is only valid for that case.
     * @param columnName Column to read the data from
     * @param knownValue Known value from another column in the row
     * @param knownValueColumn The column header of the known value in the row
     * @return The value of the button as a WebElement.
     */
    public WebElement findOnlyLinkButtonInCell(String columnName, String knownValue, String knownValueColumn) {
        WebElement requiredRow = findRowMatchingColumnData(knownValueColumn, knownValue);
        WebElement requiredCell = findCellByRowAndColumnName(requiredRow, columnName);

        return requiredCell.findElement(By.tagName("a"));
    }

    /**
     * Finds a button that has HTML tag button. If a cell has more
     * than one button then there will be a number after the a tag so
     * this function is only valid for that case.
     * @param columnName Column to read the data from
     * @param knownValue Known value from another column in the row
     * @param knownValueColumn The column header of the known value in the row
     * @param buttonIndex The index of the button to be found
     * @return The value of the button as a WebElement.
     */
    public WebElement findIndexedButtonInCell(String columnName, String knownValue, String knownValueColumn, int buttonIndex) {
        WebElement requiredRow = findRowMatchingColumnData(knownValueColumn, knownValue);
        WebElement requiredCell = findCellByRowAndColumnName(requiredRow, columnName);

        return requiredCell.findElement(By.xpath(String.format("div/button[%d]", buttonIndex)));
    }

    /**
     * Finds a button that has HTML tag button. If a cell only has
     * one button then there will be no number after the a tag so
     * this function is only valid for that case.
     * @param columnName Column to read the data from
     * @param knownValue Known value from another column in the row
     * @param knownValueColumn The column header of the known value in the row
     * @return The value of the button as a WebElement.
     */
    public WebElement findOnlyButtonInCell(String columnName, String knownValue, String knownValueColumn) {
        WebElement requiredRow = findRowMatchingColumnData(knownValueColumn, knownValue);
        WebElement requiredCell = findCellByRowAndColumnName(requiredRow, columnName);

        return requiredCell.findElement(By.xpath("div/button"));
    }
}
