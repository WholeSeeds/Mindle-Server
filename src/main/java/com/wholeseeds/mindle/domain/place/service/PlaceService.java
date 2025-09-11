package com.wholeseeds.mindle.domain.place.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.wholeseeds.mindle.domain.place.dto.command.PlaceUpsertCmd;
import com.wholeseeds.mindle.domain.place.entity.Place;
import com.wholeseeds.mindle.domain.place.entity.PlaceType;
import com.wholeseeds.mindle.domain.place.exception.PlaceNameRequiredException;
import com.wholeseeds.mindle.domain.place.exception.PlaceNotFoundException;
import com.wholeseeds.mindle.domain.place.exception.PlaceTypeRequiredException;
import com.wholeseeds.mindle.domain.place.repository.PlaceRepository;
import com.wholeseeds.mindle.domain.place.repository.PlaceTypeRepository;
import com.wholeseeds.mindle.domain.region.entity.Subdistrict;
import com.wholeseeds.mindle.domain.region.service.RegionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {

	private final PlaceRepository placeRepository;
	private final PlaceTypeRepository placeTypeRepository;
	private final RegionService regionService;

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
	 * placeId(외부 키)로 Place를 조회하고, 없으면 생성한다. soft-delete면 복구한다.
	 *
	 * @param cmd 업서트 입력(외부 placeId 필수, 신규 생성 시 type/name 필요)
	 * @return 조회/복구/신규 Place (placeId 공백이면 {@code null})
	 * @throws PlaceTypeRequiredException 신규 생성 요건(placeTypeName) 미충족 시
	 * @throws PlaceNameRequiredException 신규 생성 요건(placeName) 미충족 시
	 */
	@Transactional
	public Place findOrCreatePlace(PlaceUpsertCmd cmd) {
		if (!StringUtils.hasText(cmd.getPlaceId())) {
			return null;
		}

		// 1) 살아있는 기존 Place
		Optional<Place> alive = resolveExistingPlace(cmd.getPlaceId());
		if (alive.isPresent()) {
			return alive.get();
		}

		// 2) soft-deleted 복구
		Optional<Place> softDeleted = placeRepository.findByPlaceId(cmd.getPlaceId());
		if (softDeleted.isPresent()) {
			return restoreAndUpdate(softDeleted.get(), cmd);
		}

		// 3) 신규 생성
		validateNewPlaceInputs(cmd);
		PlaceType type = findOrCreatePlaceType(cmd.getPlaceTypeName());
		Subdistrict sub = resolveSubdistrict(cmd.getSubdistrictCode());

		try {
			Place created = buildNewPlace(cmd, type, sub);
			return placeRepository.save(created);
		} catch (DataIntegrityViolationException e) {
			return resolveAfterUniqueViolation(cmd.getPlaceId());
		}
	}

	/**
	 * 이름으로 PlaceType을 조회/복구/생성한다. 동시성 충돌 시 재조회로 멱등 보장.
	 *
	 * @param typeName PlaceType 이름
	 * @return 조회/복구/신규 PlaceType
	 * @throws PlaceNameRequiredException typeName 미지정 시
	 */
	public PlaceType findOrCreatePlaceType(String typeName) {
		if (!StringUtils.hasText(typeName)) {
			throw new PlaceNameRequiredException();
		}

		return placeTypeRepository.findByNameAndDeletedAtIsNull(typeName)
			.or(() -> placeTypeRepository.findByName(typeName)
				.map(this::restorePlaceType))
			.orElseGet(() -> createPlaceTypeSafely(typeName));
	}

	/**
	 * 살아있는 Place를 placeId로 조회한다.
	 *
	 * @param placeId 외부 placeId
	 * @return 존재하면 Optional(Place), 없으면 Optional.empty()
	 */
	private Optional<Place> resolveExistingPlace(String placeId) {
		return placeRepository.findByPlaceIdAndDeletedAtIsNull(placeId);
	}

	/**
	 * soft-deleted Place를 복구하고 옵션 필드를 반영해 저장한다.
	 *
	 * @param deleted soft-deleted Place
	 * @param cmd     옵션 필드가 담긴 커맨드
	 * @return 저장된 Place
	 */
	private Place restoreAndUpdate(Place deleted, PlaceUpsertCmd cmd) {
		deleted.restore();
		applyOptionalFields(deleted, cmd);
		return placeRepository.save(deleted);
	}

	/**
	 * 신규 Place를 조립한다.
	 *
	 * @param cmd  업서트 입력
	 * @param type PlaceType (존재/신규)
	 * @param sub  Subdistrict(선택)
	 * @return 미저장 Place 인스턴스
	 */
	private Place buildNewPlace(PlaceUpsertCmd cmd, PlaceType type, Subdistrict sub) {
		return Place.builder()
			.type(type)
			.subdistrict(sub)
			.placeId(cmd.getPlaceId())
			.name(cmd.getPlaceName())
			.description(cmd.getDescription())
			.latitude(cmd.getLatitude())
			.longitude(cmd.getLongitude())
			.build();
	}

	/**
	 * 유니크 제약 위반 발생 시 재조회하여 멱등하게 Place를 반환한다.
	 *
	 * @param placeId 외부 placeId
	 * @return 재조회된 Place(있으면), 없으면 soft-deleted 포함 재조회 결과 또는 null
	 */
	private Place resolveAfterUniqueViolation(String placeId) {
		return placeRepository.findByPlaceIdAndDeletedAtIsNull(placeId)
			.orElseGet(() -> placeRepository.findByPlaceId(placeId).orElse(null));
	}

	/**
	 * Subdistrict 코드를 행정동 엔티티로 해석한다. 공백/미지정이면 null.
	 *
	 * @param subdistrictCode 행정동 코드
	 * @return Subdistrict 또는 null
	 */
	private Subdistrict resolveSubdistrict(String subdistrictCode) {
		if (!StringUtils.hasText(subdistrictCode)) {
			return null;
		}
		return regionService.findSubdistrict(subdistrictCode);
	}

	/**
	 * PlaceType을 안전하게 생성한다. 동시성 충돌 시 재조회로 멱등 보장.
	 *
	 * @param typeName PlaceType 이름
	 * @return 생성(또는 경쟁 후 재조회한) PlaceType
	 */
	private PlaceType createPlaceTypeSafely(String typeName) {
		try {
			return placeTypeRepository.save(PlaceType.builder().name(typeName).build());
		} catch (DataIntegrityViolationException e) {
			return placeTypeRepository.findByNameAndDeletedAtIsNull(typeName)
				.orElseGet(() -> placeTypeRepository.findByName(typeName).orElseThrow());
		}
	}

	/**
	 * soft-deleted PlaceType을 복구해 저장한다.
	 *
	 * @param deleted soft-deleted PlaceType
	 * @return 저장된 PlaceType
	 */
	private PlaceType restorePlaceType(PlaceType deleted) {
		deleted.restore();
		return placeTypeRepository.save(deleted);
	}

	/**
	 * 신규 Place 생성 요건을 검증한다.
	 *
	 * @param cmd upsert 입력
	 * @throws PlaceTypeRequiredException type 미지정 시
	 * @throws PlaceNameRequiredException name 미지정 시
	 */
	private void validateNewPlaceInputs(PlaceUpsertCmd cmd) {
		if (!StringUtils.hasText(cmd.getPlaceTypeName())) {
			throw new PlaceTypeRequiredException();
		}
		if (!StringUtils.hasText(cmd.getPlaceName())) {
			throw new PlaceNameRequiredException();
		}
	}

	/**
	 * 기존 Place에 옵션 필드를 반영한다(name/description/lat,lng/subdistrict).
	 *
	 * @param target 갱신 대상 Place
	 * @param cmd    입력(옵션 필드 포함)
	 */
	private void applyOptionalFields(Place target, PlaceUpsertCmd cmd) {
		if (StringUtils.hasText(cmd.getPlaceName())) {
			target.changeName(cmd.getPlaceName());
		}
		if (cmd.getDescription() != null) {
			target.changeDescription(cmd.getDescription());
		}

		boolean hasLat = cmd.getLatitude() != null;
		boolean hasLng = cmd.getLongitude() != null;
		if (hasLat || hasLng) {
			Double lat = hasLat ? cmd.getLatitude() : target.getLatitude();
			Double lng = hasLng ? cmd.getLongitude() : target.getLongitude();
			target.changeLatLng(lat, lng);
		}

		if (StringUtils.hasText(cmd.getSubdistrictCode())) {
			target.changeSubdistrict(regionService.findSubdistrict(cmd.getSubdistrictCode()));
		}
	}
}
