package com.anticorruption.report.service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anticorruption.report.cache.RedisCacheService;
import com.anticorruption.report.config.ReferenceServiceClient;
import com.anticorruption.report.config.UserServiceClient;
import com.anticorruption.report.dto.ReportCreateRequest;
import com.anticorruption.report.dto.ReportSearchRequest;
import com.anticorruption.report.entity.Report;
import com.anticorruption.report.entity.SubscriptionType;
import com.anticorruption.report.exception.InvalidDateException;
import com.anticorruption.report.exception.InvalidNumberException;
import com.anticorruption.report.exception.InvalidValueException;
import com.anticorruption.report.repository.ReportRepository;
import com.anticorruption.report.repository.ReportView;
import com.anticorruption.report.util.ReportNumberGenerator;

import lombok.NoArgsConstructor;

@Service
public class ReportService {

	private final ReportRepository reportRepository;

	private final RedisCacheService cacheService;

	private final UserServiceClient userService;
	
	private final ReferenceServiceClient referenceServiceClient;
	
	public ReportService(ReportRepository reportRepository,
						 RedisCacheService cacheService,
						 UserServiceClient userServiceClient,
						 ReferenceServiceClient referenceServiceClient) {
			this.reportRepository = reportRepository;
			this.cacheService = cacheService;
			this.userService = userServiceClient;
			this.referenceServiceClient = referenceServiceClient;
	}
	
	@Transactional(readOnly = true)
	public List<ReportView> getFreeReport(UUID userId) {

		UserServiceClient.InternalUserResponse user = userService.getInternalUser(userId);

		UUID districtId = user.districtId();

		LocalDate today = LocalDate.now();

		List<ReportView> cachedReports = cacheService.getFreeDistrictReports(districtId, today);

		if (cachedReports != null) {
			return cachedReports;
		}
		List<ReportView> reports = reportRepository.findTodayReportByDistrict(districtId, today);

		cacheService.cacheFreeDistrictReport(districtId, today, reports);
		return reports;
	}

	@Transactional(readOnly = true)
	public Page<ReportView> searchPremiumReport(UUID userId, ReportSearchRequest request, int page, int size) throws AccessDeniedException  {
		UserServiceClient.InternalUserResponse user = userService.getInternalUser(userId);

		try {
			checkPremium(user);
		} catch (AccessDeniedException e) {
			throw new AccessDeniedException("Access denied");
		}

		validateDateRange(request);

		Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "reported_date"));

		return reportRepository.searchPremiumReports(request.getStateId(), request.getDistrictId(),
				request.getTalukId(), request.getDepartment(), request.getReportNumber(), request.getFromDate(),
				request.getToDate(), pageable);
	}

	public void checkPremiumForDownload(UUID userId) throws AccessDeniedException {
		UserServiceClient.InternalUserResponse user = userService.getInternalUser(userId);
		checkPremium(user);
	}

	public void invalidateDistrictCache(UUID districtId) {
		cacheService.evictFreeDistrictReports(districtId, LocalDate.now());
	}
	
	private void validateDateRange(ReportSearchRequest request) {
		if(request.getFromDate() != null && request.getToDate() != null
				&& request.getFromDate().isAfter(request.getToDate())) {
			throw new IllegalArgumentException("From date cannot be after to date");
		}
	}
	
	public void checkPremium(UserServiceClient.InternalUserResponse user) throws AccessDeniedException{
		if ( user.subscriptionType() != SubscriptionType.PREMIUM) {
			throw new AccessDeniedException("This feature is available only to premium users");
		}
	}

	public List<ReportView> searchReports(ReportSearchRequest request) {
		if (request.getFromDate() != null && request.getToDate() != null
				&& request.getFromDate().isAfter(request.getToDate())) {
			throw new InvalidValueException("From date cannot be after to date");
		}

		return reportRepository.searchReports(request.getStateId(), request.getDistrictId(), request.getTalukId(),
				request.getDepartment(), request.getReportNumber(), request.getFromDate(), request.getToDate());
	}

	public Report createReport(ReportCreateRequest request) {

		if (request.getDemandedAmount() == BigDecimal.ZERO) {
			throw new InvalidNumberException("Demanded amount must be greater than zero");
		}
		if (request.getDemandedAmount().compareTo(new BigDecimal("9999999999.99")) > 0) {
			throw new InvalidNumberException("Demanded amount is too large");
		} 
		if (request.getPaidAmount() == BigDecimal.ZERO) {
			throw new InvalidNumberException("Paid amount should be greater than zero");
		}
		if (request.getPaidAmount().compareTo(request.getDemandedAmount()) > 0) {
			throw new InvalidNumberException("Paid amount should be less or equal to demanded amount");
		}

		if (request.getIncidentDate().isBefore(LocalDate.of(2026, 10, 1))
				|| request.getIncidentDate().isAfter(LocalDate.now())) {
			throw new InvalidDateException("Invalid Date");
		}

		if (request.getLocation().isBlank()) {
			throw new InvalidValueException("Location can not be null");
		}

		if (request.getReason().isBlank()) {
			throw new InvalidValueException("Reason can not be null");
		}

		if (request.getDescription().isBlank()) {
			throw new InvalidValueException("Description can not be null");
		}

		if (request.getDepartment().isBlank()) {
			throw new InvalidValueException("Department can not be null");
		}

		ReferenceServiceClient.StateResponse state = referenceServiceClient.getStateByName(request.getState());
		
		ReferenceServiceClient.DistrictResponse district = referenceServiceClient.getStateAndDistrictByName(
				request.getState(),request.getDistrict()
				);
		ReferenceServiceClient.TalukResponse taluk =
				referenceServiceClient.getTalukByDistrictIdAndTalukName(district.id(), request.getSubDistrict());
		
		Report report = Report.builder().reportNumber(generateUniqueReportNumber(state.stateCode()))
				.demandedAmount(request.getDemandedAmount()).paidAmount(request.getPaidAmount())
				.stateId(state.stateId()).talukId(taluk.id()).districtId(district.id())
				.location(request.getLocation()).reason(request.getReason()).department(request.getDepartment())
				.officialName(request.getOfficialName()).designation(request.getDesignation()).proof(request.getProof())
				.description(request.getDescription()).incidentDate(request.getIncidentDate())
				.reportedAt(OffsetDateTime.now(ZoneOffset.UTC)).reportedDate(LocalDate.now()).build();
		return reportRepository.save(null);
	}

	private String generateUniqueReportNumber(String stateCode) {
		String reportNumber;

		do {
			reportNumber = ReportNumberGenerator.generate(stateCode);
		} while (reportRepository.existsByReportNumber(reportNumber));
		return reportNumber;
	}
	
//	public List<Report> searchReports(ReportSearchRequest request) {
//		return reportRepository.findAll(ReportSpecification.search(request));
//	}
}
