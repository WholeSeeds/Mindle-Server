package com.wholeseeds.mindle.common.repository;

import java.util.List;
import java.util.Optional;

public interface JpaBaseRepository<T, ID> {

	Optional<T> findByIdNotDeleted(ID id);

	List<T> findAllNotDeleted();
}
