package frontier;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import common.ErrorCode.CrError;
import common.URLObject;

public class BackEndQueue {
	private Queue<URLObject> m_urlsQueue = new PriorityQueue<URLObject>(200, new URLObject.URLObjectRelevanceAndPriorityComparator());
	private long m_minNextProcessTimeInMillisec = Long.MIN_VALUE;
	private String m_domain = null;
	private int m_priority;
	
	public BackEndQueue() {
	}

	public static class BackEndQueueComparator implements Comparator<BackEndQueue> {
		public BackEndQueueComparator() {
		}

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
	
	public synchronized CrError pullUrl(URLObject url) {
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
	
	public synchronized CrError pushUrl(URLObject url) {
		m_urlsQueue.add(url);
		
		return CrError.CR_OK;
	}
	
	public synchronized int size() {
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
