package database;

import model.ReservationModel;

import javax.swing.*;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


public class BookMYSQL {
    private static volatile  BookMYSQL instance;

    public static BookMYSQL getInstance() {
        if (instance == null) {
            synchronized (BookMYSQL.class) {
                if (instance == null) {
                    instance = new BookMYSQL();
                }
            }
        }
        return instance;
    }

    // get the reservation by user id
    public static List<ReservationModel> getAllReservations(int userId) {
        String query = "SELECT * FROM reservationtable WHERE user_id = ?";
        List<ReservationModel> reservations = new java.util.ArrayList<>();

        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             java.sql.PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            java.sql.ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String reservationID = resultSet.getString("reservation_id");
                String event = resultSet.getString("event");
                String date = resultSet.getString("date");
                String time = resultSet.getString("time");
                String status = resultSet.getString("status");
                String reason = resultSet.getString("reason");
                reservations.add(new ReservationModel(reservationID, event, date, time, status, reason));
            }
            return reservations;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // get all reservations
    public static List<ReservationModel> getAllReservations1() {
        String query = "SELECT * FROM reservationtable";
        List<ReservationModel> reservations = new java.util.ArrayList<>();

        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             java.sql.PreparedStatement statement = connection.prepareStatement(query);
             java.sql.ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String reservationID = resultSet.getString("reservation_id");
                String event = resultSet.getString("event");
                String date = resultSet.getString("date");
                String time = resultSet.getString("time");
                String status = resultSet.getString("status");
                String reason = resultSet.getString("reason");
                reservations.add(new ReservationModel(reservationID, event, date, time, status, reason));
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error fetching reservations: " + e.getMessage());
        }

