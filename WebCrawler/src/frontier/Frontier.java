package frontier;

import static common.ErrorCode.FAILED;
import static common.LogManager.writeGenericLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import common.ErrorCode.CrError;
import common.Globals;
import common.Helper;
import common.URLObject;
import database.IDatabaseConnection;

public class Frontier implements IFrontier {
	// Back end queue that disperse urls to crawler threads
	private PriorityQueue<BackEndQueue> m_backEndQueues;

	// Front end queue that crawler threads enqueue into
	private Queue<URLObject> m_frontEndQueue;
	
	// Table mapping between domain and the backend queue
	private Map<String, BackEndQueue> m_domainToBackEndQueueMap;
	
	private int m_maxNumBackEndQueues;
	
	private IDatabaseConnection m_databaseConnection = null;
	
	public Frontier(int numQueues, IDatabaseConnection databaseConnection)
	{	
		m_frontEndQueue = new LinkedList<URLObject>();
		m_backEndQueues = new PriorityQueue<BackEndQueue>(numQueues /* initialCapacity */, new BackEndQueue.BackEndQueueComparator());
		m_domainToBackEndQueueMap = new HashMap<String, BackEndQueue>();
		m_maxNumBackEndQueues = numQueues;
		m_databaseConnection = databaseConnection;
	}
	
	@Override
	public CrError releaseBackEndQueue(URLObject originalUrl) {
		synchronized(m_backEndQueues) {
		synchronized(m_domainToBackEndQueueMap) {
			if (m_domainToBackEndQueueMap.containsKey(originalUrl.getDomain())) {
				BackEndQueue backEndQueue = m_domainToBackEndQueueMap.get(originalUrl.getDomain());

				if (!m_backEndQueues.contains(backEndQueue)) {
					writeGenericLog("Push back back end queue of domain " + backEndQueue.getDomain() + " with download duration " + originalUrl.get_downloadDuration());
					backEndQueue.set_minNextProcessTimeInMillisec(Helper.getCurrentTimeInMillisec() + originalUrl.get_downloadDuration() * Globals.NPOLITENESSFACTOR);
					m_backEndQueues.add(backEndQueue);
				}
			}
		}
		}
		
		return CrError.CR_OK;
	}
	
	@Override
	public CrError pullUrl(URLObject outUrl) {
		ArrayList<URLObject> outUrls = new ArrayList<URLObject>();
		
		CrError hr = this.pullUrls(outUrls, 1);
		if (FAILED(hr)) {
			writeGenericLog("Pull url fail with hr = " + hr);
			return hr;
		}

		outUrl.assign(outUrls.get(0));
		writeGenericLog("Pull url " + outUrl.toString());
		
		return hr;
	}

	@Override
	public CrError pushUrl(URLObject originalUrl, URLObject inUrl) {
		synchronized(m_frontEndQueue) {
			m_frontEndQueue.add(inUrl);
			
			ArrayList<URLObject> inUrls = new ArrayList<URLObject>();
			inUrls.add(inUrl);
			CrError hr = m_databaseConnection.pushFrontierDatabase(inUrls);
			if (FAILED(hr)) {
				writeGenericLog("Fail to push url " + inUrl.getAbsoluteLink() + " into frontier database");
				return hr;
			}
			
			// Print out front end queue size every 100 times
			if (m_frontEndQueue.size() % 100 == 0) {
				writeGenericLog("Front end queue size : " + m_frontEndQueue.size());
			}
		}
		
		releaseBackEndQueue(originalUrl);

		return CrError.CR_OK;
	}

	@Override
	public CrError pullUrls(ArrayList<URLObject> outUrls, int maxNumUrls) {
		CrError hr = pullUrlsInternal(outUrls, maxNumUrls);
		
		if (FAILED(hr)) {
			writeGenericLog("Pull urls fail with hr = " + hr);
		}
		
		return hr;
	}
	
