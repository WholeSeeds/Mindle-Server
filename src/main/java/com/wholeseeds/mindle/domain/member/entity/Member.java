package com.wholeseeds.mindle.domain.member.entity;

import com.wholeseeds.mindle.common.entity.BaseEntity;
import com.wholeseeds.mindle.domain.location.entity.Subdistrict;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Member extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subdistrict_id")
	private Subdistrict subdistrict;

	@Column(name = "firebase_uid", nullable = false, unique = true)
	private String firebaseUid;

	@Column(name = "email", length = 100)
	private String email;

	@Column(name = "provider", nullable = false, length = 50)
	private String provider;

	@Column(name = "nickname", nullable = false, length = 50, unique = true)
	private String nickname;

	@Column(name = "phone", length = 13)
	private String phone;

	@Builder.Default
	@Column(name = "notification_push", nullable = false)
	private Boolean notificationPush = false;

	@Builder.Default
	@Column(name = "notification_inapp", nullable = false)
	private Boolean notificationInapp = false;

	@Builder.Default
	@Column(name = "contribution_score", nullable = false)
	private Integer contributionScore = 0;

	public void updateNickname(String newNickname) {
		this.nickname = newNickname;
	}

	public void updateSubdistrict(Subdistrict subdistrict) {
		this.subdistrict = subdistrict;
	}

	public void updateNotificationPush(boolean value) {
		this.notificationPush = value;
	}

	public void updateNotificationInapp(boolean value) {
		this.notificationInapp = value;
	}
}
