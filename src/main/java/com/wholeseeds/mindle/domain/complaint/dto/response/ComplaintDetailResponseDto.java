package com.wholeseeds.mindle.domain.complaint.dto.response;

import com.wholeseeds.mindle.domain.complaint.dto.ComplaintDetailWithImagesDto;
import com.wholeseeds.mindle.domain.complaint.dto.ReactionDto;

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
public class ComplaintDetailResponseDto {
	ComplaintDetailWithImagesDto complaintDetailWithImagesDto;
	ReactionDto reactionDto;
}
