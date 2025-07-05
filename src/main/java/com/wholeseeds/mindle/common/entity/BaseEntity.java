package com.wholeseeds.mindle.common.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseEntity {

	/**
	 * ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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

	/**
	 * 삭제일시 (soft delete)
	 */
	@Column(name = "deleted_at")
	protected LocalDateTime deletedAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = this.createdAt;
		onPrePersist();
	}

	/* 하위 클래스에서 초기값 줘야 하는 경우 override 해서 사용 */
	protected void onPrePersist() {
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}

	public boolean isDeleted() {
		return this.deletedAt != null;
	}
}
