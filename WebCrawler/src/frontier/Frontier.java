package frontier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import common.ErrorCode.CrError;
import common.Globals;
import common.Helper;
import common.URLObject;

import static common.ErrorCode.*;
import static common.LogManager.*;

public class Frontier implements IFrontier {
	// Back end queue that disperse urls to crawler threads
	private PriorityQueue<BackEndQueue> m_backEndQueues;

	// Front end queue that crawler threads enqueue into
	private Queue<URLObject> m_frontEndQueue;
	
	// Table mapping between domain and the backend queue
	private Map<String, BackEndQueue> m_domainToBackEndQueueMap;
	
	private int m_maxNumBackEndQueues;

	private class BackEndQueueComparator implements Comparator<BackEndQueue> {
		@Override
		public int compare(BackEndQueue arg0, BackEndQueue arg1) {
			if (arg0.getPriority() != arg1.getPriority()) {
				return arg0.getPriority() - arg1.getPriority();
			}
			
			if (arg0.get_minNextProcessTimeInMillisec() - arg1.get_minNextProcessTimeInMillisec() > 0) {
				return 1;
			}
			
			return -1;
		}
	}
	
	// Backend queue, typically each queue contains URLs from 1 or a few web server at most
	private class BackEndQueue {
		private Queue<URLObject> m_urlsQueue = new LinkedList<URLObject>();
		private long m_minNextProcessTimeInMillisec = Long.MIN_VALUE;
		private String m_domain = null;
		private int m_priority;
		
		private BackEndQueue() {
		}
		
		private synchronized CrError pullUrl(URLObject url) {
			CrError hr = CrError.CR_OK;
			
			// Get the next url from queue
			URLObject nextUrl = m_urlsQueue.poll();
			
			// Return error if the queue is empty
			if (nextUrl == null) {
				return CrError.CR_EMPTY_QUEUE;
			}
			
			url.assign(nextUrl);
			
			return hr; 
		}
		
		private synchronized CrError pushUrl(URLObject url) {
			m_urlsQueue.add(url);
			
			return CrError.CR_OK;
		}
		
		private synchronized int size() {
			return m_urlsQueue.size();
		}

		public long get_minNextProcessTimeInMillisec() {
			return m_minNextProcessTimeInMillisec;
		}

		public void set_minNextProcessTimeInMillisec(long m_minNextProcessTimeInMillisec) {
			this.m_minNextProcessTimeInMillisec = m_minNextProcessTimeInMillisec;
		}

		public String getDomain() {
			return m_domain;
		}

		public void setDomain(String domain) {
			this.m_domain = domain;
		}

		public int getPriority() {
			return m_priority;
		}

		public void setPriority(int priority) {
			this.m_priority = priority;
		}
		
		@Override
		public int hashCode() {
			return this.m_domain.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			BackEndQueue queue = (BackEndQueue)obj;
			
			return this.m_domain.equals(queue.getDomain());
		}
	}
	
	public Frontier(int numQueues)
	{
		URLObject url = new URLObject();
		url.setDomain("http://vnexpress.net");
		url.setLink("http://vnexpress.net");
		
		m_frontEndQueue = new LinkedList<URLObject>();
		m_frontEndQueue.add(url);
		m_backEndQueues = new PriorityQueue<BackEndQueue>(numQueues /* initialCapacity */, new BackEndQueueComparator());
		m_domainToBackEndQueueMap = new HashMap<String, BackEndQueue>();
		m_maxNumBackEndQueues = numQueues;
	}
	
