package database;

import java.sql.SQLException;
import java.util.List;

public interface ExtractedTextDAO {
	List<ExtractedText> get() throws SQLException;

	ExtractedText get(int id) throws SQLException;

	List<ExtractedText> get(int lowerBound, int maxCount) throws SQLException;

	int create(ExtractedText extractedText) throws SQLException;

	List<ExtractedText> getExtactedTextNotIdInTable(int lowerBound, int maxNumResult, String compareAgainstTable) throws SQLException;
	
	boolean update(ExtractedText extractedText) throws SQLException;

	int createIncrement(ExtractedText extractedText) throws SQLException;
}
