package extractor;

import common.ErrorCode;
import common.Globals;
import database.ExtractedText;
import database.IDatabaseConnection;
import database.MySQLDatabaseConnection;
import database.RawHTML;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import frontier.RawHTMLFrontier;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static common.ErrorCode.FAILED;

public class TextExtractor {
    private static Logger LOG = LogManager.getLogger(TextExtractor.class
            .getName());

    private IDatabaseConnection m_databaseConnection = null;
    private RawHTMLFrontier m_frontier = null;

    private static final ExecutorService m_exec = Executors
            .newFixedThreadPool(Globals.NTHREADS);

    private class ExtractTask implements Runnable {
        private Long threadId;

        private ExtractTask() {
        }

        @Override
        public void run() {
            threadId = Thread.currentThread().getId();
            LOG.info("Start thread " + this.threadId);
            ErrorCode.CrError hr = ErrorCode.CrError.CR_OK;

            while (true) {
                hr = extractOneText();

                if (FAILED(hr)) {
                    LOG.error("Fail to get extract page because hr = " + hr);
                }
            }
        }

        private ErrorCode.CrError extractOneText() {
            ErrorCode.CrError hr = ErrorCode.CrError.CR_OK;
            RawHTML rawHTML = new RawHTML();
            hr = m_frontier.pullRawHTML(rawHTML);
            if (FAILED(hr)) {
                return hr;
            }

            try {
                ExtractedText extractedText = TextExtractor.extractedText
                        (rawHTML.getHtml(), rawHTML.getId());

                m_databaseConnection.createExtractedText(extractedText);
            } catch (Exception ex) {
                LOG.error("Fail to extract article from html with exception "
                        + ex);
                return ErrorCode.CrError.CR_MALFORM_HTML;
            }

            return hr;
        }
    }

    public static ExtractedText extractedText(String html, int id) throws
            Exception {
        LOG.info("Extract text for rawhtml with id " + id + " and content " +
                "length = " + html.length());
        InputSource inputSource = new InputSource(new StringReader(html));

        final BoilerpipeSAXInput in = new BoilerpipeSAXInput
                (inputSource);
        final TextDocument doc = in.getTextDocument();
        String bodyText = ArticleExtractor.INSTANCE.getText(doc);

        ExtractedText extractedText = new ExtractedText();
        extractedText.setId(id);
        extractedText.setExtractedText(bodyText);
        LOG.info("Extract text for id " + extractedText.getId() + " get body " +
                "length = " + bodyText.length());
        return extractedText;
    }

    public TextExtractor(String username, String password, String server,
                         String database) throws ClassNotFoundException,
            SQLException {
        LOG.setLevel(Level.ALL);
        m_databaseConnection = new MySQLDatabaseConnection(username,
                password, server, database);
        m_frontier = new RawHTMLFrontier(m_databaseConnection);
    }

    public ErrorCode.CrError extractText() {
        for (int i = 0; i < Globals.NTHREADS; ++i) {
            m_exec.execute(new ExtractTask());
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
            TextExtractor extractor = new TextExtractor(Globals.username,
                    Globals.password, Globals.server, Globals.database);

            // Only return when error happens. Otherwise, while true loop
            ErrorCode.CrError hr = extractor.extractText();

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
