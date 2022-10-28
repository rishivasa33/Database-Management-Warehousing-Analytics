import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Transaction implements Runnable {
    private final ReadWriteLock dbLock = new ReentrantReadWriteLock();
    private final Lock writeLock = dbLock.writeLock();
    private String transactionName;
    private Connection localDbConnection;
    private String queriesToExecute;

    public Transaction(String transaction, String queries, Connection localDbConnection) {
        this.transactionName = transaction;
        this.queriesToExecute = queries;
        this.localDbConnection = localDbConnection;
        System.out.println("\nCreating " + transaction + " with queries: \n" + queries);
    }


    @Override
    public void run() {
        try {
            //Acquire Lock
            acquireLock(this);
            //Perform Operations
            operate();
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
        }
        System.out.println(transactionName + " Exiting...");
    }

    //Reference Taken from: https://www.baeldung.com/java-concurrent-locks
    private void acquireLock(Transaction transaction) throws InterruptedException {
        //Check  if lock is free
        //if(dbLock.){
            System.out.println("\n" + transaction.transactionName + " is acquiring Lock");
            writeLock.lock();
        //} else{
          //  System.out.println("\n" + transaction.transactionName + " is waiting to acquire Lock");
            //Thread.sleep(1000);
        //}
    }


    private void operate() {
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


    //Reference Taken from: https://stackoverflow.com/questions/10797794/multiple-queries-executed-in-java-in-single-statement
    private static void printResults(Statement transactionStatement, boolean hasMoreResults) throws SQLException {
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


    private void releaseLock() {
        System.out.println("\n" + transactionName + " is releasing Lock");
        writeLock.unlock();
    }

}
