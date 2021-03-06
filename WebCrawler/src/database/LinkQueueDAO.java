package database;

import java.sql.SQLException;
import java.util.List;

public interface LinkQueueDAO {
	public List<LinkQueue> get(int maxUrls) throws SQLException;
	
	public int create(LinkQueue linkQueue) throws SQLException;
	
	public boolean linkExists(LinkQueue linkQueue) throws SQLException;
}
