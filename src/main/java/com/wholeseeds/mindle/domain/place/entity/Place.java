package com.wholeseeds.mindle.domain.place.entity;

import com.wholeseeds.mindle.common.entity.BaseEntity;
import com.wholeseeds.mindle.domain.location.entity.Subdistrict;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "place")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Place extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_id", nullable = false)
	private PlaceType type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subdistrict_id")
	private Subdistrict subdistrict;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(length = 500)
	private String description;

	@Column
	private Double latitude;

	@Column
	private Double longitude;
}
