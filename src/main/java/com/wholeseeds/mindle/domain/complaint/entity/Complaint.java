package com.wholeseeds.mindle.domain.complaint.entity;

import com.wholeseeds.mindle.common.entity.BaseEntity;
import com.wholeseeds.mindle.domain.category.entity.Category;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.place.entity.Place;
import com.wholeseeds.mindle.domain.region.entity.Subdistrict;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "complaint")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Complaint extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subdistrict_code", referencedColumnName = "code")
	private Subdistrict subdistrict;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	private Place place;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column
	private Double latitude;

	@Column
	private Double longitude;

	@Enumerated(EnumType.STRING)
	@Column
	private Status status;

	@Column(name = "is_resolved")
	private Boolean isResolved;

	@Builder.Default
	@Column(name = "resolved_vote_count", nullable = false)
	private Integer resolvedVoteCount = 0;

	public enum Status {
		IN_PROGRESS,
		RESOLVED
	}

	@Override
	protected void onPrePersist() {
		if (status == null) {
			status = Status.IN_PROGRESS;
		}
		if (isResolved == null) {
			isResolved = false;
		}
		if (resolvedVoteCount == null) {
			resolvedVoteCount = 0;
		}
	}

	public void changeCategory(Category category) {
		this.category = category;
	}

	public void changeSubdistrict(Subdistrict subdistrict) {
		this.subdistrict = subdistrict;
	}

	public void changePlace(Place place) {
		this.place = place;
	}

	public void changeTitle(String title) {
		this.title = title;
	}

	public void changeContent(String content) {
		this.content = content;
	}

	public void changeLatLng(Double lat, Double lng) {
		this.latitude = lat;
		this.longitude = lng;
	}

	/** 투표 카운트 +1 (호출 시점은 서비스에서 중복 투표 검증 후) */
	public void incrementResolvedVoteCount() {
		this.resolvedVoteCount = this.resolvedVoteCount + 1;
	}

	/** threshold 도달 시 상태를 RESOLVED로, isResolved=true */
	public void markResolvedIfThresholdReached(int threshold) {
		if (this.resolvedVoteCount != null && this.resolvedVoteCount >= threshold) {
			this.status = Status.RESOLVED;
			this.isResolved = true;
		}
	}
}
