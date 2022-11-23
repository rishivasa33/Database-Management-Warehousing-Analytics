import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class NewsExtraction {

    //Reference: https://stackoverflow.com/questions/35132693/set-encoding-as-utf-8-for-a-filewriter to resolve issue of MalformedInputException while loading data to MongoDB
    protected void extractNewsToFile(String keyword, String apiResponse) {
        File newsFile;
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
    }

}
