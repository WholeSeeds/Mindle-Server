package com.wholeseeds.mindle.domain.location.repository.impl;

import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.location.entity.District;
import com.wholeseeds.mindle.domain.location.entity.QDistrict;
import com.wholeseeds.mindle.domain.location.repository.custom.DistrictRepositoryCustom;

import jakarta.persistence.EntityManager;

public class DistrictRepositoryImpl extends JpaBaseRepositoryImpl<District, Long> implements DistrictRepositoryCustom {
	public static final QDistrict district = QDistrict.district;

	public DistrictRepositoryImpl(EntityManager em) {
		super(District.class, em, district, district.id, district.deletedAt);
	}
}
