package database;

import common.ErrorCode.CrError;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

import static common.ErrorCode.FAILED;

public class MySQLDatabaseConnection implements IDatabaseConnection {
	private static Logger LOG = LogManager.getLogger(MySQLDatabaseConnection.class.getName());

	private RawHTMLDAO m_rawHTMLDAO = null;
	private ExtractedTextDAO m_extractedTextDAO = null;
	
	public MySQLDatabaseConnection(String username, String password, String server, String database) throws SQLException, ClassNotFoundException {
		LOG.setLevel(Level.ALL);
		DAOFactory daoFactory = DAOFactory.getInstance(username, password, server + database);

		this.m_rawHTMLDAO = new RawHTMLDAOJDBC(daoFactory);
		this.m_extractedTextDAO = new ExtractedTextDAOJDBC(daoFactory);
	}

	public List<RawHTML> getNonExtractedTextRawHTML(int lowerBound, int maxNumResult) throws SQLException {
		return this.m_rawHTMLDAO.getNonextractedTextRawHTML(lowerBound, maxNumResult);
	}

	public int createExtractedText(ExtractedText extractedText) throws SQLException {
		return this.m_extractedTextDAO.create(extractedText);
	}
}
