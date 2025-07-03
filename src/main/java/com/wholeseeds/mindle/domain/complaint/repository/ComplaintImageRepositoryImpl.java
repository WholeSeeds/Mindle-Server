package com.wholeseeds.mindle.domain.complaint.repository;

import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.complaint.entity.ComplaintImage;
import com.wholeseeds.mindle.domain.complaint.entity.QComplaintImage;

import jakarta.persistence.EntityManager;

public class ComplaintImageRepositoryImpl extends JpaBaseRepositoryImpl<ComplaintImage, Long>
	implements ComplaintImageRepositoryCustom {
	public static final QComplaintImage complaintImage = QComplaintImage.complaintImage;

	public ComplaintImageRepositoryImpl(EntityManager em) {
		super(ComplaintImage.class, em, complaintImage, complaintImage.id, complaintImage.deletedAt);
	}
}
