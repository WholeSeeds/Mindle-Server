package com.wholeseeds.mindle.domain.location.repository.impl;

import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.location.entity.QSubdistrict;
import com.wholeseeds.mindle.domain.location.entity.Subdistrict;
import com.wholeseeds.mindle.domain.location.repository.custom.SubdistrictRepositoryCustom;

import jakarta.persistence.EntityManager;

public class SubdistrictRepositoryImpl extends JpaBaseRepositoryImpl<Subdistrict, Long>
	implements SubdistrictRepositoryCustom {
	private static final QSubdistrict subdistrict = QSubdistrict.subdistrict;

	public SubdistrictRepositoryImpl(EntityManager em) {
		super(Subdistrict.class, em, subdistrict, subdistrict.id, subdistrict.deletedAt);
	}
}
