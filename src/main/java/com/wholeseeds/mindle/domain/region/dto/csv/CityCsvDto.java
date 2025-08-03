package com.wholeseeds.mindle.domain.region.dto.csv;

import com.opencsv.bean.CsvBindByName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityCsvDto {

	@CsvBindByName(column = "code")
	private String code;

	@CsvBindByName(column = "name")
	private String name;

	@CsvBindByName(column = "type")
	private String type;
}
