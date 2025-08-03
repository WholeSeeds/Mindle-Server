package com.wholeseeds.mindle.global.initializer;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.common.util.CsvLoader;
import com.wholeseeds.mindle.domain.region.dto.csv.CityCsvDto;
import com.wholeseeds.mindle.domain.region.dto.csv.DistrictCsvDto;
import com.wholeseeds.mindle.domain.region.dto.csv.SubdistrictCsvDto;
import com.wholeseeds.mindle.domain.region.entity.City;
import com.wholeseeds.mindle.domain.region.entity.District;
import com.wholeseeds.mindle.domain.region.entity.Subdistrict;
import com.wholeseeds.mindle.domain.region.entity.type.CityType;
import com.wholeseeds.mindle.domain.region.entity.type.DistrictType;
import com.wholeseeds.mindle.domain.region.entity.type.SubdistrictType;
import com.wholeseeds.mindle.domain.region.repository.CityRepository;
import com.wholeseeds.mindle.domain.region.repository.DistrictRepository;
import com.wholeseeds.mindle.domain.region.repository.SubdistrictRepository;

import lombok.RequiredArgsConstructor;

/**
 * 경기도 행정구역 데이터를 초기화하는 서비스
 */
@Service
@RequiredArgsConstructor
public class RegionInitializer implements CommandLineRunner {

	private final CsvLoader csvLoader;
	private final CityRepository cityRepository;
	private final DistrictRepository districtRepository;
	private final SubdistrictRepository subdistrictRepository;

	/**
	 * 애플리케이션 시작 시 경기도 행정구역 데이터를 로드하여 데이터베이스에 저장합니다.
	 *
	 * @param args 명령줄 인자
	 */
	@Override
	@Transactional
	public void run(String... args) {
		// City
		List<CityCsvDto> cities = csvLoader.loadCsv("/data/city.csv", CityCsvDto.class);
		for (CityCsvDto city : cities) {
			if (!cityRepository.existsById(city.getCode())) {
				cityRepository.save(City.builder()
					.code(city.getCode())
					.name(city.getName())
					.type(CityType.valueOf(city.getType()))
					.build());
			}
		}

		// District
		List<DistrictCsvDto> districts = csvLoader.loadCsv("/data/district.csv", DistrictCsvDto.class);
		for (DistrictCsvDto district : districts) {
			if (!districtRepository.existsById(district.getCode())) {
				City city = cityRepository.findById(district.getCityCode()).orElseThrow();
				districtRepository.save(District.builder()
					.code(district.getCode())
					.name(district.getName())
					.city(city)
					.type(DistrictType.valueOf(district.getType()))
					.build());
			}
		}

		// Subdistrict
		List<SubdistrictCsvDto> subs = csvLoader.loadCsv("/data/subdistrict.csv", SubdistrictCsvDto.class);
		for (SubdistrictCsvDto sub : subs) {
			if (!subdistrictRepository.existsById(sub.getCode())) {
				City city = null;
				District district = null;

				if (sub.getDistrictCode() != null && !sub.getDistrictCode().isBlank()) {
					district = districtRepository.findById(sub.getDistrictCode()).orElseThrow();
				} else {
					city = cityRepository.findById(sub.getCityCode()).orElseThrow();
				}

				subdistrictRepository.save(Subdistrict.builder()
					.code(sub.getCode())
					.name(sub.getName())
					.city(city)
					.district(district)
					.type(SubdistrictType.valueOf(sub.getType()))
					.build());
			}
		}
	}
}
