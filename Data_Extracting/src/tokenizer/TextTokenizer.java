package tokenizer;

import common.ErrorCode;
import common.Globals;
import database.ExtractedText;
import database.IDatabaseConnection;
import database.MySQLDatabaseConnection;
import frontier.ExtractedTextFrontier;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static common.ErrorCode.FAILED;

public class TextTokenizer {
    private static Logger LOG = LogManager.getLogger(TextTokenizer.class
            .getName());

    private IDatabaseConnection m_databaseConnection = null;
    private ExtractedTextFrontier m_frontier = null;

    private static final ExecutorService m_exec = Executors
            .newFixedThreadPool(Globals.NTHREADS);

    private class TokenizerTask implements Runnable {
        private Long threadId;

        private TokenizerTask() {
        }

        @Override
        public void run() {
            threadId = Thread.currentThread().getId();
            LOG.info("Start thread " + this.threadId);
            ErrorCode.CrError hr = ErrorCode.CrError.CR_OK;

            int count = 0;
            while (true) {
                hr = tokenizeOneText();

                if (FAILED(hr)) {
                    LOG.error("Fail to get tokenize extracted text because hr" +
                            " = " + hr);
                }
                count++;
                LOG.info("Process count = " + count);
            }
        }

        private ErrorCode.CrError tokenizeOneText() {
            ErrorCode.CrError hr = ErrorCode.CrError.CR_OK;
            ExtractedText extractedText = new ExtractedText();
            hr = m_frontier.pullExtractedText(extractedText);
            if (FAILED(hr)) {
                return hr;
            }

            try {
                List<String> result = TextTokenizer.tokenizeString
                        (extractedText.getExtractedText(), extractedText
                                .getId());

                for (String word : result) {
                    if (word.matches("[a-z]+")) {
                        m_databaseConnection.createDictionaryWord(word);
                    }
                }
                ExtractedText cleanText = new ExtractedText();
                cleanText.setId(extractedText.getId());
                cleanText.setExtractedText(String.join(",", result));
                m_databaseConnection.createCleanText(cleanText);
            } catch (Exception ex) {
                LOG.error("Fail to tokenize with exception " + ex);
                return ErrorCode.CrError.CR_MALFORM_HTML;
            }

            return hr;
        }

    }

    public static List<String> tokenizeString(String text, int id) throws
            IOException {
        LOG.info("Tokenize extracted text with id " + id + " and content " +
                "length = " + text.length());
        Analyzer analyzer = new StandardAnalyzer(Globals.STOPWORDS);
        TokenStream stream = analyzer.tokenStream(null, new
                StringReader(text));
        stream.reset();
        List<String> result = new ArrayList<>();
        while (stream.incrementToken()) {
            final String word = stream.getAttribute(CharTermAttribute
                    .class).toString();
            result.add(word);
        }
        LOG.info("Tokenize = " + result);
        return result;
    }

    public TextTokenizer(String username, String password, String server,
                         String database) throws ClassNotFoundException,
            SQLException {
        LOG.setLevel(Level.ALL);
        m_databaseConnection = new MySQLDatabaseConnection(username,
                password, server, database);
        m_frontier = new ExtractedTextFrontier(m_databaseConnection);
    }

    public ErrorCode.CrError tokenizeText() {
        for (int i = 0; i < Globals.NTHREADS; ++i) {
            m_exec.execute(new TokenizerTask());
        }

        m_exec.shutdown();

        try {
            m_exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }

        return ErrorCode.CrError.CR_OK;
    }

    public static void main(String[] args) {
        new Globals();

        try {
            TextTokenizer tokenizer = new TextTokenizer(Globals.username,
                    Globals.password, Globals.server, Globals.database);

            // Only return when error happens. Otherwise, while true loop
            ErrorCode.CrError hr = tokenizer.tokenizeText();

            if (FAILED(hr)) {
                LOG.error("Extract fails, hr = " + hr);
            }
        } catch (ClassNotFoundException e) {
            LOG.error(e.getMessage());
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
    }
}
