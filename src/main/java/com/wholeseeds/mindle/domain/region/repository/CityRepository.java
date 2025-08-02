package com.wholeseeds.mindle.domain.region.repository;

import java.util.Optional;

import com.wholeseeds.mindle.common.repository.JpaBaseRepository;
import com.wholeseeds.mindle.domain.region.entity.City;

public interface CityRepository extends JpaBaseRepository<City, Long> {
	Optional<City> findByName(String name);
}
