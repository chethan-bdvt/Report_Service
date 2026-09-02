package com.anticorruption.report.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.anticorruption.report.entity.Report;
//
public interface ReportRepository extends JpaRepository<Report, UUID>, JpaSpecificationExecutor<Report>{

//	List<ReportView> findTop3ByReportedDateOrderByReportedAtDesc(LocalDate reportedDate);
	
//~~(Could not parse as Java:
//public class ReportRepository implements JpaRepository<Report, UUID>, JpaSpecificationExecutor<Report>{__TEMPLATE_cfcc2025-6662__
//Example(""SELECT r.id AS id, r.demandedAmount AS demandedAmount, r.paidAmount AS paidAmount, r.stateId AS stateId, r.talukId AS talukId, r.districtId AS districtId, r.reason AS reason, r.department AS department, r.reportedAt AS reportedAt, r.reportedDate AS reportedDate, r.incidentDate AS incidentDate FROM Report r WHERE r.reportedDate BETWEEN :fromDate AND :toDate ORDER BY r.reportedAt desc"")
// void $method() {}}
//@interface $Placeholder {})~~>*/@Query("SELECT r.id AS id, r.demandedAmount AS demandedAmount, r.paidAmount AS paidAmount, r.stateId AS stateId, r.talukId AS talukId, r.districtId AS districtId, r.reason AS reason, r.department AS department, r.reportedAt AS reportedAt, r.reportedDate AS reportedDate, r.incidentDate AS incidentDate FROM Report r WHERE r.reportedDate BETWEEN :fromDate AND :toDate ORDER BY r.reportedAt desc")
    public List<ReportView> findByReportedDateBetweenOrderByReportedAtDesc(LocalDate fromDate, 
			LocalDate toDate);
    
    @Query(value = """
    		Select
    		r.id AS "Id",
    		r.report_number AS "ReportNumber",
    		r.demanded_amount AS "DemandedAmount",
    		r.paid_amount AS "PaidAmount",
    		s.name AS "State",
    		d.name AS "District",
    		t.name AS "Taluk",
    		r.state_id AS "StateId",
    		r.district_id AS "DistrictId",
    		r.taluk_id  AS "TalukId",
    		r.department AS "Department",
    		r.reason AS "Reason",
    		r.reported_date AS "Date",
    		r.incident_date AS "IncidentDate"
    		FROM reports r
    		LEFT JOIN states s ON r.state_id = s.id
    		LEFT JOIN district d ON r.district_id = d.id
    		LEFT JOIN taluk t ON r.taluk_id = t.id
    		WHERE(:stateId IS NULL OR r.state_id = :stateId)
    		AND (:districtId IS NULL OR r.district_id = :districtId)
    		AND (:talukId IS NULL OR r.taluk_id = :talukId)
    		AND (:department IS NULL OR LOWER(r.department) = LOWER(:department))
    		AND (:reportNumber IS NULL OR r.reportNumber = :reportNumber)
    		AND (:reason IS NULL OR LOWER(r.rason) LIKE LOWER(CONCAT('%',:reason, '%')))
    		AND (:fromDate IS NULL OR r.reported_date >= :fromDate)
    		AND (:toDate IS NULL OR r.reported_date <= :toDate) 
    		ORDER BY r.reported_at DESC""", nativeQuery = true)
    public List<ReportView> searchReports(
    		@Param("stateId") UUID stateId,
    		@Param("districtId") UUID districtId,
    		@Param("talukId") UUID talukId,
    		@Param("department") String department,
    		@Param("reportNumber") String reportNumber,
    		@Param("fromDate") LocalDate fromDate,
    		@Param("toDate") LocalDate toDate);
    
    
    @Query(value = """
    		SELECT
    		r.id AS "Id",
    		r.report_number AS "ReportNumber",
    		r.demanded_amount AS "DemandedAmount",
    		r.paid_amount AS "PaidAmount",
    		s.name AS "State",
    		d.name AS "District",
    		t.name AS "Taluk",
    		r.department AS "Department",
    		r.reason AS "Reason",
    		r.reported_date AS "Date",
    		r.incident_date AS "IncidentDate"
    		FROM reports r
    		LEFT JOIN states s ON r.state_id = s.id
    		LEFT JOIN district d ON r.district_id = d.id
    		LEFT JOIN taluk t ON r.taluk_id = t.id
    		WHERE r.district_id = :districtId
    		AND r.reported_date = :today
    		ORDER BY r.reported_at DESC
    		""", nativeQuery = true)
    public List<ReportView> findTodayReportByDistrict(@Param("districtId") UUID districtId,
    		@Param("today") LocalDate today); 
    
    @Query(value = """
    		SELECT
    		r.id AS "Id",
    		r.report_number AS "ReportNumber",
    		r.demanded_amount AS "Demanded Amount",
    		r.paid_amount AS "Paid Amount",
    		s.name AS "State",
    		d.name AS "District",
    		t.name AS "Taluk",
    		r.department AS "Department",
    		r.reason AS "Reason",
    		r.reported_date AS "Date",
    		r.incident_date AS "Incident Date"
    		FROM reports r
    		LEFT JOIN states s ON r.state_id = s.id,
    		LEFT JOIN district d ON r.district_id = d.id,
    		LEFT JOIN taluk t ON r.taluk_id = t.id
    		WHERE(:stateId IS NULL OR r.state_id = :stateId)
    		AND (:districtId IS NULL OR r.district_id = :districtId)
    		AND (:talukId IS NULL OR r.taluk_id = :talukId)
    		AND (:department IS NULL OR LOWER(r.department) = LOWER(:department))
    		AND (:reportNumber IS NULL OR r.report_number = :reportNumber)
    		AND (:fromDate IS NULL OR r.reported_date >= :fromDate)
    		AND (:toDate IS NULL OR r.reported_date >= :toDate)
    		ORDER BY r.reported_at DESC
    		""", 
    		countQuery = """
    				SELECT COUNT(*)
    				FROM reports r
    				WHERE (:stateId IS NULL OR r.state_id = :stateId)
    				AND (:districtId IS NULL OR r.district_id = :districtId)
    				AND (:talukId IS NULL OR r.taluk_id = :talukId)
    				AND (:department IS NULL OR r.department = :department)
    				AND (:reportNumber IS NULL OR r.report_number = :reportNumber)
    				AND (:fromDate IS NULL OR r.reported_date >= :fromDate)
    				AND (:toDate IS NULL OR r.reported_date <= :toDate)
    				""",
    		nativeQuery = true)
    public Page<ReportView> searchPremiumReports(
    		@Param("stateId") UUID stateId,
    		@Param("districtId") UUID districtId,
    		@Param("talukId") UUID talukId,
    		@Param("department") String department,
    		@Param("reportNumber") String reportNumber,
    		@Param("fromDate") LocalDate fromDate,
    		@Param("toDate") LocalDate toDate,
    		Pageable pageable
    		);
    
    public boolean existsByReportNumber(String reportNumber);
 
}
