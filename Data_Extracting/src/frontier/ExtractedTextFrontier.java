package frontier;

import common.ErrorCode;
import common.Globals;
import database.ExtractedText;
import database.IDatabaseConnection;
import database.RawHTML;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExtractedTextFrontier {
    private List<ExtractedText> m_backEndQueues = new
            ArrayList<ExtractedText>();
    private IDatabaseConnection m_databaseConnection = null;

    public ExtractedTextFrontier(IDatabaseConnection databaseConnection) {
        m_databaseConnection = databaseConnection;
    }

    public ErrorCode.CrError pullExtractedText(ExtractedText extractedText) {
        synchronized (m_backEndQueues) {
            if (m_backEndQueues.isEmpty()) {
                try {
                    m_backEndQueues = m_databaseConnection
                            .getNonCleanTextExtractedText(0, Globals.NTHREADS
                                    * 10);
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
