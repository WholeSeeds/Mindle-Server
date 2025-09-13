package com.wholeseeds.mindle.domain.region.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.common.util.ObjectUtils;
import com.wholeseeds.mindle.domain.region.dto.request.RegionNameRequestDto;
import com.wholeseeds.mindle.domain.region.dto.response.RegionDetailResponseDto;
import com.wholeseeds.mindle.domain.region.entity.City;
import com.wholeseeds.mindle.domain.region.entity.District;
import com.wholeseeds.mindle.domain.region.entity.Subdistrict;
import com.wholeseeds.mindle.domain.region.enums.RegionType;
import com.wholeseeds.mindle.domain.region.exception.CityNotFoundException;
import com.wholeseeds.mindle.domain.region.exception.DistrictNotFoundException;
import com.wholeseeds.mindle.domain.region.exception.InvalidRegionNameCombinationException;
import com.wholeseeds.mindle.domain.region.exception.SubdistrictNotFoundException;
import com.wholeseeds.mindle.domain.region.mapper.RegionDetailMapper;
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
	private final RegionMapper regionMapper;
	private final RegionDetailMapper regionDetailMapper;

	/**
	 * City(시/도) 조회.
	 *
	 * @param cityCode 시/도 코드 (null 허용)
	 * @return City 엔티티 또는 null
	 * @throws CityNotFoundException 코드가 존재하지 않으면 발생
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
	 * District(시/군/구) 조회.
	 *
	 * @param districtCode 시/군/구 코드 (null 허용)
	 * @return District 엔티티 또는 null
	 * @throws DistrictNotFoundException 코드가 존재하지 않으면 발생
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
	 * Subdistrict(읍/면/동) 조회.
	 *
	 * @param subdistrictCode 읍/면/동 코드 (null 허용)
	 * @return Subdistrict 엔티티 또는 null
	 * @throws SubdistrictNotFoundException 코드가 존재하지 않으면 발생
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
	 * 행정구역 코드 기준 상세 조회.
	 *
	 * @param regionType 조회할 행정구역 타입
	 * @param code 행정구역 코드
	 * @return 상세 응답 DTO
	 */
	@Transactional(readOnly = true)
	public RegionDetailResponseDto<?> getRegionDetail(RegionType regionType, String code) {
		return switch (regionType) {
			case CITY -> {
				City city = cityRepository.findById(code)
					.orElseThrow(CityNotFoundException::new);
				yield regionDetailMapper.toCityDetail(
					city,
					districtRepository.findAllByCityCode(city.getCode()),
					subdistrictRepository.findAllByCityCode(city.getCode()),
					regionMapper
				);
			}
			case DISTRICT -> {
				District district = districtRepository.findById(code)
					.orElseThrow(DistrictNotFoundException::new);
				yield regionDetailMapper.toDistrictDetail(
					district,
					subdistrictRepository.findAllByDistrictCode(district.getCode()),
					regionMapper
				);
			}
			case SUBDISTRICT -> {
				Subdistrict subdistrict = subdistrictRepository.findById(code)
					.orElseThrow(SubdistrictNotFoundException::new);
				yield regionDetailMapper.toSubdistrictDetail(subdistrict, regionMapper);
			}
		};
	}

	/**
	 * 행정구역 이름 조합으로 상세 조회.
	 *
	 * @param req 이름 기반 요청 DTO
	 * @return 상세 응답 DTO
	 * @throws InvalidRegionNameCombinationException 허용되지 않는 조합일 경우
	 * @throws CityNotFoundException                 시/도가 없을 경우
	 * @throws DistrictNotFoundException             구가 없을 경우
	 * @throws SubdistrictNotFoundException          읍/면/동이 없을 경우
	 */
	@Transactional(readOnly = true)
	public RegionDetailResponseDto<?> getRegionDetailByNames(RegionNameRequestDto req) {
		final String cityName = ObjectUtils.normalize(req.getCityName());
		final String districtName = ObjectUtils.normalize(req.getDistrictName());
		final String subdistrictName = ObjectUtils.normalize(req.getSubdistrictName());

		validateNameCombination(cityName, districtName, subdistrictName);

		City city = cityRepository.findByName(cityName)
			.orElseThrow(CityNotFoundException::new);

		boolean hasDistrict = districtName != null && !districtName.isBlank();
		boolean hasSubdistrict = subdistrictName != null && !subdistrictName.isBlank();

		if (!hasDistrict && !hasSubdistrict) {
			return regionDetailMapper.toCityDetail(
				city,
				districtRepository.findAllByCityCode(city.getCode()),
				subdistrictRepository.findAllByCityCode(city.getCode()),
				regionMapper
			);
		}

		District district = districtRepository
			.findByCityCodeAndName(city.getCode(), districtName)
			.orElseThrow(DistrictNotFoundException::new);

		if (!hasSubdistrict) {
			return regionDetailMapper.toDistrictDetail(
				district,
				subdistrictRepository.findAllByDistrictCode(district.getCode()),
				regionMapper
			);
		}

		Subdistrict subdistrict = subdistrictRepository
			.findByDistrictCodeAndName(district.getCode(), subdistrictName)
			.orElseThrow(SubdistrictNotFoundException::new);

		return regionDetailMapper.toSubdistrictDetail(subdistrict, regionMapper);
	}

	/**
	 * 이름 조합 유효성 검증
	 */
	private void validateNameCombination(String cityName, String districtName, String subdistrictName) {
		if (cityName == null || cityName.isBlank()) {
			throw new InvalidRegionNameCombinationException();
		}

		final boolean hasSubdistrict = subdistrictName != null && !subdistrictName.isBlank();
		final boolean missingDistrict = districtName == null || districtName.isBlank();

		if (hasSubdistrict && missingDistrict) {
			throw new InvalidRegionNameCombinationException();
		}
	}
}
