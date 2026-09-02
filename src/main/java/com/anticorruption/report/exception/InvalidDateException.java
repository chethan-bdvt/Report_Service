package com.anticorruption.report.exception;

import java.time.DateTimeException;
import java.time.LocalDate;

public class InvalidDateException extends DateTimeException {

	public InvalidDateException(String date) {
		super(date);
	}
	
}
