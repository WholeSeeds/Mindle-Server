package com.wholeseeds.mindle.domain.complaint.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class SaveComplaintRequestDto {
	@NotNull
	private Long categoryId;
	@NotNull
	private Long memberId;
	private String cityName;
	private String districtName;
	private String subdistrictName;
	private String placeId;
	@NotNull
	private String title;
	@NotNull
	private String content;
	private double latitude;
	private double longitude;
	// private String photoUrl; // TODO 이미지 파일
}
