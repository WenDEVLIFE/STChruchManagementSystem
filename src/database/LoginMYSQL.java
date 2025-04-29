package database;

import UI.AdminMainMenu;
import UI.Login;
import UI.UserMainMenu;

public class LoginMYSQL {
    private static volatile  LoginMYSQL instance;

    public static LoginMYSQL getInstance() {
        if (instance == null) {
            synchronized (LoginMYSQL.class) {
                if (instance == null) {
                    instance = new LoginMYSQL();
                }
            }
        }
        return instance;
    }

    public void LoginUser(String username, String password, Login login) {
        String loginSQL = "SELECT user_id, role FROM users WHERE username = ? AND password = ?";
        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             java.sql.PreparedStatement preparedStatement = connection.prepareStatement(loginSQL)) {

            // Set the parameters for the query
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Execute the query
            try (java.sql.ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int userId = resultSet.getInt("user_id"); // Retrieve the userId
                    String role = resultSet.getString("role"); // Retrieve the role
                    System.out.println("Login successful! User ID: " + userId);

                    if ("Admin".equals(role)) {
                        // Open the admin main menu
                        AdminMainMenu adminMenu = new AdminMainMenu();
                        adminMenu.setVisible(true);
                        login.dispose();
                    } else if ("User".equals(role)) {
                        // Open the user main menu
                        UserMainMenu userMenu = new UserMainMenu(userId);
                        userMenu.setVisible(true);
                        login.dispose();
                    }
                } else {
                    System.out.println("Invalid username or password.");
                    javax.swing.JOptionPane.showMessageDialog(login, "Invalid username or password.", "Login Failed", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred during login: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(login, "An error occurred during login: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
}
