package com.github.tj123.db.exception;

/**
 * Created by TJ on 2016/10/24.
 */
public class DBException extends Exception {
	
	public DBException(String message) {
		super(message);
	}
	
	public DBException(Throwable cause) {
		super(cause);
	}
}
