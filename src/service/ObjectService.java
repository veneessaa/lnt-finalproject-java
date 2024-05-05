package service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Object;

public class ObjectService {
	
	private static Database db = Database.getInstance();
	
	public static void save(Object object) {
		String query = "INSERT INTO object VALUES (?, ?, ?, ?)";
		
		try {
			PreparedStatement ps = db.prepareSatement(query);
			ps.setString(1, object.getCode());
			ps.setString(2, object.getName());
			ps.setInt(3, object.getPrice());
			ps.setInt(4, object.getStock());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void update(Object object) {
		String query = "UPDATE object SET price = ?, stock = ? WHERE code = ? AND name = ?";
		
		try {
			PreparedStatement ps = db.prepareSatement(query);
			ps.setInt(1, object.getPrice());
			ps.setInt(2, object.getStock());
			ps.setString(3, object.getCode());
			ps.setString(4, object.getName());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void delete(Object object) {
		String query = "DELETE FROM object WHERE code = ?";
		
		try {
			PreparedStatement ps = db.prepareSatement(query);
			ps.setString(1, object.getCode());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static ObservableList<Object> getAllItems(){
		String query = "SELECT * FROM object";
		ObservableList<Object> itemList = FXCollections.observableArrayList();
		
		try {
			PreparedStatement ps = db.prepareSatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				itemList.add(new Object(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return itemList;
	}
	
}