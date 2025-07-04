package com.wholeseeds.mindle.domain.complaint.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.wholeseeds.mindle.domain.complaint.dto.CommentDto;
import com.wholeseeds.mindle.domain.complaint.dto.ComplaintDetailWithImagesDto;
import com.wholeseeds.mindle.domain.complaint.dto.ComplaintListResponseDto;
import com.wholeseeds.mindle.domain.complaint.dto.ReactionDto;

public interface ComplaintRepositoryCustom {
	Optional<ComplaintDetailWithImagesDto> getComplaintWithImages(Long complaintId);

	Optional<ReactionDto> getReaction(Long complaintId, Long memberId);

	List<CommentDto> getComment(Long complaintId, LocalDateTime cursorCreatedAt, int pageSize);

	List<ComplaintListResponseDto> findListWithCursor(Long cursorComplaintId, int size,
		Long cityId, Long districtId, Long categoryId);
}
