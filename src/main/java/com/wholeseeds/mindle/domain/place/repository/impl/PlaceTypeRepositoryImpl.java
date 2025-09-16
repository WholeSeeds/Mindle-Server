package com.wholeseeds.mindle.domain.place.repository.impl;

import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.place.entity.PlaceType;
import com.wholeseeds.mindle.domain.place.entity.QPlaceType;
import com.wholeseeds.mindle.domain.place.repository.custom.PlaceTypeRepositoryCustom;

import jakarta.persistence.EntityManager;

public class PlaceTypeRepositoryImpl extends JpaBaseRepositoryImpl<PlaceType, Long> implements
	PlaceTypeRepositoryCustom {
	private static final QPlaceType PT = QPlaceType.placeType;

	public PlaceTypeRepositoryImpl(EntityManager em) {
		super(PlaceType.class, em, PT, PT.id, PT.deletedAt);
	}
}
