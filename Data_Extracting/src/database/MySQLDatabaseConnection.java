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
	private ExtractedTextDAO m_cleanExtractedTextDAO = null;
	private ExtractedTextDAO m_dictionaryDAO = null;
	private NaiveBayesParamDAO m_naiveBayesParamDAO = null;
	
	public MySQLDatabaseConnection(String username, String password, String server, String database) throws SQLException, ClassNotFoundException {
		LOG.setLevel(Level.ALL);
		DAOFactory daoFactory = DAOFactory.getInstance(username, password, server + database);

		this.m_rawHTMLDAO = new RawHTMLDAOJDBC(daoFactory);
		this.m_extractedTextDAO = new ExtractedTextDAOJDBC(daoFactory, "extracted_text_table");
		this.m_cleanExtractedTextDAO = new ExtractedTextDAOJDBC(daoFactory, "clean_extracted_text_table");
		this.m_dictionaryDAO = new ExtractedTextDAOJDBC(daoFactory, "dictionary_table");
		this.m_naiveBayesParamDAO = new NaiveBayesParamDAOJDBC(daoFactory);
	}

	public List<RawHTML> getNonExtractedTextRawHTML(int lowerBound, int maxNumResult) throws SQLException {
		return this.m_rawHTMLDAO.getNonextractedTextRawHTML(lowerBound, maxNumResult);
	}

	public int createExtractedText(ExtractedText extractedText) throws SQLException {
		return this.m_extractedTextDAO.create(extractedText);
	}

	public int createCleanText(ExtractedText extractedText) throws SQLException {
		return this.m_cleanExtractedTextDAO.create(extractedText);
	}

	public int createNaiveBayesParam(NaiveBayesParam param) throws SQLException {
		return this.m_naiveBayesParamDAO.create(param);
	}

	public int createDictionaryWord(String word) throws SQLException {
		ExtractedText extractedText = new ExtractedText();
		extractedText.setExtractedText(word);
		return this.m_dictionaryDAO.createIncrement(extractedText);
	}

	public List<ExtractedText> getNonCleanTextExtractedText(int lowerBound, int maxNumResult) throws SQLException {
		return this.m_extractedTextDAO.getExtactedTextNotIdInTable(lowerBound, maxNumResult, "clean_extracted_text_table");
	}

	public List<ExtractedText> getCleanExtractedText(int lowerBound, int maxNumResult) throws SQLException {
		return this.m_cleanExtractedTextDAO.get(lowerBound, maxNumResult);
	}

	public List<ExtractedText> getDictionaryWords() throws SQLException {
		return this.m_dictionaryDAO.get();
	}
}
