package unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import common.ErrorCode.CrError;
import common.IWebPage;
import common.URLObject;
import common.WebPage;
import httpFetcher.HttpFetcher;
import httpFetcher.IHttpFetcher;
import linkExtractor.ILinkExtractor;
import linkExtractor.LinkExtractor;

public class Test_ILinkExtractor {

	@Test
	public void test() {
		Test_LinkExtractor();
	}
	
	public void Test_LinkExtractor() {
		CrError hr = CrError.CR_OK;

		IHttpFetcher httpFetcher = new HttpFetcher();
		
		URLObject inUrl = new URLObject();
		inUrl.setDomain("https://www.hodinkee.com/");
		inUrl.setLink("https://www.hodinkee.com/");

		IWebPage downloadedWebPage = new WebPage();

		try {
			hr = httpFetcher.getWebPage(inUrl, downloadedWebPage);
			assertEquals(hr, CrError.CR_OK);
			
			assertTrue("Hodinkee HTML is not null or empty", downloadedWebPage.getString() != null && downloadedWebPage.getString().length() > 0);
			assertTrue("Hodinkee Document is not null", downloadedWebPage.getDocument() != null);
			
			ILinkExtractor linkExtractor = new LinkExtractor();
			ArrayList<URLObject> extractedUrls = new ArrayList<URLObject>();

			hr = linkExtractor.extractURLs(inUrl, downloadedWebPage, extractedUrls);
			assertEquals(hr, CrError.CR_OK);
			
			for (URLObject extractedUrl : extractedUrls)
			{
				System.out.println(extractedUrl.getLink() + " " + extractedUrl.getAbsoluteLink());
				assertNotEquals(null, extractedUrl.getLink());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			assertTrue(false);
		}
	}
}
