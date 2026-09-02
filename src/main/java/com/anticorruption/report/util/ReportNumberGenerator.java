package com.anticorruption.report.util;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class ReportNumberGenerator {

	private final static String CHARACTERS = "QWERTYUIOPLKJHGFDSAZXCVBNM1234567890";
	
	private static final SecureRandom RANDOM = new SecureRandom();
	
	private ReportNumberGenerator() {
	
	}
	public static String generate(String stateCode) {
	StringBuilder value = new StringBuilder("AC-26-");
	
	String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	
	int number = ThreadLocalRandom.current().nextInt(1,1_000_000);

	return String.format("%s-%s-%06d", stateCode,date,number);
	}
	
}
