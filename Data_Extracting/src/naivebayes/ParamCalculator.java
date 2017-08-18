package naivebayes;

import common.ErrorCode;
import common.Globals;
import database.ExtractedText;
import database.IDatabaseConnection;
import database.MySQLDatabaseConnection;
import database.NaiveBayesParam;
import frontier.CleanExtractedTextFrontier;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static common.ErrorCode.FAILED;

public class ParamCalculator {
    private static Logger LOG = LogManager.getLogger(ParamCalculator.class
            .getName());

    private IDatabaseConnection m_databaseConnection = null;
    private CleanExtractedTextFrontier m_frontier = null;
    private Map<Integer, Integer> m_params = null;
    private Map<String, ExtractedText> m_wordToDictionaryWord = null;

    private static final ExecutorService m_exec = Executors
            .newFixedThreadPool(Globals.NTHREADS);

    private class CalTask implements Runnable {
        private Long threadId;

        private CalTask() {
        }

        @Override
        public void run() {
            threadId = Thread.currentThread().getId();
            LOG.info("Start thread " + this.threadId);
            ErrorCode.CrError hr = ErrorCode.CrError.CR_OK;

            int count = 0;
            while (true) {
                hr = calOneText();

                if (FAILED(hr)) {
                    LOG.error("Fail to calculate param for a text because hr " +
                            "= " + hr);
                }
                count++;
                LOG.info("Process count = " + count);
            }
        }

        private ErrorCode.CrError calOneText() {
            ErrorCode.CrError hr = ErrorCode.CrError.CR_OK;
            ExtractedText extractedText = new ExtractedText();
            hr = m_frontier.pullCleanExtractedText(extractedText);
            if (FAILED(hr)) {
                return hr;
            }

            LOG.info("Cal param extracted text with id " + extractedText
                    .getId() + " and content length = " + extractedText
                    .getExtractedText().length());

            try {
                String[] texts = extractedText.getExtractedText().split(",");
                // Calculate params locally for this particular text
                Map<Integer, Integer> params = new HashMap<>();
                for (String text : texts) {
                    if (m_wordToDictionaryWord.containsKey(text)) {
                        int wordId = m_wordToDictionaryWord.get(text).getId();
                        int freq = 1;
                        params.put(wordId, freq);
                    }
                }
                // Merge the local param with the global param
                synchronized (m_params) {
                    for (Map.Entry<Integer, Integer> entry : params.entrySet
                            ()) {
                        int freq = entry.getValue();
                        if (m_params.containsKey(entry.getKey())) {
                            freq += m_params.get(entry.getKey());
                        }
                        m_params.put(entry.getKey(), freq);
                    }
                }
            } catch (Exception ex) {
                LOG.error("Fail to calculate param with exception " + ex);
                return ErrorCode.CrError.CR_MALFORM_HTML;
            }

            return hr;
        }
    }

    private void populateDictionaryWord() throws SQLException {
        List<ExtractedText> words = m_databaseConnection.getDictionaryWords();
        for (ExtractedText word : words) {
            m_wordToDictionaryWord.put(word.getExtractedText(), word);
        }
    }

    public ParamCalculator(String username, String password, String server,
                           String database) throws ClassNotFoundException,
            SQLException {
        LOG.setLevel(Level.ALL);
        m_databaseConnection = new MySQLDatabaseConnection(username,
                password, server, database);
        m_frontier = new CleanExtractedTextFrontier(m_databaseConnection,
                /*lowerBound=*/0, /*maxCount=*/2000);
        m_params = new HashMap<>();
        m_wordToDictionaryWord = new HashMap<>();
    }

    public ErrorCode.CrError calParam() {
        try {
            this.populateDictionaryWord();
        } catch (SQLException e) {
            LOG.error("Fail to populate dictionary words because " + e
                    .getMessage());
        }

        for (int i = 0; i < Globals.NTHREADS; ++i) {
            m_exec.execute(new CalTask());
        }

        m_exec.shutdown();

        try {
            m_exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }

        LOG.info("Start inserting param");
        for (Map.Entry<Integer, Integer> entry : m_params.entrySet()) {
            NaiveBayesParam param = new NaiveBayesParam();
            param.setWord(entry.getKey());
            param.setCount(entry.getValue());
            // TODO: make this general
            param.setLabel(1);
            try {
                m_databaseConnection.createNaiveBayesParam(param);
            } catch (SQLException e) {
                LOG.error("Fail to create param " + param.toString() + " " +
                        "because " + e.getMessage());
            }
        }

        return ErrorCode.CrError.CR_OK;
    }

    public static void main(String[] args) {
        new Globals();

        try {
            ParamCalculator tokenizer = new ParamCalculator(Globals.username,
                    Globals.password, Globals.server, Globals.database);

            // Only return when error happens. Otherwise, while true loop
            ErrorCode.CrError hr = tokenizer.calParam();

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
