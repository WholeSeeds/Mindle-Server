package com.wholeseeds.mindle.domain.auth.service;

import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

@Service
public class FirebaseAuthService {

	public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
		return FirebaseAuth.getInstance().verifyIdToken(idToken);
	}
}
