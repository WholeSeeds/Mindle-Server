package com.wholeseeds.mindle.domain.place.repository.impl;

import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.place.entity.Place;
import com.wholeseeds.mindle.domain.place.entity.QPlace;
import com.wholeseeds.mindle.domain.place.repository.custom.PlaceRepositoryCustom;

import jakarta.persistence.EntityManager;

public class PlaceRepositoryImpl extends JpaBaseRepositoryImpl<Place, Long> implements PlaceRepositoryCustom {
	public static final QPlace place = QPlace.place;

	public PlaceRepositoryImpl(EntityManager em) {
		super(Place.class, em, place, place.id, place.deletedAt);
	}
}
