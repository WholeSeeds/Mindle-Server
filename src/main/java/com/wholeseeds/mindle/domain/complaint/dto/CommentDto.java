package com.wholeseeds.mindle.domain.complaint.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommentDto {
	private Long id;
	private String content;
	private LocalDateTime createdAt;

	private Long memberId;
	private String nickname;
}
