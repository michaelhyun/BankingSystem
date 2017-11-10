import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * Manage connection to database and perform SQL statements.
 */
public class BankingSystem {
	// Connection properties
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	
	// JDBC Objects
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;

	/**
	 * Initialize database connection given properties file.
	 * @param filename name of properties file
	 */
	public static void init(String filename) {
		try {
			Properties props = new Properties();						// Create a new Properties object
			FileInputStream input = new FileInputStream(filename);	// Create a new FileInputStream object using our filename parameter
			props.load(input);										// Load the file contents into the Properties object
			driver = props.getProperty("jdbc.driver");				// Load the driver
			url = props.getProperty("jdbc.url");						// Load the url
			username = props.getProperty("jdbc.username");			// Load the username
			password = props.getProperty("jdbc.password");			// Load the password
		}
	    catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test database connection.
	 */
	public static void testConnection() {
		System.out.println(":: TEST - CONNECTING TO DATABASE");
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			con.close();
			System.out.println(":: TEST - SUCCESSFULLY CONNECTED TO DATABASE\n");
			} catch (Exception e) {
				System.out.println(":: TEST - FAILED CONNECTED TO DATABASE");
				e.printStackTrace();
			}
	  }

	//Validates that the account number is under the user who is currently logged in
	public static boolean validateAction(String accountNumber){
		try{
		System.out.println(":: VALIDATING ACCOUNT NUMBER");

		String query = String.format("Select ID from p1.Account where Number = %s", accountNumber);
		Class.forName(driver);                                                                  
        Connection con = DriverManager.getConnection(url, username, password);                  
        Statement stmt = con.createStatement();                                                 
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()) {                                                                      
          String id = "" + rs.getInt(1);
          if(id.equals(ProgramLauncher.loginId)){
          	System.out.println(":: VALIDATION COMPLETE");
          	return true;
          } 
        } 
        System.out.println(":: VALIDATION ERROR - PLEASE INPUT VALID ACCOUNT NUMBER");
                                          
        rs.close();                                                                             
        stmt.close();                                                                           
        con.close();
        return false;
		} catch (Exception e) {
        return false;
      }
	}
	//authenticate 
	public static boolean login(String id, String pin) {
    try {
        System.out.println(":: LOGGING IN...");

		String query = String.format("Select id from p1.Customer where id = %s and pin = %s", id, pin);
		Class.forName(driver);                                                                  
        Connection con = DriverManager.getConnection(url, username, password);                  
        Statement stmt = con.createStatement();                                                 
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()) {                                                                      
          int idNum = rs.getInt(1);
          if(id.equals("" + idNum)){
          	System.out.println(":: LOG IN COMPLETE");
          	return true;
          } 
        }         
        System.out.println(":: PLEASE INPUT VALID CREDENTIALS");
                                  
        rs.close();                                                                             
        stmt.close();                                                                           
        con.close();
        return false;
		} catch (Exception e) {
        return false;
      }
	}

	/**
	 * Create a new customer.
	 * @param name customer name
	 * @param gender customer gender
	 * @param age customer age
	 * @param pin customer pin
	 */
	public static boolean newCustomer(String name, String gender, String age, String pin) 
	{
		try{
			System.out.println(":: CREATE NEW CUSTOMER - RUNNING");
			Class.forName("com.ibm.db2.jcc.DB2Driver");                             
	    	con = DriverManager.getConnection (url, username, password);                 
	  		con.setAutoCommit(false);
			stmt = con.createStatement(); 
			String query = String.format("Insert into P1.CUSTOMER(Name, Gender, Age, Pin) values ('%s', '%s', %s, %s)", name, gender, age, pin); 
			stmt.executeUpdate(query);
			stmt.close();
			con.commit();
			con.close();
			System.out.println(":: CREATE NEW CUSTOMER - SUCCESS\n");
			return true;
		}
		catch(Exception e){
			System.out.println(":: PLEASE INPUT VALID VALUES");

			return false;
		}
		
	}
	/**
	 * Open a new account.
	 * @param id customer id
	 * @param type type of account
	 * @param amount initial deposit amount
	 */
	public static void openAccount(String id, String type, String amount) 
	{
		try{
			System.out.println(":: OPEN ACCOUNT - RUNNING");
			Class.forName("com.ibm.db2.jcc.DB2Driver");                             
	    	con = DriverManager.getConnection (url, username, password);                 
	  		con.setAutoCommit(false);
			stmt = con.createStatement(); 
			String query = String.format("Insert into P1.Account(ID, Balance, Type, Status) values (%s, %s, '%s', 'A')", id, amount, type);
			stmt.executeUpdate(query);
			stmt.close();
			con.commit();
			con.close();
			System.out.println(":: OPEN ACCOUNT - SUCCESS\n");
		}
		catch(Exception e){
			System.out.println("Error");
		}
	}

	/**
	 * Close an account.
	 * @param accNum account number
	 */
	public static void closeAccount(String accNum) 
	{
		try{
			System.out.println(":: CLOSE ACCOUNT - RUNNING");
			Class.forName("com.ibm.db2.jcc.DB2Driver");                             
	    	con = DriverManager.getConnection (url, username, password);                 
	  		con.setAutoCommit(false);
			stmt = con.createStatement();
			String query = String.format("Update P1.Account set balance = 0, status = 'I' where Number = %s and status = 'A' and ID = %s", accNum, ProgramLauncher.loginId);
			stmt.executeUpdate(query);
			stmt.close();
			con.commit();
			con.close();
			System.out.println(":: CLOSE ACCOUNT - SUCCESS\n");
		}
		catch(Exception e){
        	System.out.println(":: ERROR");

		}
	}

	/**
	 * Deposit into an account.
	 * @param accNum account number
	 * @param amount deposit amount
	 */
	public static void deposit(String accNum, String amount) 
	{
		try{
			System.out.println(":: DEPOSIT - RUNNING");
			Class.forName("com.ibm.db2.jcc.DB2Driver");                             
	    	con = DriverManager.getConnection (url, username, password);                 
	  		con.setAutoCommit(false);
			stmt = con.createStatement(); 
			String query = String.format("Update P1.Account set Balance = Balance + %s where Number = %s and status = 'A'", amount, accNum);
			stmt.executeUpdate(query);
			stmt.close();
			con.commit();
			con.close();
			System.out.println(":: DEPOSIT - SUCCESS\n");
		}
		catch(Exception e){
			System.out.println("Deposit Error");
		}
	}

	/**
	 * Withdraw from an account.
	 * @param accNum account number
	 * @param amount withdraw amount
	 */
	public static void withdraw(String accNum, String amount) 
	{
		try{
			System.out.println(":: WITHDRAW - RUNNING");
			Class.forName("com.ibm.db2.jcc.DB2Driver");                             
	    	con = DriverManager.getConnection (url, username, password);                 
	  		con.setAutoCommit(false);
			stmt = con.createStatement(); 
			String query = String.format("Update P1.Account set Balance = Balance - %s where Number = %s and status = 'A'", amount, accNum);
			stmt.executeUpdate(query);
			stmt.close();
			con.commit();
			con.close();
			System.out.println(":: WITHDRAW - SUCCESS\n");
		}
		catch(Exception e){
			System.out.println("Withdraw Error");
		}
	}

	/**
	 * Transfer amount from source account to destination account. 
	 * @param srcAccNum source account number
	 * @param destAccNum destination account number
	 * @param amount transfer amount
	 */
	public static void transfer(String srcAccNum, String destAccNum, String amount) 
	{		
		try{
			System.out.println(":: TRANSFER - RUNNING");
			Class.forName("com.ibm.db2.jcc.DB2Driver");                             
	    	con = DriverManager.getConnection (url, username, password);                 
	  		con.setAutoCommit(false);
			stmt = con.createStatement(); 
			String query = String.format("Update P1.Account set Balance = Balance - %s where Number = %s and status = 'A'", amount, srcAccNum);
			stmt.addBatch(query);
			String query2 = String.format("Update P1.Account set Balance = Balance + %s where Number = %s and status = 'A'", amount, destAccNum);
			stmt.addBatch(query2);
			stmt.executeBatch();
			stmt.close();
			con.commit();
			con.close();
			System.out.println(":: TRANSFER - SUCCESS\n");
		}
		catch(Exception e){
			System.out.println("Transfer Error");
		}
	}

	/**
	 * Display account summary.
	 * @param accNum account number
	 */
	public static void accountSummary(String accNum)
	{
      try {
      	System.out.println(":: ACCOUNT SUMMARY - RUNNING");
        Class.forName(driver);                                                                  
        Connection con = DriverManager.getConnection(url, username, password);                  
        Statement stmt = con.createStatement();                                                 
		String query = String.format("Select Number, Balance from P1.Account where ID = %s and status = 'A'", accNum);
		int total = 0;
		System.out.println("Number        Balance");
		System.out.println("--------    ---------");
        ResultSet rs = stmt.executeQuery(query);                                                
        while(rs.next()) {                                                                      
          int Number = rs.getInt(1);
          int Balance = rs.getInt(2);
          total += Balance;
          System.out.printf("%10.10s  %10.10s\n", Number, Balance);      
        }
        System.out.println("_____________________");
        System.out.println("\nTOTAL:  " + total);
        rs.close();                                                                             
        stmt.close();                                                                           
        con.close();
        System.out.println(":: ACCOUNT SUMMARY - SUCCESS\n");                                                                     
      } catch (Exception e) {
        System.out.println(":: ERROR");

      }
	}

	/**
	 * Display Report A - Customer Information with Total Balance in Decreasing Order.
	 */
	public static void reportA() 
	{	
      try {
      	System.out.println(":: REPORT A - RUNNING");
        Class.forName(driver);                                                                  
        Connection con = DriverManager.getConnection(url, username, password);                  
        Statement stmt = con.createStatement();                                                 
		String query = "Select ID, Name, Gender, Age, PIN, TOTAL from p1.customer Join (select ID as accountID,sum(balance) as TOTAL from p1.account group by id) on accountID = p1.customer.id order by Total Desc";
		System.out.println("    ID         Name   Gender Age       Pin     Total");
		System.out.println("----------- ---------- ---- ----       ----    -----");	
        ResultSet rs = stmt.executeQuery(query);                                                
        while(rs.next()) {
        	int id = rs.getInt(1);
        	String name = rs.getString(2);
        	String gender = rs.getString(3);
        	int age = rs.getInt(4);
        	int pin = rs.getInt(5);
        	int total = rs.getInt(6);
        	System.out.printf("%10.10s  %10.10s   %s   %s %10.10s %10.10s\n", id, name, gender, age, pin , total); 
        }
        rs.close();                                                                             
        stmt.close();                                                                           
        con.close();
		System.out.println(":: REPORT A - SUCCESS\n");
		} catch (Exception e) {
        	System.out.println(":: ERROR");
      }
	}

	/**
	 * Display Report B - Customer Information with Total Balance in Decreasing Order.
	 * @param min minimum age
	 * @param max maximum age
	 */
	public static void reportB(String min, String max) 
	{
      try {
		System.out.println(":: REPORT B - RUNNING");
		String query = String.format("Select AVG(balance) from p1.account JOIN p1.customer ON p1.customer.id = p1.account.id where p1.customer.age >=%s AND p1.customer.age <= %s and p1.account.status = 'A'", min, max);
		Class.forName(driver);                                                                  
        Connection con = DriverManager.getConnection(url, username, password);                  
        Statement stmt = con.createStatement();                                                 
		int total = 0;
        ResultSet rs = stmt.executeQuery(query);                                                
        while(rs.next()) {
        	int id = rs.getInt(1);
        	System.out.println("AVERAGE");
        	System.out.println("-------");
        	System.out.println("  "+ id);
        }
        rs.close();                                                                             
        stmt.close();                                                                           
        con.close();
		System.out.println(":: REPORT B - SUCCESS\n");
		} catch (Exception e) {
        	System.out.println("::  ERROR");

		}
	}
}
