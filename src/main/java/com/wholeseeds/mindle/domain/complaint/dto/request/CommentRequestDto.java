package com.wholeseeds.mindle.domain.complaint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {

	private Long complaintId;
	private String cursorCreatedAt;
	private int pageSize;
}
