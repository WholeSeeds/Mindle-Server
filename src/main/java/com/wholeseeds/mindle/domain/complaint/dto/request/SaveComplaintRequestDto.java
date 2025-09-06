package com.wholeseeds.mindle.domain.complaint.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SaveComplaintRequestDto {

	@NotNull
	private Long categoryId;
	private String subdistrictCode;
	private String placeId;
	@NotNull
	private String title;
	@NotNull
	private String content;
	private Double latitude;
	private Double longitude;
	// private String photoUrl; // TODO 이미지 파일
}
