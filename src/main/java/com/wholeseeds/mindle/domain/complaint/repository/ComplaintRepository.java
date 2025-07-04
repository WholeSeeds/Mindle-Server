package com.wholeseeds.mindle.domain.complaint.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wholeseeds.mindle.domain.complaint.entity.Complaint;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

}
