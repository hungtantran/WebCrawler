package database;

import java.sql.SQLException;
import java.util.List;

public interface IDatabaseConnection {
    List<RawHTML> getNonExtractedTextRawHTML(int lowerBound, int maxNumResult) throws SQLException;

    int createExtractedText(ExtractedText extractedText) throws SQLException;
}
