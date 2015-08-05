package frontier;

import java.util.ArrayList;

import common.ErrorCode.CrError;
import common.URLObject;

public interface IFrontier {
	public CrError pullUrl(URLObject outUrl);

	public CrError pullUrls(ArrayList<URLObject> outUrls, int maxNumUrls);
	
	public CrError pushUrl(URLObject originalUrl, URLObject inUrl);
	
	public CrError pushUrls(URLObject originalUrl, ArrayList<URLObject> inUrls);
	
	public CrError releaseBackEndQueue(URLObject originalUrl);
}
