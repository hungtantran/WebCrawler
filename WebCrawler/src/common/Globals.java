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
}
