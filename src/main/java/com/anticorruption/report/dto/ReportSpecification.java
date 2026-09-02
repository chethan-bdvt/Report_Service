package com.anticorruption.report.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.anticorruption.report.entity.Report;

import jakarta.persistence.criteria.Predicate;

public class ReportSpecification {

	private ReportSpecification() {
		
	} 
	
	private static Specification<Report> search(ReportSearchRequest request) {
		return(root, query,criterialBuilder)->{
			List<Predicate> predicate = new ArrayList<>();
			
			if(request.getStateId() != null) {
				predicate.add(criterialBuilder.equal(root.get("stateId"), 
						request.getStateId()));
			}
			
			if(request.getDistrictId() != null) {
				predicate.add(criterialBuilder.equal(root.get("districtId"),
						request.getDistrictId()));
			}
			
			if(request.getTalukId() != null) {
				predicate.add(criterialBuilder.equal(root.get("talukId"),
						request.getTalukId()));
			}
			
			if(request.getDepartment() != null) {
				predicate.add(criterialBuilder.equal(criterialBuilder.lower(root.get("department")),
						request.getDepartment().trim().toLowerCase()));
			}
			
			if(request.getReportNumber() != null && !request.getReportNumber().isBlank()) {
				predicate.add(criterialBuilder.equal(root.get("reportNumber"),
						request.getReportNumber().trim()));
			}
			
			if(request.getFromDate() != null) {
				predicate.add(criterialBuilder.greaterThanOrEqualTo(root.get("reportedDate"),
						 request.getFromDate()));
			}
			
			if(request.getToDate() != null) {
				predicate.add(criterialBuilder.lessThanOrEqualTo(root.get("reportedDate"),
						request.getFromDate()));
			}
			return criterialBuilder.and(predicate.toArray(new Predicate[0]));
		};
	}
		
} 
  