package linkExtractor;

import java.util.ArrayList;

import common.ErrorCode.CrError;
import common.IWebPage;
import common.URLObject;

public interface ILinkExtractor {
	public CrError extractURLs(URLObject originalUrl, IWebPage webPage, ArrayList<URLObject> extractedUrls); 
}
