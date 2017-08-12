package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import common.URLObject;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import common.Globals;
import common.Helper;

public class InitializeDB {
    private static Logger LOG = LogManager.getLogger(InitializeDB.class.getName());

    private Connection con = null;
    private String username = null;
    private String password = null;
    private String server = null;
    private String database = null;

    private LinkQueueDAO m_linkQueueDAO = null;
    private DomainDAO m_domainDAO = null;
    private Map<Integer, String> domainIdToDomainMap = null;
    private Map<String, Integer> domainToDomainIdMap = null;

    public InitializeDB(String username, String password, String server, String database) {
        LOG.setLevel(Level.ALL);
        this.username = username;
        this.password = password;
        this.server = server;
        this.database = database;

        // Set up sql connection
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.con = DriverManager.getConnection("jdbc:mysql://" + this.server, this.username, this.password);

            DAOFactory daoFactory = DAOFactory.getInstance(username, password, server + database);
            this.m_linkQueueDAO = new LinkQueueDAOJDBC(daoFactory);
            this.m_domainDAO = new DomainDAOJDBC(daoFactory);

            this.domainIdToDomainMap = new HashMap<>();
            this.domainToDomainIdMap = new HashMap<>();
            List<Domain> domains = this.m_domainDAO.get();
            for (Domain domain : domains) {
                this.domainIdToDomainMap.put(domain.getId(), domain.getDomain());
                this.domainToDomainIdMap.put(domain.getDomain(), domain.getId());
            }
        } catch (ClassNotFoundException e) {
            LOG.error("Driver not found");
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
    }

    // Create all the tables
    public void createDB() {
        this.createDomainTable();
        this.createLinkTable();
        this.createRawHTMLTable();
    }

    // Populate tables with some seed data
    public void populateDB(String[] seedDomains, String[] seedLinkQueue) {
        this.populateDomainTable(seedDomains);
        this.populateLinkTable(seedLinkQueue);
    }

    private void populateDomainTable(String[] seedDomains) {
        for (String domainStr : seedDomains) {
            Domain domain = new Domain();
            domain.setDomain(domainStr);
            try {
                Integer domainId = this.m_domainDAO.create(domain);
                if (domainId <= 0) {
                    continue;
                }
                this.domainIdToDomainMap.put(domainId, domainStr);
                this.domainToDomainIdMap.put(domainStr, domainId);
            } catch (SQLException e) {
                LOG.error("INSERT INTO TABLE domain_table fails, " + e.getMessage());
            }
        }

    }

    private void populateLinkTable(String[] seedLinkQueue) {
        for (String linkQueueStr : seedLinkQueue) {
            Integer domainId = -1;
            for (Map.Entry<String, Integer> entry : this.domainToDomainIdMap.entrySet()) {
                if (linkQueueStr.contains(entry.getKey())) {
                    domainId = entry.getValue();
                    LOG.info("Found domainId " + domainId);
                    break;
                }
            }
            if (domainId == -1) {
                continue;
            }

            URLObject urlObject = new URLObject();
            urlObject.setLink(linkQueueStr);
            urlObject.set_originalLink(linkQueueStr);
            urlObject.set_absolute(true);
            urlObject.set_priority(0);
            urlObject.set_extractedTime(0);
            urlObject.set_crawledTime(0);
            urlObject.set_downloadDuration(0);
            urlObject.set_httpStatusCode(200);
            urlObject.set_relevance(Globals.MAXRELEVANCESCORE);
            urlObject.set_distanceFromRelevantPage(0);
            urlObject.set_freshness(0);
            LinkQueue linkQueue = new LinkQueue();
            linkQueue.Assign(urlObject);
            linkQueue.setDomainTableId1(domainId);
            try {
                this.m_linkQueueDAO.create(linkQueue);
            } catch (SQLException e) {
                LOG.error("INSERT INTO TABLE link_queue_table fails, " + e.getMessage());
            }
        }
    }

    // Create domain_table
    private void createRawHTMLTable() {
        try {
            Statement st = this.con.createStatement();
            st.executeQuery("USE " + this.database);
            st.executeUpdate("CREATE TABLE rawhtml_table ("
                    + "id int unsigned not null, "
                    + "html mediumtext not null, "
                    + "PRIMARY KEY(id), "
                    + "UNIQUE (id), "
                    + "FOREIGN KEY (id) REFERENCES link_crawled_table(id))");
        } catch (SQLException e) {
            LOG.error("CREATE TABLE rawhtml_table fails, " + e.getMessage());
        }
    }

    // Create domain_table
    private void createDomainTable() {
        try {
            Statement st = this.con.createStatement();
            st.executeQuery("USE " + this.database);
            st.executeUpdate("CREATE TABLE domain_table ("
                    + "id int unsigned AUTO_INCREMENT not null, "
                    + "domain char(255) not null, "
                    + "PRIMARY KEY(id), "
                    + "UNIQUE (id), "
                    + "UNIQUE (domain))");
        } catch (SQLException e) {
            LOG.error("CREATE TABLE domain_table fails, " + e.getMessage());
        }
    }

    // Create link_queue_table and link_crawled_table
    private void createLinkTable() {
        try {
            Statement st = this.con.createStatement();
            st.executeQuery("USE " + this.database);

            st.executeUpdate("CREATE TABLE link_queue_table ("
                    + "id int unsigned AUTO_INCREMENT not null, "
                    + "link char(255) not null, "
                    + "originalLink char(255) not null, "
                    + "domain_table_id_1 int unsigned not null, "
                    + "priority int unsigned, "
                    + "persistent int unsigned, "
                    + "extracted_time bigint unsigned not null, "
                    + "relevance int not null, "
                    + "distanceFromRelevantPage int not null, "
                    + "freshness int unsigned not null, "
                    + "time_crawled char(128) not null, "
                    + "date_crawled char(128) not null, "
                    + "PRIMARY KEY(id), "
                    + "UNIQUE (id), "
                    + "UNIQUE INDEX link_date (link, date_crawled), "
                    + "FOREIGN KEY (domain_table_id_1) REFERENCES domain_table(id))");

            st.executeUpdate("CREATE TABLE link_crawled_table ("
                    + "id int unsigned AUTO_INCREMENT not null, "
                    + "link char(255) not null, "
                    + "originalLink char(255) not null, "
                    + "priority int unsigned, "
                    + "domain_table_id_1 int unsigned not null, "
                    + "download_duration bigint unsigned not null, "
                    + "extracted_time bigint unsigned not null, "
                    + "statusCode int not null, "
                    + "relevance int not null, "
                    + "distanceFromRelevantPage int not null, "
                    + "freshness int unsigned not null, "
                    + "time_crawled char(128) not null, "
                    + "date_crawled char(128) not null, "
                    + "PRIMARY KEY(id), "
                    + "UNIQUE (id), "
                    + "FOREIGN KEY (domain_table_id_1) REFERENCES domain_table(id))");
        } catch (SQLException e) {
            LOG.error("CREATE TABLE link_queue_table or link_crawled_table fails, " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        InitializeDB con = new InitializeDB(Globals.username, Globals.password, Globals.server, Globals.database);
        con.createDB();
        con.populateDB(Globals.SEEDDOMAINS, Globals.SEEDLINKQUEUE);
    }
}
