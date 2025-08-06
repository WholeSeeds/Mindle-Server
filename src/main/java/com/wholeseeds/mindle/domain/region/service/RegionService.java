package com.wholeseeds.mindle.domain.region.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.domain.complaint.dto.request.SaveComplaintRequestDto;
import com.wholeseeds.mindle.domain.place.entity.Place;
import com.wholeseeds.mindle.domain.place.exception.PlaceNotFoundException;
import com.wholeseeds.mindle.domain.place.repository.PlaceRepository;
import com.wholeseeds.mindle.domain.region.dto.CityDto;
import com.wholeseeds.mindle.domain.region.dto.DistrictDto;
import com.wholeseeds.mindle.domain.region.dto.SubdistrictDto;
import com.wholeseeds.mindle.domain.region.dto.response.RegionDetailResponseDto;
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
	public RegionDetailResponseDto<CityDto, DistrictDto> getCityDetail(String code) {
		City city = cityRepository.findById(code).orElseThrow(CityNotFoundException::new);
		List<DistrictDto> districtDtos = regionMapper.toDistrictDtoList(
			districtRepository.findAllByCityCode(code)
		);
		return RegionDetailResponseDto.<CityDto, DistrictDto>builder()
			.region(regionMapper.toCityDto(city))
			.children(districtDtos)
			.build();
	}

	@Transactional(readOnly = true)
	public RegionDetailResponseDto<DistrictDto, SubdistrictDto> getDistrictDetail(String code) {
		District district = districtRepository.findById(code).orElseThrow(DistrictNotFoundException::new);
		List<SubdistrictDto> subdistrictDtos = regionMapper.toSubdistrictDtoList(
			subdistrictRepository.findAllByDistrictCode(code)
		);
		return RegionDetailResponseDto.<DistrictDto, SubdistrictDto>builder()
			.region(regionMapper.toDistrictDto(district))
			.children(subdistrictDtos)
			.build();
	}

	@Transactional(readOnly = true)
	public RegionDetailResponseDto<SubdistrictDto, Object> getSubdistrictDetail(String code) {
		Subdistrict subdistrict = subdistrictRepository.findById(code).orElseThrow(SubdistrictNotFoundException::new);
		return RegionDetailResponseDto.<SubdistrictDto, Object>builder()
			.region(regionMapper.toSubdistrictDto(subdistrict))
			.children(List.of())
			.build();
	}
}
