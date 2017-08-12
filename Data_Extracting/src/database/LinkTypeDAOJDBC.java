package database;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LinkTypeDAOJDBC implements LinkTypeDAO {
	private static Logger LOG = LogManager.getLogger(LinkTypeDAOJDBC.class.getName());

	private final String SQL_SELECT_ALL = "SELECT * FROM link_type_table";
	private final String SQL_INSERT = "INSERT INTO link_type_table (id, type) values (?, ?)";

	private final DAOFactory daoFactory;

	public LinkTypeDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
	}

	private LinkType constructLinkTypeObject(ResultSet resultSet) throws SQLException {
		LinkType linkType = new LinkType();
		linkType.setId(resultSet.getInt("id"));
		linkType.setType(resultSet.getString("type"));
		return linkType;
	}

	@Override
	public List<LinkType> get() throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_SELECT_ALL, false);
			resultSet = preparedStatement.executeQuery();

			final ArrayList<LinkType> linkTypes = new ArrayList<>();
			while (resultSet.next()) {
				final LinkType linkType = this.constructLinkTypeObject(resultSet);
				linkTypes.add(linkType);
			}

			return linkTypes;
		} catch (final SQLException e) {
			LOG.error("Get LinkType fails, " + e.getMessage());
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}

		return null;
	}

	@Override
	public int create(LinkType linkType) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();

			final Object[] values = { linkType.getId(), linkType.getType() };

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_INSERT, true, values);

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
			LOG.error("Insert into link_type_table fails, " + e.getMessage());

			return -1;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
}
