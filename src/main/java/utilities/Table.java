package utilities;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by heather.reid on 17/05/16.
 * For Xpath searching in dynamically
 * changing tables.
 * Ref: Richard Bradshaw aka Friendly
 * Tester https://github.com/FriendlyTester/Table-ControlObject
 */
public class Table
{
    // Protected reference to the table body.
    protected WebElement tableBody;
    // List of all the tableHeaders used to get the column indices.
    protected List<WebElement> tableHeaders;
    // List of all the rows to loop through to look for matches.
    protected List<WebElement> tableRows;

    public Table(WebElement table) throws Exception
    {
        // TODO do I need to have a logger maybe to catch the errors?
        try
        {
            // Look for and assign the tbody element.
            tableBody = table.findElement(By.tagName("tbody"));
        }
        catch (Exception e)
        {
            throw new Exception(String.format("Couldn't find a tbody tag for this table. %s", e.getMessage()));
        }

        try
        {
            // Look for all the table headers.
            // TODO this had a ToList() at the end of it in C# code. Is that necessary here?
            tableHeaders = table.findElements(By.tagName("th"));
        }
        catch (Exception e)
        {
            throw new Exception(String.format("Couldn't find any th tags within this table. %s", e.getMessage()));
        }

        try
        {
            // Look for and assign the tbody element.
            // TODO this had a ToList() at the end of it in C# code. Is that necessary here?
            tableRows = table.findElements(By.tagName("tr"));
        }
        catch (Exception e)
        {
            throw new Exception(String.format("This table doesn't contain any rows. %s", e.getMessage()));
        }
    }

    protected int FindColumnIndex(final String columnName) throws Exception {
        for (WebElement header : tableHeaders)
        {
            if (header.getText().equals(columnName))
            {
                return tableHeaders.indexOf(header) + 1;
            }
        }

        throw new Exception(String.format("Unable to find %s in the list of columns found", columnName));
    }

    /**
     * Find the first row that contains the specified
     * value in the specified column.
     * @param columnName The column to look for the value in.
     * @param knownValue The value to look for.
     * @return The matching row element.
     */
    public WebElement FindRowMatchingColumnData(String columnName, String knownValue) throws Exception
    {
        WebElement requiredRow = null;
        int columnIndex = FindColumnIndex(columnName);

        try
        {
            /**
             * Using the column index found by searching with the
             * column header, find the row that contains the
             * knownValue we are looking for.
             */
            while (tableRows.iterator().hasNext())
            {
                // TODO need to come back to this, not sure if this is correct.
                if (tableRows.iterator().next().findElement(By.xpath(String.format("td[%d]", columnIndex))).getText().equals(knownValue))
                {
                    requiredRow = tableRows.iterator().next();
                    // Print it just to check that I have this done right.
                    System.out.println(requiredRow);
                }
            }
            //requiredRow = tableRows
        }
        catch (Exception e)
        {
            if (e instanceof NoSuchElementException)
            {
                throw new Exception(String.format("Column index %d doesn't exist.", columnIndex));
            }
            if (e instanceof InvalidArgumentException)
            {
                //TODO is InvalidArgumentException ok here?
                throw new Exception(String.format("Row containing %s in column index %d was not found", knownValue, columnIndex));
            }
            else
            {
                // Rethrow exception.
                throw e;
            }
        }

        if (requiredRow != null)
        {
            return requiredRow;
        }

        throw new Exception("Required row is null, unknown error occurred");
    }

