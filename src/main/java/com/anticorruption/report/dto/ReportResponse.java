package com.anticorruption.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ReportResponse(
		String reportNumber,
		BigDecimal demandedAmount,
		BigDecimal paidAmount,
		String state,
		String district,
		String subDistrict,
		String reason,
		String department,
		LocalDate reportedDate,
		LocalDate incidentDate
		) {

}
