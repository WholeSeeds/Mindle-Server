package com.wholeseeds.mindle.domain.complaint.dto;

import com.wholeseeds.mindle.domain.complaint.entity.Complaint;

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
public class SaveComplaintResponseDto {
	private Long id;
	private Long categoryId;
	private Long memberId;
	private Long subdistrictId;
	private Long placeId;
	private String title;
	private String content;
	private Double latitude;
	private Double longitude;
	private Complaint.Status status;
	private Boolean isResolved;
}
