import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class RemoteDBOperations {

    public Connection openConnection() {
        Connection remoteDbConnection = null;

        String remoteDbUrl = "jdbc:mysql://34.69.198.155:3306/dbwha_lab4_iowa_remotedb";
        String remoteDbUsername = "root";
        String remoteDbPassword = "root_b00902815";

        try {
            remoteDbConnection = DriverManager.getConnection(remoteDbUrl, remoteDbUsername, remoteDbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (remoteDbConnection != null) {
            System.out.println("Connected to Remote DB..");
        }

        return remoteDbConnection;
    }

    public ArrayList<Inventory> fetchInventory(Connection remoteDbConnection) {
        String fetchInventoryQuery = "SELECT * FROM INVENTORY";
        ArrayList<Inventory> inventoryList = new ArrayList<>();

        try {

            long startTime = System.currentTimeMillis();
            Statement statement = remoteDbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(fetchInventoryQuery);
            long totalExecutionTime = System.currentTimeMillis() - startTime;

            System.out.println("\nAVAILABLE INVENTORY: ");

            while (resultSet.next()) {
                String itemId = resultSet.getString("item_id");
                String itemName = resultSet.getString("item_name");
                int availableQuantity = resultSet.getInt("available_quantity");
                Inventory inventoryItem = new Inventory(itemId, itemName, availableQuantity);
                System.out.println(inventoryItem.toString());
                inventoryList.add(inventoryItem);
            }
            System.out.println("\nFetch Inventory from Remote DB Query Execution Time: "+totalExecutionTime+" milliseconds\n");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inventoryList;
    }


    public int updateInventory(Connection remoteDbConnection, OrderInfo newOrder) throws SQLException {
        String updateInventoryQuery = "UPDATE INVENTORY set AVAILABLE_QUANTITY = AVAILABLE_QUANTITY - ? WHERE ITEM_ID = ?";

        PreparedStatement preparedStatement = remoteDbConnection.prepareStatement(updateInventoryQuery);
        preparedStatement.setInt(1, newOrder.getQuantity());
        preparedStatement.setString(2, newOrder.getItemId());

        long startTime = System.currentTimeMillis();
        int updateResult = preparedStatement.executeUpdate();
        long totalExecutionTime = System.currentTimeMillis() - startTime;

        if (updateResult > 0) {
            System.out.println("Inventory Updated Successfully in Remote DB! Execution Time: "+totalExecutionTime+" milliseconds\n");
        } else {
            System.out.println("Inventory not Updated!");
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
