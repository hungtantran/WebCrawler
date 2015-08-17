package linkExtractor;

import static common.LogManager.writeGenericLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import common.ErrorCode.CrError;
import common.Helper;
import common.IWebPage;
import common.URLObject;

public class LinkExtractor implements ILinkExtractor {
	public LinkExtractor() {	
	}
	
	private String sanitizeURL(String url) {
		if (url == null) {
			return url;
		}
		
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		
		int index = url.indexOf("#");
		if (index != -1) {
			url = url.substring(0, index);
		}
		
		return url;
	}
	
	@Override
	public CrError extractURLs(URLObject originalUrl, IWebPage webPage, ArrayList<URLObject> extractedUrls) {
		if (webPage == null || extractedUrls == null) {
			return CrError.CR_INVALID_ARGS;
		}
		
		Document doc = webPage.getDocument();
		if (doc == null) {
			return CrError.CR_INVALID_ARGS;
		}

		Set<String> extractedUrlsSet = new HashSet<String>();
		
		StringBuilder builder = new StringBuilder();
		builder.append("Extract links: ");

		Elements linkElems = doc.select("a[href]");
		for (Element linkElem : linkElems) {
			URLObject url = new URLObject();
			
			String link = linkElem.attr("href").toString().trim();
			link = sanitizeURL(link);

			// Don't duplicate url
			if (extractedUrlsSet.contains(link)) {
				continue;
			} else {
				extractedUrlsSet.add(link);
			}

			builder.append(link + "; ");

			url.setDomain(originalUrl.getDomain());
			url.setLink(link);
			url.set_originalLink(originalUrl.getAbsoluteLink());
			url.set_downloadDuration(webPage.getDownloadDuationInhMillisec());
			url.set_extractedTime(Helper.getCurrentTimeInMillisec());
			extractedUrls.add(url);
		}
		
		builder.append(". Size: " + extractedUrls.size());
		writeGenericLog(builder.toString());
		
		return CrError.CR_OK;
	}

}
