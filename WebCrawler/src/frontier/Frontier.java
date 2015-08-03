package frontier;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import common.ErrorCode.CrError;
import common.URLObject;

public class Frontier implements IFrontier {
	// Back end queue that disperse urls to crawler threads
	private PriorityQueue<BackEndQueue> m_backEndQueues;

	// Front end queue that crawler threads enqueue into
	private PriorityBlockingQueue<URLObject> m_frontEndQueue;

	// Backend queue, typically each queue contains URLs from 1 or a few web server at most
	private class BackEndQueue {
		private Queue<URLObject> urlsQueue = new PriorityQueue<URLObject>();
		private long m_minNextDequeueTimeInMillisec = Long.MIN_VALUE;
		
		private BackEndQueue() {
		}
		
		private synchronized CrError pullUrl(URLObject url) {
			CrError hr = CrError.CR_OK;
			
			// Get the next url from queue
			URLObject nextUrl = urlsQueue.poll();
			
			// Return error if the queue is empty
			if (nextUrl == null) {
				return CrError.CR_EMPTY_QUEUE;
			}
			
			url = nextUrl;
			
			return hr; 
		}
		
		private synchronized CrError pushUrl(URLObject url) {
			urlsQueue.add(url);
			
			return CrError.CR_OK;
		}
	}
	
	public Frontier(int numQueues)
	{
		m_frontEndQueue = new PriorityBlockingQueue<URLObject>();
		m_backEndQueues = new PriorityQueue<BackEndQueue>();
	}
	
	@Override
	public CrError pullUrl(URLObject outUrl) {
		// TODO Auto-generated method stub
		return CrError.CR_OK;
	}

	@Override
	public CrError pushUrl(URLObject inUrl) {
		m_frontEndQueue.add(inUrl);

		return CrError.CR_OK;
	}

	@Override
	public CrError pullUrls(ArrayList<URLObject> outUrls) {
		// TODO Auto-generated method stub
		return CrError.CR_OK;
	}

	@Override
	public CrError pushUrls(ArrayList<URLObject> inUrls) {
		for (URLObject inUrl : inUrls) {
			m_frontEndQueue.add(inUrl);
		}
		
		return CrError.CR_OK;
	}
}
