package crawler;

import common.ErrorCode.*;
import common.Globals;

import static common.ErrorCode.*;
import static common.LogManager.*;

import java.sql.SQLException;

public class Main {
	public static void main(String[] args) {
		WebCrawler crawler;
		try {
			crawler = new WebCrawler(Globals.username, Globals.password, Globals.server, Globals.database);

			// Only return when error happens. Otherwise, while true loop
			CrError hr = crawler.crawl();
			
			if (FAILED(hr))
			{
				writeGenericLog("Crawl fails, hr = " + hr);
			}
		} catch (ClassNotFoundException e) {
			writeGenericLog(e.getMessage());
		} catch (SQLException e) {
			writeGenericLog(e.getMessage());
		}
	}
}
