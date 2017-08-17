package database;

import common.Globals;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class InitializeDB {
    private static Logger LOG = LogManager.getLogger(InitializeDB.class.getName());

    private Connection con = null;
    private String username = null;
    private String password = null;
    private String server = null;
    private String database = null;
    private DAOFactory daoFactory = null;

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
            this.daoFactory = DAOFactory.getInstance(username, password, server + database);
        } catch (ClassNotFoundException e) {
            LOG.error("Driver not found");
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
    }

    // Create all the tables
    public void createDB() {
        this.createLinkTypeTable();
        this.createLinkCategoryTable();
        this.createExtractedTextTable();
        this.createDictionaryTable();
        this.createParameterTable();
    }

    // Populate tables with some seed data
    public void populateDB(String[] linkTypes) {
        this.populateLinkTypeTable(linkTypes);
    }

    private void populateLinkTypeTable(String[] linkTypes) {
        try {
            LinkTypeDAO linkTypeDao = new LinkTypeDAOJDBC(this.daoFactory);
            for (String linkTypeStr : linkTypes) {
                LinkType linkType = new LinkType();
                linkType.setType(linkTypeStr);
                try {
                    Integer linkTypeId = linkTypeDao.create(linkType);
                    if (linkTypeId <= 0) {
                        continue;
                    }
                } catch (SQLException e) {
                    LOG.error("INSERT INTO TABLE link_type_table fails, " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            LOG.error("INSERT INTO TABLE link_type_table fails, " + e.getMessage());
        }
    }

    // Create link_type_table
    private void createLinkTypeTable() {
        try {
            Statement st = this.con.createStatement();
            st.executeQuery("USE " + this.database);
            st.executeUpdate("CREATE TABLE link_type_table ("
                    + "id int unsigned AUTO_INCREMENT not null, "
                    + "type char(255) not null, "
                    + "PRIMARY KEY(id), "
                    + "UNIQUE (id), "
                    + "UNIQUE (type))");
        } catch (SQLException e) {
            LOG.error("CREATE TABLE link_type_table fails, " + e.getMessage());
        }
    }

    // Create link_category_table
    private void createLinkCategoryTable() {
        try {
            Statement st = this.con.createStatement();
            st.executeQuery("USE " + this.database);
            st.executeUpdate("CREATE TABLE link_category_table ("
                    + "id int unsigned, "
                    + "type_id int unsigned, "
                    + "PRIMARY KEY(id), "
                    + "UNIQUE (id), "
                    + "FOREIGN KEY (id) REFERENCES link_crawled_table(id),"
                    + "FOREIGN KEY (type_id) REFERENCES link_type_table(id))");
        } catch (SQLException e) {
            LOG.error("CREATE TABLE link_type_table fails, " + e.getMessage());
        }
    }

    // Create extracted_text_table
    private void createExtractedTextTable() {
        try {
            Statement st = this.con.createStatement();
            st.executeQuery("USE " + this.database);
            st.executeUpdate("CREATE TABLE extracted_text_table ("
                    + "id int unsigned not null, "
                    + "extracted_text mediumtext not null, "
                    + "PRIMARY KEY(id), "
                    + "UNIQUE (id), "
                    + "FOREIGN KEY (id) REFERENCES link_crawled_table(id))");
        } catch (SQLException e) {
            LOG.error("CREATE TABLE extracted_text_table fails, " + e.getMessage());
        }

        try {
            Statement st = this.con.createStatement();
            st.executeQuery("USE " + this.database);
            st.executeUpdate("CREATE TABLE clean_extracted_text_table ("
                    + "id int unsigned not null, "
                    + "extracted_text mediumtext not null, "
                    + "PRIMARY KEY(id), "
                    + "UNIQUE (id), "
                    + "FOREIGN KEY (id) REFERENCES link_crawled_table(id))");
        } catch (SQLException e) {
            LOG.error("CREATE TABLE clean_extracted_text_table fails, " + e.getMessage());
        }
    }

    // Create extracted_text_table
    private void createDictionaryTable() {
        try {
            Statement st = this.con.createStatement();
            st.executeQuery("USE " + this.database);
            st.executeUpdate("CREATE TABLE dictionary_table ("
                    + "id int unsigned AUTO_INCREMENT not null, "
                    + "extracted_text char(255) not null, "
                    + "PRIMARY KEY(id), "
                    + "UNIQUE (extracted_text))");
        } catch (SQLException e) {
            LOG.error("CREATE TABLE dictionary_table fails, " + e.getMessage());
        }
    }

    // Create parameters table
    private void createParameterTable() {
        try {
            Statement st = this.con.createStatement();
            st.executeQuery("USE " + this.database);
            st.executeUpdate("CREATE TABLE naive_bayes_param ("
                    + "word int unsigned not null, " // word is xi id (word i in dictionary)
                    + "label int unsigned not null, " // label is y value (whether word i is associated with a label)
                    + "count int unsigned not null, " // count of number of that particular xi and y values appear together
                    + "UNIQUE (word, label), "
                    + "FOREIGN KEY (word) REFERENCES dictionary_table(id))");
        } catch (SQLException e) {
            LOG.error("CREATE TABLE naive_bayes_param fails, " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        InitializeDB con = new InitializeDB(Globals.username, Globals.password, Globals.server, Globals.database);
        con.createDB();
        con.populateDB(Globals.LINKTYPES);
    }
}
