package common;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Globals {
	public static final String PATHSEPARATOR = File.separator;
	
	// Directory to output log
	public static final String DEFAULTLOGDIRECTORY = "Log";
	
	// Prefix for log file name
	public static final String DEFAULTLOGFILEPREFIX = "generalLog";
	
	// The duration each log file should exists for in minutes
	public static final int DEFAULTLOGFILEDURATIONINMIN = 5; 
	
	// The size of the log writer buffer before writing to disk
	public static final int MAXLOGBUFFERSIZEINMB = 1;
	
	// Number of crawling thread
	public static final int NTHREADS = 5;
	
	// Number of backend queue in the url frontier (should be approximately NTHREADS * 3)
	public static final int NQUEUES = 12;
	
	// Number of milliseconds thread have to wait to dequeue from a frontier backend queue
	public static final long BACKENDQUEUEWAITINMILLISEC = 2000;
	
	// Number of number of retry times to fetch a page
	public static final int NRETRIESDOWNLOAD = 3;
	
	// Number of megabytes used for the url duplication eliminator bloomfilter
	public static final int NMEGABYTESFORBLOOMFILTER = 32;
	
	// Directory to save/read bloomfilter
	public static final String DEFAULTBLOOMFILTERDIRECTORY = "Bloomfilter";
	
	// Prefix for bloomfilter filename
	public static final String DEFAULTBLOOMFILTERFILENAME = "bloomfilter";
	
	// The number of query to bloomfilter before writing it down to disk
	public static final int MAXWRITETOBLOOMFILTERBEFOREFLUSHINGTODISK = 100;
	
	// Politeness factor to download a webpage, used in frontier
	public static final int NPOLITENESSFACTOR = 10;
	
	// Maximum number of urls pull out of frontier database at a time
	public static final int NMAXURLSFROMFRONTIERPERPULL = 10;
	
	// Minimum wait time to pull a page from frontier
	public static final long MINWAITTIMETOPULLURLFROMFRONTIERINMILLISEC = 2 * 1000;
	
	// Maximum wait time to pull a page from frontier
	public static final long MAXWAITTIMETOPULLURLFROMFRONTIERINMILLISEC = 5 * 60 * 1000;
	
	// Maximum relevance score
	public static final long MAXRELEVANCESCORE = 10000;
	
	// Minimum relevance score of a page for it to be considered relevant
	public static final long MINRELEVANCESCORETOBERELEVANT = 3000;
	
	// Relevance decay factor from parent to child page
	public static final double RELEVANCEDECAYFACTOR = 0.8;
	
	// Max distance from relevant page to be considered
	public static final int MAXDISTANCEFROMRELEVANTPAGE = 2;
	
	// Blacklist domain
	public static final String[] BLACKLISTDOMAINS = {
			"https://www.facebook.com",
			"http://www.facebook.com",
			"https://twitter.com",
			"http://twitter.com",
			"https://www.google.com",
			"http://www.google.com",
			"https://www.bing.com",
			"http://www.bing.com",
			"https://www.pinterest.com",
			"http://www.pinterest.com",
			"https://www.youtube.com",
			"http://www.youtube.com",
			"https://bit.ly",
			"http://bit.ly",
			"https://t.co",
			"http://t.co"};
	public static Set<String> BLACKLISTDOMAINSET = new HashSet<String>();
	static {
		for (int i = 0; i < BLACKLISTDOMAINS.length; ++i) {
			BLACKLISTDOMAINSET.add(BLACKLISTDOMAINS[i]);
		}
	}
	
	// Database
	public static final String username = "root";
	public static final String password = "";
	public static final String server = "localhost:3306/";
	public static final String database = "webcrawler2";
}
