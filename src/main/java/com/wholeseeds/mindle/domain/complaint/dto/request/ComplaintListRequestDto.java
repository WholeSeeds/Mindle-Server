package com.wholeseeds.mindle.domain.complaint.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintListRequestDto {

	private Long cursorComplaintId;
	@Min(1)
	private int pageSize = 20;
	private String cityCode;
	private String districtCode;
	private Long categoryId;
}
