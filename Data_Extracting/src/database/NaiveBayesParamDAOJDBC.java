package database;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NaiveBayesParamDAOJDBC implements NaiveBayesParamDAO {
    private static Logger LOG = LogManager.getLogger(NaiveBayesParamDAOJDBC
            .class.getName());

    private final String SQL_SELECT_ALL = "SELECT * FROM naive_bayes_param";
    private final String SQL_SELECT_BY_LABEL = "SELECT * FROM " +
            "naive_bayes_param WHERE label = ?";
    private final String SQL_INSERT = "INSERT INTO naive_bayes_param (word, " +
            "label, count) values (?, ?, ?) ON DUPLICATE KEY UPDATE count = ?";

    private final DAOFactory daoFactory;

    public NaiveBayesParamDAOJDBC(DAOFactory daoFactory) throws SQLException {
        this.daoFactory = daoFactory;
    }

    private NaiveBayesParam constructNaiveBayesParam(ResultSet resultSet)
            throws SQLException {
        NaiveBayesParam param = new NaiveBayesParam();
        param.setWord(resultSet.getInt("word"));
        param.setLabel(resultSet.getInt("label"));
        param.setCount(resultSet.getInt("count"));
        return param;
    }

    @Override
    public List<NaiveBayesParam> get() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.daoFactory.getConnection();
            preparedStatement = DAOUtil.prepareStatement(connection, this
                    .SQL_SELECT_ALL, false);
            resultSet = preparedStatement.executeQuery();

            final ArrayList<NaiveBayesParam> params = new ArrayList<>();
            while (resultSet.next()) {
                final NaiveBayesParam param = this.constructNaiveBayesParam
                        (resultSet);
                params.add(param);
            }

            return params;
        } catch (final SQLException e) {
            LOG.error("Get NaiveBayesParam fails, " + e.getMessage());
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }

        return null;
    }

    @Override
    public List<NaiveBayesParam> get(int label) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.daoFactory.getConnection();
            final Object[] values = {label};
            preparedStatement = DAOUtil.prepareStatement(connection, this
                    .SQL_SELECT_BY_LABEL, false, values);
            resultSet = preparedStatement.executeQuery();

            final ArrayList<NaiveBayesParam> params = new ArrayList<>();
            while (resultSet.next()) {
                final NaiveBayesParam param = this.constructNaiveBayesParam
                        (resultSet);
                params.add(param);
            }

            return params;
        } catch (final SQLException e) {
            LOG.error("Get NaiveBayesParam fails, " + e.getMessage());
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }

        return null;
    }

    @Override
    public int create(NaiveBayesParam param) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.daoFactory.getConnection();

            final Object[] values = {param.getWord(), param.getLabel(), param
                    .getCount(), param.getCount()};
            preparedStatement = DAOUtil.prepareStatement(connection, this
                    .SQL_INSERT, true, values);

            LOG.info(preparedStatement.toString());

            preparedStatement.executeUpdate();

            // Get the generated key (id)
            resultSet = preparedStatement.getGeneratedKeys();
            int generatedKey = -1;

            if (resultSet.next()) {
                generatedKey = resultSet.getInt(1);
            }

            return generatedKey;
        } catch (final SQLException e) {
            LOG.error("Insert into naive_bayes_param fails, " + e.getMessage());
            return -1;
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }
    }
}
