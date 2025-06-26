package com.wholeseeds.mindle.domain.complaint.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wholeseeds.mindle.domain.complaint.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
