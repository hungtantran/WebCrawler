package urlFilter;

import java.util.ArrayList;

import common.ErrorCode.CrError;
import common.URLObject;

public interface IUrlFilter {
	public CrError filterURLs(ArrayList<URLObject> inoutUrls);
	
	public CrError filterURL(ArrayList<URLObject> inoutUrl);
}
