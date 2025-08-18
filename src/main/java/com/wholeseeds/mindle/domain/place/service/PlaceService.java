package com.wholeseeds.mindle.domain.place.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.domain.place.entity.Place;
import com.wholeseeds.mindle.domain.place.exception.PlaceNotFoundException;
import com.wholeseeds.mindle.domain.place.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {

	private final PlaceRepository placeRepository;

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
}
