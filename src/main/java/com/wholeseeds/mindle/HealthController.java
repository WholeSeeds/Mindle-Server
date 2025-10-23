package com.wholeseeds.mindle;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "헬스체크")
@RestController
@RequestMapping("/api/health")
public class HealthController {

	@Operation(summary = "헬스체크")
	@GetMapping
	public ResponseEntity<String> ok() {
		return ResponseEntity.ok("OK");
	}
}
