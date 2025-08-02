package com.wholeseeds.mindle.domain.region.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wholeseeds.mindle.domain.region.entity.District;

public interface DistrictRepository extends JpaRepository<District, Long> {
	Optional<District> findByName(String name);
}
