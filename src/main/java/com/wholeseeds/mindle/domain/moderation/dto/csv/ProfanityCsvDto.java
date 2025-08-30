package com.wholeseeds.mindle.domain.moderation.dto.csv;

import com.opencsv.bean.CsvBindByName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfanityCsvDto {
	@CsvBindByName(column = "profanity")
	private String profanity;
}
