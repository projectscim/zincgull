package local;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database implements SecretDBinfo {
	
	/**
	 * Connects to MySQL-database.<br>
	 * @see {@link #host}, {@link #user} and {@link #pass}
	 * @return Connection if successful, otherwise null.
	 */
	public static Connection connect() {
		Connection conn = null;
		
		System.out.println("Connecting to MySQL-database..");
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, user, pass);
			System.out.println("Connection to MySQL-datbase established");
		} catch (InstantiationException e) {
			System.out.println("ERROR: InstantiationException\n" +
					"ERROR: Connection FAILED");
			conn = null;
		} catch (IllegalAccessException e) {
			System.out.println("ERROR: IllegalAccessException\n" +
					"ERROR: Connection FAILED");
			conn = null;
		} catch (ClassNotFoundException e) {
			System.out.println("ERROR: ClassNotFoundException\n" +
					"ERROR: Connection FAILED");
			conn = null;
		} catch (SQLException e) {
			System.out.println("ERROR: SQLException\n" +
					"ERROR: Connection FAILED");
			conn = null;
		}
		
		return conn;
	}
}
