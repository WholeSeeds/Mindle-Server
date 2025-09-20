package com.wholeseeds.mindle.domain.complaint.entity;

import com.wholeseeds.mindle.common.entity.BaseEntity;
import com.wholeseeds.mindle.domain.member.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "complaint_reaction",
	uniqueConstraints = @UniqueConstraint(
		name = "uk_complaint_reaction_complaint_member",
		columnNames = {"complaint_id", "member_id"}
	)
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ComplaintReaction extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "complaint_id", nullable = false)
	private Complaint complaint;
}

