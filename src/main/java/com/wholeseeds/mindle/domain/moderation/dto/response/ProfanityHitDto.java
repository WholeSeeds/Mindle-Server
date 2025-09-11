package com.wholeseeds.mindle.domain.moderation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfanityHitDto {
	private String profanity;
	private int index;
}
