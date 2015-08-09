package httpFetcher;

import java.util.Random;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import common.ErrorCode.CrError;
import common.Globals;
import common.Helper;
import common.IWebPage;
import common.URLObject;

import static common.LogManager.writeGenericLog;
import static common.ErrorCode.*;

public class HttpFetcher implements IHttpFetcher {
	private static final String[] userAgents = {
        "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6",
        "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)",
        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)",
        "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0 )",
        "Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90)",
        "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1",
        "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.14) Gecko/20080404 Firefox/2.0.0.14",
        "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.29 Safari/525.13",
        "Mozilla/4.8 [en] (Windows NT 6.0; U)",
        "Mozilla/4.8 [en] (Windows NT 5.1; U)",
        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; en) Opera 8.0",
        "Mozilla/5.0 (Windows; U; Win98; en-US; rv:1.4) Gecko Netscape/7.1 (ax)",
        "Mozilla/5.0 (Windows; U; Windows XP) Gecko MultiZilla/1.6.1.0a",
        "Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en) AppleWebKit/125.2 (KHTML, like Gecko) Safari/125.8",
        "Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en) AppleWebKit/125.2 (KHTML, like Gecko) Safari/85.8",
        "Mozilla/4.0 (compatible; MSIE 5.15; Mac_PowerPC)",
        "Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.7a) Gecko/20050614 Firefox/0.9.0+",
        "Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en-US) AppleWebKit/125.4 (KHTML, like Gecko, Safari) OmniWeb/v563.15"
    };
	
	public HttpFetcher()
	{
		
	}
	
	@Override
	public CrError getWebPage(URLObject inUrl, IWebPage downloadedWebPage) throws Exception {
		return getWebPage(inUrl, downloadedWebPage, Globals.NRETRIESDOWNLOAD);
	}
	
	public CrError getWebPage(URLObject inUrl, IWebPage downloadedWebPage, int numRetries) throws Exception {
		CrError hr = CrError.CR_OK;

	    Random rand = new Random(); 
	    int ranIndex = rand.nextInt(HttpFetcher.userAgents.length); 
	    
	    String url = inUrl.getAbsoluteLink();
	    
		for (int i = 0; i < numRetries; i++) {
			try {
				long startTime = Helper.getCurrentTimeInMillisec();
				inUrl.set_crawledTime(startTime);
				
				Connection connection= Jsoup
					.connect(url)
					.userAgent(HttpFetcher.userAgents[ranIndex])
					.header("Proxy-Connection", "keep-alive")
					.header("Cache-Control", "max-age=0")
					.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
					.header("Accept-Encoding", "gzip, deflate, sdch")
					.header("Accept-Language", "en-US,en;q=0.8,de;q=0.6,vi;q=0.4")
					.header("Cookie", "cl_b=mAqo5vfg5BG2A_Lt3WzWGw2dXR8; cl_def_lang=en; cl_def_hp=detroit; cl_tocmode=sss%3Agrid%2Csso%3Alist%2Chhh%3Alist")
					.timeout(10000);
				
				Response response = connection.execute();
				writeGenericLog("Download successfully link " + url + " after " + i + " retries with user agent " + HttpFetcher.userAgents[ranIndex]);
				
				long duration = Helper.getCurrentTimeInMillisec() - startTime;
				writeGenericLog("duration here = " + duration + " start time = " + startTime + " current time = " + Helper.getCurrentTimeInMillisec());
				inUrl.set_downloadDuration(duration);
				inUrl.set_httpStatusCode(200);
				
				downloadedWebPage.setDownloadDuationInMillisec(duration);
				downloadedWebPage.set_originalUrl(inUrl);
				Document doc = response.parse();
				hr = downloadedWebPage.setDocument(doc);
				if (FAILED(hr)) {
					writeGenericLog("Failed to set document, hr = " + hr);
					return hr;
				}
				
				if (SUCCEEDED(hr)) {
					return CrError.CR_OK;
				}
			} catch (Exception e) {
				// Only print out fail on the last fail
				if (i == numRetries - 1) {
					if (e instanceof HttpStatusException) {
						HttpStatusException statusException = (HttpStatusException) e;
						inUrl.set_httpStatusCode(statusException.getStatusCode());
						writeGenericLog("Fail to download link " + url + " after " + i + " retries with user agent " + HttpFetcher.userAgents[ranIndex] + " with status code " + statusException.getStatusCode());
					} else {
						writeGenericLog("Fail to download link " + url + " after " + i + " retries with user agent " + HttpFetcher.userAgents[ranIndex]);
						inUrl.set_httpStatusCode(-1);
					}

					writeGenericLog(e.getMessage());
					throw e;
				}
			}
		}

		return CrError.CR_OK;
	}

}
