package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Helper;

import static common.LogManager.*;

public class LinkQueueDAOJDBC implements LinkQueueDAO {
	private final String SQL_SELECT_WITH_LIMIT = "SELECT * FROM link_queue_table LIMIT ?";
	private final String SQL_DELETE_WITH_LIMIT = "DELETE FROM link_queue_table LIMIT ?";
	private final String SQL_INSERT = "INSERT INTO link_queue_table (link, domain_table_id_1, priority, persistent, extracted_time, time_crawled, date_crawled) values (?, ?, ?, ?, ?, ?, ?)";
	private final String SQL_CHECK_EXISTS = "SELECT COUNT(*) AS count FROM link_queue_table WHERE link = ?";

	private final DAOFactory daoFactory;

	public LinkQueueDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
	}

	private LinkQueue constructLinkQueueObject(ResultSet resultSet) throws SQLException {
		final LinkQueue linkQueue = new LinkQueue();

		linkQueue.setId(resultSet.getInt("id"));
		if (resultSet.wasNull()) {
			linkQueue.setId(null);
		}

		linkQueue.setLink(resultSet.getString("link"));
		if (resultSet.wasNull()) {
			linkQueue.setLink(null);
		}

		linkQueue.setDomainTableId1(resultSet.getInt("domain_table_id_1"));
		if (resultSet.wasNull()) {
			linkQueue.setDomainTableId1(null);
		}

		linkQueue.setPriority(resultSet.getInt("priority"));
		if (resultSet.wasNull()) {
			linkQueue.setPriority(null);
		}

		linkQueue.setPersistent(resultSet.getInt("persistent"));
		if (resultSet.wasNull()) {
			linkQueue.setPersistent(null);
		}
		
		linkQueue.set_extractedTime(resultSet.getLong("extracted_time"));
		if (resultSet.wasNull()) {
			linkQueue.set_extractedTime(null);
		}

		linkQueue.setTimeCrawled(resultSet.getString("time_crawled"));
		if (resultSet.wasNull()) {
			linkQueue.setTimeCrawled(null);
		}

		linkQueue.setDateCrawled(resultSet.getString("date_crawled"));
		if (resultSet.wasNull()) {
			linkQueue.setDateCrawled(null);
		}

		return linkQueue;
	}
	
	public void remove(int maxUrls) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();
			
			// TODO make it transactional
			// Pull urls from the queue
			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_DELETE_WITH_LIMIT, false, maxUrls);
			preparedStatement.executeUpdate();
		} catch (final SQLException e) {
			writeGenericLog("Remove from link_queue_table fails" + e.getMessage());
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
	
	@Override
	public List<LinkQueue> get(int maxUrls) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();
			
			List<LinkQueue> linksQueue = new ArrayList<LinkQueue>();
			
			// TODO make it transactional
			// Pull urls from the queue
			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_SELECT_WITH_LIMIT, false, maxUrls);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				final LinkQueue linkQueue = this.constructLinkQueueObject(resultSet);
				linksQueue.add(linkQueue);
			}
			
			if (!linksQueue.isEmpty()) {
				this.remove(maxUrls);
			}
			
			return linksQueue;
		} catch (final SQLException e) {
			writeGenericLog("Get link_queue_table fails" + e.getMessage());

			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public int create(LinkQueue linkQueue) throws SQLException {
		if (linkQueue.getLink() == null) {
			return -1;
		}

		// If the time crawled is not specified, use the current time
		if (linkQueue.getTimeCrawled() == null || linkQueue.getDateCrawled() == null) {
			linkQueue.setTimeCrawled(Helper.getCurrentTime());
			linkQueue.setDateCrawled(Helper.getCurrentDate());
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();

			final Object[] values = { linkQueue.getLink(),
				linkQueue.getDomainTableId1(), linkQueue.getPriority(),
				linkQueue.getPersistent(), linkQueue.get_extractedTime(),
				linkQueue.getTimeCrawled(), linkQueue.getDateCrawled() };

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
			writeGenericLog("Insert into link_queue_table fails, " + e.getMessage());

			return -1;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public boolean linkExists(LinkQueue linkQueue) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();

			final Object[] values = { linkQueue.getLink() };

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
