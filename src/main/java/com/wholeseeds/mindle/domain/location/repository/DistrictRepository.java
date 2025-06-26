package com.wholeseeds.mindle.domain.location.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wholeseeds.mindle.domain.location.entity.District;

public interface DistrictRepository extends JpaRepository<District, Long> {
	Optional<District> findByName(String name);
}
