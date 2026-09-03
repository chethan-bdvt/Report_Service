package com.anticorruption.report.exception;

import java.time.DateTimeException;

public class InvalidDateException extends DateTimeException {

	public InvalidDateException(String date) {
		super(date);
	}
	
}
