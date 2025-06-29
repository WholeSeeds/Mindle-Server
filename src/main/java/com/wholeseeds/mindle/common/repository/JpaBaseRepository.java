package com.wholeseeds.mindle.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 공통 JPA 레포지토리 인터페이스
 *
 * @param <T>  엔티티 타입
 * @param <ID> 엔티티 ID 타입
 */
public interface JpaBaseRepository<T, ID> extends JpaRepository<T, ID> {
	/**
	 * ID로 엔티티를 조회하되, 삭제되지 않은 엔티티만 반환
	 *
	 * @param id 엔티티 ID
	 * @return 삭제되지 않은 엔티티가 존재하면 Optional에 포함, 없으면 Optional.empty()
	 */
	Optional<T> findByIdNotDeleted(ID id);

	/**
	 * 삭제되지 않은 모든 엔티티를 조회
	 *
	 * @return 삭제되지 않은 엔티티 리스트
	 */
	List<T> findAllNotDeleted();
}