    /**
     * Find a row that contains the given value in any column.
     * @param knownValue The value to look for.
     * @return The matching row element.
     */
    public WebElement FindFirstRowByKnownValue(String knownValue) throws Exception
    {
        int i = 1;

        // TODO not sure if size is correct here.
        while (i <= tableHeaders.size())
        {
            for (WebElement row: tableRows)
            {
                if (row.findElement(By.xpath(String.format("td[%d]", i))).getText() == knownValue)
                {
                    return row;
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
    public boolean IsValuePresentWithinColumn(String columnName, String knownValue) throws Exception
    {
        try
        {
            /**
             * Using the column index found by searching with the
             * column header, see if the knownValue we are
             * looking for is in the column.
             */
            while (tableRows.iterator().hasNext())
            {
                // TODO need to come back to this, not sure if this is correct.
                if (tableRows.iterator().next().findElement(By.xpath(String.format("td[%d]", FindColumnIndex(columnName)))).getText().equals(knownValue))
                {
                    WebElement matchedColumn = tableRows.iterator().next();
                    // Print it just to check that I have this done right.
                    System.out.println(matchedColumn);
                }
            }
        }
        catch (Exception e) // TODO is the format of this catch statement correct?
        {
            return false;
        }

        return true;
    }

    /**
     * Find a cell by the column name and known value.
     * @param columnName Column to look for the known value in.
     * @param knownValue The known value to look for.
     * @return Matching cell element.
     */
    public WebElement FindCellByColumnAndKnownValue(String columnName, String knownValue) throws Exception
    {
        WebElement matchedRow = null;

        try
        {
            /**
             * Using the column index found by searching with the
             * column header, see if the knownValue we are
             * looking for is in the row.
             */
            while (tableRows.iterator().hasNext())
            {
                // TODO need to come back to this, not sure if this is correct.
                if (tableRows.iterator().next().findElement(By.xpath(String.format("td[%d]", FindColumnIndex(columnName)))).getText().equals(knownValue))
                {
                    matchedRow = tableRows.iterator().next();
                    // Print it just to check that I have this done right.
                    System.out.println(matchedRow);
                }
            }
        }
        catch (Exception e) // TODO is the format of this catch statement correct?
        {
            throw new Exception(String.format("Unable to find a cell in column: %s containing %s", columnName, knownValue));
        }

        return matchedRow.findElement(By.xpath(String.format("td[%d]", FindColumnIndex(columnName))));
    }

    /**
     * Find a cell by using the row element and the column name.
     * @param row The row element to read column value from.
     * @param columnName The column name to read value from.
     * @return Matching cell element.
     */
    public WebElement FindCellByRowAndColumnName(WebElement row, String columnName) throws Exception
    {
        WebElement cell;

        try
        {
            cell = row.findElement(By.xpath(String.format("td[%d]", FindColumnIndex(columnName))));
        }
        catch (Exception e) // TODO is the format of this catch statement correct?
        {
            throw new Exception("Unable to find a cell using given row and columnName");
        }

        return cell;
    }

    /**
     * Returns a list of all the column headers.
     * @return A list of all the column names as strings.
     */
    public List<String> readAllColumnHeaders()
    {
        List<String> columnNames = new ArrayList<String>();

        for (WebElement element: tableHeaders)
        {
            columnNames.add(element.getText()); //TODO is this getText correct?
        }

        return columnNames;
    }

    /**
     * Return the number of columns from the table.
     * @return Number of columns as an int.
     */
    public int ColumnCount()
    {
        // TODO not sure if .size is the best way to do this.
        return tableHeaders.size();
    }

    /**
     * Returns the number of rows in the table.
     * @return Number of rows as an int.
     */
    public int RowCount()
    {
        //TODO not sure about this .size either.
        return tableRows.size();
    }

    /**
     * Find a cell by column name and the row number.
     * @param columnName The name of the column to read from.
     * @param row The number of rows.
     * @return Matching cell element.
     */
    public WebElement FindCellByColumnAndRowNumber(String columnName, int row) throws Exception
    {
        WebElement matchingCell = tableBody.findElement(By.xpath(String.format("tr[%d]/td[%d]", row, FindColumnIndex(columnName))));

        return matchingCell;
    }

    /**
     * Read all the data from a specific column.
     * @param columnName The name of the column to read from.
     * @return A list of all column data as strings.
     */
    public List<String> ReadAllDataFromAColumn(String columnName) throws Exception
    {
        // Need to be able to add items to columnData so declare as ArrayList.
        List<String> columnData = new ArrayList<String>();
        int columnIndex = FindColumnIndex(columnName);

        for (WebElement row: tableRows)
        {
            //TODO is getText correct here?
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
    public String ReadColumnValueForRowContaining(String columnName, String knownValue) throws Exception
    {
        WebElement requiredRow = FindFirstRowByKnownValue(knownValue);
        WebElement requiredCell = FindCellByRowAndColumnName(requiredRow, columnName);

        //TODO is getText appropriate here?
        return requiredCell.getText();
    }

    /**
     * Read a cells value by finding the value using a known value from a specific column.
     * @param columnToRead Column to read data from.
     * @param knownValue Known value to match.
     * @param knownValueColumn Column which should contain the known value.
     * @return Value of the matching cell as a string.
     */
    public String ReadAColumnForRowContainingValueInColumn(String columnToRead, String knownValue, String knownValueColumn) throws Exception
    {
        WebElement requiredRow = FindRowMatchingColumnData(knownValueColumn, knownValue);
        WebElement requiredCell = FindCellByRowAndColumnName(requiredRow, columnToRead);

        //TODO is getText appropriate here?
        return requiredCell.getText();
    }
}
