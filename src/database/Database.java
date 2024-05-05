package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
	
	private final String URL = "jdbc:mysql://localhost:3306/ptPudding";
	private final String USERNAME = "root";
	private final String PASSWORD = "";
	
	private static Database instance;
	private Connection con;
	
	private Database() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Database getInstance() {
		if (instance == null) {
			instance = new Database();
		}
		return instance;
	}
	
	public PreparedStatement prepareSatement(String query) throws SQLException {
		return con.prepareStatement(query);
	}
	
}