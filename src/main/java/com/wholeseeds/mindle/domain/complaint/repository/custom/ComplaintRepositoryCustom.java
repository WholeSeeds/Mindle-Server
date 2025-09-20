package com.wholeseeds.mindle.domain.complaint.repository.custom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.wholeseeds.mindle.domain.complaint.dto.CommentDto;
import com.wholeseeds.mindle.domain.complaint.dto.ComplaintDetailWithImagesDto;
import com.wholeseeds.mindle.domain.complaint.dto.ReactionDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.ComplaintListResponseDto;

public interface ComplaintRepositoryCustom {
	Optional<ComplaintDetailWithImagesDto> getComplaintWithImages(Long complaintId);

	Optional<ReactionDto> getReaction(Long complaintId, Long memberId);

	List<CommentDto> getComment(Long complaintId, LocalDateTime cursorCreatedAt, int pageSize);

	List<ComplaintListResponseDto> findListWithCursor(
		Long cursorComplaintId,
		int pageSize,
		String cityCode,
		String districtCode,
		Long categoryId
	);

	List<ComplaintListResponseDto> findMyListWithCursor(
		Long memberId,
		Long cursorComplaintId,
		int pageSize
	);

	List<ComplaintListResponseDto> findReactedListWithCursor(
		Long memberId,
		Long cursorComplaintId,
		int pageSize
	);
}
