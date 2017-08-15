package common;

public class Globals {
    // Database
    public static final String username = "root";
    public static final String password = "";
    public static final String server = "localhost:3306/";
    public static final String database = "news";

    // Number of threads
    // public static final int NTHREADS = 1;
    public static final int NTHREADS = 5;

    // Link types
    public static final String[] LINKTYPES = {
            "MAIN_PAGE",
            "SEARCH_PAGE",
            "TOPIC_PAGE",
            "ARTICLE_PAGE",
            "OTHER_PAGE"};
}
