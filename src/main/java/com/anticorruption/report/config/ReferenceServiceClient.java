package com.anticorruption.report.config;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
		name = "Reference-Service",
		url = "reference.service.url"
		)
public interface ReferenceServiceClient {

	@GetMapping("/states/name/{stateName}")
	StateResponse getStateByName(@PathVariable("stateName") String stateName);
	
	@GetMapping("/district/name/{districtName}")
	DistrictResponse getDistrictByName(@PathVariable("districtName") String districtName);
	
	@GetMapping("/district/state/{stateName}/district/{districtName}")
	DistrictResponse getStateAndDistrictByName(@PathVariable("stateName") String stateName, 
			@PathVariable("districtName") String districtName);
	

	@GetMapping("/district/{districtId}/taluk/{talukName}")
	TalukResponse getTalukByDistrictIdAndTalukName(@PathVariable("districtId") UUID districtId,
			@PathVariable("talukName") String talukName);
	
	record StateResponse(String name, UUID stateId, String stateCode) {}
	
	record DistrictResponse(String name, UUID id, UUID stateId) {}
	
	record TalukResponse(String name, UUID id, UUID districtId) {}
}
