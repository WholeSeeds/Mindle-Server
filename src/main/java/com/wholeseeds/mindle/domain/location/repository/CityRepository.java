package com.wholeseeds.mindle.domain.location.repository;

import java.util.Optional;

import com.wholeseeds.mindle.common.repository.JpaBaseRepository;
import com.wholeseeds.mindle.domain.location.entity.City;

public interface CityRepository extends JpaBaseRepository<City, Long> {
	Optional<City> findByName(String name);
}
