package database;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RawHTMLDAOJDBC implements RawHTMLDAO {
    private static Logger LOG = LogManager.getLogger(RawHTMLDAOJDBC.class
            .getName());

    private final String SQL_SELECT_BY_ID = "SELECT * FROM %s WHERE id = ?";
    private final String SQL_SELECT_ALL = "SELECT * FROM %s";
    private final String SQL_SELECT_WITH_LIMIT = "SELECT * FROM %s LIMIT ?, ?";
    private final String SQL_SELECT_NON_EXTRACTED_WITH_LIMIT = "SELECT * FROM" +
            " %s WHERE id NOT IN (SELECT id FROM %s) LIMIT ?, ?";
    private final String SQL_INSERT = "INSERT INTO %s (id, html) values (?, ?)";
    private final String SQL_UPDATE = "UPDATE %s SET html = ? WHERE id = ?";

    private final DAOFactory daoFactory;
    private String tableName;
    private String extractedTextTableName;

    public RawHTMLDAOJDBC(DAOFactory daoFactory, String tableName, String
            extractedTextTableName) throws
            SQLException {
        this.daoFactory = daoFactory;
        this.tableName = tableName;
        this.extractedTextTableName = extractedTextTableName;
    }

    private RawHTML constructRawHTMLObject(ResultSet resultSet) throws
            SQLException {
        final RawHTML rawHTML = new RawHTML();

        rawHTML.setId(resultSet.getInt("id"));
        if (resultSet.wasNull()) {
            rawHTML.setId(null);
        }

        rawHTML.setHtml(resultSet.getString("html"));
        if (resultSet.wasNull()) {
            rawHTML.setHtml(null);
        }

        return rawHTML;
    }

    @Override
    public RawHTML get(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.daoFactory.getConnection();
            preparedStatement = DAOUtil.prepareStatement(connection, String
                    .format(this.SQL_SELECT_BY_ID, this
                            .tableName), false, id);
            resultSet = preparedStatement.executeQuery();

            RawHTML rawHTML = null;
            if (resultSet.next()) {
                rawHTML = this.constructRawHTMLObject(resultSet);
            }

            return rawHTML;
        } catch (final SQLException e) {
            LOG.error("Get RawHTML fails, " + e.getMessage());
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }

        return null;
    }

    @Override
    public List<RawHTML> get() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.daoFactory.getConnection();
            preparedStatement = DAOUtil.prepareStatement(connection, String
                    .format(this.SQL_SELECT_ALL, this
                            .tableName), false);
            resultSet = preparedStatement.executeQuery();

            final ArrayList<RawHTML> htmls = new ArrayList<RawHTML>();
            while (resultSet.next()) {
                final RawHTML rawHTML = this.constructRawHTMLObject(resultSet);
                htmls.add(rawHTML);
            }

            return htmls;
        } catch (final SQLException e) {
            LOG.error("Get RawHTML fails, " + e.getMessage());
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }

        return null;
    }

    @Override
    public List<RawHTML> get(int lowerBound, int maxNumResult) throws
            SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.daoFactory.getConnection();

            if (lowerBound > 0 || maxNumResult > 0) {
                preparedStatement = DAOUtil.prepareStatement(connection,
                        String.format(this.SQL_SELECT_WITH_LIMIT,
                                this.tableName), false, lowerBound,
                        maxNumResult);
            } else {
                preparedStatement = DAOUtil.prepareStatement(connection,
                        String.format(this.SQL_SELECT_ALL, this
                                .tableName), false);
            }

            resultSet = preparedStatement.executeQuery();

            final ArrayList<RawHTML> htmls = new ArrayList<RawHTML>();
            while (resultSet.next()) {
                final RawHTML rawHTML = this.constructRawHTMLObject(resultSet);
                htmls.add(rawHTML);
            }

            return htmls;
        } catch (final SQLException e) {
            LOG.error("Get RawHTML fails, " + e.getMessage());
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }

        return null;
    }

    @Override
    public List<RawHTML> getNonextractedTextRawHTML(int lowerBound, int
            maxNumResult) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.daoFactory.getConnection();

            preparedStatement = DAOUtil.prepareStatement(connection, String
                            .format(this.SQL_SELECT_NON_EXTRACTED_WITH_LIMIT,
                                    this.tableName, this
                                            .extractedTextTableName),
                    false, lowerBound, maxNumResult);

            resultSet = preparedStatement.executeQuery();

            final ArrayList<RawHTML> htmls = new ArrayList<RawHTML>();
            while (resultSet.next()) {
                final RawHTML rawHTML = this.constructRawHTMLObject(resultSet);
                htmls.add(rawHTML);
            }

            return htmls;
        } catch (final SQLException e) {
            LOG.error("Get RawHTML fails, " + e.getMessage());
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }

        return null;
    }

    @Override
    public int create(RawHTML rawHTML) throws SQLException {
        if (rawHTML == null || !rawHTML.isValid()) {
            return -1;
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.daoFactory.getConnection();

            final Object[] values = {rawHTML.getId(), rawHTML.getHtml()};

            preparedStatement = DAOUtil.prepareStatement(connection, String
                            .format(this.SQL_INSERT, this.tableName),
                    true, values);

            LOG.info("INSERT INTO RawHTML " + rawHTML.getId() + ", Content " +
                    "Length = " + rawHTML.getHtml().length());

            preparedStatement.executeUpdate();

            return rawHTML.getId();
        } catch (final SQLException e) {
            LOG.error("Insert into table RawHTML fails, " + e.getMessage());
            return -1;
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public boolean update(RawHTML rawHTML) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        final ResultSet resultSet = null;

        try {
            connection = this.daoFactory.getConnection();

            final Object[] values = {rawHTML.getHtml(), rawHTML.getId()};

            preparedStatement = DAOUtil.prepareStatement(connection, String
                            .format(this.SQL_UPDATE, this.tableName),
                    false, values);

            LOG.info("Update RawHTML (" + rawHTML.getId() + ", Content Length" +
                    " = " + rawHTML.getHtml().length());

            preparedStatement.executeUpdate();

            return true;
        } catch (final SQLException e) {
            LOG.error("Update RawHTML fails, " + e.getMessage());

            return false;
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }
    }
}
