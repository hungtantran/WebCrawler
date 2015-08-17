package database;

import java.util.ArrayList;
import java.util.Set;

import common.ErrorCode.CrError;
import common.IWebPage;
import common.URLObject;

public interface IDatabaseConnection {
	// maxUrls = 0 means no limit
	public CrError pullFrontierDatabase(ArrayList<URLObject> outUrls, int maxUrls);
	
	public CrError pushFrontierDatabase(ArrayList<URLObject> inUrls);
	
	// maxUrls = 0 means no limit
	public CrError pullURLDistributorDatabase(ArrayList<URLObject> outUrls, int maxUrls);
	
	public CrError pushURLDistributorDatabase(ArrayList<URLObject> inUrls);
	
	// maxUrls = 0 means no limit
	public CrError pullURLDuplicationDatabase(ArrayList<URLObject> outUrls, int maxUrls);
	
	public CrError pushURLDuplicationDatabase(ArrayList<URLObject> inUrls);

	public boolean checkURLDuplicationDatabase(URLObject inUrl);
	
	public CrError storeWebPage(IWebPage inWebPage);
	
	public CrError getExistingDomains(Set<String> domains);
}
