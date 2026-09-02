package com.anticorruption.report.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.anticorruption.report.dto.ReportSearchRequest;
import com.anticorruption.report.entity.Report;

import jakarta.persistence.criteria.Predicate;

public class ReportSpecification {

	public static Specification<Report> search(ReportSearchRequest request) {
		return (root, query,cb) -> {
			
			List<Predicate> predicates = new ArrayList<>();
			if(request.getStateId() != null) {
				predicates.add(cb.equal(root.get("stateId"), request.getStateId()));
			}
			
			if(request.getDistrictId() != null) {
				predicates.add(cb.equal(root.get("districtId"), request.getDistrictId()));
			}
			
			if(request.getTalukId() != null) {
				predicates.add(cb.equal(root.get("talukId"), request.getTalukId()));
			}
			
			if(request.getDepartment() != null) {
				predicates.add(cb.equal(root.get("department"), request.getDepartment()));
			}
			
			if(request.getFromDate() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("fromDate"), request.getFromDate()));
			}
			
			if(request.getToDate() != null) {
				predicates.add(
						cb.lessThanOrEqualTo(root.get("toDate"), request.getToDate()));
			}
			
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
	
}