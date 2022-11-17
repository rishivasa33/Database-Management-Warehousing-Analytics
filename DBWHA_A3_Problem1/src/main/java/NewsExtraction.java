import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class NewsExtraction {
    static List<NewsModel> newsList;


    public static void main(String[] args) {

        List<String> keywords = List.of("Canada", "Halifax", "hockey", "hurricane", "electricity", "house", "inflation");

        for (String keyword : keywords) {
            newsList = new ArrayList<>();
            //get News Articles related to the keywords from NewsAPI
            getArticles(keyword);
            //Create New File for each keyword
            writeNewsDetailsToTextFile(keyword);
        }
        System.exit(0);
    }

    private static void writeNewsDetailsToTextFile(String keyword) {
        File newsFile;
        FileWriter fileWriter;

        //Adding one-second sleep as the anonymous inner class to fetch News from NewsAPI executes asynchronously
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            newsFile = new File(keyword + ".txt");
            if (newsFile.createNewFile()) {
                System.out.println("New File Created: " + newsFile.getName() + " for Keyword: " + keyword);
            } else {
                fileWriter = new FileWriter(newsFile.getName());
                fileWriter.close();
                System.out.println("File Already Exists. File reset and overwritten for Keyword: " + keyword);
            }
            fileWriter = new FileWriter(newsFile.getName(), true);
            fileWriter.write(newsList.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getArticles(String keyword) {
        NewsApiClient newsApiClient = new NewsApiClient("431acb67574942b19f2280cbe593fe8a");

        newsApiClient.getEverything(
                new EverythingRequest.Builder().q(keyword).build(), new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse response) {
                        String source;
                        for (Article article : response.getArticles()) {
                            source =
                                    "Source{" +
                                            "id='" + article.getSource().getId() + '\'' +
                                            ", name='" + article.getSource().getName() + '\'' +
                                            ", description='" + article.getSource().getDescription() + '\'' +
                                            ", url='" + article.getSource().getUrl() + '\'' +
                                            ", category='" + article.getSource().getCategory() + '\'' +
                                            ", language='" + article.getSource().getLanguage() + '\'' +
                                            ", country='" + article.getSource().getCountry() + '\'' +
                                            '}';
                            NewsModel newsModel = new NewsModel();
                            newsModel.setAuthor(article.getAuthor());
                            newsModel.setTitle(article.getTitle());
                            newsModel.setContent(article.getContent());
                            newsModel.setDescription(article.getContent());
                            newsModel.setUrl(article.getUrl());
                            newsModel.setUrlToImage(article.getUrlToImage());
                            newsModel.setPublishedAt(article.getPublishedAt());
                            newsModel.setSource(source);
                            newsList.add(newsModel);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        System.out.println(throwable.getMessage());
                    }
                }
        );
    }

}
