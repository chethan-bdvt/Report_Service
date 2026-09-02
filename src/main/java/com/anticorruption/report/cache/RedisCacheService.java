package com.anticorruption.report.cache;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.anticorruption.report.repository.ReportView;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

	private final RedisTemplate<String,Object> redisTemplate;
		
	private static final Duration FREE_REPORT_TTL = Duration.ofMinutes(5);
	
	private String freeDistrictKey(
			UUID districtId, LocalDate date) {
		return "reports:free:district:"+districtId+"date:"+date;
		}
	
	public List<ReportView> getFreeDistrictReports(UUID districtId, LocalDate date) {
		String key = freeDistrictKey(districtId, date);
		
		Object value = redisTemplate.opsForValue().get(key);
		
		if(value == null) {
			return null;
		}
		
		return (List<ReportView>) value;
	}
	
	public void cacheFreeDistrictReport(UUID districtId,
			LocalDate date, List<ReportView> reports) {
		String key = freeDistrictKey(districtId, date);
		
		redisTemplate.opsForValue().set(key,reports, FREE_REPORT_TTL);
	}
	
	public void evictFreeDistrictReports(
			UUID districtId, LocalDate date ) {
		String key = freeDistrictKey(districtId, date);
		
		redisTemplate.delete(key);
	}
}

