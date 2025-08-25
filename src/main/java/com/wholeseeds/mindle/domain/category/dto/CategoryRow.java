package com.wholeseeds.mindle.domain.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryRow {
	private Long id;
	private String name;
	private String description;
	private Long parentId;
}
