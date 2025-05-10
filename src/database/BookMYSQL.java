package database;

import model.ReservationModel;

import javax.swing.*;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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

    // get the pending status
    public static List<ReservationModel> getReservationPending() {
        String query = "SELECT * FROM reservationtable WHERE status = 'Pending'";
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

    // This will update the reservation status
    public static void updateReservationStatus(String reservationID, String status) {
        String query = "UPDATE reservationtable SET status = ? WHERE reservation_id = ?";
        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             java.sql.PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, status);
            statement.setString(2, reservationID);
            statement.executeUpdate();

            System.out.println("Reservation with ID " + reservationID + " updated successfully.");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while updating the reservation: " + e.getMessage());
        }
    }

    // This will update the reservation status to rejected
    public static void rejected(String reservationID, String reason, String status, JDialog dialog) {
        String query = "UPDATE reservationtable SET status = ?, reason = ? WHERE reservation_id = ?";
        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             java.sql.PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, status);
            statement.setString(2, reason);
            statement.setString(3, reservationID);
            statement.executeUpdate();

            System.out.println("Reservation with ID " + reservationID + " updated successfully.");
            JOptionPane.showMessageDialog(null, "Reservation Rejected Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while updating the reservation: " + e.getMessage());
        }
    }

    public static String getName(int userId, String event) {
        if (event.equals("Christening")) {
            String query = "SELECT child_name FROM christening_table WHERE user_id = ?";
            try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                    MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
                 java.sql.PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                java.sql.ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getString("child_name");
                }
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }

        } else if (event.equals("Funeral")) {
            String query = "SELECT desceased_name FROM funeral_table WHERE user_id = ?";
            try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                    MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
                 java.sql.PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                java.sql.ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getString("desceased_name");
                }
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        } else if (event.equals("Wedding")) {
            String query = "SELECT groom_name FROM wedding_table WHERE user_id = ?";
            try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                    MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
                 java.sql.PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                java.sql.ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getString("groom_name");
                }
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    public static List<Map<String, Object>> getReservationsByWeek(String startDate) {
         String query = "SELECT * FROM reservationtable WHERE date >= ? AND date < DATE_ADD(?, INTERVAL 7 DAY)";

        List<Map<String, Object>> reservations = new ArrayList<>();

         try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             java.sql.PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, startDate);
            statement.setString(2, startDate);
            java.sql.ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Map<String, Object> reservation = new HashMap<>();
                reservation.put("reservationID", resultSet.getString("reservation_id"));
                reservation.put("event", resultSet.getString("event"));
                reservation.put("time", resultSet.getString("time"));
                reservation.put("status", resultSet.getString("status"));
                reservations.add(reservation);
            }


        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching reservations: " + e.getMessage());
         }

        return reservations;
    }

    public void insertChristening(Map<String, Object> christening, JDialog dialog) {
        String generateIdSQL = "SELECT MAX(reservation_id) AS reservation_id FROM christening_table";
        String countChristeningsSQL = "SELECT COUNT(*) AS count FROM reservationtable WHERE date_filled = ? AND event = 'Christening'";
        String insertSQL = "INSERT INTO christening_table (reservation_id, child_name, parent_name, contact_number, date, time_slot, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertReservationSQL = "INSERT INTO reservationtable (reservation_id, event, date, time, status, reason, user_id, date_filled) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             java.sql.Statement statement = connection.createStatement();
             java.sql.PreparedStatement countStatement = connection.prepareStatement(countChristeningsSQL);
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

            // Get today's date for date_filled
            String currentDate = LocalDate.now().format(formatter);

            // Check if the limit of 3 christenings per day is reached based on date_filled and event
            countStatement.setString(1, currentDate);
            try (java.sql.ResultSet resultSet = countStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt("count") >= 3) {
                    JOptionPane.showMessageDialog(null, "The maximum number of christenings for this date has been reached.", "Limit Reached", JOptionPane.ERROR_MESSAGE);
                    return;
                }
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
                reservationStatement.setInt(7, (int) christening.get("user_id"));
                reservationStatement.setString(8, currentDate); // Use today's date for date_filled

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
            JOptionPane.showMessageDialog(null, "An error occurred while inserting christening data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // insert funeral
    public  void insertFuneral(Map<String, Object> funeral, JDialog dialog) {
        String generateIdSQL = "SELECT MAX(reservation_id) AS reservation_id FROM funeral_table";
        String insertSQL = "INSERT INTO funeral_table (reservation_id, deceased_name, family_rep_name, contact_number, date, time_slot, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertReservationSQL = "INSERT INTO reservationtable (reservation_id, event, date, time, status, reason, user_id, date_filled) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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
            christeningStatement.setString(2, (String) funeral.get("deceased_name"));
            christeningStatement.setString(3, (String) funeral.get("family_rep_name"));
            christeningStatement.setString(4, (String) funeral.get("contactNumber"));
            christeningStatement.setString(5, dateString);
            christeningStatement.setString(6, (String) funeral.get("timeSlot"));
            christeningStatement.setInt(7, (int) funeral.get("user_id"));

            // Get today's date for date_filled
            String currentDate = LocalDate.now().format(formatter);

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
                reservationStatement.setInt(7, (int) funeral.get("user_id"));
                reservationStatement.setString(8, currentDate); // Use today's date for date_filled

                int reservationRowsInserted = reservationStatement.executeUpdate();
                if (reservationRowsInserted > 0) {
                    System.out.println("Reservation record inserted successfully with ID: " + newId);
                    dialog.dispose();
                }
                JOptionPane.showMessageDialog(null, "YOUR RESERVATION ID NUMBER IS " + newId,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while inserting christening data: " + e.getMessage());
        }

    }

    // insert wedding
    public void insertWedding(Map<String, Object> wedding, JDialog dialog) {
        // Implementation for inserting wedding data into MySQL database
        String generateIdSQL = "SELECT MAX(reservation_id) AS reservation_id FROM wedding_table";
        String insertSQL = "INSERT INTO wedding_table (reservation_id, groom_name, bride_name, contact_number, date, time_slot, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertReservationSQL = "INSERT INTO reservationtable (reservation_id, event, date, time, status, reason, user_id, date_filled) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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
            String newId = "WED000001"; // Default ID if no records exist
            try (java.sql.ResultSet resultSet = statement.executeQuery(generateIdSQL)) {
                if (resultSet.next() && resultSet.getString("reservation_id") != null) {
                    String maxId = resultSet.getString("reservation_id");
                    int numericPart = Integer.parseInt(maxId.substring(3)); // Extract numeric part
                    newId = String.format("WED%06d", numericPart + 1); // Increment and format
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


            // Get today's date for date_filled
            String currentDate = LocalDate.now().format(formatter);

            int rowsInserted = christeningStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Christening record inserted successfully with ID: " + newId);

                // Insert into reservationtable
                reservationStatement.setString(1, newId);
                reservationStatement.setString(2, "Wedding");
                reservationStatement.setString(3, dateString);
                reservationStatement.setString(4, (String) wedding.get("timeSlot"));
                reservationStatement.setString(5, "Pending");
                reservationStatement.setString(6, "n/a");
                reservationStatement.setInt(7, (int) wedding.get("user_id"));
                reservationStatement.setString(8, currentDate); // Use today's date for date_filled


                int reservationRowsInserted = reservationStatement.executeUpdate();
                if (reservationRowsInserted > 0) {
                    System.out.println("Reservation record inserted successfully with ID: " + newId);
                    dialog.dispose();
                }

                JOptionPane.showMessageDialog(null, "YOUR RESERVATION ID NUMBER IS " + newId,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
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

    public static List<Map<String, Object>> getReservationsByDate(String date) {
        List<Map<String, Object>> reservations = new ArrayList<>();
        String query = "SELECT reservation_id, event, time, status, date, date_filled FROM reservationtable WHERE date = ?";

        try (Connection conn = DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, date);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> reservation = new HashMap<>();
                String reservationId = rs.getString("reservation_id");
                String event = rs.getString("event");
                String dateStart = rs.getString("date");
                String dateFilled = rs.getString("date_filled");
                String name = "";

                // Fetch additional info based on the event type
                if ("Christening".equals(event)) {
                    String christeningQuery = "SELECT child_name FROM christening_table WHERE reservation_id = ?";
                    try (PreparedStatement christeningStmt = conn.prepareStatement(christeningQuery)) {
                        christeningStmt.setString(1, reservationId);
                        ResultSet christeningResult = christeningStmt.executeQuery();
                        if (christeningResult.next()) {
                            name = christeningResult.getString("child_name");
                        }
                    }
                } else if ("Funeral".equals(event)) {
                    String funeralQuery = "SELECT deceased_name FROM funeral_table WHERE reservation_id = ?";
                    try (PreparedStatement funeralStmt = conn.prepareStatement(funeralQuery)) {
                        funeralStmt.setString(1, reservationId);
                        ResultSet funeralResult = funeralStmt.executeQuery();
                        if (funeralResult.next()) {
                            name = funeralResult.getString("deceased_name");
                        }
                    }
                } else if ("Wedding".equals(event)) {
                    String weddingQuery = "SELECT groom_name FROM wedding_table WHERE reservation_id = ?";
                    try (PreparedStatement weddingStmt = conn.prepareStatement(weddingQuery)) {
                        weddingStmt.setString(1, reservationId);
                        ResultSet weddingResult = weddingStmt.executeQuery();
                        if (weddingResult.next()) {
                            name = weddingResult.getString("groom_name");
                        }
                    }
                }

                // Add reservation details to the list
                reservation.put("reservationID", reservationId);
                reservation.put("event", event);
                reservation.put("time", rs.getString("time"));
                reservation.put("status", rs.getString("status"));
                reservation.put("name", name);
                reservation.put("dates", dateStart);
                reservation.put("date_filled", dateFilled);
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public static List<Map<String, Object>> getReservationsByDateRange(String startDate, String endDate) {
        List<Map<String, Object>> reservations = new ArrayList<>();
        String query = "SELECT reservation_id, event, date, time, status, date_filled FROM reservationtable WHERE date BETWEEN ? AND ?";

        try (Connection conn = DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> reservation = new HashMap<>();
                String reservationId = rs.getString("reservation_id");
                String event = rs.getString("event");
                String dateStart = rs.getString("date");
                String dateFilled = rs.getString("date_filled");
                String name = fetchEventName(conn, reservationId, event);

                // Add reservation details to the list
                reservation.put("reservationID", reservationId);
                reservation.put("event", event);
                reservation.put("time", rs.getString("time"));
                reservation.put("status", rs.getString("status"));
                reservation.put("name", name);
                reservation.put("dates", dateStart);
                reservation.put("date_filled", dateFilled);
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reservations by date range: " + e.getMessage());
            e.printStackTrace();
        }
        return reservations;
    }

    // Helper method to fetch additional info based on event type
    private static String fetchEventName(Connection conn, String reservationId, String event) {
        String query = null;
        switch (event) {
            case "Christening":
                query = "SELECT child_name FROM christening_table WHERE reservation_id = ?";
                break;
            case "Funeral":
                query = "SELECT deceased_name FROM funeral_table WHERE reservation_id = ?";
                break;
            case "Wedding":
                query = "SELECT groom_name FROM wedding_table WHERE reservation_id = ?";
                break;
            default:
                return ""; // Return empty if event type is unknown
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, reservationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1); // Fetch the first column (name)
            }
        } catch (SQLException e) {
            System.err.println("Error fetching name for event type " + event + ": " + e.getMessage());
        }
        return ""; // Return empty if no result is found
    }

    public void notifyUpcomingEvents() {
        String query = "SELECT reservation_id, event, date, user_id FROM reservationtable WHERE date = ? AND status = 'Accepted'";
        LocalDate nextDay = LocalDate.now().plusDays(1);
        String nextDayString = nextDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try (Connection connection = DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, nextDayString);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String reservationId = resultSet.getString("reservation_id");
                String event = resultSet.getString("event");
                String date = resultSet.getString("date");
                int userId = resultSet.getInt("user_id");

                String additionalInfo = "";

                // If the event is "Christening," fetch the child's name
                if ("Christening".equals(event)) {
                    String christeningQuery = "SELECT child_name FROM christening_table WHERE reservation_id = ?";
                    try (PreparedStatement christeningStatement = connection.prepareStatement(christeningQuery)) {
                        christeningStatement.setString(1, reservationId);
                        ResultSet christeningResult = christeningStatement.executeQuery();
                        if (christeningResult.next()) {
                            additionalInfo = " for " + christeningResult.getString("child_name");
                        }
                    }
                }

                if ("Funeral".equals(event)) {
                    String funeralQuery = "SELECT deceased_name FROM funeral_table WHERE reservation_id = ?";
                    try (PreparedStatement funeralStatement = connection.prepareStatement(funeralQuery)) {
                        funeralStatement.setString(1, reservationId);
                        ResultSet funeralResult = funeralStatement.executeQuery();
                        if (funeralResult.next()) {
                            additionalInfo = " for " + funeralResult.getString("deceased_name");
                        }
                    }
                }

                if ("Wedding".equals(event)) {
                    String weddingQuery = "SELECT groom_name FROM wedding_table WHERE reservation_id = ?";
                    try (PreparedStatement weddingStatement = connection.prepareStatement(weddingQuery)) {
                        weddingStatement.setString(1, reservationId);
                        ResultSet weddingResult = weddingStatement.executeQuery();
                        if (weddingResult.next()) {
                            additionalInfo = " for " + weddingResult.getString("groom_name");
                        }
                    }
                }

                // Notify the user
                JOptionPane.showMessageDialog(null,
                        "Reminder: The " + event + " reservation (ID: " + reservationId + ")" + additionalInfo + " is scheduled for " + date + ".",
                        "Upcoming Event Notification",
                        JOptionPane.INFORMATION_MESSAGE);

                System.out.println("Notification sent to user ID: " + userId + " for reservation ID: " + reservationId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "An error occurred while fetching upcoming events: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void notifyUser(int userId) {
        String query = "SELECT reservation_id, event, date, user_id FROM reservationtable WHERE date = ? AND user_id = ? AND status = 'Accepted'";
        LocalDate nextDay = LocalDate.now().plusDays(1);
        String nextDayString = nextDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try (Connection connection = DriverManager.getConnection(
                MYSQLConnection.databaseUrl, MYSQLConnection.user, MYSQLConnection.password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, nextDayString);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String reservationId = resultSet.getString("reservation_id");
                String event = resultSet.getString("event");
                String date = resultSet.getString("date");
                String additionalInfo = "";

                // If the event is "Christening," fetch the child's name
                if ("Christening".equals(event)) {
                    String christeningQuery = "SELECT child_name FROM christening_table WHERE reservation_id = ?";
                    try (PreparedStatement christeningStatement = connection.prepareStatement(christeningQuery)) {
                        christeningStatement.setString(1, reservationId);
                        ResultSet christeningResult = christeningStatement.executeQuery();
                        if (christeningResult.next()) {
                            additionalInfo = " for " + christeningResult.getString("child_name");
                        }
                    }
                }

                if ("Funeral".equals(event)) {
                    String funeralQuery = "SELECT deceased_name FROM funeral_table WHERE reservation_id = ?";
                    try (PreparedStatement funeralStatement = connection.prepareStatement(funeralQuery)) {
                        funeralStatement.setString(1, reservationId);
                        ResultSet funeralResult = funeralStatement.executeQuery();
                        if (funeralResult.next()) {
                            additionalInfo = " for " + funeralResult.getString("deceased_name");
                        }
                    }
                }

                if ("Wedding".equals(event)) {
                    String weddingQuery = "SELECT groom_name FROM wedding_table WHERE reservation_id = ?";
                    try (PreparedStatement weddingStatement = connection.prepareStatement(weddingQuery)) {
                        weddingStatement.setString(1, reservationId);
                        ResultSet weddingResult = weddingStatement.executeQuery();
                        if (weddingResult.next()) {
                            additionalInfo = " for " + weddingResult.getString("groom_name");
                        }
                    }
                }

                // Notify the user
                JOptionPane.showMessageDialog(null,
                        "Reminder: The of Your" + event + " reservation (ID: " + reservationId + ")" + additionalInfo + " is scheduled for " + date + ".",
                        "Upcoming Event Notification",
                        JOptionPane.INFORMATION_MESSAGE);

                System.out.println("Notification sent to user ID: " + userId + " for reservation ID: " + reservationId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "An error occurred while fetching upcoming events: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
