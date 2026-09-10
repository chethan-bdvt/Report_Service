package com.anticorruption.report.controller;

import com.anticorruption.report.service.EvidenceValidationService;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.anticorruption.report.dto.ReportCreateRequest;
import com.anticorruption.report.dto.ReportResponse;
import com.anticorruption.report.dto.ReportSearchRequest;
import com.anticorruption.report.entity.Report;
import com.anticorruption.report.entity.ReportEvidence;
import com.anticorruption.report.repository.ReportView;
import com.anticorruption.report.service.EvidenceStorageService;
import com.anticorruption.report.service.ReportService;

@RestController
@RequestMapping("/reports")
public class ReportController {

	private final ReportService reportService;

	private final EvidenceStorageService evidenceStorageService;
	
	private final EvidenceValidationService evidenceValidationService;

	public ReportController(ReportService reportService, EvidenceStorageService evidenceStorageService,
			EvidenceValidationService evidenceValidationService) {
		this.evidenceStorageService = evidenceStorageService;
		this.reportService = reportService;
		this.evidenceValidationService = evidenceValidationService;
	}

	@PostMapping("/search")
	public List<ReportResponse> search(@RequestBody ReportSearchRequest request) {
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
		// UUID userId = //getUserId(auth);
		return reportService.getFreeReport(null);
	}

	public Page<ReportView> searchPremiumReports(@RequestBody ReportSearchRequest request,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size,
			Authentication auth) throws AccessDeniedException {
		// UUID userId = getUserId(auth);
		return reportService.searchPremiumReport(null, request, page, size);
	}

	@PostMapping("/export/")
	public ResponseEntity<byte[]> export(@RequestBody ReportSearchRequest request, Authentication auth)
			throws AccessDeniedException {
		// UUID userId = getUserId(auth);
		reportService.checkPremiumForDownload(null);
		return null;
	}

//	@PostMapping("/{reportId}/evidence")
//	public ResponseEntity<UUID> uploadEvidence(@PathVariable UUID reportId,
//			@RequestParam("file") MultipartFile file) {
//		if(!reportService.reportExists(reportId)) {
//			return ResponseEntity.notFound().build();
//		}
//		String storageKey = evidenceStorageService.store(file);
//		
//		ReportEvidence evidence = ReportEvidence.builder()
//				.reportId(reportId)
//				.fileName(file.getOriginalFilename())
//				.contentType(file.getContentType())
//				.fileSize(file.getSize())
//				.storageKey(storageKey)
//				.build();
//		ReportEvidence save = reportService.saveEvidence(evidence);
//		
//		return ResponseEntity.ok(save.getId());
//	}

	@GetMapping("/evidence/{reportId}")
	public ResponseEntity<List<ReportEvidence>> getReportEvidenceByReportId(@PathVariable UUID reportId) {
		if (reportService.reportExists(reportId)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(reportService.getEvidenceReportById(reportId));
	}

	@GetMapping("/{reportId}/evidence")
	public ResponseEntity<List<ReportEvidence>> getEvidence(@PathVariable UUID reportId) {
		if (!reportService.reportExists(reportId)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(reportService.getEvidencrByReportId(reportId));
	}

	@GetMapping("/{reportId}/evidence/{evidenceId}")
	public ResponseEntity<Resource> downloadEvidence(@PathVariable UUID reportId, @PathVariable UUID evidenceId) {
		if (!reportService.reportExists(reportId)) {
			return ResponseEntity.notFound().build();
		}

		ReportEvidence evidence = reportService.getEvidenceById(evidenceId);

		if (evidence == null || !evidence.getReportId().equals(reportId)) {
			return ResponseEntity.notFound().build();
		}

		Resource resource = evidenceStorageService.load(evidence.getStorageKey());

		MediaType mediaType;

		try {
			mediaType = MediaType.parseMediaType(evidence.getContentType());

		} catch (Exception e) {
			mediaType = MediaType.APPLICATION_OCTET_STREAM;
		}

		return ResponseEntity.ok().contentType(mediaType).body(resource);
	}

	@PostMapping("/{reportId}/evidence")
	public ResponseEntity<List<UUID>> uploadEvidence(@PathVariable UUID reportId,
			@RequestParam("files") List<MultipartFile> files) {
		
		if(files.size() > 3) {
			return ResponseEntity.badRequest().body(null);
		}
		
		if (!reportService.reportExists(reportId)) {
			return ResponseEntity.notFound().build();
		}
		
		for (MultipartFile file : files) {
			evidenceValidationService.validate(file);
		}

		List<UUID> evidenceIds = new ArrayList<>();
		for (MultipartFile file : files) {

			String storageKey = evidenceStorageService.store(file);

			ReportEvidence evidence = ReportEvidence.builder().reportId(reportId).fileName(file.getOriginalFilename())
					.contentType(file.getContentType()).fileSize(file.getSize()).storageKey(storageKey)
					.sha256Hash(evidenceValidationService.calculateSha256Hash(file))
					.build();

			ReportEvidence saved = reportService.saveEvidence(evidence);

			evidenceIds.add(saved.getId());
		}
		return ResponseEntity.ok(evidenceIds);
	}
}
