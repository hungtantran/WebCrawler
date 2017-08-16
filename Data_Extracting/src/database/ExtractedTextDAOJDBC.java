package database;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExtractedTextDAOJDBC implements ExtractedTextDAO {
    private static Logger LOG = LogManager.getLogger(ExtractedTextDAOJDBC.class.getName());

    private final String SQL_SELECT_BY_ID = "SELECT * FROM %s WHERE id = ?";
    private final String SQL_SELECT_NON_EXIST_ID_WITH_LIMIT = "SELECT * FROM %s WHERE id NOT IN (SELECT id FROM %s) LIMIT ?, ?";
    private final String SQL_INSERT = "INSERT INTO %s (id, extracted_text) values (?, ?)";
    private final String SQL_INSERT_INCREMENT = "INSERT IGNORE INTO %s (extracted_text) values (?)";
    private final String SQL_UPDATE = "UPDATE %s SET extracted_text = ? WHERE id = ?";
    String tableName = null;

    private final DAOFactory daoFactory;

    public ExtractedTextDAOJDBC(DAOFactory daoFactory, String tableName) throws SQLException {
        this.tableName = tableName;
        this.daoFactory = daoFactory;
    }

    private ExtractedText constructExtractedTextObject(ResultSet resultSet) throws SQLException {
        ExtractedText extractedText = new ExtractedText();
        extractedText.setId(resultSet.getInt("id"));
        extractedText.setExtractedText(resultSet.getString("extracted_text"));
        return extractedText;
    }

    @Override
    public ExtractedText get(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.daoFactory.getConnection();
            preparedStatement = DAOUtil.prepareStatement(connection, String.format(this.SQL_SELECT_BY_ID, this.tableName), false, id);
            resultSet = preparedStatement.executeQuery();

            ExtractedText extractedText = null;
            if (resultSet.next()) {
                extractedText = this.constructExtractedTextObject(resultSet);
            }

            return extractedText;
        } catch (final SQLException e) {
            LOG.error("Get ExtractedText fails, " + e.getMessage());
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }

        return null;
    }

    @Override
    public int create(ExtractedText extractedText) throws SQLException {
        if (extractedText == null || !extractedText.isValid()) {
            return -1;
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.daoFactory.getConnection();

            final Object[] values = {extractedText.getId(), extractedText.getExtractedText()};

            preparedStatement = DAOUtil.prepareStatement(connection, String.format(this.SQL_INSERT, this.tableName), true, values);

            LOG.info("INSERT INTO ExtractedText " + extractedText.getId() + ", Content Length = " + extractedText.getExtractedText().length());

            preparedStatement.executeUpdate();

            return extractedText.getId();
        } catch (final SQLException e) {
            LOG.error("Insert into table ExtractedText fails, " + e.getMessage());
            return -1;
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public int createIncrement(ExtractedText extractedText) throws SQLException {
        if (extractedText == null || !extractedText.isValid()) {
            return -1;
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.daoFactory.getConnection();

            final Object[] values = {extractedText.getExtractedText()};

            preparedStatement = DAOUtil.prepareStatement(connection, String.format(this.SQL_INSERT_INCREMENT, this.tableName), true, values);

            LOG.info("INSERT INTO ExtractedText content Length = " + extractedText.getExtractedText().length());

            preparedStatement.executeUpdate();

            // TODO: return the actual id here
            return 0;
        } catch (final SQLException e) {
            LOG.error("Insert into table ExtractedText fails, " + e.getMessage());
            return -1;
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }
    }


    @Override
    public boolean update(ExtractedText extractedText) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        final ResultSet resultSet = null;

        try {
            connection = this.daoFactory.getConnection();

            final Object[] values = {extractedText.getExtractedText(), extractedText.getId()};

            preparedStatement = DAOUtil.prepareStatement(connection, String.format(this.SQL_UPDATE, this.tableName), false, values);

            LOG.info("Update ExtractedText (" + extractedText.getId() + ", Content Length = " + extractedText.getExtractedText().length());

            preparedStatement.executeUpdate();

            return true;
        } catch (final SQLException e) {
            LOG.error("Update ExtractedText fails, " + e.getMessage());

            return false;
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public List<ExtractedText> getExtactedTextNotIdInTable(int lowerBound, int maxNumResult, String compareAgainstTable) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.daoFactory.getConnection();

            preparedStatement = DAOUtil.prepareStatement(
                    connection, String.format(this.SQL_SELECT_NON_EXIST_ID_WITH_LIMIT, this.tableName, compareAgainstTable),
                    false, lowerBound, maxNumResult);

            resultSet = preparedStatement.executeQuery();

            final ArrayList<ExtractedText> extractedTexts = new ArrayList<>();
            while (resultSet.next()) {
                final ExtractedText extractedText = this.constructExtractedTextObject(resultSet);
                extractedTexts.add(extractedText);
            }

            return extractedTexts;
        } catch (final SQLException e) {
            LOG.error("Get extracted text fails, " + e.getMessage());
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }

        return null;
    }
}
