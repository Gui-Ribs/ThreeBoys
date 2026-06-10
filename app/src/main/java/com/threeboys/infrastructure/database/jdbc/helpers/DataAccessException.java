package com.threeboys.infrastructure.database.jdbc.helpers;

public class DataAccessException extends RuntimeException {

	public DataAccessException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DataAccessException(String msg) {
		super(msg);
	}
}
