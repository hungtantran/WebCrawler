package crawler;

import common.ErrorCode.*;
import static common.ErrorCode.*;

public class Main {
	public static void main(String[] args) {
		WebCrawler crawler = new WebCrawler();
		
		// Only return when error happens. Otherwise, while true loop
		CrError result = crawler.crawl();
		
		if (FAILED(result))
		{
			//TODO: log error or do something
		}
	}
}
