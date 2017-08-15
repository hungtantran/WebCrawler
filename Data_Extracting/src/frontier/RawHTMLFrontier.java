package frontier;

import common.ErrorCode;
import common.Globals;
import database.IDatabaseConnection;
import database.RawHTML;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RawHTMLFrontier implements IFrontier {
    private List<RawHTML> m_backEndQueues = new ArrayList<RawHTML>();
    private IDatabaseConnection m_databaseConnection = null;

    public RawHTMLFrontier(IDatabaseConnection databaseConnection) {
        m_databaseConnection = databaseConnection;
    }

    public ErrorCode.CrError pullRawHTML(RawHTML rawHTML) {
        synchronized (m_backEndQueues) {
            if (m_backEndQueues.isEmpty()) {
                try {
                    m_backEndQueues = m_databaseConnection.getNonExtractedTextRawHTML(0, Globals.NTHREADS * 10);
                } catch (SQLException ex) {
                    return ErrorCode.CrError.CR_DATABASE_ERROR;
                }
            }

            RawHTML rawHTMLSource = m_backEndQueues.remove(0);
            rawHTML.setId(rawHTMLSource.getId());
            rawHTML.setHtml(rawHTMLSource.getHtml());
        }

        return ErrorCode.CrError.CR_OK;
    }
}
