import java.sql.*;
import java.util.ArrayList;

public class LocalDBOperations {

    private final String url;
    private final String port;
    private final String databaseName;
    private final String username;
    private final String password;

    public LocalDBOperations(String url, String port, String databaseName, String username, String password) {
        this.url = url;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    public Connection openConnection() throws SQLException {
        Connection localDbConnection = null;

        String localDbUrl = "jdbc:mysql://" + url + ":" + port + "/" + databaseName;

        try {
            localDbConnection = DriverManager.getConnection(localDbUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (localDbConnection != null) {
            localDbConnection.setAutoCommit(false);
            System.out.println("Connected to Local DB: " + localDbConnection.getMetaData().getUserName() + "@" + localDbConnection.getMetaData().getURL());
        }

        return localDbConnection;
    }

    public void fetchParks(Connection localDbConnection) {
        String fetchParksQuery = "SELECT * FROM PARK_MASTER where PARK_ID = \"PARK0004\"";
        ArrayList<ParkMaster> parksList = new ArrayList<>();

        try {

            long startTime = System.currentTimeMillis();
            Statement statement = localDbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(fetchParksQuery);
            long totalExecutionTime = System.currentTimeMillis() - startTime;

            System.out.println("\nPARKS: ");

            while (resultSet.next()) {
                String parkId = resultSet.getString("park_id");
                String parkName = resultSet.getString("park_name");
                String parkRegion = resultSet.getString("park_region");
                ParkMaster park = new ParkMaster(parkId, parkName, parkRegion);
                System.out.println(park.toString());
                parksList.add(park);
            }
            System.out.println("\nFetch Parks from Local DB Query Execution Time: "+totalExecutionTime+" milliseconds");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int updateParkRegion(Connection localDbConnection) throws SQLException {

        String updateParkRegionQuery = "UPDATE PARK_MASTER set PARK_REGION = ? WHERE PARK_ID = ?";

        PreparedStatement preparedStatement = localDbConnection.prepareStatement(updateParkRegionQuery);
        preparedStatement.setString(1, "West End");
        preparedStatement.setString(2, "PARK0004");

        long startTime = System.currentTimeMillis();
        int updateResult = preparedStatement.executeUpdate();
        long totalExecutionTime = System.currentTimeMillis() - startTime;

        if (updateResult > 0) {
            System.out.println("Park Region Updated Successfully in Local DB! Execution Time: "+totalExecutionTime+" milliseconds");
        } else {
            System.out.println("Park Region not Updated!");
        }
        return updateResult;
    }
    public void closeConnection(Connection localDbConnection) throws SQLException {
        if (localDbConnection != null) {
            System.out.println("\nClosing Local DB Connection..");
            localDbConnection.close();
        }
    }
}
