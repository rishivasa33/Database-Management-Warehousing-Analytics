import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {

        //NOTE: mysql-socket-factory-connector jars need to be set in the classpath
        Connection transactionConnection1 = null;
        Connection transactionConnection2 = null;
        DBConnection localDB = null;

        try {
            localDB = new DBConnection();

            transactionConnection1 = localDB.openConnectionWithMultiQueries();
            transactionConnection2 = localDB.openConnectionWithMultiQueries();

            //Multiple queries set in a String to execute together instead of individual execution
            String t1Query = "SELECT * FROM DEPARTMENTS where DEPARTMENT_ID = \"CSDEPT001\"; " +
                    "UPDATE DEPARTMENTS set DEPARTMENT_CONTACT1 = '111111111' where DEPARTMENT_ID = \"CSDEPT001\"; " +
                    "SELECT * FROM DEPARTMENTS where DEPARTMENT_ID = \"CSDEPT001\"";

            Transaction transaction1 = new Transaction("Transaction 1", t1Query, transactionConnection1);
            Thread t1 = new Thread(transaction1);
            t1.start();
            Thread.sleep(50); //To allow thread to execute completely before executing finally block

            String t2Query = "SELECT * FROM DEPARTMENTS where DEPARTMENT_ID = \"CSDEPT001\"; " +
                    "UPDATE DEPARTMENTS set DEPARTMENT_CONTACT1 = '222222222' where DEPARTMENT_ID = \"CSDEPT001\"; " +
                    "SELECT * FROM DEPARTMENTS where DEPARTMENT_ID = \"CSDEPT001\"";

            Transaction transaction2 = new Transaction("Transaction 2", t2Query, transactionConnection2);
            Thread t2 = new Thread(transaction2);
            t2.start();

            Thread.sleep(50); //To allow thread to execute completely before executing finally block

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("\n");
            //Close Connections
            localDB.closeConnection(transactionConnection1, "Transaction 1");
            localDB.closeConnection(transactionConnection2, "Transaction 2");
        }
    }
}
