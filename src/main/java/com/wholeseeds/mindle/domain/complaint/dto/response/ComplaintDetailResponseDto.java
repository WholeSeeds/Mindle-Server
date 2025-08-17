package com.wholeseeds.mindle.domain.complaint.dto.response;

import com.wholeseeds.mindle.domain.complaint.dto.ComplaintDetailWithImagesDto;
import com.wholeseeds.mindle.domain.complaint.dto.ReactionDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ComplaintDetailResponseDto {

	ComplaintDetailWithImagesDto complaintDetailWithImagesDto;
	ReactionDto reactionDto;
}
