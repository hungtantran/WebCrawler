package urlDistributor;

import java.util.ArrayList;

import common.ErrorCode.CrError;
import common.URLObject;

public interface IURLDistributor {
	public CrError distributeURLs(ArrayList<URLObject> urls);

	public CrError distributeURL(URLObject url);
}
