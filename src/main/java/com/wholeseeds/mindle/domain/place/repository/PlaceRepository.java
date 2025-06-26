package com.wholeseeds.mindle.domain.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wholeseeds.mindle.domain.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
	Place findByName(String name); // FIXME : 중복된 이름이 있다면 구분을 어떻게?
}
