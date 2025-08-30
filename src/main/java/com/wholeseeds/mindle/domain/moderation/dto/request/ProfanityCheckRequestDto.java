package com.wholeseeds.mindle.domain.moderation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfanityCheckRequestDto {
	@NotBlank
	private String text;
}
