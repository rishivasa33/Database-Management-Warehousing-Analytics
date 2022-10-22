import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Main {
    public static void main(String args[]) throws SQLException {

        //NOTE: mysql-socket-factory-connector jars need to be set in the classpath
        LocalDBOperations localDB = null;
        RemoteDBOperations remoteDB = null;
        Connection localDbConnection = null;
        Connection remoteDbConnection = null;

        try {
            //Create Local and Remote DB Connections
            localDB = new LocalDBOperations();
            localDbConnection = localDB.openConnection();

            remoteDB = new RemoteDBOperations();
            remoteDbConnection = remoteDB.openConnection();

            //Fetch Inventory from Remote
            ArrayList<Inventory> inventory = remoteDB.fetchInventory(remoteDbConnection);

            //Create Order in Local (Hardcoding OrderId = DummyOrderForLab001, UserId = LabUser001, Item = Shoes, quantity = 1)
            OrderInfo newOrder = new OrderInfo();
            newOrder.setOrderId("DummyOrderForLab001");
            newOrder.setUserId("LabUser001");
            newOrder.setItemName(inventory.get(0).getItemName());
            newOrder.setQuantity(1);
            newOrder.setItemId(inventory.get(0).getItemId());

            System.out.println("NEW ORDER: \n"+newOrder.toString());
            int insertResult = localDB.createNewOrder(localDbConnection, newOrder);

            //Update Remote DB
            int updateResult = 0;
            if (insertResult > 0) {
                updateResult = remoteDB.updateInventory(remoteDbConnection, newOrder);
            }

            if(updateResult > 0 ){
                System.out.println("\nTransaction Done.\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Close Connections
            localDB.closeConnection(localDbConnection);
            remoteDB.closeConnection(remoteDbConnection);
        }
    }
}
