package com.wholeseeds.mindle.domain.region.entity;

import com.wholeseeds.mindle.domain.region.entity.type.DistrictType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	name = "district",
	uniqueConstraints = @UniqueConstraint(columnNames = {"city_id", "name"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class District extends AdministrativeRegion {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_code", referencedColumnName = "administrativeCode", nullable = false)
	private City city;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DistrictType type;
}
