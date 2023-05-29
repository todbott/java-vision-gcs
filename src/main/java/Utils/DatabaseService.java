package Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;


public class DatabaseService {
    Connection connection;

    public DatabaseService() {

        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("org.postgresql.Driver");

            String connectionString = "jdbc:postgresql://35.222.180.129/?user=" + System.getenv("DB_USERNAME") + "&password=" + System.getenv("DB_PASSWORD") + "&useSSL=false&allowPublicKeyRetrieval=true";

            connection = DriverManager.getConnection(connectionString);
        } catch (SQLException exc) {
            System.out.println("Exception occurred");
            exc.printStackTrace();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void addToDatabase(String center, String currentlyHandling) {

        String sql = "INSERT INTO uscis_stats (center, currently_handling) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);) {

            pstmt.setString(1, center);
            pstmt.setDate(2, Date.valueOf(currentlyHandling));
            pstmt.executeUpdate();  // Execute the query

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
