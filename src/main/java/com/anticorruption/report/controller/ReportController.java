package com.anticorruption.report.controller;

import java.util.List;
import java.util.UUID;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anticorruption.report.dto.ReportCreateRequest;
import com.anticorruption.report.dto.ReportResponse;
import com.anticorruption.report.dto.ReportSearchRequest;
import com.anticorruption.report.entity.Report;
import com.anticorruption.report.repository.ReportView;
import com.anticorruption.report.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;
	
	@PostMapping("/search")
	public List<ReportView> search(@RequestBody ReportSearchRequest request) {
		return reportService.searchReports(request);
	}
	
	@PostMapping("")
	public ResponseEntity<UUID> createReport(@RequestBody ReportCreateRequest request) {
		Report report = reportService.createReport(request);
		return ResponseEntity.ok(report.getId());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ReportResponse> getReportById(@PathVariable String id) {
		return null;
	}
	
	@GetMapping
	public List<ReportView> getReports(Authentication auth) {
		UUID userId = getUserId(auth);
		return reportService.getFreeReport(userId);
	}
	
	public Page<ReportView> searchPremiumReports(
			@RequestBody ReportSearchRequest request,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size,
			Authentication auth) {
		UUID userId = getUserId(auth);
		return reportService.searchPremiumReport(userId, request, page, size);
	}
	
	@PostMapping("/export/")
	public ResponseEntity<byte[]> export(@RequestBody ReportSearchRequest request, Authentication auth) {
		UUID userId = getUserId(auth);
		reportService.checkPremiumForDownload(userId);
		return null;
	}
}
 