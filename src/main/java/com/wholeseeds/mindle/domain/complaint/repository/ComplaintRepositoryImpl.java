package com.wholeseeds.mindle.domain.complaint.repository;

import java.util.Map;
import java.util.Optional;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.complaint.dto.ComplaintDetailWithImagesDto;
import com.wholeseeds.mindle.domain.complaint.dto.ReactionDto;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.entity.QComplaint;
import com.wholeseeds.mindle.domain.complaint.entity.QComplaintImage;
import com.wholeseeds.mindle.domain.complaint.entity.QComplaintReaction;

import jakarta.persistence.EntityManager;

public class ComplaintRepositoryImpl extends JpaBaseRepositoryImpl<Complaint, Long>
	implements ComplaintRepositoryCustom {

	private static final QComplaint C = QComplaint.complaint;
	private static final QComplaintImage I = QComplaintImage.complaintImage;
	private static final QComplaintReaction R = QComplaintReaction.complaintReaction;

	public ComplaintRepositoryImpl(EntityManager em) {
		super(Complaint.class, em, C, C.id, C.deletedAt);
	}

	/* 민원상세, 이미지 URL 조회 */
	@Override
	public Optional<ComplaintDetailWithImagesDto> getComplaintWithImages(Long complaintId) {
		// transform 결과 Map<Id, DTO> 여서 맵핑
		Map<Long, ComplaintDetailWithImagesDto> resultMap = queryFactory
			.from(C)
			.leftJoin(I).on(I.complaint.id.eq(C.id))
			.where(C.id.eq(complaintId)
				.and(C.deletedAt.isNull()))
			.transform(
				GroupBy.groupBy(C.id).as(
					Projections.constructor(
						ComplaintDetailWithImagesDto.class,
						C.id,
						C.title,
						C.content,
						C.category.name,
						C.member.nickname,
						C.place.name,
						C.subdistrict.city.name,
						C.subdistrict.district.name,
						C.subdistrict.name,
						C.createdAt,
						GroupBy.list(I.imageUrl)
					)
				)
			);
		// Map 에서 해당 id의 DTO 꺼냄
		return Optional.ofNullable(resultMap.get(complaintId));
	}

	/* '특정 민원의 총 공감 수'와 '로그인 사용자의 해당 글 공감 여부' 조회 */
	@Override
	public Optional<ReactionDto> getReaction(Long complaintId, Long memberId) {
		// 전체 count
		NumberExpression<Long> reactionCount = R.id.count().coalesce(0L);

		// 로그인 사용자 공감 여부
		BooleanExpression isReacted = JPAExpressions
			.selectOne()
			.from(R)
			.where(R.complaint.id.eq(complaintId).and(R.member.id.eq(memberId)).and(R.deletedAt.isNull()))
			.exists();

		return Optional.ofNullable(queryFactory
			.select(Projections.constructor(
				ReactionDto.class,
				reactionCount,
				isReacted
			))
			.from(R)
			.where(R.complaint.id.eq(complaintId).and(R.deletedAt.isNull()))
			.fetchOne());
	}
}
