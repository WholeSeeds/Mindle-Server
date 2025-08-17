package com.wholeseeds.mindle.domain.complaint.dto.csv;

import com.opencsv.bean.CsvBindByName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryCsvDto {
	@CsvBindByName(column = "id")
	private Long id;

	@CsvBindByName(column = "name")
	private String name;

	@CsvBindByName(column = "description")
	private String description;

	@CsvBindByName(column = "parent_id")
	private Long parentId;
}
