package database;

import java.sql.SQLException;
import java.util.List;

public interface LinkTypeDAO {
	public List<LinkType> get() throws SQLException;

	public int create(LinkType linkType) throws SQLException;
}
