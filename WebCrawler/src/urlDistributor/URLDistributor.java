package urlDistributor;

import java.util.ArrayList;

import common.ErrorCode.CrError;
import common.URLObject;

public class URLDistributor implements IURLDistributor {
	public URLDistributor() {
		
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
