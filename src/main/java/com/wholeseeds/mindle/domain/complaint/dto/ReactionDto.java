package com.wholeseeds.mindle.domain.complaint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class ReactionDto {
	private Long reactionCount;
	private boolean isReacted; // 로그인 사용자의 공감 여부
}
