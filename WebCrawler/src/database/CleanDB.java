package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.URLObject;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import common.Globals;
import common.Helper;

public class CleanDB {
    private static Logger LOG = LogManager.getLogger(InitializeDB.class.getName());

    private Connection con = null;
    private String username = null;
    private String password = null;
    private String server = null;
    private String database = null;

    public CleanDB(String username, String password, String server, String database) {
        LOG.setLevel(Level.ALL);
        this.username = username;
        this.password = password;
        this.server = server;
        this.database = database;

        // Set up sql connection
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.con = DriverManager.getConnection("jdbc:mysql://" + this.server, this.username, this.password);
        } catch (ClassNotFoundException e) {
            LOG.error("Driver not found");
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
    }

    // Drop all the tables
    public void cleanDB() {
        this.dropDomainTable();
        this.dropLinkTable();
        this.dropRawHTMLTable();
    }

    // Drop domain_table
    private void dropRawHTMLTable() {
        try {
            Statement st = this.con.createStatement();
            st.executeQuery("USE " + this.database);
            st.executeUpdate("DROP TABLE rawhtml_table");
        } catch (SQLException e) {
            LOG.error("DROP TABLE rawhtml_table fails, " + e.getMessage());
        }
    }

    // Drop domain_table
    private void dropDomainTable() {
        try {
            Statement st = this.con.createStatement();
            st.executeQuery("USE " + this.database);
            st.executeUpdate("DROP TABLE domain_table");
        } catch (SQLException e) {
            LOG.error("DROP TABLE domain_table fails, " + e.getMessage());
        }
    }

    // Drop link_queue_table and link_crawled_table
    private void dropLinkTable() {
        try {
            Statement st = this.con.createStatement();
            st.executeQuery("USE " + this.database);

            st.executeUpdate("DROP TABLE link_queue_table");
            st.executeUpdate("DROP TABLE link_crawled_table");
        } catch (SQLException e) {
            LOG.error("DROP TABLE link_queue_table or link_crawled_table fails, " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        CleanDB con = new CleanDB(Globals.username, Globals.password, Globals.server, Globals.database);
        con.cleanDB();
    }
}
