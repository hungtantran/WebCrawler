package frontier;

import java.util.ArrayList;

import common.ErrorCode.CrError;
import common.URLObject;

public interface IFrontier {
	public CrError pullUrl(URLObject outUrl);
	
	public CrError pullUrls(ArrayList<URLObject> outUrls);
	
	public CrError pushUrl(URLObject inUrl);
	
	public CrError pushUrls(ArrayList<URLObject> inUrls);
}