	public CrError pullUrlsInternal(ArrayList<URLObject> outUrls, int maxNumUrls) {
		BackEndQueue backEndQueue = null;
		CrError hr = CrError.CR_OK;

		synchronized(m_backEndQueues) {
		synchronized(m_domainToBackEndQueueMap) {
			while (true) {
				while (m_backEndQueues.isEmpty()) {
					if (m_domainToBackEndQueueMap.size() >= m_maxNumBackEndQueues) {
						// Empty backend queue and the map is already full, return error
						writeGenericLog("All back-end queues are in active. Can't dequeue any more url to crawl.");
						return CrError.CR_EMPTY_QUEUE;
					}

					// Emtpy backend queue but the map is not full, try to get url from the front end queue
					synchronized(m_frontEndQueue) {
						if (m_frontEndQueue.isEmpty()) {
							ArrayList<URLObject> frontierUrls = new ArrayList<URLObject>();
							hr = m_databaseConnection.pullFrontierDatabase(frontierUrls, Globals.NMAXURLSFROMFRONTIERPERPULL);
							
							if (FAILED(hr)) {
								writeGenericLog("Fail to pull urls from frontier");
								return hr;
							}
							
							for (URLObject frontierUrl : frontierUrls) {
								m_frontEndQueue.add(frontierUrl);
							}
						}
						
						if (m_frontEndQueue.isEmpty()) {
							// Empty front end queue, there is no more url to crawl, return error
							writeGenericLog("No more url in the front end queue to crawl");
							return CrError.CR_EMPTY_QUEUE;
						} else {
							// Dequeue from the front end queue until we find a new web server that hasn't exists in the map yet
							boolean foundNew = false;
							while (true) {
								if (m_frontEndQueue.isEmpty()) {
									break;
								}
								
								URLObject curUrl = m_frontEndQueue.remove();
								if (curUrl.getDomain() == null) {
									continue;
								}
								
								// Ignore pages that are too far from relevant page
								if (curUrl.get_distanceFromRelevantPage() > Globals.MAXDISTANCEFROMRELEVANTPAGE) {
									continue;
								}

								if (!m_domainToBackEndQueueMap.containsKey(curUrl.getDomain()) && foundNew) {
									break;
								}
								
								if (!m_domainToBackEndQueueMap.containsKey(curUrl.getDomain()) && !foundNew) {
									writeGenericLog("Create new back end queue with new domain " + curUrl.getDomain());
									BackEndQueue newBackEndQueue = new BackEndQueue();
									newBackEndQueue.setDomain(curUrl.getDomain());
									newBackEndQueue.setPriority(curUrl.get_priority());
									
									m_backEndQueues.add(newBackEndQueue);
									m_domainToBackEndQueueMap.put(curUrl.getDomain(), newBackEndQueue);
									
									foundNew = true;
								}
								
								hr = m_domainToBackEndQueueMap.get(curUrl.getDomain()).pushUrl(curUrl);
								if (FAILED(hr)) {
									return hr;
								}
							}
							
							StringBuilder builder = new StringBuilder();
							builder.append("Num backend queues : " + m_domainToBackEndQueueMap.size() + "\n");
							for (Map.Entry<String, BackEndQueue> entry : m_domainToBackEndQueueMap.entrySet()) {
								builder.append("Domain " + entry.getKey() + " with queue size " + entry.getValue().size() + "\n");
							}
							writeGenericLog(builder.toString());
						}
					}
				}
				
				// The queue shouldn't be empty here
				if (m_backEndQueues.isEmpty()) {
					writeGenericLog("Back end queues is empty, unexpected");
					System.exit(1);
				}
				
				backEndQueue = m_backEndQueues.remove();
				
				if (backEndQueue.size() > 0) {
					break;
				} else {
					m_domainToBackEndQueueMap.remove(backEndQueue.getDomain());
				}
			}
		}
		}
			
		if (backEndQueue == null) {
			writeGenericLog("Back end queue is null unexpected");
			return CrError.CR_UNEXPECTED;
		}
		
		int numResults = 0;
		while (numResults < maxNumUrls) {
			URLObject url = new URLObject();

			hr = backEndQueue.pullUrl(url);
			if (FAILED(hr)) {
				writeGenericLog("Pull urls from backend queue fail with hr = " + hr);
				break;
			}
			
			outUrls.add(url);

			++numResults;
		}
		
		// Wait for politeness to the webserver
		long minNextProcessTimeInMillisec = backEndQueue.get_minNextProcessTimeInMillisec();
		long waitDuration = minNextProcessTimeInMillisec - Helper.getCurrentTimeInMillisec();
		if (minNextProcessTimeInMillisec >0 && waitDuration > 0) {
			if (waitDuration > Globals.MAXWAITTIMETOPULLURLFROMFRONTIERINMILLISEC) {
				waitDuration = Globals.MAXWAITTIMETOPULLURLFROMFRONTIERINMILLISEC;
			}

			Helper.waitMilliSec(waitDuration, waitDuration);
		}
		
		return CrError.CR_OK;
	}

	@Override
	public CrError pushUrls(URLObject originalUrl, ArrayList<URLObject> inUrls) {
		CrError hr = CrError.CR_OK;
		
		for (URLObject inUrl : inUrls) {
			hr = this.pushUrl(originalUrl, inUrl);
			if (FAILED(hr)) {
				return hr;
			}
		}
		
		releaseBackEndQueue(originalUrl);
		
		return CrError.CR_OK;
	}
}
