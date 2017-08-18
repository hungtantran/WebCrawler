package naivebayes;

import common.*;
import database.ExtractedText;
import database.IDatabaseConnection;
import database.MySQLDatabaseConnection;
import database.NaiveBayesParam;
import extractor.TextExtractor;
import httpFetcher.HttpFetcher;
import httpFetcher.IHttpFetcher;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tokenizer.TextTokenizer;

import java.sql.SQLException;
import java.util.*;

public class Predictor {
    private static Logger LOG = LogManager.getLogger(Predictor.class.getName());
    private IDatabaseConnection m_databaseConnection;
    private IHttpFetcher m_httpFetcher;

    private Map<String, Double> m_wordToProb0;
    private Map<String, Double> m_wordToProb1;
    private Double m_prob1;
    private Double m_prob0;

    public Predictor(String username, String password, String server,
                     String database) throws ClassNotFoundException,
            SQLException {
        LOG.setLevel(Level.ALL);
        m_databaseConnection = new MySQLDatabaseConnection(username,
                password, server, database);
        m_httpFetcher = new HttpFetcher();
        this.calParam();
    }

    private void calParam() throws SQLException {
        int num0 = m_databaseConnection.getCount("clean_extracted_text_table_2");
        int num1 = m_databaseConnection.getCount("clean_extracted_text_table_1");
        this.m_prob1 = (double)num1/(num0 + num1);
        this.m_prob0 = (double)num0/(num0 + num1);

        List<ExtractedText> words = m_databaseConnection.getDictionaryWords();
        Map<Integer, String> wordToId = new HashMap<>();
        for (ExtractedText word : words) {
            wordToId.put(word.getId(), word.getExtractedText());
        }

        Map<Integer, Integer> params0WordToCount = new HashMap<>();
        List<NaiveBayesParam> params0 = m_databaseConnection.getNaiveBayesParam(0);
        for (NaiveBayesParam param : params0) {
            params0WordToCount.put(param.getWord(), param.getCount());
        }
        for (Map.Entry<Integer, String> entry : wordToId.entrySet()) {
            Double prob = (double)1 / (num0 + 2);
            if (params0WordToCount.containsKey(entry.getKey())) {
                prob = (double)(params0WordToCount.get(entry.getKey()) + 1) / (num0 + 2);
            }
            this.m_wordToProb0.put(entry.getValue(), prob);
        }

        Map<Integer, Integer> params1WordToCount = new HashMap<>();
        List<NaiveBayesParam> params1 = m_databaseConnection.getNaiveBayesParam(1);
        for (NaiveBayesParam param : params1) {
            params1WordToCount.put(param.getWord(), param.getCount());
        }
        for (Map.Entry<Integer, String> entry : wordToId.entrySet()) {
            Double prob = (double)1 / (num1 + 2);
            if (params1WordToCount.containsKey(entry.getKey())) {
                prob = (double)(params1WordToCount.get(entry.getKey()) + 1) / (num1 + 2);
            }
            this.m_wordToProb1.put(entry.getValue(), prob);
        }
    }

    public int Predict(String url) throws Exception {
        IWebPage webPage = new WebPage();
        URLObject urlObject = new URLObject();
        urlObject.setLink(url);

        try {
            ErrorCode.CrError hr = m_httpFetcher.getWebPage(urlObject, webPage);
            if (hr != ErrorCode.CrError.CR_OK) {
                LOG.error("Fail to download link " + url + " with error " + hr);
                return -1;
            }
        } catch (Exception e) {
            LOG.error("Fail to download link " + url + " with error " + e
                    .getMessage());
            return -1;
        }

        return PredictHtml(webPage.getString());
    }

    public int PredictHtml(String html) throws Exception {
        ExtractedText extractedText = TextExtractor.extractedText(html, 0);
        List<String> tokenizeString = TextTokenizer.tokenizeString
                (extractedText.getExtractedText(), extractedText.getId());
        double prob0 = this.m_prob0;
        double prob1 = this.m_prob1;
        Set<String> words = new HashSet<>(tokenizeString);
        for (String word : words) {
            if (this.m_wordToProb0.containsKey(word)) {
                prob0 *= this.m_wordToProb0.get(word);
                prob1 *= this.m_wordToProb1.get(word);
            }
        }
        if (prob0 > prob1) {
            return 0;
        }

        return 1;
    }

    public static void main(String[] args) {
        new Globals();

        try {
            Predictor predictor = new Predictor(Globals.username,
                    Globals.password, Globals.server, Globals.database);

            // Only return when error happens. Otherwise, while true loop
            int result = predictor.Predict("https://www.theatlantic" +
                    ".com/politics/archive/2017/08/congress-confederate" +
                    "-statues-capitol/537276/");
            LOG.info("result = " + result);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}
