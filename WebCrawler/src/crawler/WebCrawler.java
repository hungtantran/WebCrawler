package crawler;

import java.util.ArrayList;

import common.ErrorCode.*;
import common.IWebPage;

import static common.ErrorCode.*;
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
		CrError hr = CrError.CR_OK;

		while (true)
		{
			// Get urls from frontier
			URLObject outUrl = new URLObject();
			hr = this.frontier.pullUrl(outUrl);
			if (FAILED(hr))
			{
				break;
			}
			
			//
			// Process url got from the frontier
			//
			
			// Fetch the url from the web server
			IWebPage webPage = new WebPage();
			hr = this.httpFetcher.getWebPage(outUrl,  webPage);
			if (FAILED(hr))
			{
				break;
			}
			
			// Extract all links from page
			ArrayList<URLObject> extractedUrls = new ArrayList<URLObject>();
			hr = this.linkExtractor.extractURLs(webPage, extractedUrls);
			if (FAILED(hr))
			{
				break;
			}
			
			// Distribute links to appropriate distributor
			hr = this.urlDistributor.distributeURLs(extractedUrls);
			if (FAILED(hr))
			{
				break;
			}
			
			// Filter out unwanted url like url with parameter, malformed, etc...
			hr = this.urlFilter.filterURLs(extractedUrls);
			if (FAILED(hr))
			{
				break;
			}
			
			// Remove duplicated urls
			hr = this.urlDuplicationEliminator.eliminateDuplicatedURLs(extractedUrls);
			if (FAILED(hr))
			{
				break;
			}
			
			// Prioritize urls
			hr = this.urlPrioritizer.prioritizeUrl(extractedUrls);
			if (FAILED(hr))
			{
				break;
			}
			
			// Push prioritized urls back into the frontier
			hr = this.frontier.pushUrls(extractedUrls);
			if (FAILED(hr))
			{
				break;
			}
		}

		return hr;
	}
}
