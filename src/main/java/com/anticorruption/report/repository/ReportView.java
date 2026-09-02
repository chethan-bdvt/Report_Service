package com.anticorruption.report.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface ReportView {

	UUID getId();
	
	BigDecimal getDemandedAmount();   
	
	BigDecimal getPaidAmount();
	
	UUID getStateId();
	
	UUID getTalukId();
	
	UUID getDistrictId();
	
	String getReason();
	
	String getDepartment();
	
	OffsetDateTime getReportedAt();
	
	LocalDate getReportedDate();
	
	LocalDate getIncidentDate();
	
	String reportNumber();
	
}
