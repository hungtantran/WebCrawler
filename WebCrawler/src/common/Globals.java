package common;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import urlFilter.DefaultURLFilterProvider;
import urlFilter.URLFilters;
import urlPrioritizer.PassThroughURLPrioritizerProvider;
import urlPrioritizer.URLPrioritizerProvider;
import urlPrioritizer.URLPrioritizers;
import urlProcessor.DownloadURLProvider;
import urlProcessor.URLProcessors;

public class Globals {
    // Database
    public static final String username = "root";
    public static final String password = "";
    public static final String server = "localhost:3306/";
    public static final String database = "news";

    // Path separator
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
    public static final int MAXDISTANCEFROMRELEVANTPAGE = 3;

    // Whether to limit to only domain of the existing urls in the current frontier and doesn't expand to new domain
    public static final boolean LIMITTOEXISTINGDOMAINS = true;

    // Filter base on include/exlude file types
    public static boolean FILTERBASEDONEXLUCDEFILETYPE = true;
    public static final Set<String> EXLUCDEFILETYPES = new HashSet<>(Arrays.asList(
            // "pdf",
            "jpeg", "png", "jpg", "gif",
            "eps", "ps", "svg", "indd", "pct", "xlr", "xls", "xlsx", "accdb", "db", "dbf", "mdb", "pdb", "sql", "apk", "app", "bat", "cgi", "exe", "gadget", "jar", "pif", "vb", "wsf", "dem", "gam", "nes", "rom", "sav", "dwg", "dxf", "gpx", "kml", "kmz", "asp", "aspx", "cer", "cfm", "csr", "css", "js", "jsp", "php", "rss", "xhtml", "crx", "plugin", "fnt", "fon", "otf", "ttf", "cab", "cpl", "cur", "deskthemepack", "dll", "dmp", "drv", "icns", "ico", "lnk", "sys", "cfg", "ini", "prf", "hqx", "mim", "uue", "7z", "cbr", "deb", "gz", "pkg", "rar", "rpm", "sitx", "tar.gz", "zip", "zipx", "bin", "cue", "dmg", "iso", "mdf", "toast", "vcd", "c", "class", "cpp", "cs", "dtd", "fla", "h", "java", "lua", "m", "pl", "py", "sh", "sln", "swift", "vcxproj", "xcodeproj", "bak", "tmp", "crdownload", "ics", "msi", "part", "torrent"));

    public static boolean FILTERBASEDOINCLUDEFILETYPE = false;
    // public static final Set<String> INLUCDEFILETYPES = new HashSet<String>(Arrays.asList("jpeg", "png", "jpg"));
    public static final Set<String> INLUCDEFILETYPES = new HashSet<>(Arrays.asList("pdf"));

    // The name of the url prioritizer to use
    public static final String URLPRIORITIZER = "PassThroughUrlPrioritizer";

    static {
        URLPrioritizers.RegisterURLPrioritizerProvider("UrlPrioritizer", new URLPrioritizerProvider());
        URLPrioritizers.RegisterURLPrioritizerProvider("PassThroughUrlPrioritizer", new PassThroughURLPrioritizerProvider());
    }

    // The name of the url filter to use
    public static final String URLFILTERTYPE = "DefaultURLFilter";

    static {
        URLFilters.RegisterURLFilterProvider("DefaultURLFilter", new DefaultURLFilterProvider());
    }

    // URL processors to use
    // public static final String[] URLPROCESSORS = { "DownloadURL" };
    public static final String[] URLPROCESSORS = {};

    static {
        URLProcessors.RegisterURLProcessorProvider("DownloadURL", new DownloadURLProvider());
    }

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

    {
        for (int i = 0; i < BLACKLISTDOMAINS.length; ++i) {
            BLACKLISTDOMAINSET.add(BLACKLISTDOMAINS[i]);
        }
    }

    // Seed domains
    public static final String[] SEEDDOMAINS = {
            "https://spectator.org",
            "http://nypost.com",
            "http://www.washingtonexaminer.com",
            "http://www.americanthinker.com",
            "http://www.nationalreview.com",
            "http://www.thegatewaypundit.com"};

    // Seed links
    public static final String[] SEEDLINKQUEUE = {
            "https://spectator.org",
            "http://nypost.com",
            "http://www.washingtonexaminer.com",
            "http://www.americanthinker.com",
            "http://www.nationalreview.com",
            "http://www.thegatewaypundit.com"};
}
