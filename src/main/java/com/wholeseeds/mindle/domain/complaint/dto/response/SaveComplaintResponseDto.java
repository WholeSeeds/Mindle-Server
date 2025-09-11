package com.wholeseeds.mindle.domain.complaint.dto.response;

import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.place.dto.PlaceDto;
import com.wholeseeds.mindle.domain.region.dto.SubdistrictDto;

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
	private SubdistrictDto subdistrictDto;
	private PlaceDto placeDto;
	private String title;
	private String content;
	private Double latitude;
	private Double longitude;
	private Complaint.Status status;
	private Boolean isResolved;
}
