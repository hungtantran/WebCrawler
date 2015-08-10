package common;

import java.io.File;

public class Globals {
	public static final String PATHSEPARATOR = File.separator;
	
	// Number of crawling thread
	public static final int NTHREADS = 4;
	
	// Number of backend queue in the url frontier (should be approximately NTHREADS * 3)
	public static final int NQUEUES = 12;
	
	// Number of milliseconds thread have to wait to dequeue from a frontier backend queue
	public static final long BACKENDQUEUEWAITINMILLISEC = 2000;
	
	// Number of number of retry times to fetch a page
	public static final int NRETRIESDOWNLOAD = 3;
	
	// Number of megabytes used for the url duplication eliminator bloomfilter
	public static final int NMEGABYTESFORBLOOMFILTER = 512; 
	
	// Politeness factor to download a webpage, used in frontier
	public static final int NPOLITENESSFACTOR = 10;
	
	// Maximum number of urls pull out of frontier database at a time
	public static final int NMAXURLSFROMFRONTIERPERPULL = 10;
	
	// Maximum wait time to pull a page from frontier
	public static final long MAXWAITTIMETOPULLURLFROMFRONTIERINMILLISEC = 5 * 60 * 1000;
	
	// Maximum relevance score
	public static final long MAXRELEVANCESCORE = 10000;
	
	// Minimum relevance score of a page for it to be considered relevant
	public static final long MINRELEVANCESCORETOBERELEVANT = 3000;
	
	// Relevance decay factor from parent to child page
	public static final double RELEVANCEDECAYFACTOR = 0.8;
	
	// Max distance from relevant page to be considered
	public static final int MAXDISTANCEFROMRELEVANTPAGE = 5;
	
	// Database
	public static final String username = "root";
	public static final String password = "";
	public static final String server = "localhost:3306/";
	public static final String database = "webcrawler";
}
