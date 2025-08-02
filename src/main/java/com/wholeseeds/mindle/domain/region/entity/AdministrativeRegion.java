package com.wholeseeds.mindle.domain.region.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class AdministrativeRegion {

	/**
	 * 행정구역 코드
	 */
	@Id
	@Column(length = 10, nullable = false, unique = true)
	protected String administrativeCode;

	/**
	 * 행정구역 이름
	 */
	@Column(length = 100, nullable = false)
	protected String name;

	/**
	 * 생성일시
	 */
	@Column(name = "created_at", nullable = false, updatable = false)
	protected LocalDateTime createdAt;

	/**
	 * 수정일시
	 */
	@Column(name = "updated_at")
	protected LocalDateTime updatedAt;

	protected void onPrePersist() {
	}

	protected void onPreUpdate() {
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = this.createdAt;
		onPrePersist();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
