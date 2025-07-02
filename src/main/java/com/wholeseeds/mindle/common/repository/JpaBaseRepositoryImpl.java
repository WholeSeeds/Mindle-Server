package com.wholeseeds.mindle.common.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wholeseeds.mindle.common.entity.BaseEntity;

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

	// Spring Data JPA가 사용하는 생성자
	public JpaBaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
		super(entityInformation, em);
		this.em = em;
		this.queryFactory = new JPAQueryFactory(em);

		// 이 3개는 null 처리해도 되고, 하위 클래스에서 재정의할 수 있음
		this.entityPath = null;
		this.idPath = null;
		this.deletedAtPath = null;
	}

	// 커스텀용 생성자 (직접 구현할 때 사용)
	public JpaBaseRepositoryImpl(Class<T> domainClass, EntityManager em, EntityPath<T> entityPath,
		NumberPath<Long> idPath, DateTimePath<?> deletedAtPath) {
		super(domainClass, em);
		this.em = em;
		this.queryFactory = new JPAQueryFactory(em);
		this.entityPath = entityPath;
		this.idPath = idPath;
		this.deletedAtPath = deletedAtPath;
	}

	@Override
	public Optional<T> findByIdNotDeleted(ID id) {
		BooleanExpression condition = idPath.eq((Long)id).and(deletedAtPath.isNull());
		return Optional.ofNullable(queryFactory.selectFrom(entityPath).where(condition).fetchOne());
	}

	@Override
	public List<T> findAllNotDeleted() {
		return queryFactory.selectFrom(entityPath)
			.where(deletedAtPath.isNull())
			.fetch();
	}
}
