import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class NewsApiHelper {

    //Reference: https://newsapi.org/docs/endpoints/everything
    //Reference: https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html to convert API Response to String
    protected String callNewsApiToGetArticles(String keyword, String apiKey) {
        HttpURLConnection conn = null;
        try {
            String url = "https://newsapi.org/v2/everything?q=\"" + keyword + "\"&language=en";
            URL newsApiEndpointURL = new URL(url);
            conn = (HttpURLConnection) newsApiEndpointURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("X-Api-Key", apiKey);
            conn.setRequestProperty("Connection", "close");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to get NewsAPI response. Error code : " + conn.getResponseCode());
            }

            BufferedReader responseReader = new BufferedReader(new InputStreamReader((conn.getInputStream()), StandardCharsets.UTF_8));

            return responseReader.lines().collect(Collectors.joining());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
}
