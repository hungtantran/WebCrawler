package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import common.IWebPage;
import common.URLObject;
import common.WebPage;
import common.ErrorCode.CrError;
import httpFetcher.HttpFetcher;
import httpFetcher.IHttpFetcher;

public class Test_IHttpFetcher {

	@Test
	public void test() {
		TestHttpFetcher();
	}
	
	public void TestHttpFetcher() {
		CrError hr = CrError.CR_OK;

		IHttpFetcher httpFetcher = new HttpFetcher();
		
		URLObject inUrl = new URLObject();
		inUrl.setLink("http://www.artnet.com/");

		IWebPage downloadedWebPage = new WebPage();

		try {
			hr = httpFetcher.getWebPage(inUrl, downloadedWebPage);
			assertEquals(hr, CrError.CR_OK);
			
			assertTrue("Artnet HTML is not null or empty", downloadedWebPage.getString() != null && downloadedWebPage.getString().length() > 0);
			assertTrue("Artnet Document is not null", downloadedWebPage.getDocument() != null);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}
}
