package linkExtractor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import common.ErrorCode.CrError;
import common.Helper;
import common.IWebPage;
import common.URLObject;

public class LinkExtractor implements ILinkExtractor {
	private static Logger LOG = LogManager.getLogger(LinkExtractor.class.getName());

	public LinkExtractor() {
		LOG.setLevel(Level.ALL);
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

		Set<String> extractedUrlsSet = new HashSet<>();
		
		StringBuilder builder = new StringBuilder();
		builder.append("Extract links: ");

		Elements linkElems = doc.select("a[href]");
		for (Element linkElem : linkElems) {
			String link = linkElem.attr("href").toString().trim();
			link = sanitizeURL(link);

			// Don't duplicate url
			if (extractedUrlsSet.contains(link)) {
				continue;
			} else {
				extractedUrlsSet.add(link);
			}
		}

		Elements imgLinkElems = doc.select("img[src]");
		for (Element imgLinkElem : imgLinkElems) {
			String link = imgLinkElem.attr("src").toString().trim();
			link = sanitizeURL(link);

			// Don't duplicate url
			if (extractedUrlsSet.contains(link)) {
				continue;
			} else {
				extractedUrlsSet.add(link);
			}
		}
		
		for (String link : extractedUrlsSet) {
			builder.append(link + "; ");

			URLObject url = new URLObject();
			url.setDomain(originalUrl.getDomain());
			url.setLink(link);
			url.set_originalLink(originalUrl.getAbsoluteLink());
			url.set_downloadDuration(webPage.getDownloadDuationInhMillisec());
			url.set_extractedTime(Helper.getCurrentTimeInMillisec());
			extractedUrls.add(url);
		}
		
		builder.append(". Size: " + extractedUrls.size());
		LOG.info(builder.toString());
		
		return CrError.CR_OK;
	}

}
