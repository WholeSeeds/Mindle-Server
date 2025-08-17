package com.wholeseeds.mindle.domain.complaint.repository.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.comment.entity.QComment;
import com.wholeseeds.mindle.domain.complaint.dto.CommentDto;
import com.wholeseeds.mindle.domain.complaint.dto.ComplaintDetailWithImagesDto;
import com.wholeseeds.mindle.domain.complaint.dto.ReactionDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.ComplaintListResponseDto;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.entity.QComplaint;
import com.wholeseeds.mindle.domain.complaint.entity.QComplaintImage;
import com.wholeseeds.mindle.domain.complaint.entity.QComplaintReaction;
import com.wholeseeds.mindle.domain.complaint.repository.custom.ComplaintRepositoryCustom;

import jakarta.persistence.EntityManager;

public class ComplaintRepositoryImpl extends JpaBaseRepositoryImpl<Complaint, Long>
	implements ComplaintRepositoryCustom {

	private static final QComplaint C = QComplaint.complaint;
	private static final QComplaintImage I = QComplaintImage.complaintImage;
	private static final QComplaintReaction R = QComplaintReaction.complaintReaction;
	private static final QComment COMMENT = QComment.comment;

	public ComplaintRepositoryImpl(EntityManager em) {
		super(Complaint.class, em, C, C.id, C.deletedAt);
	}

	/* 민원상세, 이미지 URL 조회 */
	@Override
	public Optional<ComplaintDetailWithImagesDto> getComplaintWithImages(Long complaintId) {
		// 먼저 Complaint 엔티티를 이미지와 함께 조회
		Complaint complaint = queryFactory
			.selectFrom(C)
			.leftJoin(C.place).fetchJoin()
			.leftJoin(C.place.type).fetchJoin()
			.leftJoin(C.subdistrict).fetchJoin()
			.leftJoin(C.subdistrict.district).fetchJoin()
			.leftJoin(C.subdistrict.city).fetchJoin()
			.leftJoin(C.category).fetchJoin()
			.leftJoin(C.member).fetchJoin()
			.where(C.id.eq(complaintId)
				.and(C.deletedAt.isNull()))
			.fetchOne();

		if (complaint == null) {
			return Optional.empty();
		}

		// 이미지 URL 목록 조회
		List<String> imageUrls = queryFactory
			.select(I.imageUrl)
			.from(I)
			.where(I.complaint.id.eq(complaintId))
			.fetch();

		// DTO 변환은 서비스에서 처리하도록 데이터만 전달
		// 임시로 기존 방식 유지하되 DTO 객체 생성 부분만 수정
		ComplaintDetailWithImagesDto dto = ComplaintDetailWithImagesDto.builder()
			.id(complaint.getId())
			.title(complaint.getTitle())
			.content(complaint.getContent())
			.categoryName(complaint.getCategory().getName())
			.memberNickname(complaint.getMember().getNickname())
			.place(null) // 매퍼에서 처리
			.city(null) // 매퍼에서 처리
			.district(null) // 매퍼에서 처리
			.subdistrict(null) // 매퍼에서 처리
			.createdAt(complaint.getCreatedAt())
			.imageUrlList(imageUrls)
			.build();

		return Optional.of(dto);
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
			.where(
				R.complaint.id.eq(complaintId)
					.and(R.member.id.eq(memberId))
					.and(R.deletedAt.isNull())
			)
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

	/* 댓글 조회 cursor 기반 페이지네이션 (최신순) */
	@Override
	public List<CommentDto> getComment(
		Long complaintId,
		LocalDateTime cursorCreatedAt,
		int pageSize
	) {
		// 주어진 시각 이전에 작성된 과거 댓글 조회 (페이지네이션)
		BooleanExpression beforeCursor = cursorCreatedAt != null ? COMMENT.createdAt.lt(cursorCreatedAt) : null;

		return queryFactory
			.select(Projections.constructor(
				CommentDto.class,
				COMMENT.id,
				COMMENT.content,
				COMMENT.createdAt,
				COMMENT.member.id,
				COMMENT.member.nickname
			))
			.from(COMMENT)
			.where(COMMENT.complaint.id.eq(complaintId).and(COMMENT.deletedAt.isNull()), beforeCursor)
			.orderBy(COMMENT.createdAt.desc())
			.limit(pageSize)
			.fetch();
	}

	@Override
	public List<ComplaintListResponseDto> findListWithCursor(
		Long cursorComplaintId,
		int pageSize,
		String cityCode,
		String districtCode,
		Long categoryId
	) {
		SubQueryExpression<Long> commentCount = JPAExpressions
			.select(COMMENT.id.count())
			.from(COMMENT)
			.where(COMMENT.complaint.id.eq(C.id).and(COMMENT.deletedAt.isNull()));

		// reactionCount 서브쿼리
		SubQueryExpression<Long> reactionCount = JPAExpressions
			.select(R.id.count())
			.from(R)
			.where(R.complaint.id.eq(C.id).and(R.deletedAt.isNull()));

		// 대표 이미지 URL
		SubQueryExpression<String> imageUrl = JPAExpressions
			.select(I.imageUrl)
			.from(I)
			.where(I.complaint.id.eq(C.id).and(I.deletedAt.isNull()))
			.limit(1);

		return queryFactory
			.select(Projections.constructor(
				ComplaintListResponseDto.class,
				C.id,
				C.title,
				C.content,
				C.createdAt,
				C.isResolved,
				commentCount,
				reactionCount,
				imageUrl
			))
			.from(C)
			.where(C.deletedAt.isNull(),
				cursorComplaintId != null ? C.id.lt(cursorComplaintId) : null
			)
			.where(
				categoryId != null ? C.category.id.eq(categoryId) : null,
				cityCode != null ? C.subdistrict.city.code.eq(cityCode) : null,
				districtCode != null ? C.subdistrict.district.code.eq(districtCode) : null
			)
			.orderBy(C.id.desc())
			.limit(pageSize)
			.fetch();
	}
}