	@Override
	public CrError releaseBackEndQueue(URLObject originalUrl) {
		synchronized(m_backEndQueues) {
			if (m_domainToBackEndQueueMap.containsKey(originalUrl.getDomain())) {
				BackEndQueue backEndQueue = m_domainToBackEndQueueMap.get(originalUrl.getDomain());

				if (!m_backEndQueues.contains(backEndQueue)) {
					writeGenericLog("Push back back end queue of domain " + backEndQueue.getDomain());
					backEndQueue.set_minNextProcessTimeInMillisec(Helper.getCurrentTimeInMillisec() + originalUrl.get_downloadDuration() * Globals.NPOLITENESSFACTOR);
					m_backEndQueues.add(backEndQueue);
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
			// TODO log error
			return hr;
		}

		outUrl.assign(outUrls.get(0));
		
		return hr;
	}

	@Override
	public CrError pushUrl(URLObject originalUrl, URLObject inUrl) {
		synchronized(m_frontEndQueue) {
			m_frontEndQueue.add(inUrl);
			writeGenericLog("Push back : " + inUrl.getAbsoluteLink() + ", front end queue size : " + m_frontEndQueue.size());
		}
		
		releaseBackEndQueue(originalUrl);

		return CrError.CR_OK;
	}

	@Override
	public CrError pullUrls(ArrayList<URLObject> outUrls, int maxNumUrls) {
		CrError hr = pullUrlsInternal(outUrls, maxNumUrls);
		
		writeGenericLog("Pull " + outUrls.size() + " urls with hr = " + hr);
		if (FAILED(hr)) {
			// TODO log error
		}
		
		return hr;
	}
	
	public CrError pullUrlsInternal(ArrayList<URLObject> outUrls, int maxNumUrls) {
		BackEndQueue backEndQueue = null;

		synchronized(m_backEndQueues) {
			while (true) {
				if (m_backEndQueues.isEmpty()) {
					if (m_domainToBackEndQueueMap.size() < m_maxNumBackEndQueues) {
						// Emtpy backend queue but the map is not full, try to get url from the front end queue
						synchronized(m_frontEndQueue) {
							if (m_frontEndQueue.isEmpty()) {
								// Empty front end queue, there is no more url to crawl, return error
								writeGenericLog("No more url in the front end queue to crawl");
								return CrError.CR_EMPTY_QUEUE;
							} else {
								URLObject url = m_frontEndQueue.peek();
								BackEndQueue newBackEndQueue = new BackEndQueue();
								newBackEndQueue.setDomain(url.getDomain());
								newBackEndQueue.setPriority(url.get_priority());
								
								m_backEndQueues.add(newBackEndQueue);
								// TODO error this domain is not necessary not only 
								m_domainToBackEndQueueMap.put(url.getDomain(), newBackEndQueue);
								writeGenericLog("Num backend queues : " + m_domainToBackEndQueueMap.size());
								
								CrError hr = CrError.CR_OK;
								// Dequeue from the front end queue until we find a new web server that hasn't exists in the map yet
								while (true) {
									if (m_frontEndQueue.isEmpty()) {
										break;
									}
									
									URLObject curUrl = m_frontEndQueue.remove();
									if (!m_domainToBackEndQueueMap.containsKey(curUrl.getDomain())) {
										break;
									}
									
									hr = m_domainToBackEndQueueMap.get(curUrl.getDomain()).pushUrl(curUrl);
									if (FAILED(hr)) {
										return hr;
									}
								}
							}
						}
					} else {
						// Empty backend queue and the map is already full, return error
						writeGenericLog("All back-end queues are in active. Can't dequeue any more url to crawl.");
						return CrError.CR_EMPTY_QUEUE;
					}
				}
				
				// The queue shouldn't be empty here
				if (m_backEndQueues.isEmpty()) {
					// TODO log error here
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
			
		if (backEndQueue == null) {
			// TODO log error
			return CrError.CR_UNEXPECTED;
		}
		
		int numResults = 0;
		CrError hr = CrError.CR_OK;
		while (numResults < maxNumUrls) {
			URLObject url = new URLObject();

			hr = backEndQueue.pullUrl(url);
			if (FAILED(hr)) {
				// TODO log error
				break;
			}
			
			outUrls.add(url);

			++numResults;
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
