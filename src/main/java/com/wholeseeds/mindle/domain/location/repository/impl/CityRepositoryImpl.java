package com.wholeseeds.mindle.domain.location.repository.impl;

import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.location.entity.City;
import com.wholeseeds.mindle.domain.location.entity.QCity;
import com.wholeseeds.mindle.domain.location.repository.custom.CityRepositoryCustom;

import jakarta.persistence.EntityManager;

public class CityRepositoryImpl extends JpaBaseRepositoryImpl<City, Long> implements CityRepositoryCustom {
	public static final QCity city = QCity.city;

	public CityRepositoryImpl(EntityManager em) {
		super(City.class, em, city, city.id, city.deletedAt);
	}
}
