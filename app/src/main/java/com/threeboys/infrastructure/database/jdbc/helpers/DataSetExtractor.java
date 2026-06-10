package com.threeboys.infrastructure.database.jdbc.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface DataSetExtractor<T> {
	T extract(ResultSet rs) throws SQLException;
}
