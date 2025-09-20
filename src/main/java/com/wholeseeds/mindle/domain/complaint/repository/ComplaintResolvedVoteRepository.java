package com.wholeseeds.mindle.domain.complaint.repository;

import com.wholeseeds.mindle.common.repository.JpaBaseRepository;
import com.wholeseeds.mindle.domain.complaint.entity.ComplaintResolvedVote;

public interface ComplaintResolvedVoteRepository extends JpaBaseRepository<ComplaintResolvedVote, Long> {
	boolean existsByComplaintIdAndMemberId(Long complaintId, Long memberId);
}
