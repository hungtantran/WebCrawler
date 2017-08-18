package frontier;

import common.ErrorCode;
import common.Globals;
import database.ExtractedText;
import database.IDatabaseConnection;
import database.RawHTML;
import naivebayes.ParamCalculator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CleanExtractedTextFrontier {
    private static Logger LOG = LogManager.getLogger(ParamCalculator.class
            .getName());

    private List<ExtractedText> m_backEndQueues = new
            ArrayList<ExtractedText>();
    private IDatabaseConnection m_databaseConnection = null;
    private int m_totalCount = 0;
    private int m_lowerBound = 0;
    private int m_maxCount = 0;

    public CleanExtractedTextFrontier(IDatabaseConnection databaseConnection,
                                      int lowerBound, int maxCount) {
        LOG.setLevel(Level.ALL);
        m_databaseConnection = databaseConnection;
        m_lowerBound = lowerBound;
        m_maxCount = maxCount;
        LOG.info("Starting lowerbound = " + m_lowerBound + ", maxCount = " +
                m_maxCount);
    }

    public ErrorCode.CrError pullCleanExtractedText(ExtractedText
                                                            extractedText) {
        synchronized (m_backEndQueues) {
            if (m_backEndQueues.isEmpty()) {
                try {
                    int lowerBound = m_lowerBound + m_totalCount;
                    int count = Globals.NTHREADS * 10;
                    if (count > m_maxCount - m_totalCount) {
                        count = Math.max(m_maxCount - m_totalCount, 0);
                    }
                    m_backEndQueues = m_databaseConnection
                            .getCleanExtractedText(lowerBound, count);
                    m_totalCount += count;
                    LOG.info("LowerBound = " + lowerBound + ", count = " +
                            count);
                } catch (SQLException ex) {
                    return ErrorCode.CrError.CR_DATABASE_ERROR;
                }
            }

            ExtractedText extractedTextSource = m_backEndQueues.remove(0);
            extractedText.setId(extractedTextSource.getId());
            extractedText.setExtractedText(extractedTextSource
                    .getExtractedText());
        }

        return ErrorCode.CrError.CR_OK;
    }
}
