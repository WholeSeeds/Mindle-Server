package com.wholeseeds.mindle.common.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.opencsv.bean.CsvToBeanBuilder;
import com.wholeseeds.mindle.common.exception.CsvLoadException;

@Component
public class CsvLoader {
	public <T> List<T> loadCsv(String path, Class<T> type) {
		InputStream resourceAsStream = Objects.requireNonNull(getClass().getResourceAsStream(path));
		try (InputStreamReader reader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)) {
			return new CsvToBeanBuilder<T>(reader)
				.withType(type)
				.withIgnoreLeadingWhiteSpace(true)
				.build()
				.parse();
		} catch (Exception e) {
			throw new CsvLoadException("Failed to load CSV: " + path);
		}
	}
}
