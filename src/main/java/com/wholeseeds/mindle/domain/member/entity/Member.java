package com.wholeseeds.mindle.domain.member.entity;

import java.time.LocalDateTime;

import com.wholeseeds.mindle.domain.location.entity.Subdistrict;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subdistrict_id", nullable = false)
	private Subdistrict subdistrictId;

	@Column(nullable = false, length = 50)
	private String nickname;

	@Column(name = "phone", length = 13)
	private String phone;

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
