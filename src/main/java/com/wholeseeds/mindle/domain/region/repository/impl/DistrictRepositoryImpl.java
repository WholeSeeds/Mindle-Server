package com.wholeseeds.mindle.domain.region.repository.impl;

import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.region.entity.District;
import com.wholeseeds.mindle.domain.region.entity.QDistrict;
import com.wholeseeds.mindle.domain.region.repository.custom.DistrictRepositoryCustom;

import jakarta.persistence.EntityManager;

public class DistrictRepositoryImpl extends JpaBaseRepositoryImpl<District, Long> implements DistrictRepositoryCustom {
	public static final QDistrict district = QDistrict.district;

	public DistrictRepositoryImpl(EntityManager em) {
		super(District.class, em, district, district.id, district.deletedAt);
	}
}
