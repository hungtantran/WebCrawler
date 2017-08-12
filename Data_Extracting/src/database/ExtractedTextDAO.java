package database;

import java.sql.SQLException;
import java.util.List;

public interface ExtractedTextDAO {
	public ExtractedText get(int id) throws SQLException;

	public int create(ExtractedText extractedText) throws SQLException;
	
	public boolean update(ExtractedText extractedText) throws SQLException;
}
