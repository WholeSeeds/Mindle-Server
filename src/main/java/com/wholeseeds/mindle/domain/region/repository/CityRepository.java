package com.wholeseeds.mindle.domain.region.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wholeseeds.mindle.domain.region.entity.City;

public interface CityRepository extends JpaRepository<City, Long> {
	Optional<City> findByName(String name);
}
