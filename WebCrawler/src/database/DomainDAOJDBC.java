package database;

import static common.LogManager.writeGenericLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DomainDAOJDBC implements DomainDAO {
	private final String SQL_SELECT = "SELECT * FROM domain_table";
	private final String SQL_INSERT = "INSERT INTO domain_table (id, domain) values (?, ?)";

	private final DAOFactory daoFactory;

	public DomainDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
	}

	private Domain constructDomainObject(ResultSet resultSet) throws SQLException {
		final Domain domain = new Domain();

		domain.setId(resultSet.getInt("id"));
		if (resultSet.wasNull()) {
			domain.setId(null);
		}

		domain.setDomain(resultSet.getString("link"));
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
			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_SELECT, false);
			resultSet = preparedStatement.executeQuery();

			final List<Domain> domains = new ArrayList<Domain>();
			while (resultSet.next()) {
				final Domain domain = this.constructDomainObject(resultSet);
				domains.add(domain);
			}

			return domains;
		} catch (final SQLException e) {
			writeGenericLog("Get domain_table fails, " + e.getMessage());

			return null;
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
			connection = this.daoFactory.getConnection();

			final Object[] values = { domain.getId(), domain.getDomain() };

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_INSERT, true, values);
			
			writeGenericLog(preparedStatement.toString());

			preparedStatement.executeUpdate();

			// Get the generated key (id)
			resultSet = preparedStatement.getGeneratedKeys();
			int generatedKey = -1;

			if (resultSet.next()) {
				generatedKey = resultSet.getInt(1);
			}

			return generatedKey;
		} catch (final SQLException e) {
			writeGenericLog("Insert into domain_table fails, " + e.getMessage());

			return -1;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
}
