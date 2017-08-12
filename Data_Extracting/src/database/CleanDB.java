package database;

import common.Globals;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
        this.dropExtractedTextTable();
        this.dropLinkCategoryTable();
        this.dropLinkTypeTable();
    }

    // Drop extracted_text_table
    private void dropExtractedTextTable() {
        try {
            Statement st = this.con.createStatement();
            st.executeQuery("USE " + this.database);
            st.executeUpdate("DROP TABLE extracted_text_table");
        } catch (SQLException e) {
            LOG.error("DROP TABLE extracted_text_table fails, " + e.getMessage());
        }
    }

    // Drop link_category_table
    private void dropLinkCategoryTable() {
        try {
            Statement st = this.con.createStatement();
            st.executeQuery("USE " + this.database);
            st.executeUpdate("DROP TABLE link_category_table");
        } catch (SQLException e) {
            LOG.error("DROP TABLE link_category_table fails, " + e.getMessage());
        }
    }

    // Drop link_type_table
    private void dropLinkTypeTable() {
        try {
            Statement st = this.con.createStatement();
            st.executeQuery("USE " + this.database);
            st.executeUpdate("DROP TABLE link_type_table");
        } catch (SQLException e) {
            LOG.error("DROP TABLE link_type_table fails, " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        CleanDB con = new CleanDB(Globals.username, Globals.password, Globals.server, Globals.database);
        con.cleanDB();
    }
}
