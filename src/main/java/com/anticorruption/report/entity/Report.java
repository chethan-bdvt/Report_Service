package com.anticorruption.report.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="reports")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Report {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(name = "demanded_amount", precision = 15, scale = 2, nullable = true)
	private BigDecimal demandedAmount;
	
	@Column(name = "paid_amount", precision = 15, scale = 2, nullable = false)	
	private BigDecimal paidAmount;
	
	@Column(name = "state_id", nullable = false)
	private UUID stateId;
	
	@Column(name = "district_id", nullable = false)
	private UUID districtId;
	
	@Column(name = "taluk_id", nullable = false)
	private UUID talukId;
	
	@Column(name = "location", nullable = false)
	private String location;
	
	@Column(name = "reason", nullable = false, length = 30)
	private String reason;
	
	@Column(name = "department", nullable = false, length = 10)
	private String department;
	
	@Column(name = "official_name", length = 20)
	private String officialName;
	
	@Column(name = "designation", length = 20)
	private String designation;
	
	@Column(name = "proof")
	private String proof;
	
	@Column(name = "description", length = 200)
	private String description;
	
	@Column(name = "reported_at", nullable = false, updatable = false)
	private OffsetDateTime reportedAt;
	
	@Column(name = "reported_date", nullable = false, updatable = false)
	private LocalDate reportedDate;
	 
	@Column(name = "incident_date", nullable = true, updatable = false)
	private LocalDate incidentDate;
	
	@Column(name = "report_number", nullable = false, unique = true, updatable = false, length = 20)
	private String reportNumber;
	
	@Column(name = "submitted_by", nullable = true, updatable = false)
	private UUID submitterBy;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "submission_type", nullable = false, updatable = false, length = 20)
	private SubmissionType submissionType;

}
