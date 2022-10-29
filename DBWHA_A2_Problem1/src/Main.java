import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
    //NOTE: mysql-socket-factory-connector jars need to be set in the classpath
    private static Connection localDbConnection = null;
    private static Connection remoteDbConnection = null;
    private static LocalDBOperations localDB = null;
    private static RemoteDBOperations remoteDB = null;

    public static void main(String[] args) throws SQLException {


        try {
            //Reading Global Data Dictionary
            readGDDAndInitializeConnections();

            //Create Local and Remote DB Connections
            localDbConnection = localDB.openConnection();
            remoteDbConnection = remoteDB.openConnection();

            //Perform Distributed Transaction

            //Select Initial values from Local
            System.out.println("\nInitial values for Park PARK0004 in Local DB: ");
            localDB.fetchParks(localDbConnection);
            //Update Local
            int updateResult = localDB.updateParkRegion(localDbConnection);
            if(updateResult > 0 ){
                System.out.println("\nPark Region Updated in Local DB. New Values: ");
            }
            //Select Updated values from Local
            localDB.fetchParks(localDbConnection);


            //Select Initial values from Remote
            System.out.println("\nInitial values for Icon ICON002 in Remote DB: ");
            remoteDB.fetchIcons(remoteDbConnection);
            //Update Remote
            int updateRemoteResult = remoteDB.updateIconName(remoteDbConnection);
            if(updateRemoteResult > 0 ){
            System.out.println("\nIconName Updated in Remote DB. New Values: ");
            }
            //Select Updated values from Remote
            remoteDB.fetchIcons(remoteDbConnection);


            //Commit Transaction
            localDbConnection.commit();
            remoteDbConnection.commit();
        } catch (Exception e) {
            localDbConnection.rollback();
            remoteDbConnection.rollback();
            e.printStackTrace();
        } finally {
            //Close Connections
            localDB.closeConnection(localDbConnection);
            remoteDB.closeConnection(remoteDbConnection);
        }
    }


    //Reference for parsing XML taken from: https://www.tutorialspoint.com/java_xml/java_dom_parse_document.html
    private static void readGDDAndInitializeConnections() {
        try {
            File gddXMLFile = new File("GlobalDataDictionary.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document gddParsedXML = dBuilder.parse(gddXMLFile);
            gddParsedXML.getDocumentElement().normalize();

            NodeList databaseList = gddParsedXML.getElementsByTagName("database");

            System.out.println("Reading Global Data Dictionary to Establish Connections to Distributed Database");

            for (int item = 0; item < databaseList.getLength(); item++) {
                Node databaseNode = databaseList.item(item);
                if (databaseNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element databaseElement = (Element) databaseNode;
                    if (databaseElement.getAttribute("type").equalsIgnoreCase("LOCAL")) {
                        String url = databaseElement.getAttribute("url");
                        String port = databaseElement.getAttribute("port");
                        String databaseName = databaseElement.getAttribute("name");
                        String username = databaseElement.getAttribute("username");
                        String password = databaseElement.getAttribute("password");
                        localDB = new LocalDBOperations(url, port, databaseName, username, password);
                    } else if (databaseElement.getAttribute("type").equalsIgnoreCase("REMOTE")) {
                        String url = databaseElement.getAttribute("url");
                        String port = databaseElement.getAttribute("port");
                        String databaseName = databaseElement.getAttribute("name");
                        String username = databaseElement.getAttribute("username");
                        String password = databaseElement.getAttribute("password");
                        remoteDB = new RemoteDBOperations(url, port, databaseName, username, password);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
