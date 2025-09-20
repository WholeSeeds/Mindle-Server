package com.wholeseeds.mindle.domain.complaint.repository;

import java.util.Optional;

import com.wholeseeds.mindle.common.repository.JpaBaseRepository;
import com.wholeseeds.mindle.domain.complaint.entity.ComplaintReaction;

public interface ComplaintReactionRepository extends JpaBaseRepository<ComplaintReaction, Long> {

	Optional<ComplaintReaction> findByComplaintIdAndMemberId(Long complaintId, Long memberId);

	Optional<ComplaintReaction> findByComplaintIdAndMemberIdAndDeletedAtIsNull(Long complaintId, Long memberId);

	boolean existsByComplaintIdAndMemberIdAndDeletedAtIsNull(Long complaintId, Long memberId);
}
