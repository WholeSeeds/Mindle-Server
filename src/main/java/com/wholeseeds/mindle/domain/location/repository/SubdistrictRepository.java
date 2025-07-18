package com.wholeseeds.mindle.domain.location.repository;

import java.util.Optional;

import com.wholeseeds.mindle.common.repository.JpaBaseRepository;
import com.wholeseeds.mindle.domain.location.entity.City;
import com.wholeseeds.mindle.domain.location.entity.Subdistrict;

public interface SubdistrictRepository extends JpaBaseRepository<Subdistrict, Long> {
	Optional<Subdistrict> findByNameAndCity(String name, City city);
}
