import java.sql.*;
import java.util.ArrayList;

public class RemoteDBOperations {
    private final String url;
    private final String port;
    private final String databaseName;
    private final String username;
    private final String password;

    public RemoteDBOperations(String url, String port, String databaseName, String username, String password) {
        this.url = url;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    public Connection openConnection() throws SQLException {
        Connection remoteDbConnection = null;

        String remoteDbUrl = "jdbc:mysql://" + url + ":" + port + "/" + databaseName;

        try {
            remoteDbConnection = DriverManager.getConnection(remoteDbUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (remoteDbConnection != null) {
            remoteDbConnection.setAutoCommit(false);
            System.out.println("Connected to Remote DB: " + remoteDbConnection.getMetaData().getUserName() + "@" + remoteDbConnection.getMetaData().getURL());
        }

        return remoteDbConnection;
    }


    public void fetchIcons(Connection remoteDbConnection) {
        String fetchIconsQuery = "SELECT * FROM icons where ICON_ID = \"ICON002\"";
        ArrayList<Icons> iconsList = new ArrayList<>();

        try {
            long startTime = System.currentTimeMillis();
            Statement statement = remoteDbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(fetchIconsQuery);
            long totalExecutionTime = System.currentTimeMillis() - startTime;

            System.out.println("\nICONS: ");

            while (resultSet.next()) {
                String iconId = resultSet.getString("icon_id");
                String iconName = resultSet.getString("icon_name");
                Blob iconImage = resultSet.getBlob("icon_image");
                Icons icon = new Icons(iconId, iconName, iconImage);
                System.out.println(icon.toString());
                iconsList.add(icon);
            }
            System.out.println("\nFetch Icons from Remote DB Query Execution Time: "+totalExecutionTime+" milliseconds");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int updateIconName(Connection remoteDbConnection) throws SQLException {
        String updateIconNameQuery = "UPDATE icons set ICON_NAME = ? WHERE ICON_ID = ?";

        PreparedStatement preparedStatement = remoteDbConnection.prepareStatement(updateIconNameQuery);
        preparedStatement.setString(1, "Top View");
        preparedStatement.setString(2, "ICON002");

        long startTime = System.currentTimeMillis();
        int updateResult = preparedStatement.executeUpdate();
        long totalExecutionTime = System.currentTimeMillis() - startTime;

        if (updateResult > 0) {
            System.out.println("Icon Name Updated Successfully in Remote DB! Execution Time: "+totalExecutionTime+" milliseconds");
        } else {
            System.out.println("Icon Name not Updated!");
        }
        return updateResult;
    }

    public void closeConnection(Connection remoteDbConnection) throws SQLException {
        if (remoteDbConnection != null) {
            System.out.println("Closing Remote DB Connection..");
            remoteDbConnection.close();
        }
    }


}
