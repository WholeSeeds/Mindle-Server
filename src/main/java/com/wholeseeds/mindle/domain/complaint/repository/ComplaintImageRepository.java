package com.wholeseeds.mindle.domain.complaint.repository;

import java.util.List;

import com.wholeseeds.mindle.common.repository.JpaBaseRepository;


import com.wholeseeds.mindle.domain.complaint.entity.ComplaintImage;

public interface ComplaintImageRepository extends JpaBaseRepository<ComplaintImage, Long> {
	List<ComplaintImage> findAllByComplaintId(Long complaintId);
}
