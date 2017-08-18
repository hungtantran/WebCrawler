package database;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DomainDAOJDBC implements DomainDAO {
    private static Logger LOG = LogManager.getLogger(DomainDAOJDBC.class
            .getName());

    private final String SQL_SELECT = "SELECT * FROM domain_table";
    private final String SQL_INSERT = "INSERT INTO domain_table (id, domain) " +
			"values (?, ?)";

    private DAOFactory m_daoFactory = null;

    public DomainDAOJDBC(DAOFactory daoFactory) throws SQLException {
        this.m_daoFactory = daoFactory;
    }

    private Domain constructDomainObject(ResultSet resultSet) throws
			SQLException {
        final Domain domain = new Domain();

        domain.setId(resultSet.getInt("id"));
        if (resultSet.wasNull()) {
            domain.setId(null);
        }

        domain.setDomain(resultSet.getString("domain"));
        if (resultSet.wasNull()) {
            domain.setDomain(null);
        }

        return domain;
    }

    @Override
    public List<Domain> get() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.m_daoFactory.getConnection();
            preparedStatement = DAOUtil.prepareStatement(connection, this
					.SQL_SELECT, false);
            resultSet = preparedStatement.executeQuery();

            final List<Domain> domains = new ArrayList<Domain>();
            while (resultSet.next()) {
                final Domain domain = this.constructDomainObject(resultSet);
                domains.add(domain);
            }

            return domains;
        } catch (final SQLException e) {
            LOG.error("Get domain_table fails, " + e.getMessage());

            throw e;
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public int create(Domain domain) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.m_daoFactory.getConnection();

            final Object[] values = {domain.getId(), domain.getDomain()};

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
            LOG.error("Insert into domain_table fails, " + e.getMessage());

            return -1;
        } finally {
            DAOUtil.close(connection, preparedStatement, resultSet);
        }
    }
}
