# table-controlobjects
Tests and code for table control object. Converted to Java from Richard Bradshaws C#which can be found at https://github.com/FriendlyTester/Table-ControlObject

The tests use the table located at http://www.thefriendlytester.co.uk/2012/12/table-controlobject.html

I use a version of this code in my work to test dynamically changing tables on an AngularJS web application.
This code is a type of page object for a UI control (table) that appears on many pages within an application.  This can be called from a page object or a test, depending on how you plan to use it.

If you plan to use this code be sure to set the language level to 8 in IntelliJ by: File -> Project Structure -> Modules -> Language level.  This is to ensure that you don't get a compile error because of the Lambda expressions used in the code.
