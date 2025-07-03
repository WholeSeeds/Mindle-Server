package com.wholeseeds.mindle.domain.complaint.repository;

import java.util.Optional;

import com.querydsl.core.types.Projections;
import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.complaint.dto.DetailComplaintDto;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.entity.QComplaint;

import jakarta.persistence.EntityManager;

public class ComplaintRepositoryImpl extends JpaBaseRepositoryImpl<Complaint, Long>
	implements ComplaintRepositoryCustom {
	private static final QComplaint complaint = QComplaint.complaint;

	public ComplaintRepositoryImpl(EntityManager em) {
		super(Complaint.class, em, complaint, complaint.id, complaint.deletedAt);
	}

	// TODO : complaint_reaction
	@Override
	public Optional<DetailComplaintDto> getDetailById(Long id) {
		return Optional.ofNullable(queryFactory
			.select(Projections.constructor(
				DetailComplaintDto.class,
				complaint.id,
				complaint.title,
				complaint.content,
				complaint.category.name,
				complaint.member.nickname,
				complaint.place.name,
				complaint.subdistrict.city.name,
				complaint.subdistrict.district.name,
				complaint.subdistrict.name,
				complaint.createdAt
			))
			.from(complaint)
			.where(complaint.id.eq(id).and(complaint.deletedAt.isNull()))
			.fetchOne());
	}
}
