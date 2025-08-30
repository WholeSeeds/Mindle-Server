package com.wholeseeds.mindle.domain.moderation.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfanityCheckResponseDto {
	private boolean passed;
	private List<ProfanityHitDto> hits;
}
