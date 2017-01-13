// JDBC Example - printing a database's metadata
// Coded by Chen Li/Kirill Petrov Winter, 2005
// Slightly revised for ICS185 Spring 2005, by Norman Jacobson
"Hello World";

import java.sql.*;                              // Enable SQL processing

public class JDBC1
{

       public static void main(String[] arg) throws Exception
       {
    	   MovieDBConsole console = new MovieDBConsole();

           console.connect();
           console.printMoviesWithStars();
           console.insertNewStar();
           console.insertNewCustomer();
       }
}
