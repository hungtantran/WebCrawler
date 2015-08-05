package crawler;

import static common.ErrorCode.FAILED;
import static common.LogManager.writeGenericLog;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import common.ErrorCode.CrError;
import common.Globals;
import common.Helper;
import common.IWebPage;
import common.URLObject;
import common.WebPage;
import frontier.Frontier;
import frontier.IFrontier;
import httpFetcher.HttpFetcher;
import httpFetcher.IHttpFetcher;
import linkExtractor.ILinkExtractor;
import linkExtractor.LinkExtractor;
import urlDistributor.IURLDistributor;
import urlDistributor.URLDistributor;
import urlDuplicationEliminator.IURLDuplicationEliminator;
import urlDuplicationEliminator.URLDuplicationEliminator;
import urlFilter.IURLFilter;
import urlFilter.URLFilter;
import urlPrioritizer.IURLPrioritizer;
import urlPrioritizer.URLPrioritizer;

public class WebCrawler {
	private IFrontier m_frontier = null;
	private IHttpFetcher m_httpFetcher = null;
	private ILinkExtractor m_linkExtractor = null;
	private IURLDistributor m_urlDistributor = null;
	private IURLFilter m_urlFilter = null;
	private IURLDuplicationEliminator m_urlDuplicationEliminator = null;
	private IURLPrioritizer m_urlPrioritizer = null;
	
	private static final ExecutorService m_exec = Executors.newFixedThreadPool(Globals.NTHREADS);
	
	private class CrawlTask implements Runnable
	{
		private CrError hr = CrError.CR_OK; 
		private Long threadId;
				
		private CrawlTask() {
		}
	
		@Override
		public void run() {
			threadId = Thread.currentThread().getId();
			writeGenericLog("Start thread " + this.threadId);

			while (true)
			{
				// Get urls from frontier
				URLObject outUrl = new URLObject();
				hr = m_frontier.pullUrl(outUrl);
				if (hr == CrError.CR_EMPTY_QUEUE) {
					Helper.waitSec(5, 10);
					continue;
				}
				
				if (FAILED(hr))
				{
					m_frontier.releaseBackEndQueue(outUrl);
					break;
				}
				
				//
				// Process url got from the frontier
				//
				
				// Fetch the url from the web server
				IWebPage webPage = new WebPage();
				try {
					hr = m_httpFetcher.getWebPage(outUrl,  webPage);
				} catch (Exception e) {
					if (outUrl.getLink() != null) {
						writeGenericLog("Fails to fetch webpage " + outUrl.getLink() + " : " + e.getMessage());
					} else {
						writeGenericLog("No outurl " + e.getMessage());
					}
				}

				if (FAILED(hr))
				{
					m_frontier.releaseBackEndQueue(outUrl);
					continue;
				}
				
				// Extract all links from page
				ArrayList<URLObject> extractedUrls = new ArrayList<URLObject>();
				hr = m_linkExtractor.extractURLs(outUrl, webPage, extractedUrls);
				if (FAILED(hr))
				{
					m_frontier.releaseBackEndQueue(outUrl);
					continue;
				}
				
				// Distribute links to appropriate distributor
				hr = m_urlDistributor.distributeURLs(extractedUrls);
				if (FAILED(hr))
				{
					m_frontier.releaseBackEndQueue(outUrl);
					continue;
				}
				
				// Filter out unwanted url like url with parameter, malformed, etc...
				hr = m_urlFilter.filterURLs(extractedUrls);
				if (FAILED(hr))
				{
					m_frontier.releaseBackEndQueue(outUrl);
					continue;
				}
				
				// Remove duplicated urls
				hr = m_urlDuplicationEliminator.eliminateDuplicatedURLs(extractedUrls);
				if (FAILED(hr))
				{
					m_frontier.releaseBackEndQueue(outUrl);
					continue;
				}
				
				// Prioritize urls
				hr = m_urlPrioritizer.prioritizeUrl(extractedUrls);
				if (FAILED(hr))
				{
					m_frontier.releaseBackEndQueue(outUrl);
					continue;
				}
				
				// Push prioritized urls back into the frontier
				hr = m_frontier.pushUrls(outUrl, extractedUrls);
				if (FAILED(hr))
				{
					m_frontier.releaseBackEndQueue(outUrl);
					continue;
				}
			}
		}
	}
	
	public WebCrawler()
	{
		m_frontier = new Frontier(Globals.NQUEUES);
		m_httpFetcher = new HttpFetcher();
		m_linkExtractor = new LinkExtractor();
		m_urlDistributor = new URLDistributor();
		m_urlFilter = new URLFilter();
		m_urlDuplicationEliminator = new URLDuplicationEliminator();
		m_urlPrioritizer = new URLPrioritizer();
	}
	
	public CrError crawl() {
		for (int i = 0; i < Globals.NTHREADS + 5; ++i) {
			m_exec.execute(new CrawlTask());
		}
		
		m_exec.shutdown();

		try {
			m_exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			writeGenericLog(e.getMessage());
		}
		
		return CrError.CR_OK;
	}
}
