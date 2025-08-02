package com.wholeseeds.mindle.domain.member.dto.response;

import java.time.LocalDateTime;

import com.wholeseeds.mindle.domain.region.dto.SubdistrictDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberResponseDto {

	private Long id;
	private String firebaseUid;
	private String email;
	private String phone;
	private String provider;
	private String nickname;
	private Boolean notificationPush;
	private Boolean notificationInapp;
	private Integer contributionScore;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;

	private SubdistrictDto subdistrict;
}
