package com.wholeseeds.mindle.domain.complaint.repository;

import com.wholeseeds.mindle.common.repository.JpaBaseRepository;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;

public interface ComplaintRepository extends JpaBaseRepository<Complaint, Long>, ComplaintRepositoryCustom {

}
