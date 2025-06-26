package com.wholeseeds.mindle.domain.location.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wholeseeds.mindle.domain.location.entity.City;

public interface CityRepository extends JpaRepository<City, Long> {
	Optional<City> findByName(String name);
}
