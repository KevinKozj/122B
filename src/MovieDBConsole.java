import java.sql.*;
import java.util.Scanner;

public class MovieDBConsole {
	
	private Connection connection;
	private Scanner s;

	public MovieDBConsole()
	{
		connection = null;
		s = new Scanner(System.in);
	}
	
	public void connect()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch(Exception e)
		{
			System.out.println("Failed to Instantiate a New JDBC Driver. Please Try Again Later.\n");
			return;
		}
		
		while(connection == null)
		{
			
			try
			{
				connection = DriverManager.getConnection("jdbc:mysql:///moviedb", "kevinke", "0000");
			}
			catch(SQLException e)
			{
				System.out.println("Invalid Username and/or Password. Try Again.\n");
			}
		}
		System.out.println("\n***** Connect Successfully *****\n");
	}
	
	public void printMoviesWithStars()
	{
		Statement statement = null;
		System.out.println(">>>>>>>>>> Searching Movies by Stars <<<<<<<<<< \n");
		try {	
			statement = connection.createStatement();
			ResultSet result;
			
			System.out.print("Enter Star's Name or ID (Name Could Be First Name and/or Last Name): ");
			if(s.hasNextInt())
			{
				System.out.println(1);
				int id = s.nextInt();
				result = statement.executeQuery(
						"select movies.id, title, year, director, banner_url, trailer_url "
						+ "from movies, stars_in_movies "
						+ "where movies.id = stars_in_movies.movie_id and stars_in_movies.star_id = " + id);
			}
			else
			{
				String[] names = s.nextLine().split(" ");
				if(names.length == 1)
				{
					String name = names[0];
					result = statement.executeQuery(
							"select movies.id, title, year, director, banner_url, trailer_url "
							+ "from movies, stars_in_movies, stars "
							+ "where movies.id = stars_in_movies.movie_id and stars.id = stars_in_movies.star_id "
							+ "and (stars.first_name = '" + name + "' or stars.last_name = '" + name + "')");
					
				}
				else if(names.length == 2)
				{
					String first_name = names[0];
					String last_name = names[1];
					
					result = statement.executeQuery(
							"select movies.id, title, year, director, banner_url, trailer_url "
							+ "from movies, stars_in_movies, stars "
							+ "where movies.id = stars_in_movies.movie_id and stars.id = stars_in_movies.star_id "
							+ "and stars.first_name = '" + first_name + "'and stars.last_name = '" + last_name + "'");
					
				}
				else
				{
					System.out.println("Please Enter a Valid Name or ID\n");
					return;
				}
			}
			
			if(!result.isBeforeFirst())
			{
				System.out.println("Sorry. Record Not Found.\n");
			}
			else
			{
				printMoviesResultSet(result);
			}
			
		} catch (SQLException e) {
			System.out.println("Database Access Denied.\n");
			System.out.println(e.toString());
		}
		finally {
			if(statement != null)
			{
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
		}
	     
	}
	
	private void printMoviesResultSet(ResultSet result) throws SQLException
	{
		while(result.next())
		{
			int id = result.getInt("id");
			int year = result.getInt("year");
			String title = result.getString("title");
			String director = result.getString("director");
			String banner_url = result.getString("banner_url");
			String trailer_url = result.getString("trailer_url");
			
			System.out.format("|ID: %-8d | Title: %-45s | Year: %d | Director: %-25s | \n"
					+ "|Banner URL: %-106s | \n"
					+ "|Trailer URL: %-105s | \n", id, title, year, director, banner_url, trailer_url);
			System.out.println();
		}
	}
	
	public void insertNewStar()
	{
		System.out.println(">>>>>>>>>> Inserting A New Star <<<<<<<<<< \n");
		String firstName = "";
		String lastName = "";
		String dob;
		String photoURL;
		
		while(firstName.isEmpty() && lastName.isEmpty())
		{
			System.out.println("Please Enter Star's First Name and/or Last Name");
			firstName = promptForString("First Name: ");
			lastName = promptForString("Last Name: ");
			if(!firstName.isEmpty() && lastName.isEmpty())
			{
				lastName = firstName;
				firstName = "";
			}
		}
		
		dob = promptForString("Please Enter Star's Date of Birth (YYYY-MM-DD) (Optional): ");
		photoURL = promptForString("Please Enter Star's Photo URL (Optional): ");
		
		String insertStarSQL = "insert into stars "
							   + "(first_name, last_name, dob, photo_url) "
							   + "values ( ?, ?, ?, ?)";
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(insertStarSQL);
			
			statement.setString(1, firstName);
			statement.setString(2, lastName);
			statement.setDate(3, dob.isEmpty() ? null: Date.valueOf(dob));
			statement.setString(4, photoURL);
			
			if(statement.executeUpdate() == 1)
			{
				System.out.println("\n***** A New Star Inserted Successfully *****\n");
			}
			else
			{
				System.out.println("Insertion Failed. \n");
			}
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("Insertion Failed. \n");
		}
		finally
		{
			if(statement != null)
			{
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
		}
		
	}
	
	public void insertNewCustomer()
	{
		System.out.println(">>>>>>>>>> Inserting A New Customer <<<<<<<<<< \n");
		String firstName = "";
		String lastName= "";
		String creditCardID = "";
		String address = "";
		String email = "";
		String password = "";
		
		while(firstName.isEmpty() && lastName.isEmpty())
		{
			System.out.println("Please Enter Customer's First Name and/or Last Name");
			firstName = promptForString("First Name: ");
			lastName = promptForString("Last Name: ");
			if(!firstName.isEmpty() && lastName.isEmpty())
			{
				lastName = firstName;
				firstName = "";
			}
		}
		
		while(creditCardID.isEmpty())
		{
			creditCardID = promptForString("Please Enter Customer's Credit Card ID (Required): ");
		}
		
		while(address.isEmpty())
		{
			address = promptForString("Please Enter Customer's Address (Required): " );
		}
		
		while(email.isEmpty())
		{
			email = promptForString("Please Enter Customer's Email (Required): " );
		}
		
		while(password.isEmpty())
		{
			password = promptForString("Please Enter Customer's password (Required): " );
		}
		
		String insertCustomerSQL = "insert into customers "
				   + "(first_name, last_name, cc_id, address, email, password) "
				   + "values (?, ?, ?, ?, ?, ?)";
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(insertCustomerSQL);
			
			statement.setString(1, firstName);
			statement.setString(2, lastName);
			statement.setString(3, creditCardID);
			statement.setString(4, address);
			statement.setString(5, email);
			statement.setString(6, password);
			
			if(statement.executeUpdate() == 1)
			{
				System.out.println("\n***** A New Customer Inserted Successfully *****\n");
			}
			else
			{
				System.out.println("Insertion Failed. \n");
			}
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("Insertion Failed. \n");
		}
		finally
		{
			if(statement != null)
			{
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	private String promptForString(String prompt)
	{
		System.out.print(prompt);
		return s.nextLine();
	}

}