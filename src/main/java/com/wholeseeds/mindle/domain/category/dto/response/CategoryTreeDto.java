package com.wholeseeds.mindle.domain.category.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeDto {
	private Long id;
	private String name;
	private String description;

	@Builder.Default
	private List<CategoryTreeDto> children = new ArrayList<>();
}
