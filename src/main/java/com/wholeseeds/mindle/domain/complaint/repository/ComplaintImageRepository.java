package com.wholeseeds.mindle.domain.complaint.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wholeseeds.mindle.domain.complaint.entity.ComplaintImage;

public interface ComplaintImageRepository extends JpaRepository<ComplaintImage, Long> {
	List<ComplaintImage> findAllByComplaintId(Long complaintId);
}
