package database;

import java.sql.SQLException;
import java.util.List;

public interface NaiveBayesParamDAO {
    List<NaiveBayesParam> get() throws SQLException;

    int create(NaiveBayesParam param) throws SQLException;
}
