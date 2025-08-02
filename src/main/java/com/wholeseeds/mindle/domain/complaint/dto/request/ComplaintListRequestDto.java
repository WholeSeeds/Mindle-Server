package com.wholeseeds.mindle.domain.complaint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class ComplaintListRequestDto {
	private Long cursorComplaintId;
	private int pageSize;
	private String cityCode;
	private String districtCode;
	private Long categoryId;
}
