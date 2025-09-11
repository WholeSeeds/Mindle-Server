package com.wholeseeds.mindle.domain.place.repository;

import java.util.Optional;

import com.wholeseeds.mindle.common.repository.JpaBaseRepository;
import com.wholeseeds.mindle.domain.place.entity.Place;

public interface PlaceRepository extends JpaBaseRepository<Place, Long> {
	Optional<Place> findByPlaceId(String placeId);

	Optional<Place> findByPlaceIdAndDeletedAtIsNull(String placeId);
}