        return reservations;
    }

    public static String getRejectReason(String reservationId) {
        String query = "SELECT reason FROM reservationtable WHERE reservation_id = ?";

        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             java.sql.PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, reservationId);
            java.sql.ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("reason");
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // This will delete the reservation
    public static void deleteReservation(String reservationID, String event) {
        String query1 = "DELETE FROM reservationtable WHERE reservation_id = ?";
        String query2 = null;

        if ("Christening".equals(event)) {
            query2 = "DELETE FROM christening_table WHERE reservation_id = ?";
        } else if ("Funeral".equals(event)) {
            query2 = "DELETE FROM funeral_table WHERE reservation_id = ?";
        } else if ("Wedding".equals(event)) {
            query2 = "DELETE FROM wedding_table WHERE reservation_id = ?";
        }

        if (query2 == null) {
            throw new IllegalArgumentException("Invalid event type: " + event);
        }

        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             java.sql.PreparedStatement statement1 = connection.prepareStatement(query1);
             java.sql.PreparedStatement statement2 = connection.prepareStatement(query2)) {

            // Execute the first query
            statement1.setString(1, reservationID);
            statement1.executeUpdate();

            // Execute the second query
            statement2.setString(1, reservationID);
            statement2.executeUpdate();

            System.out.println("Reservation with ID " + reservationID + " deleted successfully.");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while deleting the reservation: " + e.getMessage());
        }
    }

    public void insertChristening(Map<String, Object> christening, JDialog dialog) {
        String generateIdSQL = "SELECT MAX(reservation_id) AS reservation_id FROM christening_table";
        String insertSQL = "INSERT INTO christening_table (reservation_id, child_name, parent_name, contact_number, date, time_slot, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertReservationSQL = "INSERT INTO reservationtable (reservation_id, event, date, time, status, reason) VALUES (?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             java.sql.Statement statement = connection.createStatement();
             java.sql.PreparedStatement christeningStatement = connection.prepareStatement(insertSQL);
             java.sql.PreparedStatement reservationStatement = connection.prepareStatement(insertReservationSQL)) {

            // Validate the date
            String dateString = (String) christening.get("date");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateString, formatter);

            if (date.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(null, "The date cannot be in the past.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                JOptionPane.showMessageDialog(null, "Reservations cannot be made on Sundays.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Generate the custom ID
            String newId = "CHR000001"; // Default ID if no records exist
            try (java.sql.ResultSet resultSet = statement.executeQuery(generateIdSQL)) {
                if (resultSet.next() && resultSet.getString("reservation_id") != null) {
                    String maxId = resultSet.getString("reservation_id");
                    int numericPart = Integer.parseInt(maxId.substring(3)); // Extract numeric part
                    newId = String.format("CHR%06d", numericPart + 1); // Increment and format
                }
            }

            // Insert into christening_table
            christeningStatement.setString(1, newId);
            christeningStatement.setString(2, (String) christening.get("childName"));
            christeningStatement.setString(3, (String) christening.get("parentName"));
            christeningStatement.setString(4, (String) christening.get("contactNumber"));
            christeningStatement.setString(5, dateString);
            christeningStatement.setString(6, (String) christening.get("timeSlot"));
            christeningStatement.setInt(7, (int) christening.get("user_id"));


            int rowsInserted = christeningStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Christening record inserted successfully with ID: " + newId);

                // Insert into reservationtable
                reservationStatement.setString(1, newId);
                reservationStatement.setString(2, "Christening");
                reservationStatement.setString(3, dateString);
                reservationStatement.setString(4, (String) christening.get("timeSlot"));
                reservationStatement.setString(5, "Pending");
                reservationStatement.setString(6, "n/a");

                int reservationRowsInserted = reservationStatement.executeUpdate();
                if (reservationRowsInserted > 0) {
                    System.out.println("Reservation record inserted successfully with ID: " + newId);

                    JOptionPane.showMessageDialog(null, "YOUR RESERVATION ID NUMBER IS " + newId,
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while inserting christening data: " + e.getMessage());
        }
    }

    // insert funeral
    public  void insertFuneral(Map<String, Object> funeral, JDialog dialog) {
        String generateIdSQL = "SELECT MAX(reservation_id) AS reservation_id FROM christening_table";
        String insertSQL = "INSERT INTO funeral_table (reservation_id, desceased_name, family_rep_name, contact_number, date, time_slot, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertReservationSQL = "INSERT INTO reservationtable (reservation_id, event, date, time, status, reason) VALUES (?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             java.sql.Statement statement = connection.createStatement();
             java.sql.PreparedStatement christeningStatement = connection.prepareStatement(insertSQL);
             java.sql.PreparedStatement reservationStatement = connection.prepareStatement(insertReservationSQL)) {

            // Validate the date
            String dateString = (String) funeral.get("date");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateString, formatter);

            if (date.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(null, "The date cannot be in the past.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                JOptionPane.showMessageDialog(null, "Reservations cannot be made on Sundays.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Generate the custom ID
            String newId = "FUN000001"; // Default ID if no records exist
            try (java.sql.ResultSet resultSet = statement.executeQuery(generateIdSQL)) {
                if (resultSet.next() && resultSet.getString("reservation_id") != null) {
                    String maxId = resultSet.getString("reservation_id");
                    int numericPart = Integer.parseInt(maxId.substring(3)); // Extract numeric part
                    newId = String.format("FUN%06d", numericPart + 1); // Increment and format
                }
            }

            // Insert into christening_table
            christeningStatement.setString(1, newId);
            christeningStatement.setString(2, (String) funeral.get("deseaced_name"));
            christeningStatement.setString(3, (String) funeral.get("family_rep_name"));
            christeningStatement.setString(4, (String) funeral.get("contactNumber"));
            christeningStatement.setString(5, dateString);
            christeningStatement.setString(6, (String) funeral.get("timeSlot"));
            christeningStatement.setInt(7, (int) funeral.get("user_id"));


            int rowsInserted = christeningStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Christening record inserted successfully with ID: " + newId);

                // Insert into reservationtable
                reservationStatement.setString(1, newId);
                reservationStatement.setString(2, "Funeral");
                reservationStatement.setString(3, dateString);
                reservationStatement.setString(4, (String) funeral.get("timeSlot"));
                reservationStatement.setString(5, "Pending");
                reservationStatement.setString(6, "n/a");

                int reservationRowsInserted = reservationStatement.executeUpdate();
                if (reservationRowsInserted > 0) {
                    System.out.println("Reservation record inserted successfully with ID: " + newId);

                    JOptionPane.showMessageDialog(null, "YOUR RESERVATION ID NUMBER IS " + newId,
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while inserting christening data: " + e.getMessage());
        }

    }

    // insert wedding
    public void insertWedding(Map<String, Object> wedding, JDialog dialog) {
        // Implementation for inserting wedding data into MySQL database
        String generateIdSQL = "SELECT MAX(reservation_id) AS reservation_id FROM christening_table";
        String insertSQL = "INSERT INTO wedding_table (reservation_id, groom_name, bride_name, contact_number, date, time_slot, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertReservationSQL = "INSERT INTO reservationtable (reservation_id, event, date, time, status, reason) VALUES (?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             java.sql.Statement statement = connection.createStatement();
             java.sql.PreparedStatement christeningStatement = connection.prepareStatement(insertSQL);
             java.sql.PreparedStatement reservationStatement = connection.prepareStatement(insertReservationSQL)) {

            // Validate the date
            String dateString = (String) wedding.get("date");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateString, formatter);

            if (date.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(null, "The date cannot be in the past.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                JOptionPane.showMessageDialog(null, "Reservations cannot be made on Sundays.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Generate the custom ID
            String newId = "FUN000001"; // Default ID if no records exist
            try (java.sql.ResultSet resultSet = statement.executeQuery(generateIdSQL)) {
                if (resultSet.next() && resultSet.getString("reservation_id") != null) {
                    String maxId = resultSet.getString("reservation_id");
                    int numericPart = Integer.parseInt(maxId.substring(3)); // Extract numeric part
                    newId = String.format("FUN%06d", numericPart + 1); // Increment and format
                }
            }

            // Insert into christening_table
            christeningStatement.setString(1, newId);
            christeningStatement.setString(2, (String) wedding.get("groom_name"));
            christeningStatement.setString(3, (String) wedding.get("brides_name"));
            christeningStatement.setString(4, (String) wedding.get("contactNumber"));
            christeningStatement.setString(5, dateString);
            christeningStatement.setString(6, (String) wedding.get("timeSlot"));
            christeningStatement.setInt(7, (int) wedding.get("user_id"));


            int rowsInserted = christeningStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Christening record inserted successfully with ID: " + newId);

                // Insert into reservationtable
                reservationStatement.setString(1, newId);
                reservationStatement.setString(2, "Funeral");
                reservationStatement.setString(3, dateString);
                reservationStatement.setString(4, (String) wedding.get("timeSlot"));
                reservationStatement.setString(5, "Pending");
                reservationStatement.setString(6, "n/a");

                int reservationRowsInserted = reservationStatement.executeUpdate();
                if (reservationRowsInserted > 0) {
                    System.out.println("Reservation record inserted successfully with ID: " + newId);

                    JOptionPane.showMessageDialog(null, "YOUR RESERVATION ID NUMBER IS " + newId,
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while inserting christening data: " + e.getMessage());
        }
    }

    public String getReservationByUserId(int userId, String searchText) {
        String query = "SELECT * FROM reservationtable WHERE user_id = ? AND " +
                "(reservation_id LIKE ? OR event LIKE ? OR date LIKE ?)";
        StringBuilder result = new StringBuilder();

        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             java.sql.PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setString(2, "%" + searchText + "%");
            statement.setString(3, "%" + searchText + "%");
            statement.setString(4, "%" + searchText + "%");

            try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String status = resultSet.getString("status");

                     if ("Rejected".equals(status)) {
                        result.append("Reservation ID: ").append(resultSet.getString("reservation_id")).append("\n")
                                .append("Event: ").append(resultSet.getString("event")).append("\n")
                                .append("Date: ").append(resultSet.getString("date")).append("\n")
                                .append("Time: ").append(resultSet.getString("time")).append("\n")
                                .append("Status: ").append(status).append("\n")
                                .append("Reason: ").append(resultSet.getString("reason")).append("\n\n");
                    }

                    result.append("Reservation ID: ").append(resultSet.getString("reservation_id")).append("\n")
                            .append("Event: ").append(resultSet.getString("event")).append("\n")
                            .append("Date: ").append(resultSet.getString("date")).append("\n")
                            .append("Time: ").append(resultSet.getString("time")).append("\n")
                            .append("Status: ").append(status).append("\n");
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return "An error occurred while fetching reservations: " + e.getMessage();
        }

        return result.toString();
    }
}
