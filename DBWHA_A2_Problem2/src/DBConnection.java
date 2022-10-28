import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public Connection openConnectionWithMultiQueries() throws SQLException {
        Connection localDbConnection = null;

        String localDbUrl = "jdbc:mysql://localhost:3306/dmwha_a1_task2_dalhousiedb?allowMultiQueries=true";
        String localDbUsername = "root";
        String localDbPassword = "root";

        try {
            localDbConnection = DriverManager.getConnection(localDbUrl, localDbUsername, localDbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (localDbConnection != null) {
            localDbConnection.setAutoCommit(false);
            System.out.println("Connected to DB - dmwha_a1_task2_dalhousiedb");
        }

        return localDbConnection;
    }

    public void closeConnection(Connection localDbConnection, String transactionName) throws SQLException {
        if (localDbConnection != null) {
            System.out.println("Closing DB Connection for: "+transactionName);
            localDbConnection.close();
        }
    }
}
