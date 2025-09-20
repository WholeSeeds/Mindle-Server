package com.wholeseeds.mindle.domain.complaint.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReactionUpdateRequestDto {
	@NotNull
	private Boolean reacted;
}
