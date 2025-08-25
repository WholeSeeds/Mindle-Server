package com.wholeseeds.mindle.domain.complaint.dto.response;

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
public class CategoryDto {
	private Long id;
	private String name;
	private String description;

	@Builder.Default
	private List<CategoryDto> children = new ArrayList<>();
}
