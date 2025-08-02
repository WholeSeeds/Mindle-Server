package com.wholeseeds.mindle.domain.region.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wholeseeds.mindle.domain.region.entity.City;
import com.wholeseeds.mindle.domain.region.entity.Subdistrict;

public interface SubdistrictRepository extends JpaRepository<Subdistrict, String> {
	Optional<Subdistrict> findByNameAndCity(String name, City city);
}
