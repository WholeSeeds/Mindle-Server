package com.wholeseeds.mindle.domain.complaint.repository;

import java.util.Map;
import java.util.Optional;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.complaint.dto.DetailComplaintDto;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.entity.QComplaint;
import com.wholeseeds.mindle.domain.complaint.entity.QComplaintImage;

import jakarta.persistence.EntityManager;

public class ComplaintRepositoryImpl extends JpaBaseRepositoryImpl<Complaint, Long>
	implements ComplaintRepositoryCustom {
	private static final QComplaint complaint = QComplaint.complaint;
	private static final QComplaintImage complaintImage = QComplaintImage.complaintImage;

	public ComplaintRepositoryImpl(EntityManager em) {
		super(Complaint.class, em, complaint, complaint.id, complaint.deletedAt);
	}

	// TODO : complaint_reaction
	@Override
	public Optional<DetailComplaintDto> getDetailById(Long id) {
		// 1) complaint.id를 키로, DTO를 값으로 매핑한 Map<Long,DetailComplaintDto> 생성
		Map<Long, DetailComplaintDto> resultMap = queryFactory
			.from(complaint)
			.leftJoin(complaintImage)
			.on(complaintImage.complaint.id.eq(complaint.id))
			.where(
				complaint.id.eq(id)
					.and(complaint.deletedAt.isNull())
			)
			.transform(
				GroupBy.groupBy(complaint.id)                  // ← key 지정
					.as(Projections.constructor(
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
						complaint.createdAt,
						GroupBy.list(complaintImage.imageUrl)
					))
			);

		// 2) Map에서 해당 id의 DTO 가져오기
		return Optional.ofNullable(resultMap.get(id));
	}
}
