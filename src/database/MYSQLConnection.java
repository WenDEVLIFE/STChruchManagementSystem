package database;

public class MYSQLConnection {
    String databaseUrl = "jdbc:mysql://localhost:3306/chruch_managementdb";
    String user ="root";
    String password = "";


    void intializeConnection() {
        try {
            System.out.println("Loading MySQL driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully.");
            java.sql.Connection connection = java.sql.DriverManager.getConnection(databaseUrl, user, password);
            System.out.println("Connection established successfully");
            connection.close();
        } catch (Exception e) {
            System.out.println("Error establishing connection: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }
    public  static  void main(String[] args) {
        MYSQLConnection connection = new MYSQLConnection();
        connection.intializeConnection();
    }
}
