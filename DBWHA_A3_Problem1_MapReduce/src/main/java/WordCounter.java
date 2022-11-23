import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

public class WordCounter {

    //References: https://cloud.google.com/dataproc/docs/tutorials/gcs-connector-spark-tutorial
    //References: https://sparkbyexamples.com/spark/spark-word-count-example/
    @SuppressWarnings("resource")
    public static void main(String[] args) {

        //Note Change filePath here to point to your GCP Bucket file URI
        List<String> keywords = Arrays.asList("Canada", "Halifax", "hockey", "hurricane", "electricity", "house", "inflation");
        String filePath = "gs://dataproc-temp-us-central1-221534242827-5m4nqwcq/";

        //Initialize SparkContext and use as many worker nodes as logical cores on the machine: https://sparkbyexamples.com/spark/what-does-setmaster-local-mean-in-spark/
        JavaSparkContext sparkContext = new JavaSparkContext(new SparkConf().setAppName("News Word Counter").setMaster("local[*]"));

        //Ignore INFO and WARN Logs
        sparkContext.setLogLevel("ERROR");

        for (String fileKeyword : keywords) {
            System.out.println("\nWordCount for file: " + fileKeyword + ".json");

            //Read each file and map the contents to words (RDD: Resilient Distributed Dataset)
            JavaRDD<String> lines = sparkContext.textFile(filePath + fileKeyword + ".json");
            JavaRDD<String> words = lines.flatMap(
                    (String line) -> Arrays.asList(line.split(" ")).iterator()
            );

            for (String keywordToSearch : keywords) {

                // Filter(words that equal to search keyword)
                // Map(create map of each word as key and initialize count as 1)
                // Reduce(calculate sum) to find word count of each keyword in each file
                JavaPairRDD<String, Integer> wordCounts = words
                        .filter((String word) -> word.equalsIgnoreCase(keywordToSearch))
                        .mapToPair((String word) -> new Tuple2<>(word, 1))
                        .reduceByKey(Integer::sum);

                //Print word count result
                wordCounts.foreach(data -> System.out.println("word: " + data._1() + "\t\t count: " + data._2()));
            }
        }
    }
}
