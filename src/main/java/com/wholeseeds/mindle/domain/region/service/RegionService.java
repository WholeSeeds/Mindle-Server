package com.wholeseeds.mindle.domain.region.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.domain.place.entity.Place;
import com.wholeseeds.mindle.domain.place.exception.PlaceNotFoundException;
import com.wholeseeds.mindle.domain.place.repository.PlaceRepository;
import com.wholeseeds.mindle.domain.region.dto.response.RegionDetailResponseDto;
import com.wholeseeds.mindle.domain.region.entity.City;
import com.wholeseeds.mindle.domain.region.entity.District;
import com.wholeseeds.mindle.domain.region.entity.Subdistrict;
import com.wholeseeds.mindle.domain.region.enums.RegionType;
import com.wholeseeds.mindle.domain.region.exception.CityNotFoundException;
import com.wholeseeds.mindle.domain.region.exception.DistrictNotFoundException;
import com.wholeseeds.mindle.domain.region.exception.SubdistrictNotFoundException;
import com.wholeseeds.mindle.domain.region.mapper.RegionMapper;
import com.wholeseeds.mindle.domain.region.repository.CityRepository;
import com.wholeseeds.mindle.domain.region.repository.DistrictRepository;
import com.wholeseeds.mindle.domain.region.repository.SubdistrictRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionService {
	private final CityRepository cityRepository;
	private final DistrictRepository districtRepository;
	private final SubdistrictRepository subdistrictRepository;
	private final PlaceRepository placeRepository;
	private final RegionMapper regionMapper;

	/**
	 * City(시/도) 조회
	 *
	 * @param cityCode 시/도 코드
	 * @return City 객체
	 */
	@Transactional(readOnly = true)
	public City findCity(String cityCode) {
		if (cityCode == null) {
			return null;
		}
		return cityRepository.findById(cityCode)
			.orElseThrow(CityNotFoundException::new);
	}

	/**
	 * District(시/군/구) 조회
	 *
	 * @param districtCode 시/군/구 코드
	 * @return District 객체
	 */
	@Transactional(readOnly = true)
	public District findDistrict(String districtCode) {
		if (districtCode == null) {
			return null;
		}
		return districtRepository.findById(districtCode)
			.orElseThrow(DistrictNotFoundException::new);
	}

	/**
	 * Subdistrict(읍/면/동) 조회
	 *
	 * @param subdistrictCode 읍/면/동 코드
	 * @return Subdistrict 객체
	 */
	@Transactional(readOnly = true)
	public Subdistrict findSubdistrict(String subdistrictCode) {
		if (subdistrictCode == null) {
			return null;
		}
		return subdistrictRepository.findById(subdistrictCode)
			.orElseThrow(SubdistrictNotFoundException::new);
	}

	/**
	 * 장소 조회
	 *
	 * @param placeId 장소 ID
	 * @return Place 객체
	 */
	@Transactional(readOnly = true)
	public Place findPlace(String placeId) {
		if (placeId == null) {
			return null;
		}
		return placeRepository.findByPlaceId(placeId)
			.orElseThrow(PlaceNotFoundException::new);
	}

	/**
	 * 통합 행정구역 상세 정보 조회
	 *
	 * @param regionType 행정구역 타입
	 * @param code 행정구역 코드
	 * @return RegionDetailResponseDto - 일관된 구조의 응답 DTO
	 */
	@Transactional(readOnly = true)
	public RegionDetailResponseDto<?> getRegionDetail(RegionType regionType, String code) {
		return switch (regionType) {
			case CITY -> RegionDetailResponseDto.forCity(
				regionMapper.toCityDto(
					cityRepository.findById(code)
						.orElseThrow(CityNotFoundException::new)),
				regionMapper.toDistrictDtoList(
					districtRepository.findAllByCityCode(code)),
				regionMapper.toSubdistrictDtoList(
					subdistrictRepository.findAllByCityCode(code))
			);
			case DISTRICT -> RegionDetailResponseDto.forDistrict(
				regionMapper.toDistrictDto(
					districtRepository.findById(code)
						.orElseThrow(DistrictNotFoundException::new)),
				regionMapper.toSubdistrictDtoList(
					subdistrictRepository.findAllByDistrictCode(code))
			);
			case SUBDISTRICT -> RegionDetailResponseDto.forSubdistrict(
				regionMapper.toSubdistrictDto(
					subdistrictRepository.findById(code)
						.orElseThrow(SubdistrictNotFoundException::new))
			);
		};
	}
}
