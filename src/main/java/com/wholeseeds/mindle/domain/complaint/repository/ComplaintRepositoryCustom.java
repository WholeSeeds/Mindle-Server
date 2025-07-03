package com.wholeseeds.mindle.domain.complaint.repository;

import java.util.Optional;

import com.wholeseeds.mindle.domain.complaint.dto.DetailComplaintDto;

public interface ComplaintRepositoryCustom {
	Optional<DetailComplaintDto> getDetailById(Long id);
}
