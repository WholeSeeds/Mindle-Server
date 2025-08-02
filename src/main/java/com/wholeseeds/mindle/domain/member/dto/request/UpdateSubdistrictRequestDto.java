package com.wholeseeds.mindle.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateSubdistrictRequestDto {

	@NotNull(message = "subdistrictCode는 필수입니다.")
	private String subdistrictCode;
}
