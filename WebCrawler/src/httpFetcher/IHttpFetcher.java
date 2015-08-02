package httpFetcher;

import common.ErrorCode.CrError;
import common.IWebPage;

public interface IHttpFetcher {
	public CrError getWebPage(IWebPage downloadedWebPage);
}
