package com.anticorruption.report.dto;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportSearchRequest {

	private UUID stateId;
	
	private UUID districtId;
	 
	private UUID talukId;
	
	private String department;
	
	private LocalDate fromDate;
	
	private LocalDate toDate;

	private String reportNumber;
		
	
}
