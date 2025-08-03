package com.wholeseeds.mindle.domain.region.entity;

import com.wholeseeds.mindle.domain.region.entity.type.SubdistrictType;
import com.wholeseeds.mindle.domain.region.exception.InvalidSubdistrictReferenceException;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
	name = "subdistrict",
	uniqueConstraints = @UniqueConstraint(columnNames = {"city_code", "district_code", "name"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Subdistrict extends AdministrativeRegion {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_code", referencedColumnName = "code")
	private City city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "district_code", referencedColumnName = "code")
	private District district;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SubdistrictType type;

	@Override
	protected void onPrePersist() {
		validateReferences();
	}

	@Override
	protected void onPreUpdate() {
		validateReferences();
	}

	// City 또는 District 중 하나만 참조하도록 검증
	private void validateReferences() {
		if ((city == null && district == null) || (city != null && district != null)) {
			throw new InvalidSubdistrictReferenceException();
		}
	}
}
