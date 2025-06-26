package com.wholeseeds.mindle.domain.location.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wholeseeds.mindle.domain.location.entity.District;
import com.wholeseeds.mindle.domain.location.entity.Subdistrict;

public interface SubdistrictRepository extends JpaRepository<Subdistrict, Long> {
	Optional<Subdistrict> findByNameAndDistrict(String name, District district);
}
