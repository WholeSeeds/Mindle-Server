package com.wholeseeds.mindle.domain.region.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.domain.complaint.dto.request.SaveComplaintRequestDto;
import com.wholeseeds.mindle.domain.place.entity.Place;
import com.wholeseeds.mindle.domain.place.exception.PlaceNotFoundException;
import com.wholeseeds.mindle.domain.place.repository.PlaceRepository;
import com.wholeseeds.mindle.domain.region.dto.DistrictDto;
import com.wholeseeds.mindle.domain.region.dto.SubdistrictDto;
import com.wholeseeds.mindle.domain.region.dto.response.CityResponseDto;
import com.wholeseeds.mindle.domain.region.dto.response.DistrictResponseDto;
import com.wholeseeds.mindle.domain.region.dto.response.SubdistrictResponseDto;
import com.wholeseeds.mindle.domain.region.entity.City;
import com.wholeseeds.mindle.domain.region.entity.District;
import com.wholeseeds.mindle.domain.region.entity.Subdistrict;
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
	 * 하위 행정구역(읍/면/동) 조회
	 * @param dto 민원 요청 DTO
	 * @return Subdistrict 객체
	 */
	@Transactional(readOnly = true)
	public Subdistrict findSubdistrict(SaveComplaintRequestDto dto) {
		if (dto.getCityName() == null || dto.getSubdistrictName() == null) {
			return null;
		}
		City city = cityRepository.findByName(dto.getCityName())
			.orElseThrow(CityNotFoundException::new);
		return subdistrictRepository.findByNameAndCity(dto.getSubdistrictName(), city)
			.orElseThrow(SubdistrictNotFoundException::new);
	}

	/**
	 * 장소 조회
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

	@Transactional(readOnly = true)
	public CityResponseDto getCityDetail(String code) {
		City city = cityRepository.findById(code).orElseThrow(CityNotFoundException::new);
		List<DistrictDto> districtDtos = regionMapper.toDistrictDtoList(districtRepository.findAllByCityCode(code));
		List<SubdistrictDto> subdistrictDtos = regionMapper.toSubdistrictDtoList(subdistrictRepository.findAllByCityCode(code));

		return CityResponseDto.builder()
			.region(regionMapper.toCityDto(city))
			.districts(districtDtos)
			.subdistricts(subdistrictDtos)
			.build();
	}

	@Transactional(readOnly = true)
	public DistrictResponseDto getDistrictDetail(String code) {
		District district = districtRepository.findById(code).orElseThrow(DistrictNotFoundException::new);
		List<SubdistrictDto> subdistrictDtos = regionMapper.toSubdistrictDtoList(subdistrictRepository.findAllByDistrictCode(code));

		return DistrictResponseDto.builder()
			.region(regionMapper.toDistrictDto(district))
			.subdistricts(subdistrictDtos)
			.build();
	}

	@Transactional(readOnly = true)
	public SubdistrictResponseDto getSubdistrictDetail(String code) {
		Subdistrict subdistrict = subdistrictRepository.findById(code).orElseThrow(SubdistrictNotFoundException::new);

		return new SubdistrictResponseDto(regionMapper.toSubdistrictDto(subdistrict));
	}
}
