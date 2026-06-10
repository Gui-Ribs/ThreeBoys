package com.threeboys.infrastructure.database.jdbc.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowMapper<T> {
	T map(ResultSet rs) throws SQLException;
}
