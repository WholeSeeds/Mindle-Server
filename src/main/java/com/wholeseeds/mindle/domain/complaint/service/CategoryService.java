package com.wholeseeds.mindle.domain.complaint.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.domain.complaint.entity.Category;
import com.wholeseeds.mindle.domain.complaint.exception.CategoryNotFoundException;
import com.wholeseeds.mindle.domain.complaint.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
	private final CategoryRepository categoryRepository;

	/**
	 * 카테고리 조회
	 * @param categoryId 카테고리 ID
	 * @return Category 객체
	 */
	@Transactional(readOnly = true)
	public Category findCategory(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(CategoryNotFoundException::new);
	}
}
