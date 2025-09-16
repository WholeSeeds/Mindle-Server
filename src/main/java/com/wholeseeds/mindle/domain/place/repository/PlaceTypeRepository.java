package com.wholeseeds.mindle.domain.place.repository;

import java.util.Optional;

import com.wholeseeds.mindle.common.repository.JpaBaseRepository;
import com.wholeseeds.mindle.domain.place.entity.PlaceType;

public interface PlaceTypeRepository extends JpaBaseRepository<PlaceType, Long> {
	Optional<PlaceType> findByName(String name);

	Optional<PlaceType> findByNameAndDeletedAtIsNull(String name);
}
