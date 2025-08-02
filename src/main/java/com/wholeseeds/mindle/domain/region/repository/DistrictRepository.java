package com.wholeseeds.mindle.domain.region.repository;

import java.util.Optional;

import com.wholeseeds.mindle.common.repository.JpaBaseRepository;
import com.wholeseeds.mindle.domain.region.entity.District;

public interface DistrictRepository extends JpaBaseRepository<District, Long> {
	Optional<District> findByName(String name);
}
