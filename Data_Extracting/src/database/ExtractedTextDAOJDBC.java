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

	private final String SQL_SELECT_BY_ID = "SELECT * FROM extracted_text_table WHERE id = ?";
	private final String SQL_INSERT = "INSERT INTO extracted_text_table (id, extracted_text) values (?, ?)";
	private final String SQL_UPDATE = "UPDATE extracted_text_table SET extracted_text = ? WHERE id = ?";

	private final DAOFactory daoFactory;

	public ExtractedTextDAOJDBC(DAOFactory daoFactory) throws SQLException {
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
			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_SELECT_BY_ID, false, id);
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

			final Object[] values = { extractedText.getId(), extractedText.getExtractedText() };

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_INSERT, true, values);

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
	public boolean update(ExtractedText extractedText) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		final ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();
			
			final Object[] values = { extractedText.getExtractedText(), extractedText.getId()};

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_UPDATE, false, values);

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
}
