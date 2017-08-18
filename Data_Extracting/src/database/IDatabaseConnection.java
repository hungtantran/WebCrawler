package database;

import java.sql.SQLException;
import java.util.List;

public interface IDatabaseConnection {
    List<RawHTML> getNonExtractedTextRawHTML(int lowerBound, int
            maxNumResult) throws SQLException;

    List<ExtractedText> getNonCleanTextExtractedText(int lowerBound, int
            maxNumResult) throws SQLException;

    int createDictionaryWord(String word) throws SQLException;

    int createExtractedText(ExtractedText extractedText) throws SQLException;

    int createCleanText(ExtractedText extractedText) throws SQLException;

    int createNaiveBayesParam(NaiveBayesParam param) throws SQLException;

    List<ExtractedText> getCleanExtractedText(int lowerBound, int
            maxNumResult) throws SQLException;

    List<ExtractedText> getDictionaryWords() throws SQLException;

    List<NaiveBayesParam> getNaiveBayesParam() throws SQLException;

    List<NaiveBayesParam> getNaiveBayesParam(int label) throws SQLException;

    int getCount(String tableName) throws SQLException;
}
