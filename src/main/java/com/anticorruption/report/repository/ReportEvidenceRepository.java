package com.anticorruption.report.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anticorruption.report.entity.ReportEvidence;

public interface ReportEvidenceRepository extends JpaRepository<ReportEvidence, UUID>{

	List<ReportEvidence> findByReportId(UUID reportId);
	
	List<ReportEvidence> findByReportIdOrderByUploadedAtAsc(UUID reportId);
	
}
