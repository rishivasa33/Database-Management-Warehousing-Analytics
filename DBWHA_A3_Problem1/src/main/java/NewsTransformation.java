import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class NewsTransformation {

    //Reference: https://medium.com/factory-mind/regex-tutorial-a-simple-cheatsheet-by-examples-649dc1c3f285
    //Reference: https://regex101.com/
    //Reference: https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
    public void transformAndCleanNewsFile(String keyword) {


        Writer fileWriter;
        String newsFileContent;
        Path path = Paths.get(keyword + ".json");
        try {
            newsFileContent = Files.readString(path, StandardCharsets.UTF_8);

            //Replace the status tag as it is not required to be stored in the DB
            Pattern regexForStatusTag = Pattern.compile("\"status\":\"ok\",", Pattern.CASE_INSENSITIVE);
            newsFileContent = regexForStatusTag.matcher(newsFileContent).replaceAll("");

            //Replace [+xyz chars] in the content tag
            Pattern regexForContentCharacters = Pattern.compile("\\[\\+[0-9]* chars\\]", Pattern.CASE_INSENSITIVE);
            newsFileContent = regexForContentCharacters.matcher(newsFileContent).replaceAll("");

            //Replace URL values in url and urlToImage tags
            Pattern regexForURLKeyValues = Pattern.compile("((\"url\":\")|(\"urlToImage\":\"))(?:(?:https?):\\/\\/|www\\.)(?:\\([-A-Z0-9+&@#\\/%=~_|$?!:;,.]*\\)|[-A-Z0-9+&@#\\/%=~_|$?!:;,.])*(?:\\([-A-Z0-9+&@#\\/%=~_|$?!:;,.]*\\)|[A-Z0-9+&@#\\/%=~_|$])\",", Pattern.CASE_INSENSITIVE);
            newsFileContent = regexForURLKeyValues.matcher(newsFileContent).replaceAll("");

            //Replace All other URL values which can be present in other tags like content and description
            Pattern regexForAllOtherURLs = Pattern.compile("(?:(?:https?):\\/\\/|www\\.)(?:\\([-A-Z0-9+&@#\\/%=~_|$?!:;,.]*\\)|[-A-Z0-9+&@#\\/%=~_|$?!:;,.])*(?:\\([-A-Z0-9+&@#\\/%=~_|$?!:;,.]*\\)|[A-Z0-9+&@#\\/%=~_|$])", Pattern.CASE_INSENSITIVE);
            newsFileContent = regexForAllOtherURLs.matcher(newsFileContent).replaceAll("");

            //Replace values which are "null" with ""
            Pattern regexForNullValues = Pattern.compile("(?<=\":)null(?=,\")", Pattern.CASE_INSENSITIVE);
            newsFileContent = regexForNullValues.matcher(newsFileContent).replaceAll("\"\"");

            //Replace emoticons like :P :D :lol: :smile: :HAHA:
            Pattern regexForEmoticons = Pattern.compile("(:[a-zA-Z]+:)|(:[A-Z])", Pattern.CASE_INSENSITIVE);
            newsFileContent = regexForEmoticons.matcher(newsFileContent).replaceAll("");

            //Replace Special Characters except ' " { } [ ] ( ), . : ; _ = / \ and spaces as they are crucial
            Pattern regexForSpecialCharacters = Pattern.compile("[^A-Za-z0-9\s'\"\\[\\]\\{\\},.:;\\-\\/\\\\_\\(\\)=]", Pattern.CASE_INSENSITIVE);
            newsFileContent = regexForSpecialCharacters.matcher(newsFileContent).replaceAll("");

            //Overwrite file content with clean data
            fileWriter = new OutputStreamWriter(new FileOutputStream(keyword + ".json"), StandardCharsets.UTF_8);
            fileWriter.write(newsFileContent);
            fileWriter.close();
            System.out.println("File Cleaned and overwritten for Keyword: " + keyword);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
