package com.wholeseeds.mindle.common.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wholeseeds.mindle.common.entity.BaseEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class JpaBaseRepositoryImpl<T extends BaseEntity, ID extends Serializable>
	extends SimpleJpaRepository<T, ID>
	implements JpaBaseRepository<T, ID> {

	@PersistenceContext
	private final EntityManager em;

	protected final JPAQueryFactory queryFactory;
	protected final EntityPath<T> entityPath;
	protected final NumberPath<Long> idPath;
	protected final DateTimePath<?> deletedAtPath;

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
