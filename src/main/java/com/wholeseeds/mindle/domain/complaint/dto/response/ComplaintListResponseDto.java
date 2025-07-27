package com.wholeseeds.mindle.domain.complaint.dto.response;

import java.time.LocalDateTime;

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
public class ComplaintListResponseDto {
	private Long complaintId;
	private String title;
	private String content;
	private LocalDateTime createdAt;
	private boolean isResolved;
	private long commentCount;
	private long reactionCount;
	private String imageUrl;
}
