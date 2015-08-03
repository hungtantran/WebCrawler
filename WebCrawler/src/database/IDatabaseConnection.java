package database;

import java.util.ArrayList;

import common.ErrorCode.CrError;
import common.URLObject;

public interface IDatabaseConnection {
	public CrError pullFrontierDatabase(ArrayList<URLObject> outUrls);
	
	public CrError pushFrontierDatabase(ArrayList<URLObject> inUrls);
	
	public CrError pullURLDistributorDatabase(ArrayList<URLObject> outUrls);
	
	public CrError pushURLDistributorDatabase(ArrayList<URLObject> inUrls);
	
	public CrError pullURLDuplicationDatabase(ArrayList<URLObject> outUrls);
	
	public CrError pushURLDuplicationDatabase(ArrayList<URLObject> inUrls);
}
