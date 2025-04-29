package database;

import model.UserModel;

import javax.swing.*;
import java.util.List;
import java.util.Map;

import static database.MYSQLConnection.databaseUrl;
import static database.MYSQLConnection.user;

public class UserMYSQLConnection {

    private static volatile  UserMYSQLConnection instance;

    public static UserMYSQLConnection getInstance() {
        if (instance == null) {
            synchronized (UserMYSQLConnection.class) {
                if (instance == null) {
                    instance = new UserMYSQLConnection();
                }
            }
        }
        return instance;
    }

    // This will get the user
    public List<UserModel> getUser(){

        String sql = "SELECT * FROM users";
        List<UserModel> userList = new java.util.ArrayList<>();

        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(databaseUrl, user, MYSQLConnection.password);
             java.sql.PreparedStatement preparedStatement = connection.prepareStatement(sql);
             java.sql.ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");

                UserModel user = new UserModel(id, username, password, role);
                userList.add(user);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            System.out.println("Error retrieving data: " + e.getMessage());
        }
        return userList;

    }

    // This will check if the username already exists
    public boolean checkUsernameExist(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(databaseUrl, user, MYSQLConnection.password);
             java.sql.PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username); // Set the username parameter
            try (java.sql.ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1); // Get the count result
                    return count > 0; // Return true if count > 0
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            System.out.println("Error checking username: " + e.getMessage());
        }
        return false; // Return false if an exception occurs
    }

    // This will create the account
    public void createAcccount(Map<String, Object> userdata) {
        String username = (String) userdata.get("username");
        String password = (String) userdata.get("password");
        String phone = (String) userdata.get("contactnumber");
        String address = (String) userdata.get("address");
        String role = (String) userdata.get("role");
        String firstName = (String) userdata.get("firstname");
        String lastName = (String) userdata.get("lastname");
        String middleName = (String) userdata.get("middlename");

        String sql = "INSERT INTO users (username, password, contact_number, address, role, first_name, last_name, middle_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(databaseUrl, user, MYSQLConnection.password);
             java.sql.PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Set the values for the placeholders
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, role);
            preparedStatement.setString(6, firstName);
            preparedStatement.setString(7, lastName);
            preparedStatement.setString(8, middleName);

            // Execute the query
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new user was inserted successfully!");
                JOptionPane.showMessageDialog(null, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            System.out.println("Error inserting data: " + e.getMessage());
        }
    }

    // This will edit the user
    public boolean updateAccount(int userId, String username, String password, String role) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ? WHERE user_id = ?";
        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(databaseUrl, user, MYSQLConnection.password);
             java.sql.PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, role);
            preparedStatement.setInt(4, userId);

            return preparedStatement.executeUpdate() > 0; // Return true if update was successful
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // This will delete the account
    public void deleteAccount(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(databaseUrl, user, MYSQLConnection.password);
             java.sql.PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("User deleted successfully!");
                JOptionPane.showMessageDialog(null, "Account deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            System.out.println("Error deleting account: " + e.getMessage());
        }
    }
}
