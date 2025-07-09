package com.wholeseeds.mindle.domain.auth.service;

import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

@Service
public class FirebaseAuthService {

	/**
	 * Firebase ID 토큰을 검증하고, 유효한 경우 FirebaseToken 객체를 반환합니다.
	 *
	 * @param idToken Firebase ID 토큰
	 * @return 검증된 FirebaseToken 객체
	 * @throws FirebaseAuthException 토큰 검증 실패 시 예외 발생
	 */
	public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
		return FirebaseAuth.getInstance().verifyIdToken(idToken);
	}
}
