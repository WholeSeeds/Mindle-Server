package com.wholeseeds.mindle.domain.region.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wholeseeds.mindle.domain.region.entity.District;

public interface DistrictRepository extends JpaRepository<District, String> {

	Optional<District> findByName(String name);

	List<District> findAllByCityCode(String cityCode);

	Optional<District> findByCityCodeAndName(String cityCode, String name);
}
