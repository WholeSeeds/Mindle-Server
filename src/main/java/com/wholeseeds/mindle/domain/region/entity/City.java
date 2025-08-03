package com.wholeseeds.mindle.domain.region.entity;

import com.wholeseeds.mindle.domain.region.entity.type.CityType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "city")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class City extends AdministrativeRegion {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CityType type;
}
