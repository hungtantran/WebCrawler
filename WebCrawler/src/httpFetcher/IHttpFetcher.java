package httpFetcher;

import common.ErrorCode.CrError;
import common.IWebPage;
import common.URLObject;

public interface IHttpFetcher {
	public CrError getWebPage(URLObject inUrl, IWebPage downloadedWebPage) throws Exception;
}
