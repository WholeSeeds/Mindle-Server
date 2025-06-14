package com.wholeseeds.mindle.domain.complaint.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "complaint_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ComplaintImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "complaint_id", nullable = false)
	private Complaint complaint;

	@Column(name = "image_url", length = 500, nullable = false)
	private String imageUrl;

	@Column(name = "uploaded_at", updatable = false)
	private LocalDateTime uploadedAt;

	@PrePersist
	protected void onCreate() {
		this.uploadedAt = LocalDateTime.now();
	}
}
