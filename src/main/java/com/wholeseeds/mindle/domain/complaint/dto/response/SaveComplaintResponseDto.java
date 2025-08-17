package com.wholeseeds.mindle.domain.complaint.dto.response;

import com.wholeseeds.mindle.domain.complaint.entity.Complaint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SaveComplaintResponseDto {

	private Long id;
	private Long categoryId;
	private Long memberId;
	private String subdistrictCode;
	private Long placeId;
	private String title;
	private String content;
	private Double latitude;
	private Double longitude;
	private Complaint.Status status;
	private Boolean isResolved;
}
