import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NewsExtraction {

    public static void main(String[] args) {

        List<String> keywords = List.of("Canada", "Halifax", "hockey", "hurricane", "electricity", "house", "inflation");
        NewsTransformation newsTransformation = new NewsTransformation();
        NewsApiHelper newsAPI = new NewsApiHelper();
        for (String keyword : keywords) {
            String apiResponse;
            File rawNewsFile;

            //get News Articles related to the keywords from NewsAPI. NOTE: Change APIKey here
            apiResponse = newsAPI.callNewsApiToGetArticles(keyword, "431acb67574942b19f2280cbe593fe8a");

            //Extract the API response into new files for each keyword
            rawNewsFile = extractNewsToFile(keyword, apiResponse);

            //Transform and clean the extracted data in the files
            newsTransformation.transformAndCleanNewsFile(keyword, rawNewsFile);
        }

        //Load the data into MongoDB collections
        NewsLoading newsLoader = new NewsLoading();
        newsLoader.loadNewsCollection(keywords);
    }

    private static File extractNewsToFile(String keyword, String apiResponse) {
        //Reference: https://stackoverflow.com/questions/35132693/set-encoding-as-utf-8-for-a-filewriter to resolve issue of MalformedInputException while loading data to MongoDB
        File newsFile = null;
        Writer fileWriter;

        try {
            newsFile = new File(keyword + ".json");
            if (newsFile.createNewFile()) {
                System.out.println("New File Created: " + newsFile.getName() + " for Keyword: " + keyword);
            } else {
                fileWriter = new OutputStreamWriter(new FileOutputStream(newsFile.getName()), StandardCharsets.UTF_8);
                fileWriter.close();
                System.out.println("File Already Exists. File reset and overwritten for Keyword: " + keyword);
            }
            fileWriter = new OutputStreamWriter(new FileOutputStream(newsFile.getName()), StandardCharsets.UTF_8);
            fileWriter.write(apiResponse);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newsFile;
    }

}
