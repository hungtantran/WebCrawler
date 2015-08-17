package urlFilter;

import java.util.ArrayList;
import java.util.Set;

import common.ErrorCode.CrError;
import database.IDatabaseConnection;
import common.URLObject;

public interface IURLFilter {
	public CrError filterURLs(ArrayList<URLObject> inoutUrls);
	
	public CrError setDatabaseConnection(IDatabaseConnection databaseConnection);
	
	public CrError setFilterNonExistingDomains(Set<String> existingDomains);
	
	public CrError setFileTypesToInclude(Set<String> fileTypesToInclude);
	
	public CrError setFileTypesToExclude(Set<String> fileTypesToExclude);
}
