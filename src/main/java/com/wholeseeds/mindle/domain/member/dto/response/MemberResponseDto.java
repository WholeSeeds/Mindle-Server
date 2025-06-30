package com.wholeseeds.mindle.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponseDto {
	private Long id;
	private String firebaseUid;
	private String email;
	private String phone;
	private String provider;
}
