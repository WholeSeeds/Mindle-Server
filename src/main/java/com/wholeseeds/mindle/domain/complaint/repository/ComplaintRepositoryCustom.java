package com.wholeseeds.mindle.domain.complaint.repository;

import java.util.Optional;

import com.wholeseeds.mindle.domain.complaint.dto.ComplaintDetailWithImagesDto;
import com.wholeseeds.mindle.domain.complaint.dto.ReactionDto;

public interface ComplaintRepositoryCustom {
	Optional<ComplaintDetailWithImagesDto> getComplaintWithImages(Long complaintId);

	Optional<ReactionDto> getReaction(Long complaintId, Long memberId);
}
