public class Main {
    public static void main(String[] args) {

        //NOTE: mysql-socket-factory-connector jars need to be set in the classpath
        try {
            //Multiple queries set in a String to execute together instead of individual execution
            String t1Query = "SELECT * FROM DEPARTMENTS where DEPARTMENT_ID = \"CSDEPT001\"; " +
                    "UPDATE DEPARTMENTS set DEPARTMENT_CONTACT1 = '111111111' where DEPARTMENT_ID = \"CSDEPT001\"; " +
                    "SELECT * FROM DEPARTMENTS where DEPARTMENT_ID = \"CSDEPT001\"";

            Transaction transaction1 = new Transaction("Transaction 1", t1Query);
            Thread t1 = new Thread(transaction1);
            t1.start();

            String t2Query = "SELECT * FROM DEPARTMENTS where DEPARTMENT_ID = \"CSDEPT001\"; " +
                    "UPDATE DEPARTMENTS set DEPARTMENT_CONTACT1 = '222222222' where DEPARTMENT_ID = \"CSDEPT001\"; " +
                    "SELECT * FROM DEPARTMENTS where DEPARTMENT_ID = \"CSDEPT001\"";

            Transaction transaction2 = new Transaction("Transaction 2", t2Query);
            Thread t2 = new Thread(transaction2);
            t2.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
