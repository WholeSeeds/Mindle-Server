package com.wholeseeds.mindle.domain.complaint.repository;

import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.entity.QComplaint;

import jakarta.persistence.EntityManager;

public class ComplaintRepositoryImpl extends JpaBaseRepositoryImpl<Complaint, Long>
	implements ComplaintRepositoryCustom {
	private static final QComplaint complaint = QComplaint.complaint;

	public ComplaintRepositoryImpl(EntityManager em) {
		super(Complaint.class, em, complaint, complaint.id, complaint.deletedAt);
	}
}
