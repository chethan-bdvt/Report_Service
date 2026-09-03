package com.anticorruption.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportCreateRequest {

	private BigDecimal paidAmount;
	
	private BigDecimal demandedAmount;
	
	private String state;
	
	private String district;
	
	private String subDistrict;
	
	private String location;
	
	private String reason;
	
	private String department;
	
	private String officialName;
	
	private String designation;
	
	private String proof;
	
	private String description;
	
	private LocalDate incidentDate;
}
