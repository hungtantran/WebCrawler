package urlPrioritizer;

import java.util.ArrayList;

import common.ErrorCode.CrError;
import common.URLObject;

public interface IURLPrioritizer {
	public CrError prioritizeUrl(URLObject originalUrl, ArrayList<URLObject> inoutUrls);
	
	public CrError prioritizeUrl(URLObject originalUrl, URLObject inoutUrl);
}
