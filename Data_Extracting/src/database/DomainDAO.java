package database;

import java.sql.SQLException;
import java.util.List;

public interface DomainDAO {
    public List<Domain> get() throws SQLException;

    public int create(Domain domain) throws SQLException;
}
