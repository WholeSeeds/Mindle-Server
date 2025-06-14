package com.wholeseeds.mindle.domain.member.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 읍/면/동/리 고유 ID (외래키로 연결될 예정)
	@Column(name = "subdistrict_id", nullable = false)
	private Long subdistrictId;

	@Column(nullable = false, length = 50)
	private String nickname;

	@Column(name = "provider_id", nullable = false)
	private String providerId;

	@Column(name = "notification_push")
	private Boolean notificationPush = false;

	@Column(name = "notification_inapp")
	private Boolean notificationInapp = false;

	@Column(name = "contribution_score")
	private Integer contributionScore = 0;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = this.createdAt;
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
