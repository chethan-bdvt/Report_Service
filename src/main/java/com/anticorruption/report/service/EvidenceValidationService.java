package com.anticorruption.report.service;

import java.security.MessageDigest;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EvidenceValidationService {

	private static final Set<String> ALLOWED_CONTENT_TYPE = Set.of(
			"image/jpeg",
			"image/png",
			"video/mp4",
			"audio/mpeg",
			"audio/wav",
			"audio/mp3",
			"application/pdf"
			);
	
	private static final Set<String> NOT_ALLOWED_CONTENT_TYPE = Set.of(
			".exe",
			".dir",
			".dll",
			".cmd",
			".msi",
			".scr",
			".psl",
			".psml",
			".vbs",
			".vbe",
			".js",
			".jse",
			".jar",
			".sh",
			".php",
			".asp",
			".aspx",
			".jsp"
			);
	private static final long MAX_FILE_SIZE = 10*1024*1024;
	
	public void validate(MultipartFile file) {
		if(file == null || file.isEmpty()) {
			throw new IllegalArgumentException("Evidence file is empty");
		}
		String fileName = file.getOriginalFilename();
		
		if(fileName != null) {
			for(String extension : NOT_ALLOWED_CONTENT_TYPE) {
				if(fileName.toLowerCase(Locale.ROOT).endsWith(extension)) {
					throw new IllegalArgumentException("File is not supported");
				}
			}
		}
		
		if(file.getSize() > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("File size must be less than 10 MB ");
		}
		
		String contentType = file.getContentType();
		
		if(contentType == null || !ALLOWED_CONTENT_TYPE.contains(contentType.toLowerCase())) {
			throw new IllegalArgumentException("Unsupported file type. Allowed JPG,PNG,MP3,MP4");
		}
	}
		
	public String calculateSha256Hash(MultipartFile file) {
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(file.getBytes());
				
				StringBuilder hex = new StringBuilder();
				
				for(byte b: hash) {
					hex.append(String.format("%02x", b));
				}
				return hex.toString();
				
			} catch (Exception e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		
	}
}
