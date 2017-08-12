package database;

import common.ErrorCode.CrError;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

import static common.ErrorCode.FAILED;

public class MySQLDatabaseConnection implements IDatabaseConnection {
	private static Logger LOG = LogManager.getLogger(MySQLDatabaseConnection.class.getName());
	
	public MySQLDatabaseConnection(String username, String password, String server, String database) throws SQLException, ClassNotFoundException {
	}
}
