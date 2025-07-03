package com.wholeseeds.mindle.domain.place.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wholeseeds.mindle.domain.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
	Optional<Place> findByPlaceId(String placeId);
}
