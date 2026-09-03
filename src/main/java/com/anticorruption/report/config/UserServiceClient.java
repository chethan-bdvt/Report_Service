package com.anticorruption.report.config;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.anticorruption.report.entity.SubscriptionType;

@FeignClient(
		name = "user-service",
		url = "${user.service.url}"
		)
public interface UserServiceClient {

	@GetMapping("/users/internal/{userId}")
	InternalUserResponse getInternalUser(@PathVariable("userId") UUID userId);
	
	record InternalUserResponse(
			UUID id,
			UUID stateId,
			UUID districtId,
			SubscriptionType subscriptionType,
			boolean subscriptionActive,
			boolean active
			) 
	{}
		
	
}
