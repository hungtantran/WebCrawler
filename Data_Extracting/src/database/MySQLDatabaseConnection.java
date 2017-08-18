package database;

import common.Globals;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MySQLDatabaseConnection implements IDatabaseConnection {
    private static Logger LOG = LogManager.getLogger(MySQLDatabaseConnection
            .class.getName());

    private RawHTMLDAO m_rawHTMLDAO = null;
    private ExtractedTextDAO m_extractedTextDAO = null;
    private ExtractedTextDAO m_cleanExtractedTextDAO = null;
    private ExtractedTextDAO m_dictionaryDAO = null;
    private NaiveBayesParamDAO m_naiveBayesParamDAO = null;

    private String m_extractedTextTableName = "extracted_text_table";
    private String m_cleanExtractedTextTableName = "clean_extracted_text_table";
    private String m_dictionaryTableName = "dictionary_table";
    private String m_rawhtmlTableName = "rawhtml_table";

    private DAOFactory m_daoFactory;
    private final String SQL_SELECT_COUNT = "SELECT count(*) as count FROM %s";

    public MySQLDatabaseConnection(String username, String password, String
            server, String database) throws SQLException,
            ClassNotFoundException {
        LOG.setLevel(Level.ALL);
        this.m_daoFactory = DAOFactory.getInstance(username, password,
                server + database);

        this.m_extractedTextTableName = Globals.extractedTextTableName;
        this.m_cleanExtractedTextTableName = Globals
                .cleanExtractedTextTableName;
        this.m_dictionaryTableName = Globals.dictionaryTableName;
        this.m_rawhtmlTableName = Globals.rawhtmlTableName;

        this.m_rawHTMLDAO = new RawHTMLDAOJDBC(this.m_daoFactory, this
                .m_rawhtmlTableName, this.m_extractedTextTableName);
        this.m_extractedTextDAO = new ExtractedTextDAOJDBC(this.m_daoFactory,
                this.m_extractedTextTableName);
        this.m_cleanExtractedTextDAO = new ExtractedTextDAOJDBC(this
                .m_daoFactory, this.m_cleanExtractedTextTableName);
        this.m_dictionaryDAO = new ExtractedTextDAOJDBC(this.m_daoFactory, this
                .m_dictionaryTableName);
        this.m_naiveBayesParamDAO = new NaiveBayesParamDAOJDBC(this
                .m_daoFactory);
    }

    @Override
    public List<RawHTML> getNonExtractedTextRawHTML(int lowerBound, int
            maxNumResult) throws SQLException {
        return this.m_rawHTMLDAO.getNonextractedTextRawHTML(lowerBound,
                maxNumResult);
    }

    @Override
    public int createExtractedText(ExtractedText extractedText) throws
            SQLException {
        return this.m_extractedTextDAO.create(extractedText);
    }

    @Override
    public int createCleanText(ExtractedText extractedText) throws
            SQLException {
        return this.m_cleanExtractedTextDAO.create(extractedText);
    }

    @Override
    public int createNaiveBayesParam(NaiveBayesParam param) throws
            SQLException {
        return this.m_naiveBayesParamDAO.create(param);
    }

    @Override
    public int createDictionaryWord(String word) throws SQLException {
        ExtractedText extractedText = new ExtractedText();
        extractedText.setExtractedText(word);
        return this.m_dictionaryDAO.createIncrement(extractedText);
    }

    @Override
    public List<ExtractedText> getNonCleanTextExtractedText(int lowerBound,
                                                            int maxNumResult)
            throws SQLException {
        return this.m_extractedTextDAO.getExtactedTextNotIdInTable
                (lowerBound, maxNumResult, this.m_cleanExtractedTextTableName);
    }

    @Override
    public List<ExtractedText> getCleanExtractedText(int lowerBound, int
            maxNumResult) throws SQLException {
        return this.m_cleanExtractedTextDAO.get(lowerBound, maxNumResult);
    }

    @Override
    public List<ExtractedText> getDictionaryWords() throws SQLException {
        return this.m_dictionaryDAO.get();
    }

    @Override
    public List<NaiveBayesParam> getNaiveBayesParam() throws SQLException {
        return this.m_naiveBayesParamDAO.get();
    }

    @Override
    public List<NaiveBayesParam> getNaiveBayesParam(int label) throws
            SQLException {
        return this.m_naiveBayesParamDAO.get(label);
    }

    @Override
    public int getCount(String tableName) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.m_daoFactory.getConnection();
            preparedStatement = DAOUtil.prepareStatement(connection, String
                    .format(this.SQL_SELECT_COUNT, tableName), false);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("count");
            }

            return -1;
        } catch (final SQLException e) {
            LOG.error("Get count for table " + tableName + " fails, " + e
                    .getMessage());
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }

        return -1;
    }
}
