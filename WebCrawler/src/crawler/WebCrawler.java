package crawler;

import static common.ErrorCode.FAILED;
import static common.LogManager.writeGenericLog;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import common.ErrorCode.CrError;
import common.Globals;
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
	private IFrontier frontier = null;
	private IHttpFetcher httpFetcher = null;
	private ILinkExtractor linkExtractor = null;
	private IURLDistributor urlDistributor = null;
	private IURLFilter urlFilter = null;
	private IURLDuplicationEliminator urlDuplicationEliminator = null;
	private IURLPrioritizer urlPrioritizer = null;
	
	private static final ExecutorService exec = Executors.newFixedThreadPool(Globals.NTHREADS);
	
	private class CrawlTask implements Runnable
	{
		private CrError hr = CrError.CR_OK; 
		private Long threadId;
				
		public CrawlTask() {
		}
	
		@Override
		public void run() {
			threadId = Thread.currentThread().getId();
			writeGenericLog("Start thread " + this.threadId);

			while (true)
			{
				// Get urls from frontier
				URLObject outUrl = new URLObject();
				hr = frontier.pullUrl(outUrl);
				if (FAILED(hr))
				{
					break;
				}
				
				//
				// Process url got from the frontier
				//
				
				// Fetch the url from the web server
				IWebPage webPage = new WebPage();
				hr = httpFetcher.getWebPage(outUrl,  webPage);
				if (FAILED(hr))
				{
					break;
				}
				
				// Extract all links from page
				ArrayList<URLObject> extractedUrls = new ArrayList<URLObject>();
				hr = linkExtractor.extractURLs(webPage, extractedUrls);
				if (FAILED(hr))
				{
					break;
				}
				
				// Distribute links to appropriate distributor
				hr = urlDistributor.distributeURLs(extractedUrls);
				if (FAILED(hr))
				{
					break;
				}
				
				// Filter out unwanted url like url with parameter, malformed, etc...
				hr = urlFilter.filterURLs(extractedUrls);
				if (FAILED(hr))
				{
					break;
				}
				
				// Remove duplicated urls
				hr = urlDuplicationEliminator.eliminateDuplicatedURLs(extractedUrls);
				if (FAILED(hr))
				{
					break;
				}
				
				// Prioritize urls
				hr = urlPrioritizer.prioritizeUrl(extractedUrls);
				if (FAILED(hr))
				{
					break;
				}
				
				// Push prioritized urls back into the frontier
				hr = frontier.pushUrls(extractedUrls);
				if (FAILED(hr))
				{
					break;
				}
			}
		}
	}
	
	public WebCrawler()
	{
		frontier = new Frontier();
		httpFetcher = new HttpFetcher();
		linkExtractor = new LinkExtractor();
		urlDistributor = new URLDistributor();
		urlFilter = new URLFilter();
		urlDuplicationEliminator = new URLDuplicationEliminator();
		urlPrioritizer = new URLPrioritizer();
	}
	
	public CrError crawl() {
		for (int i = 0; i < Globals.NTHREADS + 5; ++i) {
			exec.execute(new CrawlTask());
		}
		
		exec.shutdown();

		try {
			exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			writeGenericLog(e.getMessage());
		}
		
		return CrError.CR_OK;
	}
}
