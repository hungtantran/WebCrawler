package database;

import java.sql.SQLException;
import java.util.ArrayList;

import common.ErrorCode.CrError;
import common.URLObject;

public class MySQLDatabaseConnection implements IDatabaseConnection {
	private LinkCrawledDAO m_linkCrawledDAO = null;
	private LinkQueueDAO m_linkQueueDAO = null;
	private DomainDAO m_domainDAO = null;
	private RawHTMLDAO m_rawHTMLDAO = null;
	
	public MySQLDatabaseConnection(String username, String password, String server, String database) throws SQLException, ClassNotFoundException {
		DAOFactory daoFactory = DAOFactory.getInstance(username, password, server + database);

		this.m_linkCrawledDAO = new LinkCrawledDAOJDBC(daoFactory);
		this.m_linkQueueDAO = new LinkQueueDAOJDBC(daoFactory);
		this.m_domainDAO = new DomainDAOJDBC(daoFactory);
		this.m_rawHTMLDAO = new RawHTMLDAOJDBC(daoFactory);
	}
	
	@Override
	public CrError pullFrontierDatabase(ArrayList<URLObject> outUrls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CrError pushFrontierDatabase(ArrayList<URLObject> inUrls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CrError pullURLDistributorDatabase(ArrayList<URLObject> outUrls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CrError pushURLDistributorDatabase(ArrayList<URLObject> inUrls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CrError pullURLDuplicationDatabase(ArrayList<URLObject> outUrls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CrError pushURLDuplicationDatabase(ArrayList<URLObject> inUrls) {
		// TODO Auto-generated method stub
		return null;
	}

}
