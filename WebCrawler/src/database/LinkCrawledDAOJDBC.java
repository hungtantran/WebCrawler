package database;

import static common.LogManager.writeGenericLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Helper;

public class LinkCrawledDAOJDBC implements LinkCrawledDAO {
	private final String SQL_SELECT_BY_DOMAINID = "SELECT * FROM link_crawled_table WHERE domain_table_id_1 = ?";
	private final String SQL_INSERT = "INSERT INTO link_crawled_table (link, priority, domain_table_id_1, download_duration, extracted_time, statusCode, relevance, distanceFromRelevantPage, freshness, time_crawled, date_crawled) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private final String SQL_UPDATE = "UPDATE link_crawled_table SET link = ?, priority = ? WHERE id = ?";
	private final String SQL_CHECK_EXISTS = "SELECT COUNT(*) AS count FROM link_crawled_table WHERE link = ?";

	private final DAOFactory daoFactory;

	public LinkCrawledDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
	}

	private LinkCrawled constructLinkCrawledObject(ResultSet resultSet) throws SQLException {
		final LinkCrawled linkCrawled = new LinkCrawled();

		linkCrawled.setId(resultSet.getInt("id"));
		if (resultSet.wasNull()) {
			linkCrawled.setId(null);
		}

		linkCrawled.setLink(resultSet.getString("link"));
		if (resultSet.wasNull()) {
			linkCrawled.setLink(null);
		}

		linkCrawled.setPriority(resultSet.getInt("priority"));
		if (resultSet.wasNull()) {
			linkCrawled.setPriority(null);
		}

		linkCrawled.setDomainTableId1(resultSet.getInt("domain_table_id_1"));
		if (resultSet.wasNull()) {
			linkCrawled.setDomainTableId1(null);
		}
		
		linkCrawled.set_downloadDuration(resultSet.getLong("download_duration"));
		if (resultSet.wasNull()) {
			linkCrawled.set_downloadDuration(null);
		}
		
		linkCrawled.set_extractedTime(resultSet.getLong("extracted_time"));
		if (resultSet.wasNull()) {
			linkCrawled.set_extractedTime(null);
		}
		
		linkCrawled.set_httpStatusCode(resultSet.getInt("statusCode"));
		if (resultSet.wasNull()) {
			linkCrawled.set_httpStatusCode(null);
		}
		
		linkCrawled.set_relevance(resultSet.getLong("relevance"));
		if (resultSet.wasNull()) {
			linkCrawled.set_relevance(null);
		}
		
		linkCrawled.set_distanceFromRelevantPage(resultSet.getLong("distanceFromRelevantPage"));
		if (resultSet.wasNull()) {
			linkCrawled.set_distanceFromRelevantPage(null);
		}
		
		linkCrawled.set_freshness(resultSet.getInt("freshness"));
		if (resultSet.wasNull()) {
			linkCrawled.set_freshness(null);
		}

		linkCrawled.setTimeCrawled(resultSet.getString("time_crawled"));
		if (resultSet.wasNull()) {
			linkCrawled.setTimeCrawled(null);
		}

		linkCrawled.setDateCrawled(resultSet.getString("date_crawled"));
		if (resultSet.wasNull()) {
			linkCrawled.setDateCrawled(null);
		}

		return linkCrawled;
	}

	@Override
	public List<LinkCrawled> get(int domainId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_SELECT_BY_DOMAINID, false, domainId);
			resultSet = preparedStatement.executeQuery();

			final List<LinkCrawled> linksCrawled = new ArrayList<LinkCrawled>();
			while (resultSet.next()) {
				final LinkCrawled linkCrawled = this.constructLinkCrawledObject(resultSet);
				linksCrawled.add(linkCrawled);
			}

			return linksCrawled;
		} catch (final SQLException e) {
			writeGenericLog("Get link_crawled_table fails, " + e.getMessage());

			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public int create(LinkCrawled linkCrawled) throws SQLException {
		if (linkCrawled.getLink() == null) {
			return -1;
		}

		// If the time crawled is not specified, use the current time
		if (linkCrawled.getTimeCrawled() == null || linkCrawled.getDateCrawled() == null) {
			linkCrawled.setTimeCrawled(Helper.getCurrentTime());
			linkCrawled.setDateCrawled(Helper.getCurrentDate());
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();

			final Object[] values = {
				linkCrawled.getLink(), linkCrawled.getPriority(),
				linkCrawled.getDomainTableId1(), linkCrawled.get_downloadDuration(),
				linkCrawled.get_extractedTime(), linkCrawled.get_httpStatusCode(),
				linkCrawled.get_relevance(), linkCrawled.get_distanceFromRelevantPage(),
				linkCrawled.get_freshness(), linkCrawled.getTimeCrawled(),
				linkCrawled.getDateCrawled() };

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_INSERT, true, values);

			preparedStatement.executeUpdate();

			// Get the generated key (id)
			resultSet = preparedStatement.getGeneratedKeys();
			int generatedKey = -1;

			if (resultSet.next()) {
				generatedKey = resultSet.getInt(1);
			}

			return generatedKey;
		} catch (final SQLException e) {
			writeGenericLog("Insert into link_crawled_table fails, " + e.getMessage());

			return -1;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public boolean update(LinkCrawled linkCrawled) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		final ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();

			final Object[] values = { linkCrawled.getLink(), linkCrawled.getPriority(), linkCrawled.getId() };

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_UPDATE, false, values);

			preparedStatement.executeUpdate();

			return true;
		} catch (final SQLException e) {
			writeGenericLog("Update link_crawled_table fails, " + e.getMessage());

			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public boolean linkExists(LinkCrawled linkCrawled) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();

			final Object[] values = { linkCrawled.getLink() };

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_CHECK_EXISTS, false, values);

			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				int numVal = resultSet.getInt("count");
				
				if (numVal > 0) {
					return true;
				} else {
					return false;
				}
			}

			return false;
		} catch (final SQLException e) {
			writeGenericLog("Update link_crawled_table fails, " + e.getMessage());

			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
}
