package linkExtractor;

import java.util.ArrayList;

import common.ErrorCode.CrError;
import common.IWebPage;
import common.URLObject;

public interface ILinkExtractor {
	public CrError extractURLs(IWebPage webPage, ArrayList<URLObject> extractedUrls); 
}
