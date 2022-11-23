import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.InsertManyResult;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class NewsLoading {

    private static MongoClient mongoClient = null;
    private static MongoDatabase bigMongoNewsDB = null;

    protected void loadNewsCollection(List<String> keywords) {
        try {
            //Establish Single Shared MongoDB Connection to save resources
            bigMongoNewsDB = openMongoConnectionAndReturnDatabase();

            for (String keyword : keywords) {
                try {
                    File newsFile = new File(keyword + ".json");
                    List<String> newsFileContentList = Files.readAllLines(newsFile.toPath());

                    //Create New Collection of the keyword, or replace if already exists
                    MongoCollection<Document> mongoCollection = createOrReplaceMongoCollection(keyword);

                    insertNewsCollectionToMongoDB(keyword, newsFileContentList, mongoCollection);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeMongoCollection();
        }
    }


    //Reference: https://www.mongodb.com/docs/drivers/java/sync/upcoming/quick-start/
    private static MongoDatabase openMongoConnectionAndReturnDatabase() {
        //NOTE: Change Username, Password and HostName/ClusterName here
        String mongoUsername = "mongo_root";
        String mongoPassword = "mongo_rootRV33";
        String mongoHostName = "dbwha-a3-problem1";
        String mongoURLString = "mongodb+srv://" + mongoUsername + ":" + mongoPassword + "@" + mongoHostName + ".wcvoewc.mongodb.net/?retryWrites=true&w=majority";

        try {
            ConnectionString connectionString = new ConnectionString(mongoURLString);

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .serverApi(ServerApi.builder()
                            .version(ServerApiVersion.V1)
                            .build())
                    .build();
            mongoClient = MongoClients.create(settings);
            bigMongoNewsDB = mongoClient.getDatabase("BigMongoNews");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bigMongoNewsDB;
    }

    private static MongoCollection<Document> createOrReplaceMongoCollection(String keyword) {
        MongoCollection<Document> mongoCollection;
        //Drop Collection if it already exists for the keyword
        MongoIterable<String> collectionList = bigMongoNewsDB.listCollectionNames();
        for (String collectionName : collectionList) {
            if (collectionName.equalsIgnoreCase(keyword)) {
                bigMongoNewsDB.getCollection(keyword).drop();
            }
        }

        //Create new collection for the keyword
        bigMongoNewsDB.createCollection(keyword);
        mongoCollection = bigMongoNewsDB.getCollection(keyword);
        return mongoCollection;
    }

    private static void insertNewsCollectionToMongoDB(String keyword, List<String> newsFileStringList, MongoCollection<Document> mongoCollection) {
        //Reference: https://www.mongodb.com/docs/drivers/java/sync/current/usage-examples/insertMany/
        try {
            //Convert JSON to MongoDB Document
            List<Document> newsFileDocumentList = new ArrayList<>();
            for (String json : newsFileStringList) {
                newsFileDocumentList.add(Document.parse(json));
            }

            InsertManyResult result = mongoCollection.insertMany(newsFileDocumentList);
            System.out.println("Inserted document with ID: " + result.getInsertedIds() + " for keyword: " + keyword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void closeMongoCollection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

}
