package database;

import static common.LogManager.writeGenericLog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import common.Globals;

public class InitializeDB {
	private Connection con = null;
	private String username = null;
	private String password = null;
	private String server = null;
	private String database = null;

	public InitializeDB(String username, String password, String server, String database) {
		this.username = username;
		this.password = password;
		this.server = server;
		this.database = database;
		
		// Set up sql connection
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.con = DriverManager.getConnection("jdbc:mysql://" + this.server, this.username, this.password);
		} catch (ClassNotFoundException e) {
			writeGenericLog("Driver not found");
		} catch (SQLException e) {
			writeGenericLog(e.getMessage());
		}
	}

	// Create all the tables
	public void createDB() {
		this.createDomainTable();
		this.createLinkTable();
		this.createRawHTMLTable();
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
			writeGenericLog("CREATE TABLE rawhtml_table fails, " + e.getMessage());
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
			writeGenericLog("CREATE TABLE domain_table fails, " + e.getMessage());
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
			writeGenericLog("CREATE TABLE link_queue_table or link_crawled_table fails, " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		InitializeDB con = new InitializeDB(Globals.username, Globals.password, Globals.server, Globals.database);
		con.createDB();
	}
}
