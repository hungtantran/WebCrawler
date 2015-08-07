package urlDistributor;

import java.util.ArrayList;

import common.ErrorCode.CrError;
import database.IDatabaseConnection;
import common.URLObject;

public class URLDistributor implements IURLDistributor {
	private IDatabaseConnection m_databaseConnection = null;

	public URLDistributor(IDatabaseConnection databaseConnection) {
		m_databaseConnection = databaseConnection;
	}
	
	@Override
	public CrError distributeURLs(ArrayList<URLObject> urls) {
		// TODO distribute part of the urls to different processes / machines
		return CrError.CR_OK;
	}

	@Override
	public CrError distributeURL(URLObject url) {
		// TODO distribute part of the urls to different processes / machines
		return CrError.CR_OK;
	}
}
