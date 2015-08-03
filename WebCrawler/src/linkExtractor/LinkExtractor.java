package linkExtractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import common.ErrorCode.CrError;
import common.IWebPage;
import common.URLObject;

import static common.LogManager.writeGenericLog;

public class LinkExtractor implements ILinkExtractor {
	public LinkExtractor() {	
	}
	
	@Override
	public CrError extractURLs(IWebPage webPage, ArrayList<URLObject> extractedUrls) {
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
			
			String link = linkElem.attr("href").toString();

			// Don't duplicate url
			if (extractedUrlsSet.contains(link)) {
				continue;
			} else {
				extractedUrlsSet.add(link);
			}

			builder.append(link + "; ");

			url.setLink(link);
			extractedUrls.add(url);
		}
		
		builder.append(". Size: " + extractedUrls.size());
		writeGenericLog(builder.toString());
		
		return CrError.CR_OK;
	}

}
