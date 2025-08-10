package com.wholeseeds.mindle.domain.complaint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintListRequestDto {

	private Long cursorComplaintId;
	private int pageSize;
	private String cityCode;
	private String districtCode;
	private Long categoryId;
}
