import java.util.List;

public class Main {
    public static void main(String[] args) {

        List<String> keywords = List.of("Canada", "Halifax", "hockey", "hurricane", "electricity", "house", "inflation");

        NewsApiHelper newsAPI = new NewsApiHelper();
        NewsExtraction newsExtraction = new NewsExtraction();
        NewsTransformation newsTransformation = new NewsTransformation();
        NewsLoading newsLoader = new NewsLoading();

        for (String keyword : keywords) {
            String apiResponse;

            //get News Articles related to the keywords from NewsAPI. NOTE: Change APIKey here
            apiResponse = newsAPI.callNewsApiToGetArticles(keyword, "431acb67574942b19f2280cbe593fe8a");

            //Extract the API response into new files for each keyword
            newsExtraction.extractNewsToFile(keyword, apiResponse);

            //Transform and clean the extracted data in the files
            newsTransformation.transformAndCleanNewsFile(keyword);
        }

        //Load the data into MongoDB collections
        newsLoader.loadNewsCollection(keywords);
    }
}
