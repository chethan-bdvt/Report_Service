package com.anticorruption.report.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EvidenceStorageService {

	private final Path storageDirectory;
	
	private EvidenceValidationService evidenceValidationService;

	public EvidenceStorageService(@Value("${file.storage.evidence-dir:uploads/evidence}") String storageDirectory,
			EvidenceValidationService evidenceValidationService) {
		this.storageDirectory = Paths.get(storageDirectory).toAbsolutePath().normalize();
		this.evidenceValidationService = evidenceValidationService;
		try {
			Files.createDirectories(this.storageDirectory);

		} catch (IOException e) {
			throw new IllegalArgumentException("Could not create evidence storage directory", e);
		}
	}
	
	public String store(MultipartFile file) {
		evidenceValidationService.validate(file);
		
		String originalFileName = file.getOriginalFilename();
		String extension = "";
		
		if(originalFileName != null && originalFileName.contains(".")) {
			extension = originalFileName.substring(originalFileName.lastIndexOf("."));
		
		}
		
		String storageKey = UUID.randomUUID() + extension;
		Path targetPath = storageDirectory.resolve(storageKey).normalize();
		
		if(!targetPath.startsWith(storageDirectory)) {
			throw new IllegalArgumentException("Invalid File Name");
		}
		
		try {
			Files.copy(file.getInputStream(), targetPath);
			return storageKey;
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not store evidence file ",e);
		}
	
	}
	
	public Resource load(String storageKey) {
		try {
			Path filePath = storageDirectory.resolve(storageKey).normalize();
			 
			if(!filePath.startsWith(storageDirectory)) {
				throw new IllegalArgumentException("Invalid storage key");
			} 
			
			Resource resource = new UrlResource(filePath.toUri());
			
			if(!resource.exists() || !resource.isReadable()) {
				throw new IllegalArgumentException("Evidence file not found");			
			}
			
			return resource;
		} catch (IOException e) {
			throw new IllegalStateException("Could not load evidence file", e);
		}
		
	}

}
