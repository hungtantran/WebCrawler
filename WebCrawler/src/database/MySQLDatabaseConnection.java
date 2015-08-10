package database;

import static common.ErrorCode.FAILED;
import static common.ErrorCode.SUCCEEDED;
import static common.LogManager.writeGenericLog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.ErrorCode.CrError;
import common.IWebPage;
import common.URLObject;

public class MySQLDatabaseConnection implements IDatabaseConnection {
	private LinkCrawledDAO m_linkCrawledDAO = null;
	private LinkQueueDAO m_linkQueueDAO = null;
	private DomainDAO m_domainDAO = null;
	private RawHTMLDAO m_rawHTMLDAO = null;
	private Map<Integer, String> domainIdToDomainMap = null;
	private Map<String, Integer> domainToDomainIdMap = null;
	
	public MySQLDatabaseConnection(String username, String password, String server, String database) throws SQLException, ClassNotFoundException {
		DAOFactory daoFactory = DAOFactory.getInstance(username, password, server + database);

		this.m_linkCrawledDAO = new LinkCrawledDAOJDBC(daoFactory);
		this.m_linkQueueDAO = new LinkQueueDAOJDBC(daoFactory);
		this.m_domainDAO = new DomainDAOJDBC(daoFactory);
		this.m_rawHTMLDAO = new RawHTMLDAOJDBC(daoFactory);
		
		CrError hr = this.populateDomainMap();
		if (FAILED(hr)) {
			writeGenericLog("Fail to populate domain map, hr = " + hr);
		}
	}
	
	private CrError populateDomainMap() throws SQLException {
		CrError hr = CrError.CR_OK;
		
		this.domainIdToDomainMap = new HashMap<Integer, String>();
		this.domainToDomainIdMap = new HashMap<String, Integer>();

		List<Domain> domains = this.m_domainDAO.get();
		for (Domain domain : domains) {
			this.domainIdToDomainMap.put(domain.getId(), domain.getDomain());
			this.domainToDomainIdMap.put(domain.getDomain(), domain.getId());
		}
		
		return hr;
	}
	
	@Override
	public CrError pullFrontierDatabase(ArrayList<URLObject> outUrls, int maxUrls) {
		CrError hr = CrError.CR_OK;
		List<LinkQueue> linkQueues = null;

		synchronized(this.m_linkQueueDAO) {
			try {
				 linkQueues = this.m_linkQueueDAO.get(maxUrls);
			} catch (SQLException e) {
				writeGenericLog("Fail to pull from frontier database, " + e.getMessage() + ", " + e.getSQLState() + ", " + e.getErrorCode());
				hr = CrError.CR_DATABASE_ERROR;
			}
		}
		
		if (SUCCEEDED(hr)) {
			for (LinkQueue linkQueue : linkQueues) {
				URLObject urlFrontier = new URLObject();

				urlFrontier.setLink(linkQueue.getLink());
				urlFrontier.setDomain(this.domainIdToDomainMap.get(linkQueue.getDomainTableId1()));
				urlFrontier.set_priority(linkQueue.getPriority());
				urlFrontier.set_extractedTime(linkQueue.get_extractedTime());
				urlFrontier.set_relevance(linkQueue.get_relevance());
				urlFrontier.set_distanceFromRelevantPage(linkQueue.get_distanceFromRelevantPage());
				urlFrontier.set_freshness(linkQueue.get_freshness());
				
				outUrls.add(urlFrontier);
			}
		}
		
		
		return hr;
	}

	@Override
	public CrError pushFrontierDatabase(ArrayList<URLObject> inUrls) {
		CrError hr = CrError.CR_OK;
		
		try {
			for (URLObject url : inUrls) {
				Integer domainId = -1;
				String domainStr = url.getDomain();
				
				synchronized(this.domainIdToDomainMap) {
					if (this.domainToDomainIdMap.containsKey(domainStr)) {
						domainId = this.domainToDomainIdMap.get(domainStr);
					} else {
						writeGenericLog("Domain " + domainStr + " doesn't exists yet, create and insert a new one");
						Domain domain = new Domain();
						domain.setDomain(domainStr);
						domainId = this.m_domainDAO.create(domain);
						
						this.domainIdToDomainMap.put(domainId, domainStr);
						this.domainToDomainIdMap.put(domainStr, domainId);
					}
				}
				
				if (domainId < 0) {
					writeGenericLog("Can't find or generate domain id, " + domainStr + ", " + domainId);
				}
				
				// TODO check this persistent to see if the logic is sound
				LinkQueue linkQueue = new LinkQueue();
				linkQueue.setDomainTableId1(domainId);
				linkQueue.Assign(url);
				
				synchronized(this.m_linkQueueDAO) {
					int id = this.m_linkQueueDAO.create(linkQueue);
					url.set_id(id);
				}
			}
		} catch (SQLException e) {
			writeGenericLog("Fail to push to frontier database, " + e.getMessage() + ", " + e.getSQLState() + ", " + e.getErrorCode());
			hr = CrError.CR_DATABASE_ERROR;
		}
		
		return hr;
	}

