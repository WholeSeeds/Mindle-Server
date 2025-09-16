package com.wholeseeds.mindle.domain.complaint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateComplaintRequestDto {

	private Long categoryId;

	private String subdistrictCode;

	private String placeId;

	private String placeType;

	private String placeName;

	private String placeDescription;

	private String title;

	private String content;

	private Double latitude;

	private Double longitude;

	private Boolean clearPlace;

	private Boolean replaceImages;
}
