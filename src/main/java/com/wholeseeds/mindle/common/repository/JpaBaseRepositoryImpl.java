package com.wholeseeds.mindle.common.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wholeseeds.mindle.common.entity.BaseEntity;
import com.wholeseeds.mindle.common.exception.QueryDslNotInitializedException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * 공통 JPA 레포지토리 구현체
 *
 * @param <T>  엔티티 타입
 * @param <ID> 엔티티 ID 타입
 */
public class JpaBaseRepositoryImpl<T extends BaseEntity, ID extends Serializable>
	extends SimpleJpaRepository<T, ID>
	implements JpaBaseRepository<T, ID> {

	@PersistenceContext
	private final EntityManager em;

	protected final JPAQueryFactory queryFactory;
	protected final EntityPath<T> entityPath;
	protected final NumberPath<Long> idPath;
	protected final DateTimePath<?> deletedAtPath;

	/**
	 * [Spring 자동 주입용 생성자]
	 * Spring Data JPA가 Repository 구현체를 자동 생성할 때 사용.
	 *
	 * @deprecated 코드상에서 직접 호출하지 말 것.
	 * @since 1.0
	 */
	@Deprecated(since = "1.0", forRemoval = true)
	protected JpaBaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
		super(entityInformation, em);
		this.em = em;
		this.queryFactory = new JPAQueryFactory(em);
		this.entityPath = null;
		this.idPath = null;
		this.deletedAtPath = null;
	}

	/**
	 * [QueryDSL 커스텀용 생성자]
	 * 커스텀 Repository 구현체에서 사용.
	 * QueryDSL용 필드 명시적 주입 필요.
	 */
	public JpaBaseRepositoryImpl(Class<T> domainClass, EntityManager em, EntityPath<T> entityPath,
		NumberPath<Long> idPath, DateTimePath<?> deletedAtPath) {
		super(domainClass, em);
		this.em = em;
		this.queryFactory = new JPAQueryFactory(em);
		this.entityPath = entityPath;
		this.idPath = idPath;
		this.deletedAtPath = deletedAtPath;
	}

	/**
	 * ID로 엔티티를 조회하되, 삭제되지 않은 엔티티만 반환합니다.
	 *
	 * @param id 엔티티 ID
	 * @return 삭제되지 않은 엔티티가 존재하면 Optional에 포함, 없으면 Optional.empty()
	 */
	@Override
	public Optional<T> findByIdNotDeleted(ID id) {
		validateQueryDslInitialized();
		BooleanExpression condition = Objects.requireNonNull(idPath).eq((Long)id)
			.and(Objects.requireNonNull(deletedAtPath).isNull());
		return Optional.ofNullable(queryFactory.selectFrom(entityPath).where(condition).fetchOne());
	}

	/**
	 * 삭제되지 않은 모든 엔티티를 조회합니다.
	 *
	 * @return 삭제되지 않은 엔티티 리스트
	 */
	@Override
	public List<T> findAllNotDeleted() {
		validateQueryDslInitialized();
		return queryFactory.selectFrom(entityPath)
			.where(Objects.requireNonNull(deletedAtPath).isNull())
			.fetch();
	}

	/**
	 * QueryDSL이 초기화되었는지 확인합니다.
	 * idPath, entityPath, deletedAtPath가 모두 설정되어 있어야 합니다.
	 *
	 * @throws QueryDslNotInitializedException 초기화되지 않은 경우
	 */
	private void validateQueryDslInitialized() {
		if (idPath == null || entityPath == null || deletedAtPath == null) {
			throw new QueryDslNotInitializedException();
		}
	}
}
