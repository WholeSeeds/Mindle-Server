package com.wholeseeds.mindle.domain.region.repository.impl;

import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.region.entity.QSubdistrict;
import com.wholeseeds.mindle.domain.region.entity.Subdistrict;
import com.wholeseeds.mindle.domain.region.repository.custom.SubdistrictRepositoryCustom;

import jakarta.persistence.EntityManager;

public class SubdistrictRepositoryImpl extends JpaBaseRepositoryImpl<Subdistrict, Long>
	implements SubdistrictRepositoryCustom {
	private static final QSubdistrict subdistrict = QSubdistrict.subdistrict;

	public SubdistrictRepositoryImpl(EntityManager em) {
		super(Subdistrict.class, em, subdistrict, subdistrict.id, subdistrict.deletedAt);
	}
}
