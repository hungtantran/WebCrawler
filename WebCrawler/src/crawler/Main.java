package crawler;

import common.ErrorCode.*;
import common.Globals;

import static common.ErrorCode.*;

import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Main {
	private static Logger LOG = LogManager.getLogger(Main.class.getName()); 
	
	public static void main(String[] args) {
		new Globals();

		WebCrawler crawler;
		try {
			crawler = new WebCrawler(Globals.username, Globals.password, Globals.server, Globals.database);

			// Only return when error happens. Otherwise, while true loop
			CrError hr = crawler.crawl();
			
			if (FAILED(hr))
			{
				LOG.error("Crawl fails, hr = " + hr);
			}
		} catch (ClassNotFoundException e) {
			LOG.error(e.getMessage());
		} catch (SQLException e) {
			LOG.error(e.getMessage());
		}
	}
}
