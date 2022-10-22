import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LocalDBOperations {

    public Connection openConnection() {
        Connection localDbConnection = null;

        String localDbUrl = "jdbc:mysql://localhost:3306/dbwha_lab4_localdb";
        String localDbUsername = "root";
        String localDbPassword = "root";

        try {
            localDbConnection = DriverManager.getConnection(localDbUrl, localDbUsername, localDbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (localDbConnection != null) {
            System.out.println("Connected to Local DB..");
        }

        return localDbConnection;
    }

    public int createNewOrder(Connection localDbConnection, OrderInfo newOrder) throws SQLException {
        String createOrderQuery = "INSERT INTO ORDER_INFO(ORDER_ID, USER_ID, ITEM_NAME, QUANTITY) VALUES (?, ?, ?, ?)";

        PreparedStatement preparedStatement = localDbConnection.prepareStatement(createOrderQuery);
        preparedStatement.setString(1, newOrder.getOrderId());
        preparedStatement.setString(2, newOrder.getUserId());
        preparedStatement.setString(3, newOrder.getItemName());
        preparedStatement.setInt(4, newOrder.getQuantity());

        long startTime = System.currentTimeMillis();
        int insertResult = preparedStatement.executeUpdate();
        long totalExecutionTime = System.currentTimeMillis() - startTime;

        if (insertResult > 0) {
            System.out.println("Order Created Successfully in Local DB! Execution Time: "+totalExecutionTime+" milliseconds\n");
        } else {
            System.out.println("Order not created");
        }
        return insertResult;
    }

    public void closeConnection(Connection localDbConnection) throws SQLException {
        if (localDbConnection != null) {
            System.out.println("\nClosing Local DB Connection..");
            localDbConnection.close();
        }
    }
}
