package com.wholeseeds.mindle.domain.complaint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class VoteResolvedResponseDto {

	private boolean incremented;
	private boolean transitionedToResolved;
	private SaveComplaintResponseDto complaint;
}