	@Override
	public CrError pullURLDistributorDatabase(ArrayList<URLObject> outUrls, int maxUrls) {
		CrError hr = CrError.CR_OK;
		// TODO Auto-generated method stub
		return hr;
	}

	@Override
	public CrError pushURLDistributorDatabase(ArrayList<URLObject> inUrls) {
		CrError hr = CrError.CR_OK;
		// TODO Auto-generated method stub
		return hr;
	}

	@Override
	public CrError pullURLDuplicationDatabase(ArrayList<URLObject> outUrls, int maxUrls) {
		CrError hr = CrError.CR_OK;
		// TODO Auto-generated method stub
		return hr;
	}

	@Override
	public CrError pushURLDuplicationDatabase(ArrayList<URLObject> inUrls) {
		CrError hr = CrError.CR_OK;
		
		try {
			for (URLObject url : inUrls) {
				Integer domainId = -1;
				String domainStr = url.getDomain();
				
				synchronized(this.domainIdToDomainMap) {
					if (this.domainToDomainIdMap.containsKey(domainStr)) {
						domainId = this.domainToDomainIdMap.get(domainStr);
					} else {
						writeGenericLog("Domain " + domainStr + " doesn't exists yet, create and insert a new one");
						Domain domain = new Domain();
						domain.setDomain(domainStr);
						domainId = this.m_domainDAO.create(domain);
						
						this.domainIdToDomainMap.put(domainId, domainStr);
						this.domainToDomainIdMap.put(domainStr, domainId);
					}
				}
				
				if (domainId < 0) {
					writeGenericLog("Can't find or generate domain id, " + url.getDomain() + ", " + domainId);
				}
				
				// TODO check this persistent to see if the logic is sound
				LinkCrawled linkCrawled = new LinkCrawled();
				linkCrawled.setDomainTableId1(domainId);
				linkCrawled.Assign(url);
				
				synchronized(this.m_linkCrawledDAO) {
					int id = this.m_linkCrawledDAO.create(linkCrawled);
					url.set_id(id);
				}
			}
		} catch (SQLException e) {
			writeGenericLog("Fail to push to duplication database, " + e.getMessage() + ", " + e.getSQLState() + ", " + e.getErrorCode());
			hr = CrError.CR_DATABASE_ERROR;
		}
		
		return hr;
	}

	@Override
	public boolean checkURLDuplicationDatabase(URLObject inUrl) {
		try {
			String link = inUrl.getAbsoluteLink();
			
			LinkCrawled linkCrawled = new LinkCrawled();
			linkCrawled.setLink(link);
			
			synchronized(this.m_linkCrawledDAO) {
				return this.m_linkCrawledDAO.linkExists(linkCrawled);
			}
		} catch (SQLException e) {
			writeGenericLog("Fail to check if url " + inUrl.getAbsoluteLink() + " is duplicated or not, "+ e.getMessage() + ", " + e.getSQLState() + ", " + e.getErrorCode());
		}
		
		return false;
	}

	@Override
	public CrError storeWebPage(IWebPage inWebPage) {
		CrError hr = CrError.CR_OK;
		
		try {
			RawHTML rawHTML = new RawHTML();
			rawHTML.setId(inWebPage.get_id());
			rawHTML.setHtml(inWebPage.getString());
			
			synchronized(this.m_rawHTMLDAO) {
				this.m_rawHTMLDAO.create(rawHTML);
			}
		} catch (SQLException e) {
			writeGenericLog("Fail to insert rawHTML "+ e.getMessage() + ", " + e.getSQLState() + ", " + e.getErrorCode());
			hr = CrError.CR_DATABASE_ERROR;
		}
		
		return hr;
	}
}
