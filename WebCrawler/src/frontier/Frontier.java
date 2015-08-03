package frontier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import common.ErrorCode.CrError;
import common.URLObject;

public class Frontier implements IFrontier {
	// Back end queue that disperse urls to crawler threads
	private PriorityQueue<BackEndQueue> m_backEndQueues;

	// Front end queue that crawler threads enqueue into
	private PriorityQueue<URLObject> m_frontEndQueue;
	
	// Table mapping between domain and the backend queue
	private Map<String, BackEndQueue> m_domainToBackEndQueueMap;

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
		private Queue<URLObject> m_urlsQueue = new PriorityQueue<URLObject>();
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
			
			url = nextUrl;
			
			return hr; 
		}
		
		private synchronized CrError pushUrl(URLObject url) {
			m_urlsQueue.add(url);
			
			return CrError.CR_OK;
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
	}
	
	public Frontier(int numQueues)
	{
		m_frontEndQueue = new PriorityQueue<URLObject>();
		m_backEndQueues = new PriorityQueue<BackEndQueue>(numQueues /* initialCapacity */, new BackEndQueueComparator());
		m_domainToBackEndQueueMap = new HashMap<String, BackEndQueue>();
	}
	
	@Override
	public CrError pullUrl(URLObject outUrl) {
		// TODO Auto-generated method stub
		return CrError.CR_OK;
	}

	@Override
	public CrError pushUrl(URLObject inUrl) {
		synchronized(m_frontEndQueue) {
			m_frontEndQueue.add(inUrl);
		}

		return CrError.CR_OK;
	}

	@Override
	public CrError pullUrls(ArrayList<URLObject> outUrls) {
		// TODO Auto-generated method stub
		return CrError.CR_OK;
	}

	@Override
	public CrError pushUrls(ArrayList<URLObject> inUrls) {
		synchronized(m_frontEndQueue) {
			for (URLObject inUrl : inUrls) {
				m_frontEndQueue.add(inUrl);
			}
		}
		
		return CrError.CR_OK;
	}
}
