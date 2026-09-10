package com.anticorruption.report.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "report_evidence")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportEvidence {

	@Id
	@GeneratedValue
	private UUID id;
	
	@Column(name = "report_id", nullable = false)
	private UUID reportId;
	
	@Column(name = "file_name", nullable = false)
	private String fileName;
	
	@Column(name = "content_type", nullable = false)
	private String contentType;
	
	@Column(name = "file_size", nullable = false)
	private Long fileSize;
	
	@Column(name = "sha256_hash", nullable = false, length = 64)
	private String sha256Hash;
	
	@Column(name = "storage_key", nullable = false)
	private String storageKey;
	
	@Column(name = "uploaded_at", nullable = false)
	private OffsetDateTime uploadedAt;
	
	@PrePersist
	protected void onCreate() {
		if(uploadedAt == null) {
			uploadedAt = OffsetDateTime.now();
		}
	}
}
