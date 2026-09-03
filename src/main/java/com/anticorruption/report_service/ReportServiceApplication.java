package com.anticorruption.report_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.anticorruption.report.*")
@EnableJpaRepositories(basePackages = "com.anticorruption.report.*")
@EntityScan(basePackages = "com.anticorruption.report.*")
@EnableFeignClients(basePackages = "com.anticorruption.report.config")
public class ReportServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportServiceApplication.class, args);
	}

}
