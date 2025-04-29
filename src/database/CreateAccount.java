package database;

import javax.swing.*;
import java.util.Map;

public class CreateAccount {

    private static volatile  CreateAccount instance;

    public static CreateAccount getInstance() {
        if (instance == null) {
            synchronized (CreateAccount.class) {
                if (instance == null) {
                    instance = new CreateAccount();
                }
            }
        }
        return instance;
    }

    public boolean checkUsernameExist(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
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

        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
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
}
