import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Transaction extends Thread {
    private static final Lock dbLock = new ReentrantLock();
    private String transactionName;
    private String queriesToExecute;
    private Connection localDbConnection = null;
    private DBConnection localDB = null;

    public Transaction(String transaction, String queries) {
        this.transactionName = transaction;
        this.queriesToExecute = queries;
        System.out.println("\nCreating " + transaction + " with queries: \n" + queries);
    }


    @Override
    public void run() {
        synchronized (this) {
            try {
                //Open Connection
                localDB = new DBConnection();
                localDbConnection = localDB.openConnectionWithMultiQueries();
                //Acquire Lock
                acquireLock(this);
                //Perform Operations
                operate();
                //Release Lock
                releaseLock();
            } catch (Exception e) {
                try {
                    System.out.println(transactionName + " failed. Rolling Back");
                    localDbConnection.rollback();
                } catch (SQLException ex) {
                    e.printStackTrace();
                    throw new RuntimeException(ex);
                }
                e.printStackTrace();
            } finally {
                //Release Lock
                releaseLock();
                //Close Connections
                try {
                    localDB.closeConnection(localDbConnection, transactionName);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(transactionName + " Exiting...");
        }
    }

    //Reference Taken from: https://www.baeldung.com/java-concurrent-locks & https://www.geeksforgeeks.org/wait-method-in-java-with-examples/
    private void acquireLock(Transaction transaction) throws InterruptedException {
        synchronized (this) {
            while(true) {
                if (dbLock.tryLock()) {
                    System.out.println("\n" + transactionName + " is acquiring Lock");
                    dbLock.lock();
                    break;
                } else {
                    System.out.println(transactionName + " is Waiting!!!!!!");
                    transaction.wait(1000);
                }
            }
        }
    }

    private void operate() {
        synchronized (this) {
            try {
                System.out.println("\nOperating " + transactionName);
                Statement transactionStatement = localDbConnection.createStatement();
                boolean hasMoreResults = transactionStatement.execute(queriesToExecute);
                printResults(transactionStatement, hasMoreResults);
                localDbConnection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    //Reference Taken from: https://stackoverflow.com/questions/10797794/multiple-queries-executed-in-java-in-single-statement
    private void printResults(Statement transactionStatement, boolean hasMoreResults) throws SQLException {
        System.out.println("\nSelect Initial Data: ");
        while (hasMoreResults || transactionStatement.getUpdateCount() != -1) {
            if (hasMoreResults) {
                ResultSet rs = transactionStatement.getResultSet();
                if (rs != null) {
                    while (rs.next()) {
                        System.out.print("DEPARTMENT_ID: " + rs.getString("DEPARTMENT_ID"));
                        System.out.println("\tDEPARTMENT_CONTACT1: " + rs.getString("DEPARTMENT_CONTACT1"));
                    }
                }
            } else {
                int queryResult = transactionStatement.getUpdateCount();
                if (queryResult == -1) {
                    break;
                } else {
                    System.out.println("Record Updated..");
                    System.out.println("After Update Results: ");
                }
            }
            hasMoreResults = transactionStatement.getMoreResults(Statement.CLOSE_CURRENT_RESULT);
        }
    }


    private void releaseLock() throws IllegalMonitorStateException{
        synchronized (this) {
            System.out.println("\n" + transactionName + " is releasing Lock");
            dbLock.unlock();
            notifyAll();
        }
    }

}
