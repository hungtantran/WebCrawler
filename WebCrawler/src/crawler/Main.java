package crawler;

import common.ErrorCode;
import common.ErrorCode.CrError;

public class Main {
	public void main()
	{
		WebCrawler crawler = new WebCrawler();
		
		// Only return when error happens. Otherwise, while true loop
		CrError result = crawler.crawl();
		
		if (ErrorCode.failed(result))
		{
			//TODO: log error or do something
		}
	}
}
