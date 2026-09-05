package com.anticorruption.report.config;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
		name = "Reference-Service",
		url = "${reference.service.url}"
		)
public interface ReferenceServiceClient {

	@GetMapping("/statesservice/name/{name}")
	StateResponse getStateByName(@PathVariable("name") String stateName);
	
	@GetMapping("/districtservice/name/{districtName}")
	DistrictResponse getDistrictByName(@PathVariable("districtName") String districtName);
	
	@GetMapping("/districtservice/state/{stateName}/district/{districtName}")
	DistrictResponse getStateAndDistrictByName(@PathVariable("stateName") String stateName, 
			@PathVariable("districtName") String districtName);

	@GetMapping("/talukservice/district/{districtId}/taluk/{talukName}")
	TalukResponse getTalukByDistrictIdAndTalukName(@PathVariable("districtId") UUID districtId,
			@PathVariable("talukName") String talukName);
	
	record StateResponse(String name, UUID id, String code) {}
	
	record DistrictResponse(String name, UUID id, UUID stateId) {}
	
	record TalukResponse(String name, UUID id, UUID districtId) {}
}
